/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action.change.change.number

import com.maddyhome.idea.vim.command.VimStateMachine
import com.maddyhome.idea.vim.helper.VimBehaviorDiffers
import org.jetbrains.plugins.ideavim.VimTestCase

class ChangeNumberIncActionTest : VimTestCase() {
  @VimBehaviorDiffers(originalVimAfter = "11X0")
  fun `test inc fancy number`() {
    doTest("<C-A>", "1${c}0X0", "10X1", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }
}
