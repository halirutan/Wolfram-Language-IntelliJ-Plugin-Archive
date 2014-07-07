/*
 * Copyright (c) 2014 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.findusage;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author patrick (7/7/14)
 */
public class MathematicaFindUsageHandler extends FindUsagesHandler {

  protected MathematicaFindUsageHandler(@NotNull final PsiElement psiElement) {
    super(psiElement);
  }

  @Override
  public Collection<PsiReference> findReferencesToHighlight(@NotNull final PsiElement target, @NotNull final SearchScope searchScope) {

    final Collection<PsiReference> usages = new HashSet<PsiReference>();
    if (target instanceof Symbol) {
//      usages.add(element);

      final PsiReference ref = target.getReference();
      if (ref != null) {
        usages.add(ref);
        final PsiElement resolve = ref.resolve();
        if (resolve != null && resolve instanceof Symbol) {
          final Collection<Symbol> symbolsInFile = PsiTreeUtil.findChildrenOfType(target.getContainingFile(), Symbol.class);
          for (Symbol symbol : symbolsInFile) {
            final PsiReference reference = symbol.getReference();
            if (reference != null) {
              final PsiElement resolve1 = reference.resolve();
              if (resolve1 != null && !symbol.equals(resolve1) && resolve.equals(resolve1)) {
                usages.add(reference);
              }
            }
          }
        }
      }
    }
    //endregion
    return usages;
  }
}
