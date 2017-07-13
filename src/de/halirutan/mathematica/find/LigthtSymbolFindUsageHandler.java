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

package de.halirutan.mathematica.find;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.search.CachesBasedRefSearcher;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchRequestCollector;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.impl.LightBuiltInSymbol;
import de.halirutan.mathematica.lang.psi.impl.LightSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author patrick (12.07.17).
 */
class LigthtSymbolFindUsageHandler extends FindUsagesHandler {
  private boolean myForHighlighting;

  LigthtSymbolFindUsageHandler(@NotNull LightSymbol psiElement, boolean forHighlightUsages) {
    super(psiElement);
    myForHighlighting = forHighlightUsages;
  }

  @Override
  @NotNull
  public Collection<PsiReference> findReferencesToHighlight(@NotNull PsiElement target, @NotNull SearchScope searchScope) {

    if (target instanceof LightBuiltInSymbol) {
      ReferencesSearch.SearchParameters parameters = new ReferencesSearch.SearchParameters(
          target,
          GlobalSearchScope.fileScope(target.getContainingFile()),
          true,
          null);
      CachesBasedRefSearcher searcher = new CachesBasedRefSearcher();
      final CachesBasedRefSearcher searcher1 = searcher;
      searcher1.processQuery(parameters, new Processor<PsiReference>() {
        @Override
        public boolean process(PsiReference psiReference) {
          if (psiReference instanceof Symbol) {
            return true;
          }
          return false;
        }
      });
      final Query<PsiReference> all = ReferencesSearch.search(parameters);
      final Collection<PsiReference> all1 = all.findAll();
      return all1;

    }
//    if (target instanceof LightBuiltInSymbol) {
//      final PsiFile containingFile = target.getContainingFile();
//      PsiElementFilter filter = element -> element instanceof Symbol;
//      final PsiElement[] psiElements = PsiTreeUtil.collectElements(containingFile, filter);
//      Set<PsiReference> result = new HashSet<>();
//      for (PsiElement element : psiElements) {
//        if (element instanceof Symbol && ((Symbol) element).resolve() == target) {
//          result.add(element.getReference());
//        }
//      }
//      return result;
//    }
    return Arrays.asList(PsiReference.EMPTY_ARRAY);
  }

}
