package org.ipcu.mathematicaPlugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.*;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13
 * Time: 12:17 PM
 * Purpose:
 */
public class MathematicaParser  implements PsiParser{

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {

        final PsiBuilder.Marker rootMarker = builder.mark();

        while (!builder.eof()) {

            builder.advanceLexer();
        }
        rootMarker.done(root);

        return builder.getTreeBuilt();
    }

    private void parse(PsiBuilder builder) {
        if (builder.getTokenType() == IDENTIFIER) {
            if (builder.lookAhead(1) == LEFT_BRACKET && builder.lookAhead(2) == LEFT_BRACKET) parsePart(builder);
            else if (builder.lookAhead(1) == LEFT_BRACKET) parseFunction(builder);
        }
    }

    private void parseFunction(PsiBuilder builder) {

    }

    private void parsePart(PsiBuilder builder) {

    }

    private void parseList(PsiBuilder builder) {

    }

    private void parseSequence(PsiBuilder builder) {

    }




}
