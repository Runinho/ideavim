/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.newapi

import com.intellij.openapi.components.Service
import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.api.VimEnabler

@Service
class IjVimEnabler : VimEnabler {
  override fun isEnabled(): Boolean {
    return VimPlugin.isEnabled()
  }
}
