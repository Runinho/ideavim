/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.action.change

import com.maddyhome.idea.vim.extension.ExtensionHandler
import javax.swing.KeyStroke

object Extension {
  var lastExtensionHandler: ExtensionHandler? = null

  private val keyStrokes = mutableListOf<KeyStroke>()
  private val strings = mutableListOf<String>()

  private var keystrokePointer = 0
  private var stringPointer = 0

  fun addKeystroke(key: KeyStroke) = keyStrokes.add(key)
  fun addString(key: String) = strings.add(key)

  fun consumeKeystroke(): KeyStroke? {
    if (keystrokePointer in keyStrokes.indices) {
      keystrokePointer += 1
      return keyStrokes[keystrokePointer - 1]
    }
    return null
  }

  fun consumeString(): String? {
    if (stringPointer in strings.indices) {
      stringPointer += 1
      return strings[stringPointer - 1]
    }
    return null
  }

  fun reset() {
    keystrokePointer = 0
    stringPointer = 0
  }

  fun clean() {
    keyStrokes.clear()
    strings.clear()
    keystrokePointer = 0
    stringPointer = 0
  }
}
