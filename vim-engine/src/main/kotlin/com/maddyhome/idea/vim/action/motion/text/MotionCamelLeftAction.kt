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
import com.maddyhome.idea.vim.command.Argument
import com.maddyhome.idea.vim.command.MotionType
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.common.Direction
import com.maddyhome.idea.vim.handler.Motion
import com.maddyhome.idea.vim.handler.MotionActionHandler
import com.maddyhome.idea.vim.handler.toMotionOrError

class MotionCamelLeftAction : MotionCamelAction(Direction.BACKWARDS)
class MotionCamelRightAction : MotionCamelAction(Direction.FORWARDS)

sealed class MotionCamelAction(val direction: Direction) : MotionActionHandler.ForEachCaret() {
  override fun getOffset(
    editor: VimEditor,
    caret: VimCaret,
    context: ExecutionContext,
    argument: Argument?,
    operatorArguments: OperatorArguments,
  ): Motion {
    return moveCaretToNextCamel(editor, caret, direction.toInt() * operatorArguments.count1).toMotionOrError()
  }

  override val motionType: MotionType = MotionType.EXCLUSIVE
}

private fun moveCaretToNextCamel(editor: VimEditor, caret: VimCaret, count: Int): Int {
  return if ((caret.offset.point == 0 && count < 0 || caret.offset.point >= editor.fileSize() - 1) && count > 0) {
    -1
  } else {
    injector.searchHelper.findNextCamelStart(editor, caret, count)
  }
}
