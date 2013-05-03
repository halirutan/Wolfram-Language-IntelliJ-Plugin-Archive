/*
 * Copyright (c) 2013 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.halirutan.mathematica.parsing.prattParser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

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
