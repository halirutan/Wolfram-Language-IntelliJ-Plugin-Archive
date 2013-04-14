package de.halirutan.mathematica.parsing.psi.impl.function;

import com.intellij.lang.ASTNode;
import de.halirutan.mathematica.parsing.psi.impl.ExpressionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 3/27/13
 * Time: 11:25 PM
 * Purpose:
 */
public class FunctionImpl extends ExpressionImpl {
    public FunctionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
