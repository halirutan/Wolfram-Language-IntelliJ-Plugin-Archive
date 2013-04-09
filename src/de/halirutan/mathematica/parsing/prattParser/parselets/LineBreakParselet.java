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
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.TIMES_EXPRESSION;

/**
 * @author patrick (4/9/13)
 */
public class LineBreakParselet implements InfixParselet {
    final private int  precedence;

    public LineBreakParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        if (!left.valid()) return parser.notParsed();
        final PsiBuilder.Marker infixOperationMarker = left.getMark().precede();
        parser.advanceLexer();
        MathematicaParser.Result result = parser.parseExpression(getPrecedence());
        if (!result.parsed()) {
            parser.error("More input expected.");
            infixOperationMarker.done(TIMES_EXPRESSION);
        } else {
            infixOperationMarker.done(TIMES_EXPRESSION);
            result = parser.result(infixOperationMarker, TIMES_EXPRESSION, true);
        }
        return result;
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
