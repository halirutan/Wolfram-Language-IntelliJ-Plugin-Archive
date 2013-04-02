package org.ipcu.mathematicaPlugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.ipcu.mathematicaPlugin.parser.parselets.InfixParselet;
import org.ipcu.mathematicaPlugin.parser.parselets.PrefixParselet;
import org.jetbrains.annotations.NotNull;

import static org.ipcu.mathematicaPlugin.parser.ParseletProvider.getInfixParselet;
import static org.ipcu.mathematicaPlugin.parser.ParseletProvider.getPrefixParselet;

/**
 *
 * @author patrick (3/27/13)
 */
public class MathematicaParser  implements PsiParser {

    private PsiBuilder builder = null;

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {

        boolean runthrough = false;
        int iter = 100;

        final PsiBuilder.Marker rootMarker = builder.mark();
        this.builder = builder;
        builder.setDebugMode(true);
        while (!builder.eof()) {


            if (!runthrough) {
                Result expr = parseExpression();
                if (!expr.parsed()) {
                    builder.error("Errors in the preceeding expression.");
                    builder.advanceLexer();
                }
            } else {
                builder.advanceLexer();
            }

        }
        rootMarker.done(root);

        return builder.getTreeBuilt();
    }

    public Result parseExpression() {
        return parseExpression(0);
    }


    public Result parseExpression(int precedence) {
        if (builder.eof()) return notParsed();
        IElementType token = builder.getTokenType();

        PrefixParselet prefix = getPrefixParselet(token);

        if (prefix == null) return notParsed();


        Result left = prefix.parse(this);

        while (left.parsed() && precedence < getPrecedence(builder)) {
            token = builder.getTokenType();

            InfixParselet infix = getInfixParselet(token);
            if (infix == null) return notParsed();
            left = infix.parse(this, left);
        }
        return left;
    }

    private int getPrecedence(PsiBuilder builder) {
        IElementType token = builder.getTokenType();
        InfixParselet parser = getInfixParselet(token);
        if (parser != null) {
            return parser.getPrecedence();
        }
        return 0;

    }

    public PsiBuilder getBuilder() {
        return builder;
    }


    public PsiBuilder.Marker mark() {
        return builder.mark();
    }

    public IElementType getTokenType() {
        return builder.getTokenType();
    }

    public Result result(PsiBuilder.Marker mark, IElementType token, boolean parsedQ) {
        return new Result(mark,token,parsedQ);
    }

    /**
     * This is the return value of a parser when errors happened.
     * @return
     */
    public Result notParsed() {
        return new Result(null,null,false);
    }

    public void advanceLexer() {
        builder.advanceLexer();
    }

    public boolean testToken(IElementType token) {
        return !builder.eof() && builder.getTokenType() == token;
    }

    public boolean testToken(IElementType token1, IElementType token2) {
        return builder.lookAhead(0) == token1 && builder.lookAhead(1) == token2;
    }

    /**
     * For the Pratt parser we need the left side which was already parsed.
     * An instance of this will provide all necessary information required to
     * know what expression was parsed on the left of an infix operator.
     */
    public class Result {

        private PsiBuilder.Marker leftMark = null;
        private IElementType leftToken = null;
        private boolean result = false;

        public Result(PsiBuilder.Marker leftMark, IElementType leftToken, boolean result) {
            this.leftMark = leftMark;
            this.leftToken = leftToken;
            this.result = result;
        }



        public PsiBuilder.Marker getMark() {
            return leftMark;
        }

        public IElementType getToken() {
            return leftToken;
        }

        public boolean parsed() {
            return result;
        }

        public boolean valid() {
            return leftMark!=null && leftToken!=null;
        }
    }
}
