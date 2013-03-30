package org.ipcu.mathematicaPlugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.ipcu.mathematicaPlugin.parser.parselets.InfixParselet;

/**
 * Provides some functions which are handy when we work with the PsiBuilder.
 * @author patrick
 */
public class MathematicaParserUtil {

    /**
     * Consumes one token if possible no matter what type it is.
     * @param builder Builder from which to consume the token.
     * @return true if token could be consumed
     */
    public static boolean consume(PsiBuilder builder) {
        if( !builder.eof() ) {
            builder.advanceLexer();
            return true;
        }
        return false;
    }

    /**
     * Consumes exactly one token if it matches a specific type.
     * @param builder Provides the tokens
     * @param token Token which must be matched
     * @return true if the specific token could be matched and consumed
     */
    public static boolean consume(PsiBuilder builder, IElementType token) {
        if (!builder.eof() && builder.getTokenType() == token) {
            builder.advanceLexer();
            return true;
        }
        return false;
    }

    /**
     * Like {@link #consume(com.intellij.lang.PsiBuilder, com.intellij.psi.tree.IElementType)} only that you cas specify
     * several tokens to check. Tokens are only consumed if all tokens match.
     * @param builder Provides the tokens
     * @param tokens  Sequence of tokens to check and to consume
     * @return true if all tokens could be consumed
     */
    public static boolean consumeTokens(PsiBuilder builder, IElementType... tokens) {
        boolean result = true;
        for (int i = 0; result && i < tokens.length; ++i) {
            result &= builder.lookAhead(i) == tokens[i];
        }
        if (result) {
            for (int i = 0; i < tokens.length; ++i) {
            } builder.advanceLexer();
        }
        return result;
    }

}
