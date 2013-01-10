package org.ipcu.mathematicaPlugin.psi.impl;

import com.intellij.lang.ASTNode;
import org.ipcu.mathematicaPlugin.psi.MathematicaExpression;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13
 * Time: 11:42 AM
 * Purpose:
 */
public class MathematicaExpressionImpl extends MathematicaElementImpl implements MathematicaExpression{
    public MathematicaExpressionImpl(@NotNull ASTNode node) {
        super(node);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
