/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package com.maddyhome.idea.vim.action.motion.visual

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.Command
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.command.VimStateMachine
import com.maddyhome.idea.vim.handler.VimActionHandler
import com.maddyhome.idea.vim.options.OptionConstants
import com.maddyhome.idea.vim.options.OptionScope
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString

class VisualToggleCharacterModeAction : VimActionHandler.SingleExecution() {
  override val type: Command.Type = Command.Type.OTHER_READONLY

  override fun execute(
    editor: VimEditor,
    context: ExecutionContext,
    cmd: Command,
    operatorArguments: OperatorArguments,
  ): Boolean {
    val listOption = (
      injector.optionService
        .getOptionValue(OptionScope.LOCAL(editor), OptionConstants.selectmodeName) as VimString
      ).value
    return if (listOption.contains("cmd")) {
      injector.visualMotionGroup.enterSelectMode(editor, VimStateMachine.SubMode.VISUAL_CHARACTER)
    } else injector.visualMotionGroup
      .toggleVisual(editor, cmd.count, cmd.rawCount, VimStateMachine.SubMode.VISUAL_CHARACTER)
  }
}
