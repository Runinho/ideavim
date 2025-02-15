/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.yank

import com.maddyhome.idea.vim.action.motion.updown.MotionDownLess1FirstNonSpaceAction
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimCaret
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.getLineEndForOffset
import com.maddyhome.idea.vim.api.getLineStartForOffset
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.Argument
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.command.SelectionType
import com.maddyhome.idea.vim.common.TextRange
import com.maddyhome.idea.vim.listener.VimYankListener
import org.jetbrains.annotations.Contract
import kotlin.math.min

open class YankGroupBase : VimYankGroup {
  private val yankListeners: MutableList<VimYankListener> = ArrayList()

  protected fun yankRange(
    editor: VimEditor,
    caretToRange: Map<VimCaret, TextRange>,
    range: TextRange,
    type: SelectionType,
    startOffsets: Map<VimCaret, Int>?,
  ): Boolean {
    startOffsets?.forEach { (caret, offset) ->
      caret.moveToOffset(offset)
    }

    notifyListeners(editor, range)

    var result = true
    for ((caret, myRange) in caretToRange) {
      result = caret.registerStorage.storeText(caret, editor, myRange, type, false) && result
    }
    return result
  }

  @Contract("_, _ -> new")
  protected fun getTextRange(ranges: List<Pair<Int, Int>>, type: SelectionType): TextRange? {
    if (ranges.isEmpty()) return null

    val size = ranges.size
    val starts = IntArray(size)
    val ends = IntArray(size)

    if (type == SelectionType.LINE_WISE) {
      starts[size - 1] = ranges[size - 1].first
      ends[size - 1] = ranges[size - 1].second
      for (i in 0 until size - 1) {
        val range = ranges[i]
        starts[i] = range.first
        ends[i] = range.second - 1
      }
    } else {
      for (i in 0 until size) {
        val range = ranges[i]
        starts[i] = range.first
        ends[i] = range.second
      }
    }

    return TextRange(starts, ends)
  }

  /**
   * This yanks the text moved over by the motion command argument.
   *
   * @param editor   The editor to yank from
   * @param context  The data context
   * @param count    The number of times to yank
   * @param rawCount The actual count entered by the user
   * @param argument The motion command argument
   * @return true if able to yank the text, false if not
   */
  override fun yankMotion(
    editor: VimEditor,
    context: ExecutionContext,
    argument: Argument,
    operatorArguments: OperatorArguments
  ): Boolean {
    val motion = argument.motion
    val type = if (motion.isLinewiseMotion()) SelectionType.LINE_WISE else SelectionType.CHARACTER_WISE

    val nativeCaretCount = editor.nativeCarets().size
    if (nativeCaretCount <= 0) return false

    val carretToRange = HashMap<VimCaret, TextRange>(nativeCaretCount)
    val ranges = ArrayList<Pair<Int, Int>>(nativeCaretCount)

    // This logic is from original vim
    val startOffsets = if (argument.motion.action is MotionDownLess1FirstNonSpaceAction) null else HashMap<VimCaret, Int>(nativeCaretCount)

    for (caret in editor.nativeCarets()) {
      val motionRange = injector.motion.getMotionRange(editor, caret, context, argument, operatorArguments)
        ?: continue

      assert(motionRange.size() == 1)
      ranges.add(motionRange.startOffset to motionRange.endOffset)
      startOffsets?.put(caret, motionRange.normalize().startOffset)
      carretToRange[caret] = TextRange(motionRange.startOffset, motionRange.endOffset)
    }

    val range = getTextRange(ranges, type) ?: return false

    if (range.size() == 0) return false

    return yankRange(
      editor,
      carretToRange,
      range,
      type,
      startOffsets
    )
  }

  /**
   * This yanks count lines of text
   *
   * @param editor The editor to yank from
   * @param count  The number of lines to yank
   * @return true if able to yank the lines, false if not
   */
  override fun yankLine(editor: VimEditor, count: Int): Boolean {
    val caretCount = editor.nativeCarets().size
    val ranges = ArrayList<Pair<Int, Int>>(caretCount)
    val caretToRange = HashMap<VimCaret, TextRange>(caretCount)
    for (caret in editor.nativeCarets()) {
      val start = injector.motion.moveCaretToCurrentLineStart(editor, caret)
      val end = min(injector.motion.moveCaretToRelativeLineEnd(editor, caret, count - 1, true) + 1, editor.fileSize().toInt())

      if (end == -1) continue

      ranges.add(start to end)
      caretToRange[caret] = TextRange(start, end)
    }

    val range = getTextRange(ranges, SelectionType.LINE_WISE) ?: return false
    return yankRange(editor, caretToRange, range, SelectionType.LINE_WISE, null)
  }

  /**
   * This yanks a range of text
   *
   * @param editor The editor to yank from
   * @param range  The range of text to yank
   * @param type   The type of yank
   * @return true if able to yank the range, false if not
   */
  override fun yankRange(editor: VimEditor, range: TextRange?, type: SelectionType, moveCursor: Boolean): Boolean {
    range ?: return false

    val caretToRange = HashMap<VimCaret, TextRange>()
    val selectionType = if (type == SelectionType.CHARACTER_WISE && range.isMultiple) SelectionType.BLOCK_WISE else type

    if (type == SelectionType.LINE_WISE) {
      for (i in 0 until range.size()) {
        if (editor.offsetToBufferPosition(range.startOffsets[i]).column != 0) {
          range.startOffsets[i] = editor.getLineStartForOffset(range.startOffsets[i])
        }
        if (editor.offsetToBufferPosition(range.endOffsets[i]).column != 0) {
          range.endOffsets[i] =
            (editor.getLineEndForOffset(range.endOffsets[i]) + 1).coerceAtMost(editor.fileSize().toInt())
        }
      }
    }

    val rangeStartOffsets = range.startOffsets
    val rangeEndOffsets = range.endOffsets

    val startOffsets = HashMap<VimCaret, Int>(editor.nativeCarets().size)
    if (type == SelectionType.BLOCK_WISE) {
      startOffsets[editor.primaryCaret()] = range.normalize().startOffset
      caretToRange[editor.primaryCaret()] = range
    } else {
      for ((i, caret) in editor.nativeCarets().withIndex()) {
        val textRange = TextRange(rangeStartOffsets[i], rangeEndOffsets[i])
        startOffsets[caret] = textRange.normalize().startOffset
        caretToRange[caret] = textRange
      }
    }

    return if (moveCursor) {
      yankRange(editor, caretToRange, range, selectionType, startOffsets)
    } else {
      yankRange(editor, caretToRange, range, selectionType, null)
    }
  }

  override fun addListener(listener: VimYankListener) = yankListeners.add(listener)
  override fun removeListener(listener: VimYankListener) = yankListeners.remove(listener)
  override fun notifyListeners(editor: VimEditor, textRange: TextRange) = yankListeners.forEach {
    it.yankPerformed(editor, textRange)
  }
}
