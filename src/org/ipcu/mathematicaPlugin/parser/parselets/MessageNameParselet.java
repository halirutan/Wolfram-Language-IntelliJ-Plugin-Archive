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

package org.ipcu.mathematicaPlugin.parser.parselets;

import com.intellij.lang.PsiBuilder;
import org.ipcu.mathematicaPlugin.parser.MathematicaParser;

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.*;

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
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) {
        if (left.valid() && left.getToken() != SYMBOL_EXPRESSION) {
            final PsiBuilder.Marker mark = left.getMark();
            final PsiBuilder.Marker newmark = mark.precede();
            mark.drop();
            newmark.error("Usage message expects Symbol");
            left = parser.result(newmark, left.getToken(), left.parsed());
        }

        final PsiBuilder.Marker messageNameMarker = left.getMark().precede();
        parser.advanceLexer();
        MathematicaParser.Result result = parser.parseExpression(precedence);

        if(result.parsed()) {
            // Check whether we have a symbol or a string in usage message
            if (result.getToken() != SYMBOL_EXPRESSION && result.getToken() != STRING_EXPRESSION) {
                final PsiBuilder.Marker errMark = result.getMark().precede();
                errMark.error("Usage message expects Symbol or String");
            }

            // Check whether we have the form symbol::name::language
            if (parser.testToken(DOUBLE_COLON)) {
                parser.advanceLexer();
                result = parser.parseExpression(precedence);
                if (result.parsed() && (result.getToken() != SYMBOL_EXPRESSION || result.getToken() != STRING_EXPRESSION)) {
                    final PsiBuilder.Marker errMark = result.getMark().precede();
                    errMark.error("Usage message exprects Symbol or String");
                }
            }
        } else {
            parser.getBuilder().error("Symbol or String expected as Name in Symbol::Name");
        }
        messageNameMarker.done(MESSAGE_NAME_EXPRESSION);
        return parser.result(messageNameMarker,MESSAGE_NAME_EXPRESSION,result.parsed());

    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
