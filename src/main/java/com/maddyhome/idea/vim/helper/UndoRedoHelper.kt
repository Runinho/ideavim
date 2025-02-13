/*
 * Copyright 2003-2022 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.helper

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.impl.UndoManagerImpl
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.common.ChangesListener
import com.maddyhome.idea.vim.listener.SelectionVimListenerSuppressor
import com.maddyhome.idea.vim.newapi.IjVimEditor
import com.maddyhome.idea.vim.newapi.ij
import com.maddyhome.idea.vim.newapi.vim
import com.maddyhome.idea.vim.options.OptionScope
import com.maddyhome.idea.vim.undo.UndoRedoBase
import com.maddyhome.idea.vim.vimscript.services.IjVimOptionService

/**
 * @author oleg
 */
@Service
class UndoRedoHelper : UndoRedoBase() {
  init {
    injector.optionService.addListener(IjVimOptionService.oldUndo, { UndoManagerImpl.ourNeverAskUser = !injector.optionService.isSet(OptionScope.GLOBAL, IjVimOptionService.oldUndo) }, true)
  }

  override fun undo(context: ExecutionContext): Boolean {
    val ijContext = context.context as DataContext
    val project = PlatformDataKeys.PROJECT.getData(ijContext) ?: return false
    val editor = CommonDataKeys.EDITOR.getData(context.ij) ?: return false
    val vimEditor = editor.vim
    val fileEditor = TextEditorProvider.getInstance().getTextEditor(editor)
    val undoManager = UndoManager.getInstance(project)
    if (undoManager.isUndoAvailable(fileEditor)) {
      if (injector.optionService.isSet(OptionScope.GLOBAL, IjVimOptionService.oldUndo)) {
        SelectionVimListenerSuppressor.lock().use { undoManager.undo(fileEditor) }
      } else {
        performUntilFileChanges(vimEditor, { undoManager.isUndoAvailable(fileEditor) }, { undoManager.undo(fileEditor) })

        vimEditor.carets().forEach {
          val ijCaret = it.ij
          val hasSelection = ijCaret.hasSelection()
          if (hasSelection) {
            val selectionStart = ijCaret.selectionStart
            CommandProcessor.getInstance().runUndoTransparentAction {
              it.ij.removeSelection()
              it.ij.moveToOffset(selectionStart)
            }
          }
        }
      }

      return true
    }
    return false
  }

  override fun redo(context: ExecutionContext): Boolean {
    val ijContext = context.context as DataContext
    val project = PlatformDataKeys.PROJECT.getData(ijContext) ?: return false
    val editor = CommonDataKeys.EDITOR.getData(context.ij) ?: return false
    val vimEditor = editor.vim
    val fileEditor = TextEditorProvider.getInstance().getTextEditor(editor)
    val undoManager = UndoManager.getInstance(project)
    if (undoManager.isRedoAvailable(fileEditor)) {
      if (injector.optionService.isSet(OptionScope.GLOBAL, IjVimOptionService.oldUndo)) {
        SelectionVimListenerSuppressor.lock().use { undoManager.redo(fileEditor) }
      } else {
        performUntilFileChanges(vimEditor, { undoManager.isRedoAvailable(fileEditor) }, { undoManager.redo(fileEditor) })
        CommandProcessor.getInstance().runUndoTransparentAction {
          vimEditor.carets().forEach { it.ij.removeSelection() }
        }
      }
      return true
    }
    return false
  }

  private fun performUntilFileChanges(editor: IjVimEditor?, check: () -> Boolean, action: Runnable) {
    if (editor == null) return
    val vimDocument = editor.document

    val changeListener = object : ChangesListener {
      var hasChanged = false

      override fun documentChanged(change: ChangesListener.Change) {
        hasChanged = true
      }
    }

    val oldPath = editor.getPath()
    vimDocument.addChangeListener(changeListener)
    while (check() && !changeListener.hasChanged && !ifFilePathChanged(editor, oldPath)) {
      action.run()
    }
    vimDocument.removeChangeListener(changeListener)
  }

  private fun ifFilePathChanged(editor: IjVimEditor, oldPath: String?): Boolean {
    return editor.getPath() != oldPath
  }
}
