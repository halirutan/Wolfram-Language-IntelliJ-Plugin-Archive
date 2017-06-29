/*
 * Copyright (c) 2017 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.halirutan.mathematica.actions;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.parsing.psi.api.Symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author patrick (7/7/14)
 */
@SuppressWarnings("ComponentNotRegistered")
public class HighlightElementAndReferences extends AnAction {
  public void actionPerformed(AnActionEvent e) {

    final Project project = e.getProject();
    final FileEditorManager editorManager =
        FileEditorManager.getInstance(project);
    final HighlightManager highlightManager =
        HighlightManager.getInstance(project);
    final EditorColorsManager editorColorsManager =
        EditorColorsManager.getInstance();
    final Editor editor = editorManager.getSelectedTextEditor();
    final EditorColorsScheme globalScheme =
        editorColorsManager.getGlobalScheme();
    final TextAttributes textattributes =
        globalScheme.getAttributes(
            EditorColors.SEARCH_RESULT_ATTRIBUTES);

    assert editor != null;
    assert project != null;
    final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
    assert psiFile != null;

    //region Interesting part where I track down usages
    PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
    element = (element != null) ? element.getParent() : null;
    final List<PsiElement> usages = new ArrayList<>();
    if (element instanceof Symbol) {
//      usages.add(element);

      final PsiReference ref = element.getReference();
      if (ref != null) {
        final PsiElement resolve = ref.resolve();
        if (resolve != null && resolve instanceof Symbol) {
          if (!resolve.equals(element))
            usages.add(resolve);
          final Collection<Symbol> symbolsInFile = PsiTreeUtil.findChildrenOfType(psiFile, Symbol.class);
          for (Symbol symbol : symbolsInFile) {
            final PsiReference reference = symbol.getReference();
            if (reference != null) {
              final PsiElement resolve1 = reference.resolve();
              if (resolve1 != null && !symbol.equals(resolve1) && resolve.equals(resolve1)) {
                usages.add(symbol);
              }
            }
          }
        }
      }
    }
    //endregion

    highlightManager.addOccurrenceHighlights(
        editor, usages.toArray(new PsiElement[usages.size()]), textattributes, false, null);
    final WindowManager windowManager = WindowManager.getInstance();
    final StatusBar statusBar = windowManager.getStatusBar(project);
    assert statusBar != null;
    statusBar.setInfo("Press Esc to remove highlighting");
  }
}
