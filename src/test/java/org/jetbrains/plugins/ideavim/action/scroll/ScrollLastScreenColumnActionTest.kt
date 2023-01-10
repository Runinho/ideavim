/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.action.scroll

import com.intellij.openapi.editor.Inlay
import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.helper.EditorHelper
import com.maddyhome.idea.vim.options.OptionConstants
import com.maddyhome.idea.vim.options.OptionScope
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimInt
import org.jetbrains.plugins.ideavim.VimTestCase
import org.junit.Assert
import kotlin.math.roundToInt

/*
                                                       *ze*
ze                      Scroll the text horizontally to position the cursor
                        at the end (right side) of the screen.  This only
                        works when 'wrap' is off.
 */
class ScrollLastScreenColumnActionTest : VimTestCase() {
  fun `test scroll caret column to last screen column`() {
    configureByColumns(200)
    typeText(injector.parser.parseKeys("100|" + "ze"))
    assertVisibleLineBounds(0, 20, 99)
  }

  fun `test scroll caret column to last screen column with sidescrolloff`() {
    VimPlugin.getOptionService().setOptionValue(OptionScope.GLOBAL, OptionConstants.sidescrolloffName, VimInt(10))
    configureByColumns(200)
    typeText(injector.parser.parseKeys("100|" + "ze"))
    assertVisibleLineBounds(0, 30, 109)
  }

  fun `test scroll at or near start of line does nothing`() {
    configureByColumns(200)
    typeText(injector.parser.parseKeys("10|" + "ze"))
    assertVisibleLineBounds(0, 0, 79)
  }

  fun `test scroll end of line to last screen column`() {
    configureByColumns(200)
    typeText(injector.parser.parseKeys("$" + "ze"))
    assertVisibleLineBounds(0, 120, 199)
  }

  fun `test scroll end of line to last screen column with sidescrolloff`() {
    VimPlugin.getOptionService().setOptionValue(OptionScope.GLOBAL, OptionConstants.sidescrolloffName, VimInt(10))
    configureByColumns(200)
    typeText(injector.parser.parseKeys("$" + "ze"))
    // See myFixture.editor.settings.additionalColumnsCount
    assertVisibleLineBounds(0, 120, 199)
  }

  fun `test scroll caret column to last screen column with sidescrolloff containing an inline inlay`() {
    // The offset should include space for the inlay
    VimPlugin.getOptionService().setOptionValue(OptionScope.GLOBAL, OptionConstants.sidescrolloffName, VimInt(10))
    configureByColumns(200)
    val inlay = addInlay(101, true, 5)
    typeText(injector.parser.parseKeys("100|" + "ze"))
    val availableColumns = getAvailableColumns(inlay)
    // Rightmost text column will still be the same, even if it's offset by an inlay
    // TODO: Should the offset include the visual column taken up by the inlay?
    // Note that the values for this test are -1 when compared to other tests. That's because the inlay takes up a
    // visual column, and scrolling doesn't distinguish the type of visual column
    // We need to decide if folds and/or inlays should be included in offsets, and figure out how to reasonably implement it
    assertVisibleLineBounds(0, 108 - availableColumns + 1, 108)
  }

  fun `test last screen column does not include previous inline inlay associated with preceding text`() {
    // The inlay is associated with the column before the caret, appears on the left of the caret, so does not affect
    // the last visible column
    configureByColumns(200)
    val inlay = addInlay(99, true, 5)
    typeText(injector.parser.parseKeys("100|" + "ze"))
    val availableColumns = getAvailableColumns(inlay)
    assertVisibleLineBounds(0, 99 - availableColumns + 1, 99)
  }

  fun `test last screen column does not include previous inline inlay associated with following text`() {
    // The inlay is associated with the caret, but appears on the left, so does not affect the last visible column
    configureByColumns(200)
    val inlay = addInlay(99, false, 5)
    typeText(injector.parser.parseKeys("100|" + "ze"))
    val availableColumns = getAvailableColumns(inlay)
    assertVisibleLineBounds(0, 99 - availableColumns + 1, 99)
  }

  fun `test last screen column includes subsequent inline inlay associated with preceding text`() {
    // The inlay is inserted after the caret and relates to the caret column. It should still be visible
    configureByColumns(200)
    val inlay = addInlay(100, true, 5)
    typeText(injector.parser.parseKeys("100|" + "ze"))
    val visibleArea = myFixture.editor.scrollingModel.visibleArea
    val textWidth = visibleArea.width - inlay.widthInPixels
    val availableColumns = (textWidth / EditorHelper.getPlainSpaceWidthFloat(myFixture.editor)).roundToInt()

    // The last visible text column will be 99, but it will be positioned before the inlay
    assertVisibleLineBounds(0, 99 - availableColumns + 1, 99)

    // We have to assert the location of the inlay
    val inlayX = myFixture.editor.visualPositionToPoint2D(inlay.visualPosition).x.roundToInt()
    Assert.assertEquals(visibleArea.x + textWidth, inlayX)
    Assert.assertEquals(visibleArea.x + visibleArea.width, inlayX + inlay.widthInPixels)
  }

  fun `test last screen column does not include subsequent inline inlay associated with following text`() {
    // The inlay is inserted after the caret, and relates to text after the caret. It should not affect the last visible
    // column
    configureByColumns(200)
    addInlay(100, false, 5)
    typeText(injector.parser.parseKeys("100|" + "ze"))
    assertVisibleLineBounds(0, 20, 99)
  }

  private fun getAvailableColumns(inlay: Inlay<*>): Int {
    val textWidth = myFixture.editor.scrollingModel.visibleArea.width - inlay.widthInPixels
    return (textWidth / EditorHelper.getPlainSpaceWidthFloat(myFixture.editor)).roundToInt()
  }
}
