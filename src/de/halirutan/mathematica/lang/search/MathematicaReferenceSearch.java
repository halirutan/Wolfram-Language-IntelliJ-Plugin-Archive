package de.halirutan.mathematica.lang.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.search.PsiSearchHelperImpl;
import com.intellij.psi.search.*;
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
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<PsiReference> consumer) {
        final PsiElement target = queryParameters.getElementToSearch();

        String name;
        if (target instanceof Symbol) {
            name = ((Symbol) target).getSymbolName();
        } else if (target instanceof LightSymbol) {
            name = ((LightSymbol) target).getName();
        } else {
            return;
        }


       // final PsiElement target = (target instanceof Symbol) ? ((Symbol) target).resolve() : target;
        if (StringUtil.isEmpty(name)) {
            return;
        }

        EnumSet<PsiSearchHelperImpl.Options> options = EnumSet.of(
                PsiSearchHelperImpl.Options.CASE_SENSITIVE_SEARCH,
                PsiSearchHelperImpl.Options.PROCESS_INJECTED_PSI
        );

        PsiSearchHelper helper = PsiSearchHelper.SERVICE.getInstance(target.getProject());
        if (helper instanceof PsiSearchHelperImpl) {

            TextOccurenceProcessor processor = (symbol, offsetInElement) -> {
                if (symbol instanceof Symbol) {
                    if (Objects.equals(((Symbol) symbol).resolve(), target)) {
                        consumer.process(symbol.getReference());
                    }
                }
                return true;
            };

            ((PsiSearchHelperImpl)helper).processElementsWithWord(processor, queryParameters.getEffectiveSearchScope(), name, UsageSearchContext.IN_CODE, options, null);
        }
    }


}
