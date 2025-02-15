/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action.scroll

import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.options.OptionConstants
import com.maddyhome.idea.vim.options.OptionScope
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimInt
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

/*
                                                       *CTRL-D*
CTRL-D                  Scroll window Downwards in the buffer.  The number of
                        lines comes from the 'scroll' option (default: half a
                        screen).  If [count] given, first set 'scroll' option
                        to [count].  The cursor is moved the same number of
                        lines down in the file (if possible; when lines wrap
                        and when hitting the end of the file there may be a
                        difference).  When the cursor is on the last line of
                        the buffer nothing happens and a beep is produced.
                        See also 'startofline' option.
 */
class ScrollHalfPageDownActionTest : VimTestCase() {
  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll half window downwards keeps cursor on same relative line`() {
    configureByPages(5)
    setPositionAndScroll(20, 25)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(42, 0)
    assertVisibleArea(37, 71)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards on last line causes beep`() {
    configureByPages(5)
    setPositionAndScroll(146, 175)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(175, 0)
    assertVisibleArea(146, 175)
    assertTrue(VimPlugin.isError())
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards in bottom half of last page moves caret to the last line without scrolling`() {
    configureByPages(5)
    setPositionAndScroll(140, 165)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(175, 0)
    assertVisibleArea(141, 175)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards in bottom half of last page moves caret to the last line with scrolloff`() {
    VimPlugin.getOptionService().setOptionValue(OptionScope.GLOBAL, OptionConstants.scrolloffName, VimInt(10))
    configureByPages(5)
    setPositionAndScroll(140, 164)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(175, 0)
    assertVisibleArea(141, 175)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards at end of file with existing virtual space moves caret without scrolling window`() {
    configureByPages(5)
    setPositionAndScroll(146, 165) // 146 at top line means bottom line is 181 (out of 175)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(175, 0)
    assertVisibleArea(146, 175)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards in top half of last page moves cursor down half a page`() {
    configureByPages(5)
    setPositionAndScroll(146, 150)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(167, 0)
    assertVisibleArea(146, 175)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll count lines downwards`() {
    configureByPages(5)
    setPositionAndScroll(100, 130)
    typeText(injector.parser.parseKeys("10<C-D>"))
    assertPosition(140, 0)
    assertVisibleArea(110, 144)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll count downwards modifies scroll option`() {
    configureByPages(5)
    setPositionAndScroll(100, 110)
    typeText(injector.parser.parseKeys("10<C-D>"))
    assertEquals((VimPlugin.getOptionService().getOptionValue(OptionScope.GLOBAL, OptionConstants.scrollName) as VimInt).value, 10)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards uses scroll option`() {
    VimPlugin.getOptionService().setOptionValue(OptionScope.GLOBAL, OptionConstants.scrollName, VimInt(10))
    configureByPages(5)
    setPositionAndScroll(100, 110)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(120, 0)
    assertVisibleArea(110, 144)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test count scroll downwards is limited to single page`() {
    configureByPages(5)
    setPositionAndScroll(100, 110)
    typeText(injector.parser.parseKeys("1000<C-D>"))
    assertPosition(145, 0)
    assertVisibleArea(135, 169)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards puts cursor on first non-blank column`() {
    configureByLines(100, "    I found it in a legendary land")
    setPositionAndScroll(20, 25, 14)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(42, 4)
    assertVisibleArea(37, 71)
  }

  @TestWithoutNeovim(SkipNeovimReason.SCROLL)
  fun`test scroll downwards keeps same column with nostartofline`() {
    VimPlugin.getOptionService().unsetOption(OptionScope.GLOBAL, OptionConstants.startoflineName)
    configureByLines(100, "    I found it in a legendary land")
    setPositionAndScroll(20, 25, 14)
    typeText(injector.parser.parseKeys("<C-D>"))
    assertPosition(42, 14)
    assertVisibleArea(37, 71)
  }
}
