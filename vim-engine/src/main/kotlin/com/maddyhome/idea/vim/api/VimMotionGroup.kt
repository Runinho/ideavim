/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.api

import com.maddyhome.idea.vim.action.motion.leftright.TillCharacterMotionType
import com.maddyhome.idea.vim.command.Argument
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.common.TextRange
import com.maddyhome.idea.vim.handler.Motion

interface VimMotionGroup {

  // Note that the following methods require the caret to access the intended vertical position, such as "end of line"
  fun getOffsetOfHorizontalMotion(editor: VimEditor, caret: VimCaret, count: Int, allowPastEnd: Boolean): Int
  fun getVerticalMotionOffset(editor: VimEditor, caret: VimCaret, count: Int): Motion

  // TODO: Consider naming. These don't move the caret, but calculate offsets. Also consider returning Motion

  // Move caret to specific buffer line
  fun moveCaretToLineStart(editor: VimEditor, line: Int): Int
  fun moveCaretToLineStartSkipLeading(editor: VimEditor, line: Int): Int
  fun moveCaretToLineWithStartOfLineOption(editor: VimEditor, line: Int, caret: VimCaret): Int
  fun moveCaretToLineEnd(editor: VimEditor, line: Int, allowPastEnd: Boolean): Int
  fun moveCaretToLineWithSameColumn(editor: VimEditor, line: Int, caret: VimCaret): Int
  fun moveCaretToLinePercent(editor: VimEditor, caret: VimCaret, count: Int): Int

  // Move caret relative to current line
  fun moveCaretToRelativeLineStartSkipLeading(editor: VimEditor, caret: VimCaret, linesOffset: Int): Int
  fun moveCaretToRelativeLineEnd(editor: VimEditor, caret: VimCaret, cntForward: Int, allowPastEnd: Boolean): Int
  fun moveCaretToRelativeLineEndSkipTrailing(editor: VimEditor, caret: VimCaret, linesOffset: Int): Int

  // Move caret to (IntelliJ visual) line relative to the bounds of the display (aka window)
  // (This describes what these functions *currently* do, not what they are *supposed* to do)
  // TODO: These should move to the Vim logical line at the top/bottom/middle of the display
  fun moveCaretToFirstDisplayLine(editor: VimEditor, caret: VimCaret, count: Int, normalizeToScreen: Boolean): Int
  fun moveCaretToMiddleDisplayLine(editor: VimEditor, caret: VimCaret): Int
  fun moveCaretToLastDisplayLine(editor: VimEditor, caret: VimCaret, count: Int, normalizeToScreen: Boolean): Int

  // Move caret to buffer column
  fun moveCaretToColumn(editor: VimEditor, caret: VimCaret, count: Int, allowEnd: Boolean): Motion

  // Move caret to buffer column on current line
  fun moveCaretToCurrentLineStart(editor: VimEditor, caret: VimCaret): Int
  fun moveCaretToCurrentLineStartSkipLeading(editor: VimEditor, caret: VimCaret): Int
  fun moveCaretToCurrentLineEnd(editor: VimEditor, caret: VimCaret): Int

  // Move caret to column relative to the bounds of the display (aka window)
  fun moveCaretToCurrentDisplayLineStart(editor: VimEditor, caret: VimCaret): Motion
  fun moveCaretToCurrentDisplayLineStartSkipLeading(editor: VimEditor, caret: VimCaret): Int
  fun moveCaretToCurrentDisplayLineMiddle(editor: VimEditor, caret: VimCaret): Motion
  fun moveCaretToCurrentDisplayLineEnd(editor: VimEditor, caret: VimCaret, allowEnd: Boolean): Motion

  // Move caret to other
  fun moveCaretToMark(editor: VimEditor, ch: Char, toLineStart: Boolean): Int
  fun moveCaretToFileMark(editor: VimEditor, ch: Char, toLineStart: Boolean): Int
  fun moveCaretToJump(editor: VimEditor, count: Int): Int
  fun moveCaretToMatchingPair(editor: VimEditor, caret: VimCaret): Int

  // Scrolling
  fun scrollCaretIntoView(editor: VimEditor)
  fun scrollFullPage(editor: VimEditor, caret: VimCaret, pages: Int): Boolean
  fun scrollHalfPage(editor: VimEditor, caret: VimCaret, rawCount: Int, down: Boolean): Boolean
  fun scrollLines(editor: VimEditor, lines: Int): Boolean
  fun scrollCurrentLineToDisplayTop(editor: VimEditor, rawCount: Int, start: Boolean): Boolean
  fun scrollCurrentLineToDisplayMiddle(editor: VimEditor, rawCount: Int, start: Boolean): Boolean
  fun scrollCurrentLineToDisplayBottom(editor: VimEditor, rawCount: Int, start: Boolean): Boolean
  fun scrollColumns(editor: VimEditor, columns: Int): Boolean
  fun scrollCaretColumnToDisplayLeftEdge(vimEditor: VimEditor): Boolean
  fun scrollCaretColumnToDisplayRightEdge(editor: VimEditor): Boolean

  /**
   * Find the offset of the start of the next/previous word/WORD
   *
   * @param editor      The editor to search in
   * @param searchFrom  The buffer offset to start searching from
   * @param count       The number of words to skip
   * @param bigWord     If true then find WORD, if false then find word
   * @return a [Motion] representing the offset to move to, or [Motion.Error] if not found
   */
  fun findOffsetOfNextWord(editor: VimEditor, searchFrom: Int, count: Int, bigWord: Boolean): Motion

  // Next/previous matching character - f/F and t/T motions
  val lastFTCmd: TillCharacterMotionType
  val lastFTChar: Char
  fun setLastFTCmd(lastFTCmd: TillCharacterMotionType, lastChar: Char)

  /**
   * Get the offset of the next/previous matching character on the caret's buffer line
   *
   * @param editor The editor to search in
   * @param caret  The caret to be moved
   * @param count  The number of occurrences to move to
   * @param ch     The character to search for
   * @return the buffer offset to move to, or -1 if not found
   */
  fun moveCaretToNextCharacterOnLine(editor: VimEditor, caret: VimCaret, count: Int, ch: Char): Int

  /**
   * Get the offset of the character preceding (in direction of travel) the next/previous matching character on the
   * caret's buffer line
   *
   * @param editor The editor to search in
   * @param caret  The caret to be moved
   * @param count  The number of occurrences to move to
   * @param ch     The character to search for
   * @return the buffer offset to move to, or -1 if not found
   */
  fun moveCaretToBeforeNextCharacterOnLine(editor: VimEditor, caret: VimCaret, count: Int, ch: Char): Int

  fun repeatLastMatchChar(editor: VimEditor, caret: VimCaret, count: Int): Int

  fun getMotionRange(editor: VimEditor, caret: VimCaret, context: ExecutionContext, argument: Argument, operatorArguments: OperatorArguments): TextRange?

  // TODO: These aren't caret motions. Should be moved to VimWindowGroup?
  fun moveCaretGotoNextTab(editor: VimEditor, context: ExecutionContext, rawCount: Int): Int
  fun moveCaretGotoPreviousTab(editor: VimEditor, context: ExecutionContext, rawCount: Int): Int
  fun onAppCodeMovement(editor: VimEditor, caret: VimCaret, offset: Int, oldOffset: Int)
}
