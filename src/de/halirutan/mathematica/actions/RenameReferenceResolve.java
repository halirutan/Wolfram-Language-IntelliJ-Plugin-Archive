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
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author patrick (21.12.16).
 */
public class RenameReferenceResolve extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Project project = e.getProject();
    final PsiElement element = e.getData(DataKeys.PSI_ELEMENT);

    final Editor editor = e.getData(DataKeys.EDITOR);

    final HighlightManager highlightManager = HighlightManager.getInstance(project);
    final EditorColorsManager editorColorsManager =
        EditorColorsManager.getInstance();
    final EditorColorsScheme globalScheme =
        editorColorsManager.getGlobalScheme();
    final TextAttributes textattributes =
        globalScheme.getAttributes(
            EditorColors.SEARCH_RESULT_ATTRIBUTES);


    if (element != null && editor != null) {
      final List<PsiElement> usages = new ArrayList<>();
      usages.add(element);

      final Collection<PsiReference> refs = ReferencesSearch.search(element).findAll();
      for (PsiReference ref : refs) {
        usages.add(ref.getElement());
      }
      highlightManager.addOccurrenceHighlights(
          editor, usages.toArray(new PsiElement[usages.size()]), textattributes, false, null);
    } else {
      Messages.showErrorDialog("No element under cursor.", "Error");
    }
  }
}
