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

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import de.halirutan.mathematica.lang.MathematicaLanguage

/**
 *
 * @author patrick (04.12.17).
 */
open class MathematicaEnterHandler : EnterHandlerDelegateAdapter() {

  fun skipWithResultQ(file: PsiFile, editor: Editor, dataContext: DataContext): EnterHandlerDelegate.Result? {
    val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return EnterHandlerDelegate.Result.Continue

    if (!file.viewProvider.languages.contains(MathematicaLanguage.INSTANCE)) {
      return EnterHandlerDelegate.Result.Continue
    }

    if (editor.isViewer) {
      return EnterHandlerDelegate.Result.Continue
    }

    val document = editor.document
    if (!document.isWritable) {
      return EnterHandlerDelegate.Result.Continue
    }

    if (!CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
      return EnterHandlerDelegate.Result.Continue
    }

    PsiDocumentManager.getInstance(project).commitDocument(document)

    val caret = editor.caretModel.offset
    if (caret == 0) {
      return EnterHandlerDelegate.Result.DefaultSkipIndent
    }
    return if (caret <= 0) {
      EnterHandlerDelegate.Result.Continue
    } else null
  }

}