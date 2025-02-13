/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.ui

import com.maddyhome.idea.vim.KeyHandler
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.helper.isCloseKeyStroke
import com.maddyhome.idea.vim.helper.vimStateMachine
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

/**
 * @author dhleong
 */
object ModalEntry {
  inline fun activate(editor: VimEditor, crossinline processor: (KeyStroke) -> Boolean) {

    // Firstly we pull the unfinished keys of the current mapping
    val mappingStack = KeyHandler.getInstance().keyStack
    var stroke = mappingStack.feedSomeStroke()
    while (stroke != null) {
      val result = processor(stroke)
      if (!result) {
        return
      }
      stroke = mappingStack.feedSomeStroke()
    }

    // Then start to accept user input
    val systemQueue = Toolkit.getDefaultToolkit().systemEventQueue
    val loop = systemQueue.createSecondaryLoop()

    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(object : KeyEventDispatcher {
      override fun dispatchKeyEvent(e: KeyEvent): Boolean {
        val stroke: KeyStroke
        if (e.id == KeyEvent.KEY_RELEASED) {
          stroke = KeyStroke.getKeyStrokeForEvent(e)
          if (!stroke.isCloseKeyStroke() && stroke.keyCode != KeyEvent.VK_ENTER) {
            return true
          }
        } else if (e.id == KeyEvent.KEY_TYPED) {
          stroke = KeyStroke.getKeyStrokeForEvent(e)
        } else {
          return true
        }
        if (editor.vimStateMachine.isRecording) {
          KeyHandler.getInstance().modalEntryKeys += stroke
        }
        if (!processor(stroke)) {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this)
          loop.exit()
        }
        return true
      }
    })

    loop.enter()
  }
}
