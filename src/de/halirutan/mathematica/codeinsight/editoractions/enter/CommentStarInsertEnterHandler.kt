/*
 * Copyright (c) 2017. Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package de.halirutan.mathematica.codeinsight.editoractions.enter

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile

/**
 * @Note Currently unused!
 * Inserts a * in each new line of a multi-line comment
 * @author patrick (04.12.17).
 */
class CommentStarInsertEnterHandler : MathematicaEnterHandler() {
  override fun postProcessEnter(file: PsiFile, editor: Editor, dataContext: DataContext): EnterHandlerDelegate.Result {
    skipWithResultQ(file, editor, dataContext)?.let { return it }
    val caretModel = editor.caretModel
    val offset = caretModel.offset
    val project = dataContext.getData(CommonDataKeys.PROJECT) ?: return EnterHandlerDelegate.Result.Continue
    val psiDocManager = PsiDocumentManager.getInstance(project)

    val comment = file.findElementAt(offset) ?: return EnterHandlerDelegate.Result.Continue
    val commentRange = comment.textRange ?: return EnterHandlerDelegate.Result.Continue

    if (comment is PsiComment && offset >= commentRange.startOffset + 2 && offset <= commentRange.endOffset - 2) {

      val document = editor.document
      val textLength = document.textLength

      val lineNumber = document.getLineNumber(offset)
      val elementStartLine = document.getLineNumber(comment.textOffset)
      val elementEndLine = document.getLineNumber(comment.textOffset + comment.textLength)
      val lineStartOffset = document.getLineStartOffset(lineNumber)

      val insertString: String
      val move: Int
      if (lineNumber == elementStartLine + 1) {
        insertString = " * "
        move = 3
      } else {
        insertString = "* "
        move = 2
      }

      document.insertString(offset, insertString)
      caretModel.moveToOffset(offset + move)

      if (lineNumber == elementEndLine) {
        document.insertString(commentRange.endOffset + move - 2, "\n ")
      }
      psiDocManager.commitDocument(document)
      return EnterHandlerDelegate.Result.DefaultSkipIndent
    }
    return EnterHandlerDelegate.Result.Continue
  }
}