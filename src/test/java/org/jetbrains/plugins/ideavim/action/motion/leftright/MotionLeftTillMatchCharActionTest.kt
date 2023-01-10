/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action.motion.leftright

import com.maddyhome.idea.vim.command.VimStateMachine
import org.jetbrains.plugins.ideavim.VimTestCase

class MotionLeftTillMatchCharActionTest : VimTestCase() {
  fun `test move and repeat`() {
    doTest(
      "Tx;",
      "hello x hello x hello$c",
      "hello x$c hello x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun `test move and repeat twice`() {
    doTest(
      "Tx;;",
      "hello x hello x hello x hello$c",
      "hello x$c hello x hello x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun `test move and repeat two`() {
    doTest(
      "Tx2;",
      "hello x hello x hello x hello$c",
      "hello x hello x$c hello x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }

  fun `test move and repeat three`() {
    doTest(
      "Tx3;",
      "hello x hello x hello x hello$c",
      "hello x$c hello x hello x hello",
      VimStateMachine.Mode.COMMAND,
      VimStateMachine.SubMode.NONE
    )
  }
}
