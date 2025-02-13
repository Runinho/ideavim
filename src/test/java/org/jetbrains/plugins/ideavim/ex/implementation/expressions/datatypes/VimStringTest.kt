/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package org.jetbrains.plugins.ideavim.ex.implementation.expressions.datatypes

import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import org.junit.Test
import kotlin.test.assertEquals

class VimStringTest {

  @Test
  fun `string as number`() {
    assertEquals(0.0, VimString("oh, hi Mark").asDouble())
  }

  @Test
  fun `string with zero as number`() {
    assertEquals(0.0, VimString("0oh, hi Mark").asDouble())
  }

  @Test
  fun `string with minus`() {
    assertEquals(0.0, VimString("-oh, hi Mark").asDouble())
  }

  @Test
  fun `string with minus zero`() {
    assertEquals(0.0, VimString("-0oh, hi Mark").asDouble())
  }

  @Test
  fun `string with float`() {
    assertEquals(4.0, VimString("4.67oh, hi Mark").asDouble())
  }

  @Test
  fun `string with digit`() {
    assertEquals(5.0, VimString("5oh, hi Mark").asDouble())
  }

  @Test
  fun `string with integer`() {
    assertEquals(53.0, VimString("53oh, hi Mark").asDouble())
  }

  @Test
  fun `string with negative integer`() {
    assertEquals(-1464.0, VimString("-1464 oh, hi Mark").asDouble())
  }

  @Test
  fun `string with octal number`() {
    assertEquals(83.0, VimString("0123 oh, hi Mark").asDouble())
  }

  @Test
  fun `string with negative octal number`() {
    assertEquals(-83.0, VimString("-0123 oh, hi Mark").asDouble())
  }

  @Test
  fun `string with octal number with multiple leading zeros`() {
    assertEquals(83.0, VimString("000123 oh, hi Mark").asDouble())
  }

  @Test
  fun `string with hex number`() {
    assertEquals(17.0, VimString("0x11 oh, hi Mark").asDouble())
  }

  @Test
  fun `string with negative hex number`() {
    assertEquals(-17.0, VimString("-0x11 oh, hi Mark").asDouble())
  }

  @Test
  fun `string with hex number with multiple leading zeroz`() {
    assertEquals(0.0, VimString("000x11 oh, hi Mark").asDouble())
  }

  @Test
  fun `string as boolean`() {
    assertEquals(false, VimString("oh, hi Mark").asBoolean())
  }

  @Test
  fun `string as boolean2`() {
    assertEquals(true, VimString("3oh, hi Mark").asBoolean())
  }
}
