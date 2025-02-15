/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.common

enum class Direction(private val value: Int) {
  BACKWARDS(-1), FORWARDS(1);

  fun toInt(): Int = value
  fun reverse(): Direction = when (this) {
    BACKWARDS -> FORWARDS
    FORWARDS -> BACKWARDS
  }

  companion object {
    fun fromInt(value: Int) = when (value) {
      BACKWARDS.value -> BACKWARDS
      FORWARDS.value -> FORWARDS
      else -> FORWARDS
    }
  }
}
