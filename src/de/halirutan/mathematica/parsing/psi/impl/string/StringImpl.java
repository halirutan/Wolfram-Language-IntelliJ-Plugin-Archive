package de.halirutan.mathematica.parsing.psi.impl.string;

import com.intellij.lang.ASTNode;
import de.halirutan.mathematica.parsing.psi.api.string.MString;
import de.halirutan.mathematica.parsing.psi.impl.ExpressionImpl;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class StringImpl extends ExpressionImpl implements MString {
    public StringImpl(@NotNull ASTNode node) {
        super(node);
    }
}
