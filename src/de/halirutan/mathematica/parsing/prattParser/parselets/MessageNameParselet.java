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

package de.halirutan.mathematica.parsing.prattParser.parselets;

import com.intellij.lang.PsiBuilder;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * Parselet for MessageName's like blub::usage or Sin::argx. There are some specialties about this because the left
 * operand is required to be a symbol. The right operand can be a symbol or a string.
 *
 * @author patrick (3/27/13)
 */
public class MessageNameParselet implements InfixParselet {

    private final int m_precedence;

    public MessageNameParselet(int precedence) {
        this.m_precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        if (left.isValid() && (!left.getToken().equals(MathematicaElementTypes.SYMBOL_EXPRESSION))) {
            PsiBuilder.Marker mark = left.getMark();
            PsiBuilder.Marker newmark = mark.precede();
            mark.drop();
            newmark.error("Usage message expects Symbol");
            left = MathematicaParser.result(newmark, left.getToken(), left.isParsed());
        }
        PsiBuilder.Marker messageNameMarker = left.getMark().precede();
        parser.advanceLexer();
        MathematicaParser.Result result = parser.parseExpression(m_precedence);

        if (result.isParsed()) {
            // Check whether we have a symbol or a string in usage message
            if ((!result.getToken().equals(MathematicaElementTypes.SYMBOL_EXPRESSION)) &&
                    (!result.getToken().equals(MathematicaElementTypes.STRING_EXPRESSION))) {
                PsiBuilder.Marker errMark = result.getMark().precede();
                errMark.error("Usage message expects Symbol or String");
            }

            // Check whether we have the form symbol::name::language
            if (parser.matchesToken(MathematicaElementTypes.DOUBLE_COLON)) {
                parser.advanceLexer();
                result = parser.parseExpression(m_precedence);
                if (result.isParsed() && ((!result.getToken().equals(MathematicaElementTypes.SYMBOL_EXPRESSION)) ||
                        (!result.getToken().equals(MathematicaElementTypes.STRING_EXPRESSION)))) {
                    PsiBuilder.Marker errMark = result.getMark().precede();
                    errMark.error("Usage message exprects Symbol or String");
                }
            }
        } else {
            parser.error("Symbol or String expected as Name in Symbol::Name");
        }
        messageNameMarker.done(MathematicaElementTypes.MESSAGE_NAME_EXPRESSION);
        return MathematicaParser.result(messageNameMarker, MathematicaElementTypes.MESSAGE_NAME_EXPRESSION, result.isParsed());

    }

    @Override
    public int getPrecedence() {
        return m_precedence;
    }
}
