/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.lang.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.TextOccurenceProcessor;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.impl.LightSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @author patrick (14.09.17).
 */
public class MathematicaReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

  protected MathematicaReferenceSearch() {
    super(true);
  }

  @Override
  public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
    final PsiElement target = queryParameters.getElementToSearch();

    String name;
    if (target instanceof Symbol) {
      name = ((Symbol) target).getSymbolName();
    } else if (target instanceof LightSymbol) {
      name = ((LightSymbol) target).getName();
    } else {
      return;
    }

    if (StringUtil.isEmpty(name)) {
      return;
    }

    PsiSearchHelper helper = PsiSearchHelper.getInstance(target.getProject());
    if (helper instanceof PsiSearchHelperImpl) {

      TextOccurenceProcessor processor = (symbol, offsetInElement) -> {
        if (symbol instanceof Symbol) {
          if (Objects.equals(((Symbol) symbol).resolve(), target)) {
            consumer.process(symbol.getReference());
          }
        }
        return true;
      };

      EnumSet<PsiSearchHelperImpl.Options> options = EnumSet
          .of(PsiSearchHelperImpl.Options.CASE_SENSITIVE_SEARCH, PsiSearchHelperImpl.Options.PROCESS_INJECTED_PSI);
      ((PsiSearchHelperImpl) helper).processElementsWithWord(processor, queryParameters.getEffectiveSearchScope(), name,
          UsageSearchContext.IN_CODE, options, null);
    }
  }
}



