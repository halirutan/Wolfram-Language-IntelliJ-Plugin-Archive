package de.halirutan.mathematica.parsing.prattParser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.parselets.PrefixParselet;
import de.halirutan.mathematica.parsing.prattParser.parselets.InfixParselet;
import org.jetbrains.annotations.NotNull;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;
import static de.halirutan.mathematica.parsing.prattParser.ParseletProvider.getInfixParselet;
import static de.halirutan.mathematica.parsing.prattParser.ParseletProvider.getPrefixParselet;

/**
 *
 * @author patrick (3/27/13)
 */
public class MathematicaParser  implements PsiParser {

    private PsiBuilder builder = null;
    private int recursionDepth;
    public ImportantWhitespaceHandler whitespaceHandler;

    public MathematicaParser() {
        this.recursionDepth = 0;
        whitespaceHandler = new ImportantWhitespaceHandler();
    }

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        builder.setWhitespaceSkippedCallback(this.whitespaceHandler);
        boolean runthrough = false;
        int iter = 100;

        final PsiBuilder.Marker rootMarker = builder.mark();
        this.builder = builder;
        builder.setDebugMode(true);
        try {
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
        } catch (CriticalParserError criticalParserError) {
            builder.error(criticalParserError.toString());
            while (builder.eof()) {
                builder.advanceLexer();
            }
        }
        rootMarker.done(root);

        return builder.getTreeBuilt();
    }

    public Result parseExpression() throws CriticalParserError {
        return parseExpression(0);
    }


    public Result parseExpression(int precedence) throws CriticalParserError {
        if (builder.eof()) return notParsed();
        IElementType token = builder.getTokenType();

        PrefixParselet prefix = getPrefixParselet(token);
        if (prefix == null) {
            return notParsed();
        }

        recursionDepth++;
        Result left = prefix.parse(this);

        while (left.parsed() && precedence < getPrecedence(builder)) {
            token = builder.getTokenType();

            InfixParselet infix = getInfixParselet(token);

            if (infix == null) {
                if (whitespaceHandler.hadWhitespace()) {
                    infix = getInfixParselet(TIMES);
                } else {
                    recursionDepth--;
                    return notParsed();
                }
            };

            left = infix.parse(this, left);
        }
        recursionDepth--;
        return left;
    }

    private int getPrecedence(PsiBuilder builder) {
        IElementType token = builder.getTokenType();
        InfixParselet parser = getInfixParselet(token);
        if (parser == null) {
            if(isImplicitTimesPosition()) {
                return getInfixParselet(TIMES).getPrecedence();
            }
            return 0;
        }
        return parser.getPrecedence();
    }

//
//    public PsiBuilder getBuilder() {
//        return builder;
//    }


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
        whitespaceHandler.reset();
        builder.advanceLexer();
    }

    public boolean testToken(IElementType token) {
        return !builder.eof() && builder.getTokenType() == token;
    }

    public boolean testToken(IElementType token1, IElementType token2) {
        return builder.lookAhead(0) == token1 && builder.lookAhead(1) == token2;
    }

    /**
     * Wrapper for {@link PsiBuilder#error(String)}
     * @param s Error message
     */
    public void error(String s) {
        builder.error(s);
    }

    public boolean isImplicitTimesPosition() {
        IElementType token = builder.getTokenType();
        InfixParselet parser = getInfixParselet(token);
        if (parser == null && whitespaceHandler.hadWhitespace() && (token == IDENTIFIER  || token == LEFT_BRACE || token == LEFT_PAR)) {
            return true;
        }
        return false;
    }

    public boolean eof() {
        return builder.eof();
    }


    /**
     * Finds out when a whitespace means multiplication or *sequence* of expressions.
     */
    public class ImportantWhitespaceHandler implements WhitespaceSkippedCallback {
        private boolean whitespaceSeen;
        @Override
        public void onSkip(IElementType type, int start, int end) {
            whitespaceSeen = true;
        }

        public void reset() {
            whitespaceSeen = false;
        }

        public boolean hadWhitespace() {
            return whitespaceSeen;
        }


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
