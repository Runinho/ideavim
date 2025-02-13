/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.action.window

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.Command
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.handler.VimActionHandler

/**
 * @author Alex Plate
 */
class LookupUpAction : VimActionHandler.SingleExecution() {

  private val keySet = setOf(injector.parser.parseKeys("<C-P>"))

  override val type: Command.Type = Command.Type.OTHER_READONLY

  override fun execute(
    editor: VimEditor,
    context: ExecutionContext,
    cmd: Command,
    operatorArguments: OperatorArguments,
  ): Boolean {
    val activeLookup = injector.lookupManager.getActiveLookup(editor)
    if (activeLookup != null) {
      activeLookup.up(editor.primaryCaret(), context)
    } else {
      val keyStroke = keySet.first().first()
      val actions = injector.keyGroup.getKeymapConflicts(keyStroke)
      for (action in actions) {
        if (injector.actionExecutor.executeAction(action, context)) break
      }
    }
    return true
  }
}
