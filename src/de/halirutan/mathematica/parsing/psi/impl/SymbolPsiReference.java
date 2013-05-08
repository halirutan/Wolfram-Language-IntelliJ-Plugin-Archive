package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (5/8/13)
 */
public class SymbolPsiReference implements PsiScopeProcessor {
    @Override
    public boolean execute(@NotNull PsiElement element, ResolveState state) {
        return false;
    }

    @Nullable
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(Event event, @Nullable Object associated) {
    }
}
