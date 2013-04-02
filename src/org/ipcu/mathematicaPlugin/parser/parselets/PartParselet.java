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
import com.intellij.psi.tree.IElementType;
import org.ipcu.mathematicaPlugin.parser.MathematicaParser;

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.*;

/**
 * @author patrick (3/27/13)
 */
public class PartParselet implements InfixParselet {
    private final int precedence;

    public PartParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) {
        // should never happen
        if (parser.getTokenType() != PART_BEGIN && !left.valid()) {
            return parser.notParsed();
        }
        final PsiBuilder.Marker partMarker = left.getMark().precede();
        MathematicaParser.Result result = parser.notParsed();
        boolean hasClosingBracked = false;

        final IElementType rightDel = RIGHT_BRACKET;

        parser.advanceLexer();

        while (true) {
            while (parser.testToken(COMMA)) {
                final PsiBuilder.Marker nullComma = parser.mark();
                parser.advanceLexer();
                nullComma.error("Expression expected");
            }
            if (parser.testToken(rightDel, rightDel)) {
                break;
            }
            result = parser.parseExpression();

            // if we couldn't parseEnclosedExpressionSequence the argument expression and the next token is neither a comma nor
            // a closing bracket, then we are lost at this point and should not try further to parseEnclosedExpressionSequence something.
            if (!result.parsed() && !(parser.testToken(COMMA) || parser.testToken(rightDel, rightDel))) {
                break;
            }

            if (parser.testToken(COMMA)) {
                if (parser.getBuilder().lookAhead(1) == rightDel && parser.getBuilder().lookAhead(2) == rightDel) {
                    final PsiBuilder.Marker wrongComma = parser.mark();
                    parser.advanceLexer();
                    wrongComma.error("unexpected ','");
                    // now advance over the closing bracket
                    parser.advanceLexer();
                    parser.advanceLexer();
                    hasClosingBracked = true;
                    break;
                }
                parser.advanceLexer();
            }

            if (parser.testToken(rightDel, rightDel)) {
                hasClosingBracked = true;
                parser.advanceLexer();
                parser.advanceLexer();
                break;
            }

        }


        if (result.parsed() || hasClosingBracked) {
            partMarker.done(PART_EXPRESSION);
            result = parser.result(partMarker, PART_EXPRESSION, true);
        } else {
            if (parser.getBuilder().eof()) {
                partMarker.done(PART_EXPRESSION);
                parser.getBuilder().error("']]' expected");
            } else {
                // if argument parsing went wrong and we couldn't find a closing bracket even with skipping all commas
                // AND we are not at the file end so that the user probably just writes more input, then I don't know what
                // else to do than just returning a syntax error.
                partMarker.error("Syntax error.");
            }
        }
        return result;


    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
