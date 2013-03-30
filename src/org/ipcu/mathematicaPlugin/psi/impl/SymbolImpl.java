package org.ipcu.mathematicaPlugin.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 3/28/13
 * Time: 12:32 AM
 * Purpose:
 */
public class SymbolImpl extends ExpressionImpl {
    public SymbolImpl(@NotNull ASTNode node) {
        super(node);
    }
}
