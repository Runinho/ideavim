/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.register

object RegisterConstants {
  const val UNNAMED_REGISTER = '"'
  const val LAST_SEARCH_REGISTER = '/'
  const val LAST_COMMAND_REGISTER = ':'
  const val LAST_INSERTED_TEXT_REGISTER = '.'
  const val SMALL_DELETION_REGISTER = '-'
  const val BLACK_HOLE_REGISTER = '_'
  const val ALTERNATE_BUFFER_REGISTER = '#'
  const val EXPRESSION_BUFFER_REGISTER = '='
  const val CURRENT_FILENAME_REGISTER = '%'
  const val CLIPBOARD_REGISTERS = "*+"
  const val NUMBERED_REGISTERS = "0123456789"
  const val NAMED_REGISTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

  const val WRITABLE_REGISTERS = (
    NUMBERED_REGISTERS + NAMED_REGISTERS + CLIPBOARD_REGISTERS +
      SMALL_DELETION_REGISTER + BLACK_HOLE_REGISTER + UNNAMED_REGISTER + LAST_SEARCH_REGISTER
    )

  const val READONLY_REGISTERS = (
    "" +
      CURRENT_FILENAME_REGISTER + LAST_COMMAND_REGISTER + LAST_INSERTED_TEXT_REGISTER + ALTERNATE_BUFFER_REGISTER +
      EXPRESSION_BUFFER_REGISTER
    ) // Expression buffer is not actually readonly

  const val RECORDABLE_REGISTERS = NUMBERED_REGISTERS + NAMED_REGISTERS + UNNAMED_REGISTER
  const val PLAYBACK_REGISTERS =
    RECORDABLE_REGISTERS + UNNAMED_REGISTER + CLIPBOARD_REGISTERS + LAST_INSERTED_TEXT_REGISTER
  const val VALID_REGISTERS = WRITABLE_REGISTERS + READONLY_REGISTERS
}
