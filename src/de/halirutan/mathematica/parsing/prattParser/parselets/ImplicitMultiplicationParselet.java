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
 * @author patrick (4/13/13)
 */
public class ImplicitMultiplicationParselet implements InfixParselet {

    private static final int precedence = 42;

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        if (!left.isValid()) return parser.notParsed();

        PsiBuilder.Marker timesMarker = left.getMark().precede();


        MathematicaParser.Result result = parser.parseExpression(precedence);
        if (result.isParsed()) {
            timesMarker.done(MathematicaElementTypes.TIMES_EXPRESSION);
            result = parser.result(timesMarker, MathematicaElementTypes.TIMES_EXPRESSION, true);
        } else {
            parser.error("More input expected.");
            timesMarker.done(MathematicaElementTypes.TIMES_EXPRESSION);
        }
        return result;
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }

}
