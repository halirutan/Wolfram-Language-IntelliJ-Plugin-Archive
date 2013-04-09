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
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (3/27/13)
 *
 */
public class FunctionCallParselet implements InfixParselet {

    private final int precedence;

    public FunctionCallParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public de.halirutan.mathematica.parsing.prattParser.MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        // should never happen
        if (parser.getTokenType() != LEFT_BRACKET && !left.valid()) {
            return parser.notParsed();
        }
        final PsiBuilder.Marker mainMark = left.getMark().precede();

        // parse the start. Either a Part expression like list[[ or a function call f[
        boolean isPartExpr = false;
        if (parser.testToken(LEFT_BRACKET, LEFT_BRACKET)) {
            isPartExpr = true;
            parser.advanceLexer();
            parser.advanceLexer();
        } else {
            parser.advanceLexer();
        }

        MathematicaParser.Result exprSeq = parser.notParsed();
        boolean hasArgs = false;
        if (!parser.testToken(RIGHT_BRACKET)) {
             exprSeq = ParserUtil.parseSequence(parser, RIGHT_BRACKET);
            hasArgs = true;
        }

        if (parser.testToken(RIGHT_BRACKET)) {
            if (isPartExpr && parser.testToken(RIGHT_BRACKET, RIGHT_BRACKET)) {
                if( !hasArgs ) {
                    parser.error("Part expression cannot be empty");
                }
                parser.advanceLexer();
                parser.advanceLexer();
                mainMark.done(PART_EXPRESSION);
                return parser.result(mainMark, PART_EXPRESSION, exprSeq.parsed() && hasArgs );
            } else if (isPartExpr) {
                parser.advanceLexer();
                parser.error("Closing ']' expected");
                mainMark.done(PART_EXPRESSION);
                return parser.result(mainMark, PART_EXPRESSION, false);
            } else {
                parser.advanceLexer();
                mainMark.done(FUNCTION_CALL_EXPRESSION);
                return parser.result(mainMark, FUNCTION_CALL_EXPRESSION, true);
            }
        }

        parser.error("Closing ']' expected");
        IElementType expressionType = isPartExpr ? PART_EXPRESSION : FUNCTION_CALL_EXPRESSION;
        mainMark.done(expressionType);
        return parser.result(mainMark, expressionType, false);

    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
