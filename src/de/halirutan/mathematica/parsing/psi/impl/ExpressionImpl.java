package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13
 * Time: 11:42 AM
 * Purpose:
 */
public class ExpressionImpl extends ASTWrapperPsiElement implements Expression {
    final ASTNode myNode;

    public ExpressionImpl(@NotNull ASTNode node) {
        super(node);
        myNode = node;
    }

    public String toString() {
        String classname = getClass().getSimpleName();
        if (classname.endsWith("Impl")) {
            classname = classname.substring(0, classname.length() - "Impl".length());
        }
        return classname;
    }
}
