/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package com.maddyhome.idea.vim.common

import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.NonNls
import kotlin.math.max
import kotlin.math.min

/**
 * Please prefer [com.maddyhome.idea.vim.group.visual.VimSelection] for visual selection
 */
class TextRange(val startOffsets: IntArray, val endOffsets: IntArray) {
  constructor(start: Int, end: Int) : this(intArrayOf(start), intArrayOf(end))

  val isMultiple
    get() = startOffsets.size > 1

  val maxLength: Int
    get() {
      var max = 0
      for (i in 0 until size()) {
        max = max(max, endOffsets[i] - startOffsets[i])
      }
      return max
    }

  val selectionCount: Int
    get() {
      var res = 0
      for (i in 0 until size()) {
        res += endOffsets[i] - startOffsets[i]
      }
      return res
    }

  fun size(): Int = startOffsets.size

  val startOffset: Int
    get() = startOffsets.first()

  val endOffset: Int
    get() = endOffsets.last()

  fun normalize(): TextRange {
    normalizeIndex(0)
    return this
  }

  private fun normalizeIndex(index: Int) {
    if (index < size() && endOffsets[index] < startOffsets[index]) {
      val t = startOffsets[index]
      startOffsets[index] = endOffsets[index]
      endOffsets[index] = t
    }
  }

  @Contract(mutates = "this")
  fun normalize(fileSize: Int): Boolean {
    for (i in 0 until size()) {
      normalizeIndex(i)
      startOffsets[i] = max(0, min(startOffsets[i], fileSize))
      if (startOffsets[i] == fileSize && fileSize != 0) {
        return false
      }
      endOffsets[i] = max(0, min(endOffsets[i], fileSize))
    }
    return true
  }

  operator fun contains(offset: Int): Boolean = if (isMultiple) false else offset in startOffset until endOffset

  override fun toString(): String {
    @NonNls val sb = StringBuilder()
    sb.append("TextRange")
    sb.append("{starts=")

    var i = 0
    while (i < startOffsets.size) {
      sb.append(if (i == 0) "" else ", ").append(startOffsets[i])
      ++i
    }

    sb.append(", ends=")
    i = 0
    while (i < endOffsets.size) {
      sb.append(if (i == 0) "" else ", ").append(endOffsets[i])
      ++i
    }
    sb.append('}')
    return sb.toString()
  }
}
