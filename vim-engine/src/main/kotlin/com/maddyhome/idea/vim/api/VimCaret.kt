/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.api

import com.maddyhome.idea.vim.command.SelectionType
import com.maddyhome.idea.vim.common.EditorLine
import com.maddyhome.idea.vim.common.LiveRange
import com.maddyhome.idea.vim.common.Offset
import com.maddyhome.idea.vim.common.TextRange
import com.maddyhome.idea.vim.group.visual.VisualChange
import com.maddyhome.idea.vim.group.visual.vimMoveBlockSelectionToOffset
import com.maddyhome.idea.vim.group.visual.vimMoveSelectionToCaret
import com.maddyhome.idea.vim.helper.exitVisualMode
import com.maddyhome.idea.vim.helper.inBlockSubMode
import com.maddyhome.idea.vim.helper.inSelectMode
import com.maddyhome.idea.vim.helper.inVisualMode
import com.maddyhome.idea.vim.options.helpers.StrictMode
import com.maddyhome.idea.vim.register.Register
import javax.swing.KeyStroke

// TODO: 29.12.2021 Split interface to mutable and immutable
interface VimCaret {
  val editor: VimEditor
  val offset: Offset
  val isValid: Boolean
  val isPrimary: Boolean

  fun getBufferPosition(): BufferPosition

  // TODO: [visual] Try to remove this. Visual position is an IntelliJ concept and Vim doesn't have a direct equivalent
  fun getVisualPosition(): VimVisualPosition

  fun getLine(): EditorLine.Pointer

  /**
   * Return the buffer line of the caret as a 1-based value, as used by VimScript
   */
  val vimLine: Int

  var vimLastColumn: Int
  fun resetLastColumn()

  fun hasSelection(): Boolean
  val selectionStart: Int
  val selectionEnd: Int
  var vimSelectionStart: Int
  val vimLeadSelectionOffset: Int

  fun vimSelectionStartClear()

  fun setSelection(start: Offset, end: Offset)
  fun removeSelection()

  fun moveToOffset(offset: Int) {
    if (offset < 0 || offset > editor.text().length || !isValid) return
    if (editor.inBlockSubMode) {
      StrictMode.assert(this == editor.primaryCaret(), "Block selection can only be moved with primary caret!")

      // Note that this call replaces ALL carets, so any local caret instances will be invalid!
      vimMoveBlockSelectionToOffset(editor, offset)
      injector.motion.scrollCaretIntoView(editor)
      return
    }

    // Make sure to always reposition the caret, even if the offset hasn't changed. We might need to reposition due to
    // changes in surrounding text, especially with inline inlays.
    val oldOffset = this.offset.point
    moveToInlayAwareOffset(offset)

    // Similarly, always make sure the caret is positioned within the view. Adding or removing text could move the caret
    // position relative to the view, without changing offset.
    if (this == editor.primaryCaret()) {
      injector.motion.scrollCaretIntoView(editor)
    }
    if (editor.inVisualMode || editor.inSelectMode) {
      vimMoveSelectionToCaret()
    } else {
      editor.exitVisualMode()
    }
    injector.motion.onAppCodeMovement(editor, this, offset, oldOffset)
  }

  fun moveToOffsetNative(offset: Int)
  fun moveToInlayAwareOffset(newOffset: Int)
  fun moveToBufferPosition(position: BufferPosition)

  // TODO: [visual] Try to remove this. Visual position is an IntelliJ concept and Vim doesn't have a direct equivalent
  fun moveToVisualPosition(position: VimVisualPosition)

  val visualLineStart: Int
  var vimInsertStart: LiveRange
  var vimLastVisualOperatorRange: VisualChange?

  val registerStorage: CaretRegisterStorage
}

interface CaretRegisterStorage {
  // todo methods shouldn't have caret in signature
  /**
   * Stores text to caret's recordable (named/numbered/unnamed) register
   */
  fun storeText(caret: VimCaret, editor: VimEditor, range: TextRange, type: SelectionType, isDelete: Boolean): Boolean

  /**
   * Gets text from caret's recordable register
   * If the register is not recordable - global text state will be returned
   */
  fun getRegister(caret: VimCaret, r: Char): Register?

  fun setKeys(caret: VimCaret, register: Char, keys: List<KeyStroke>)
  fun saveRegister(caret: VimCaret, r: Char, register: Register)
}
