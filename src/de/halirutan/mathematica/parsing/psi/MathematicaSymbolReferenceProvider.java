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

package de.halirutan.mathematica.parsing.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.impl.SymbolPsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author patrick (21.12.16).
 */
public class MathematicaSymbolReferenceProvider extends PsiReferenceProvider {
  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (!(element instanceof Symbol)) {
      return new PsiReference[0];
    }
    ArrayList<PsiReference> result = new ArrayList<>();

    Symbol symbol = (Symbol) element;
    final SymbolPsiReference reference = (SymbolPsiReference) symbol.getReference();
    final PsiElement resolve;
    if (reference != null) {
      result.add(reference);
      resolve = reference.resolve();
      if (resolve instanceof Symbol) {
        final PsiElement[] elemsReferencingToMe = ((Symbol) resolve).getElementsReferencingToMe();
        if (elemsReferencingToMe != null) {
          for (PsiElement psiElement : elemsReferencingToMe) {
            result.add(psiElement.getReference());
          }
        }
      }
    }
    return result.toArray(new PsiReference[result.size()]);
  }
}
