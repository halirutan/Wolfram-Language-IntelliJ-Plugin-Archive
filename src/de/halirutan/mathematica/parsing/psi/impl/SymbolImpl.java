package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.PsiImplUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 3/28/13
 * Time: 12:32 AM
 * Purpose:
 */
public class SymbolImpl  extends ExpressionImpl implements Symbol {
    private String name;

    public SymbolImpl(@NotNull ASTNode node) {
        super(node);
        name = node.getText();

    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) {
        this.name = name;
        return this;
    }


    @Override
    public String getText() {
        return name;
    }

    @Override
    public String getMathematicaContext() {
        String context;
        if (name.contains("`")) {
            context = name.substring(0, name.lastIndexOf('`'));
        } else {
            context = "System`";
        }
        return context;
    }

    @Override
    public String getSymbolName() {
        if (name.lastIndexOf('`') == -1) {
            return name;
        } else {
            return name.substring(name.lastIndexOf('`'), name.length());
        }
    }
}
