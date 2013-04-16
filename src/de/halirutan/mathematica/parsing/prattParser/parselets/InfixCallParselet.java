/*
 * Mathematica Plugin for Jetbrains IDEA
 * Copyright (C) 2013 Patrick Scheibe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.halirutan.mathematica.parsing.prattParser.parselets;

import com.intellij.lang.PsiBuilder;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * This parses infix calls of functions which are usually in the form f[a,b]. In Mathematica you can write this as
 * a~f~b. The difference to other infix operators is that it always has to be a pair of ~. Therefore, the first
 * ~ in a~f~b triggers the call of {@link InfixCallParselet#parse} which needs to ensure that we find a second
 * ~ and the expression b.
 *
 * @author patrick (3/27/13)
 *
 */
public class InfixCallParselet implements InfixParselet {

    private final int precedence;

    public InfixCallParselet(int prec) {
        precedence = prec;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        PsiBuilder.Marker infixCall = left.getMark().precede();
        parser.advanceLexer();
        MathematicaParser.Result operator = parser.parseExpression(precedence);

        if (parser.testToken(MathematicaElementTypes.INFIX_CALL)) {
            parser.advanceLexer();
            MathematicaParser.Result operand2 = parser.parseExpression(precedence);
            infixCall.done(MathematicaElementTypes.INFIX_CALL_EXPRESSION);
            return parser.result(infixCall, MathematicaElementTypes.INFIX_CALL_EXPRESSION, operator.isParsed() && operand2.isParsed());
        } else {
            // if the operator was not parsed successfully we will not display a parsing error
            if (operator.isParsed()) {
                parser.error("'~' expected in infix notation");
            } else {
                parser.error("Operator expected for infix notation");
            }
            infixCall.done(MathematicaElementTypes.INFIX_CALL_EXPRESSION);
            return parser.result(infixCall, MathematicaElementTypes.INFIX_CALL_EXPRESSION, false);
        }
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
