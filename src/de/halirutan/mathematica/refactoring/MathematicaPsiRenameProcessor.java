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

package de.halirutan.mathematica.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import de.halirutan.mathematica.parsing.psi.MathematicaRecursiveVisitor;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.string.MString;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * This class shouldn't be necessary since usually Idea does a great job of renaming if you have
 * your symbol references working correctly. Unfortunately there is a bug that was introduces by some
 * optimisation {@see https://youtrack.jetbrains.com/issue/IDEA-165760}.
 * <p>
 * Therefore, this class just adds some references that were missed by Idea. This will be removed as soon
 * as the bug is fixed.
 *
 * @author patrick (24.12.16).
 */
public class MathematicaPsiRenameProcessor extends RenamePsiElementProcessor {
  @Override
  public boolean canProcessElement(@NotNull PsiElement element) {
    return element instanceof Symbol || element instanceof MString;
  }

  @NotNull
  @Override
  public Collection<PsiReference> findReferences(PsiElement element) {
    final Collection<PsiReference> references = super.findReferences(element);
    PsiReference elementRef = element.getReference();
    PsiElement definitionElement;
    if (elementRef != null) {
      definitionElement = elementRef.resolve();
      if (definitionElement instanceof Symbol) {
        SymbolCollector collector = new SymbolCollector((Symbol) definitionElement);
        element.getContainingFile().accept(collector);
        references.addAll(collector.myReferences);
      }
    }
    return references;
  }

  private class SymbolCollector extends MathematicaRecursiveVisitor {
    Collection<PsiReference> myReferences;
    Symbol myDefinitionElement;

    SymbolCollector(Symbol definitionElement) {
      this.myDefinitionElement = definitionElement;
      myReferences = new HashSet<>();
    }

    @Override
    public void visitSymbol(Symbol symbol) {
      if (symbol.getSymbolName().equals(myDefinitionElement.getSymbolName())) {
        PsiReference reference = symbol.getReference();
        if (reference != null) {
          PsiElement resolve = reference.resolve();
          if (resolve != null && resolve.equals(myDefinitionElement)) {
            myReferences.add(reference);
          }
        }
      }
    }
  }

}
