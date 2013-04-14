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
 * @author patrick (3/27/13)
 *
 */
public class MessageNameParselet implements InfixParselet {

    final int precedence;

    public MessageNameParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        if (left.isValid() && (left.getToken() != MathematicaElementTypes.SYMBOL_EXPRESSION)) {
            PsiBuilder.Marker mark = left.getMark();
            PsiBuilder.Marker newmark = mark.precede();
            mark.drop();
            newmark.error("Usage message expects Symbol");
            left = parser.result(newmark, left.getToken(), left.isParsed());
        }

        PsiBuilder.Marker messageNameMarker = left.getMark().precede();
        parser.advanceLexer();
        MathematicaParser.Result result = parser.parseExpression(precedence);

        if(result.isParsed()) {
            // Check whether we have a symbol or a string in usage message
            if ((result.getToken() != MathematicaElementTypes.SYMBOL_EXPRESSION) && (result.getToken() != MathematicaElementTypes.STRING_EXPRESSION)) {
                PsiBuilder.Marker errMark = result.getMark().precede();
                errMark.error("Usage message expects Symbol or String");
            }

            // Check whether we have the form symbol::name::language
            if (parser.testToken(MathematicaElementTypes.DOUBLE_COLON)) {
                parser.advanceLexer();
                result = parser.parseExpression(precedence);
                if (result.isParsed() && ((result.getToken() != MathematicaElementTypes.SYMBOL_EXPRESSION) || (result.getToken() != MathematicaElementTypes.STRING_EXPRESSION))) {
                    PsiBuilder.Marker errMark = result.getMark().precede();
                    errMark.error("Usage message exprects Symbol or String");
                }
            }
        } else {
            parser.error("Symbol or String expected as Name in Symbol::Name");
        }
        messageNameMarker.done(MathematicaElementTypes.MESSAGE_NAME_EXPRESSION);
        return parser.result(messageNameMarker, MathematicaElementTypes.MESSAGE_NAME_EXPRESSION,result.isParsed());

    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
