/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package com.maddyhome.idea.vim.action.motion.text

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimCaret
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.api.normalizeOffset
import com.maddyhome.idea.vim.command.Argument
import com.maddyhome.idea.vim.command.CommandFlags
import com.maddyhome.idea.vim.command.MotionType
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.handler.Motion
import com.maddyhome.idea.vim.handler.MotionActionHandler
import com.maddyhome.idea.vim.handler.toMotionOrError
import com.maddyhome.idea.vim.helper.enumSetOf
import java.util.*

sealed class MotionUnmatchedAction(private val motionChar: Char) : MotionActionHandler.ForEachCaret() {
  override val flags: EnumSet<CommandFlags> = enumSetOf(CommandFlags.FLAG_SAVE_JUMP)

  override val motionType: MotionType = MotionType.EXCLUSIVE

  override fun getOffset(
    editor: VimEditor,
    caret: VimCaret,
    context: ExecutionContext,
    argument: Argument?,
    operatorArguments: OperatorArguments,
  ): Motion {
    return moveCaretToUnmatchedBlock(editor, caret, operatorArguments.count1, motionChar)
      .toMotionOrError()
  }
}

class MotionUnmatchedBraceCloseAction : MotionUnmatchedAction('}')
class MotionUnmatchedBraceOpenAction : MotionUnmatchedAction('{')
class MotionUnmatchedParenCloseAction : MotionUnmatchedAction(')')
class MotionUnmatchedParenOpenAction : MotionUnmatchedAction('(')

private fun moveCaretToUnmatchedBlock(editor: VimEditor, caret: VimCaret, count: Int, type: Char): Int {
  return if (editor.currentCaret().offset.point == 0 && count < 0 || editor.currentCaret().offset.point >= editor.fileSize() - 1 && count > 0) {
    -1
  } else {
    var res = injector.searchHelper.findUnmatchedBlock(editor, caret, type, count)
    if (res != -1) {
      res = editor.normalizeOffset(res, false)
    }
    res
  }
}
