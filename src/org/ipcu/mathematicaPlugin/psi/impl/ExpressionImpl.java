package org.ipcu.mathematicaPlugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
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
public class ExpressionImpl extends ASTWrapperPsiElement implements Expression {

    @NonNls
    private static final String IMPL = "Impl";

    public ExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public Language getLanguage() {
        return MathematicaLanguage.INSTANCE;
    }

    public String toString() {
        String classname = getClass().getName();
        if (classname.endsWith(IMPL)) {
            classname = classname.substring(0, classname.length() - IMPL.length());
        }

        classname = classname.substring(classname.lastIndexOf(".") + 1);
        return classname;
    }
}
