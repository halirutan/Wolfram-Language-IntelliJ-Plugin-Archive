package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.lang.ASTNode;
import de.halirutan.mathematica.parsing.psi.StringExpression;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 3/27/13
 * Time: 11:25 PM
 * Purpose:
 */
public class StringExpressionImpl extends ExpressionImpl implements StringExpression {
    public StringExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
