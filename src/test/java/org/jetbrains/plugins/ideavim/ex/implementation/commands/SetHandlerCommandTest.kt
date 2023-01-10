/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.commands

import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.key.ShortcutOwner
import com.maddyhome.idea.vim.key.ShortcutOwnerInfo
import com.maddyhome.idea.vim.vimscript.model.commands.SetHandlerCommand
import junit.framework.TestCase
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

class SetHandlerCommandTest : VimTestCase() {
  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test parse a mappings`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "a:ide")!!
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test i mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "i:ide")!!
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test n mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "n:ide")!!
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test n-v mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "n-v:ide")!!
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test v mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "v:ide")!!
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test x mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "x:ide")!!
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test s mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "s:ide")!!
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.VIM, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test i-n-v mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "i-n-v:ide")!!
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.insert)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.normal)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.select)
    TestCase.assertEquals(ShortcutOwner.IDE, newOwner.visual)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test broken mapping`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "l:ide")
    TestCase.assertNull(newOwner)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test broken mapping 2`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    val newOwner = SetHandlerCommand.updateOwner(owner, "hau2h2:ide")
    TestCase.assertNull(newOwner)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test to notation`() {
    var owner = ShortcutOwnerInfo.allPerModeVim
    owner = owner.copy(normal = ShortcutOwner.IDE)
    TestCase.assertEquals("n:ide i-v:vim", owner.toNotation())
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test to notation 2`() {
    var owner = ShortcutOwnerInfo.allPerModeVim
    owner = owner.copy(normal = ShortcutOwner.IDE, insert = ShortcutOwner.IDE)
    TestCase.assertEquals("n-i:ide v:vim", owner.toNotation())
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test to notation 3`() {
    val owner = ShortcutOwnerInfo.allPerModeVim
    TestCase.assertEquals("a:vim", owner.toNotation())
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test to notation 4`() {
    var owner = ShortcutOwnerInfo.allPerModeVim
    owner = owner.copy(
      normal = ShortcutOwner.IDE,
      insert = ShortcutOwner.IDE,
      visual = ShortcutOwner.IDE,
      select = ShortcutOwner.IDE
    )
    TestCase.assertEquals("a:ide", owner.toNotation())
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test executing command`() {
    configureByText("")
    TestCase.assertTrue(VimPlugin.getKey().savedShortcutConflicts.isEmpty())
    typeText(commandToKeys("sethandler <C-C> a:ide"))
    TestCase.assertTrue(VimPlugin.getKey().savedShortcutConflicts.isNotEmpty())
    val key = VimPlugin.getKey().savedShortcutConflicts.entries.single().key
    val owner = VimPlugin.getKey().savedShortcutConflicts.entries.single().value
    TestCase.assertEquals("<C-C>", injector.parser.toKeyNotation(key))
    assertEquals(ShortcutOwnerInfo.allPerModeIde, owner)
  }

  @TestWithoutNeovim(SkipNeovimReason.NOT_VIM_TESTING)
  fun `test executing command 1`() {
    configureByText("")
    TestCase.assertTrue(VimPlugin.getKey().savedShortcutConflicts.isEmpty())
    typeText(commandToKeys("sethandler <C-C> a:ide "))
    TestCase.assertTrue(VimPlugin.getKey().savedShortcutConflicts.isNotEmpty())
    val key = VimPlugin.getKey().savedShortcutConflicts.entries.single().key
    val owner = VimPlugin.getKey().savedShortcutConflicts.entries.single().value
    TestCase.assertEquals("<C-C>", injector.parser.toKeyNotation(key))
    assertEquals(ShortcutOwnerInfo.allPerModeIde, owner)
  }
}
