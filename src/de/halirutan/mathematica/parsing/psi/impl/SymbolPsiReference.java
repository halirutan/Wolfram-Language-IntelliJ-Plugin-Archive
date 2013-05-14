package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.PsiImplUtil;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (5/8/13)
 */
public class SymbolPsiReference extends PsiReferenceBase<Symbol> {

    public SymbolPsiReference(Symbol element, TextRange range) {
        super(element, range);
    }

    @Nullable
    @Override
    public PsiElement resolve() {

        return myElement;
    }
    @NotNull
    @Override
    public Object[] getVariants() {
        String result[] = {"var1","var2"};
        return result;
    }


    @Override
    public void setRangeInElement(TextRange range) {
        super.setRangeInElement(range);
    }

    @NotNull
    @Override
    public String getValue() {
        return super.getValue();
    }

    @Override
    public Symbol getElement() {
        return super.getElement();
    }

    @Override
    public TextRange getRangeInElement() {
        return super.getRangeInElement();
    }

    @Override
    protected TextRange calculateDefaultRangeInElement() {
        return super.calculateDefaultRangeInElement();
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return super.getCanonicalText();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return super.handleElementRename(newElementName);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return super.bindToElement(element);
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return super.isReferenceTo(element);
    }

    @Override
    public boolean isSoft() {
        return super.isSoft();
    }
}
