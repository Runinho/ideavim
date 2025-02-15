/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */
package com.maddyhome.idea.vim.ex

import com.maddyhome.idea.vim.api.injector

class InvalidCommandException(message: String, cmd: String?) : ExException(message + if (cmd != null) " | $cmd" else "")

class InvalidRangeException(s: String) : ExException(s)

class MissingArgumentException : ExException()

class MissingRangeException : ExException()

class NoArgumentAllowedException : ExException()

class NoRangeAllowedException : ExException()

class FinishException : ExException()

fun exExceptionMessage(code: String, vararg params: Any) =
  ExException(injector.messages.message(code, *params)).apply { this.code = code }
