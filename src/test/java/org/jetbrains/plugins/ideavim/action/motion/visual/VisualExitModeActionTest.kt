/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action.motion.visual

import com.maddyhome.idea.vim.command.VimStateMachine
import org.jetbrains.plugins.ideavim.VimTestCase

class VisualExitModeActionTest : VimTestCase() {
  fun `test exit visual mode after line end`() {
    doTest("vl<Esc>", "12${c}3", "12${c}3", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
    assertCaretsVisualAttributes()
  }

  fun `test double exit`() {
    doTest("vl<Esc><Esc>", "12${c}3", "12${c}3", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
    assertCaretsVisualAttributes()
  }
}
