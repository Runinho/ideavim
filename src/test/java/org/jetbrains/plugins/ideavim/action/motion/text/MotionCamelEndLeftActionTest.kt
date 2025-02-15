/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action.motion.text

import com.maddyhome.idea.vim.command.VimStateMachine
import org.jetbrains.plugins.ideavim.VimTestCase

class MotionCamelEndLeftActionTest : VimTestCase() {
  fun `test go with a single uppercase word`() {
    doTest("]b", "TES${c}T", "TES${c}T", VimStateMachine.Mode.COMMAND, VimStateMachine.SubMode.NONE)
  }
}
