/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.commands

import org.jetbrains.plugins.ideavim.VimTestCase

/**
 * @author Michal Placek
 */
class BufferCloseCommandTest : VimTestCase() {
  fun `test close file by bd command`() {

    val psiFile1 = myFixture.configureByText("A_Discovery1", "I found it in a legendary land")
    val psiFile2 = myFixture.configureByText("A_Discovery2", "all rocks and lavender and tufted grass,")

    fileManager.openFile(psiFile1.virtualFile, false)
    fileManager.openFile(psiFile2.virtualFile, true)
    assertPluginError(false)

    typeText(commandToKeys("bd"))

    assertPluginError(false)
  }
}
