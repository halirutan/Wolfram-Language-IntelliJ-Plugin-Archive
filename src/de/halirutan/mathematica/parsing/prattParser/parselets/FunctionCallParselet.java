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
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * Parses functions calls like f[x] and array element access like l[[i]] since both start with an opening bracket.
 * @author patrick (3/27/13)
 */
public class FunctionCallParselet implements InfixParselet {

    private final int myPrecedence;

    public FunctionCallParselet(int precedence) {
        myPrecedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        // should never happen
        if ((parser.getTokenType() != MathematicaElementTypes.LEFT_BRACKET) && !left.isValid()) {
            return parser.notParsed();
        }

        PsiBuilder.Marker mainMark = left.getMark().precede();

        // parse the start. Either a Part expression like list[[ or a function call f[
        boolean isPartExpr = false;
        if (parser.testToken(MathematicaElementTypes.LEFT_BRACKET, MathematicaElementTypes.LEFT_BRACKET)) {
            isPartExpr = true;
            parser.advanceLexer();
            parser.advanceLexer();
        } else {
            parser.advanceLexer();
        }

        MathematicaParser.Result exprSeq = parser.notParsed();
        boolean hasArgs = false;
        if (!parser.testToken(MathematicaElementTypes.RIGHT_BRACKET)) {
             exprSeq = ParserUtil.parseSequence(parser, MathematicaElementTypes.RIGHT_BRACKET);
            hasArgs = true;
        }

        if (parser.testToken(MathematicaElementTypes.RIGHT_BRACKET)) {
            if (isPartExpr && parser.testToken(MathematicaElementTypes.RIGHT_BRACKET, MathematicaElementTypes.RIGHT_BRACKET)) {
                if( !hasArgs ) {
                    parser.error("Part expression cannot be empty");
                }
                parser.advanceLexer();
                parser.advanceLexer();
                mainMark.done(MathematicaElementTypes.PART_EXPRESSION);
                return parser.result(mainMark, MathematicaElementTypes.PART_EXPRESSION, exprSeq.isParsed() && hasArgs );
            } else if (isPartExpr) {
                parser.advanceLexer();
                parser.error("Closing ']' expected");
                mainMark.done(MathematicaElementTypes.PART_EXPRESSION);
                return parser.result(mainMark, MathematicaElementTypes.PART_EXPRESSION, false);
            } else {
                parser.advanceLexer();
                mainMark.done(MathematicaElementTypes.FUNCTION_CALL_EXPRESSION);
                return parser.result(mainMark, MathematicaElementTypes.FUNCTION_CALL_EXPRESSION, true);
            }
        }

        parser.error("Closing ']' expected");
        IElementType expressionType = isPartExpr ? MathematicaElementTypes.PART_EXPRESSION : MathematicaElementTypes.FUNCTION_CALL_EXPRESSION;
        mainMark.done(expressionType);
        return parser.result(mainMark, expressionType, false);

    }

    @Override
    public int getPrecedence() {
        return myPrecedence;
    }
}
