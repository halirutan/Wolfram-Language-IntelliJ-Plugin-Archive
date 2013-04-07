package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 3/28/13
 * Time: 12:32 AM
 * Purpose:
 */
public class SymbolImpl  extends ExpressionImpl {
    private final String name;

    public SymbolImpl(@NotNull ASTNode node) {
        super(node);
        name = node.getText();

    }

    @Override
    public String getText() {
        return name;
    }
}
