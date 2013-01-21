package org.ipcu.mathematicaPlugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.PsiBuilder.Marker;

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.*;
import static org.ipcu.mathematicaPlugin.parser.MathematicaParserUtil.*;

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
  //          parse(builder);
            builder.advanceLexer();
        }
        rootMarker.done(root);

        return builder.getTreeBuilt();
    }

    private void parse(PsiBuilder builder) {
        while(!builder.eof()) {
            if (parsePart(builder)) continue;
            if (parseFunction(builder)) continue;
            if (parseGeneralExpression(builder)) continue;
        }
    }

    private boolean parseExpression(PsiBuilder builder) {
        boolean result = parseParenthesizedExpr(builder);
        if (!result) result = parsePart(builder);
        if (!result) result = parseFunction(builder);
        if (!result) result = parseGeneralExpression(builder);
        return result;
    }

    private boolean parseParenthesizedExpr(PsiBuilder builder) {
        boolean result = false;
        if (builder.getTokenType() == LEFT_PAR) {
            PsiBuilder.Marker mark = builder.mark();
            builder.advanceLexer();
            result = parseExpression(builder);
            if (result && builder.getTokenType() == RIGHT_PAR) {
                builder.advanceLexer();
                mark.done(PARENTHESIZED_EXPRESSION);
            } else {
                mark.error("Expression or ) expected");
            }
        }
        return result;
    }

    private boolean parsePart(PsiBuilder builder) {
        boolean result;
        PsiBuilder.Marker mark = builder.mark();
        result = parseExpression(builder);
        if (result && builder.getTokenType() == LEFT_BRACKET && builder.lookAhead(1) == LEFT_BRACKET) {
            builder.advanceLexer();
            builder.advanceLexer();
            result = parseSequence(builder);
            if (result && builder.getTokenType() == RIGHT_BRACKET && builder.lookAhead(1) == RIGHT_BRACKET) {
                builder.advanceLexer();
                builder.advanceLexer();
                mark.done(PART_EXPRESSION);
            }
        }
        if (!result) {
            mark.rollbackTo();
        }
        return result;
    }

    private boolean parseFunction(PsiBuilder builder) {
        boolean result = false;
        PsiBuilder.Marker mark = builder.mark();
        result = parseExpression(builder);
        if (builder.getTokenType() == IDENTIFIER && builder.lookAhead(1) == LEFT_BRACKET) {
            PsiBuilder.Marker functionMarker = builder.mark();
            builder.advanceLexer();
            builder.advanceLexer();
            parseSequence(builder);

            // match closing bracket
            if (builder.lookAhead(1) == RIGHT_BRACKET) {
                builder.advanceLexer();
            } else builder.error("] expected.");
            functionMarker.done(FUNCTION_EXPRESSION);
            return true;
        } else return false;
    }


    private boolean parseList(PsiBuilder builder) {
        return false;

    }

    private boolean parseSequence(PsiBuilder builder) {
        boolean correctArguments = true;
        PsiBuilder.Marker sequenceMarker = builder.mark();
        while (correctArguments) {
            correctArguments = parseGeneralExpression(builder);
            if (builder.lookAhead(1) == COMMA) {
                builder.advanceLexer();
            } else {
                sequenceMarker.done(SEQUENCE_EXPRESSION);
                return true;
            }
        }
        return false;

    }

    private boolean parseGeneralExpression(PsiBuilder builder) {
        return false;
    }



}
