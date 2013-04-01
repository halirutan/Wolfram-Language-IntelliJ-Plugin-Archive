package org.ipcu.mathematicaPlugin.psi.impl;

import com.intellij.extapi.psi.ASTDelegatePsiElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.SharedImplUtil;
import org.ipcu.mathematicaPlugin.MathematicaLanguage;
import org.ipcu.mathematicaPlugin.psi.Expression;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13
 * Time: 11:42 AM
 * Purpose:
 */
public class ExpressionImpl extends ASTDelegatePsiElement implements Expression {
    private final ASTNode node;

    @NonNls
    private static final String IMPL = "Impl";

    public ExpressionImpl(@NotNull ASTNode node) {
        super();
        this.node = node;
    }

    @NotNull
    public Language getLanguage() {
        return MathematicaLanguage.INSTANCE;
    }

    public String toString() {
        String classname = getClass().getName();
        if (classname.matches("PsiElement")) {
            classname =  node.getElementType().toString();
        }
        else if (classname.endsWith(IMPL)) {
            classname = classname.substring(0, classname.length() - IMPL.length());
        } else
            classname = classname.substring(classname.lastIndexOf(".") + 1);
        return classname;
    }

    @Override
    public PsiElement getParent() {
        return SharedImplUtil.getParent(getNode());
    }

    @NotNull
    @Override
    public ASTNode getNode() {
        return node;
    }
}
