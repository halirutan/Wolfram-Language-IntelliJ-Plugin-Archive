package de.halirutan.mathematica.parsing.prattParser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.prattParser.parselets.ImplicitMultiplicationParselet;
import de.halirutan.mathematica.parsing.prattParser.parselets.InfixParselet;
import de.halirutan.mathematica.parsing.prattParser.parselets.PrefixParselet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;
import static de.halirutan.mathematica.parsing.prattParser.ParseletProvider.getInfixParselet;
import static de.halirutan.mathematica.parsing.prattParser.ParseletProvider.getPrefixParselet;

/**
 * @author patrick (3/27/13)
 */
public class MathematicaParser implements PsiParser {

    private PsiBuilder builder;
    public ImportantWhitespaceHandler whitespaceHandler;
    private int recursionDepth;
    private static final ImplicitMultiplicationParselet myImplicitMultiplicationParselet = new ImplicitMultiplicationParselet();


    public MathematicaParser() {
        recursionDepth = 0;
        whitespaceHandler = new ImportantWhitespaceHandler();
    }

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        builder.setWhitespaceSkippedCallback(whitespaceHandler);

        PsiBuilder.Marker rootMarker = builder.mark();
        this.builder = builder;
        builder.setDebugMode(true);
        try {
            while (!builder.eof()) {
                Result expr = parseExpression();

                if (!expr.isParsed()) {
                    builder.error("Errors in the preceeding expression.");
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
        if (token.equals(LINE_BREAK)) {
            advanceLexer();
            token = builder.getTokenType();
        }

        PrefixParselet prefix = getPrefixParselet(token);
        if (prefix == null) {
            return notParsed();
        }

        increaseRecursionDepth();
        Result left = prefix.parse(this);

        while (left.isParsed()) {
            token = builder.getTokenType();
            InfixParselet infix = getInfixOrMultiplyParselet(token);
            if (infix == null) {
                break;
            }
            if (precedence >= infix.getPrecedence()){
                break;
            }
            left = infix.parse(this, left);
        }
        decreaseRecursionDepth();
        return left;
    }

    @Nullable
    private InfixParselet getInfixOrMultiplyParselet(IElementType token) {
        InfixParselet infixParselet = getInfixParselet(token);
        PrefixParselet prefixParselet = getPrefixParselet(token);

        if (infixParselet != null) return infixParselet;

        if (prefixParselet == null) {
            return null;
        }

        if (whitespaceHandler.hadWhitespace()) {
            return null;
        }

        return myImplicitMultiplicationParselet;
    }

    public PsiBuilder getBuilder() {
        return builder;
    }

    public int decreaseRecursionDepth() {
        return --recursionDepth;
    }

    public int increaseRecursionDepth() {
        return ++recursionDepth;
    }

    public PsiBuilder.Marker mark() {
        return builder.mark();
    }

    public IElementType getTokenType() {
        return builder.getTokenType();
    }

    /**
     * Function to create a
     * @param mark
     * @param token
     * @param parsedQ
     * @return
     */
    public static Result result(PsiBuilder.Marker mark, IElementType token, boolean parsedQ) {
        return new Result(mark, token, parsedQ);
    }

    /**
     * This is the return value of a parser when errors happened.
     *
     * @return
     */
    public static Result notParsed() {
        return new Result(null, null, false);
    }

    public void advanceLexer() {
        whitespaceHandler.reset();
        builder.advanceLexer();
    }

    public boolean matchesToken(IElementType token) {
        return !builder.eof() && (builder.getTokenType().equals(token));
    }

    public boolean matchesToken(IElementType token1, IElementType token2) {
        return (builder.lookAhead(0).equals(token1)) && (builder.lookAhead(1).equals(token2));
    }

    /**
     * Wrapper for {@link PsiBuilder#error(String)}
     *
     * @param s Error message
     */
    public void error(String s) {
        builder.error(s);
    }

    public boolean eof() {
        return builder.eof();
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    /**
     * Finds out when a whitespace means multiplication or *sequence* of expressions.
     */
    public class ImportantWhitespaceHandler implements WhitespaceSkippedCallback {
        private boolean whitespaceSeen;

        @Override
        public void onSkip(IElementType type, int start, int end) {
            if (type.equals(LINE_BREAK)) whitespaceSeen = true;
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
    public static final class Result {

        private final PsiBuilder.Marker leftMark;
        private final IElementType leftToken;
        private final boolean parsed;

        private Result(PsiBuilder.Marker leftMark, IElementType leftToken, boolean parsed) {
            this.leftMark = leftMark;
            this.leftToken = leftToken;
            this.parsed = parsed;
        }

        public PsiBuilder.Marker getMark() {
            return leftMark;
        }

        public IElementType getToken() {
            return leftToken;
        }

        public boolean isParsed() {
            return parsed;
        }

        public boolean isValid() {
            return (leftMark != null) && (leftToken != null);
        }
    }
}
