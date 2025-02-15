/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.vimscript.model.commands

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimCaret
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.ex.ExException
import com.maddyhome.idea.vim.ex.ranges.Ranges
import com.maddyhome.idea.vim.vimscript.model.ExecutionResult

/**
 * see "h :@"
 */
data class RepeatCommand(val ranges: Ranges, val argument: String) : Command.ForEachCaret(ranges, argument) {
  override val argFlags = flags(RangeFlag.RANGE_OPTIONAL, ArgumentFlag.ARGUMENT_REQUIRED, Access.SELF_SYNCHRONIZED)

  private var lastArg = ':'

  @Throws(ExException::class)
  override fun processCommand(
    editor: VimEditor,
    caret: VimCaret,
    context: ExecutionContext,
    operatorArguments: OperatorArguments
  ): ExecutionResult {
    var arg = argument[0]
    if (arg == '@') arg = lastArg
    lastArg = arg

    val line = getLine(editor, caret)
    caret.moveToOffset(
      injector.motion.moveCaretToLineWithSameColumn(editor, line, editor.primaryCaret())
    )

    if (arg == ':') {
      return if (injector.vimscriptExecutor.executeLastCommand(
          editor,
          context
        )
      ) ExecutionResult.Success else ExecutionResult.Error
    }

    val reg = injector.registerGroup.getPlaybackRegister(arg) ?: return ExecutionResult.Error
    val text = reg.text ?: return ExecutionResult.Error

    injector.vimscriptExecutor.execute(
      text,
      editor,
      context,
      skipHistory = false,
      indicateErrors = true,
      this.vimContext
    )
    return ExecutionResult.Success
  }
}
