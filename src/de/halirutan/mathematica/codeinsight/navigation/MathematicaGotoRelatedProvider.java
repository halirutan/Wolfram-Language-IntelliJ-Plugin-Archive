/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.navigation;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.containers.SortedList;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * Provides functionality to navigate through different usages of the symbol under the caret.
 * It will take the correct scope which means that it won't stupidly highlight symbols with the same name.
 * Rather, it will find out if the symbol is globally defined, in a Module, or in any other scoping construct.
 * Then, the list of suggestions contains only correct places which really refer to usages of the current symbol.
 * @author patrick (28.12.16).
 */
public class MathematicaGotoRelatedProvider extends GotoRelatedProvider {

  @NotNull
  @Override
  public List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
    // I want the entries in the suggestion window to be sorted by line number!
    SortedList<GotoSymbolItem> declarations = new SortedList<>(Comparator.comparingInt(GotoSymbolItem::getLineNumber));
    if (psiElement instanceof LeafPsiElement && ((LeafPsiElement) psiElement).getElementType().equals(MathematicaElementTypes.IDENTIFIER)) {
      psiElement = psiElement.getParent();
    }
    if (psiElement instanceof Symbol) {
      final Project project = psiElement.getProject();
      final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
      final Document document = documentManager.getDocument(psiElement.getContainingFile());
      if (document == null) {
        return declarations;
      }
      PsiReference ref = psiElement.getReference();
      if (ref != null) {
        PsiElement resolve = ref.resolve();
        if (resolve != null) {
          if (resolve instanceof Symbol) {
            final PsiElement[] resolveReferences = ((Symbol) resolve).getElementsReferencingToMe();
            for (PsiElement usageElement : resolveReferences) {
              if (usageElement instanceof Symbol && usageElement.isValid()) {
                // What follows is that want to collect code around the found element.
                // I will collect neighbouring PsiElements but not more than 20 characters to the right and
                // to the left of the current usageElement.
                final int elementTextOffset = usageElement.getTextOffset();
                final int lineNumber = document.getLineNumber(elementTextOffset);
                final int lineStartOffset = document.getLineStartOffset(lineNumber);
                final int lineEndOffset = document.getLineEndOffset(lineNumber);
                assert lineStartOffset <= lineEndOffset;

                String testToShow = document.getText(TextRange.create(lineStartOffset, lineEndOffset)).trim();
                testToShow = testToShow.length()>80 ? testToShow.substring(0,80) :testToShow;

                final GotoSymbolItem item = new GotoSymbolItem(
                    usageElement,
                    testToShow,
                    "(" + (lineNumber+1) + " in " + ((Symbol) resolve).getLocalizationConstruct() +")", lineNumber);
                declarations.add(item);

              }

            }
          }
        }
      }
    }
    return declarations;
  }

}
