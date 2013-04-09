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
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * Parsing a string.
 * @author patrick (3/27/13)
 */
public class StringParselet implements PrefixParselet {

    final int precedence;

    public StringParselet(int precedence) {
        this.precedence = precedence;
    }

    /**
     * Tries to parse a string consisting of beginning ", string content and final ".
     * @param parser The main parser object.
     * @return Information about the success of the parsing.
     */
    @Override
    public MathematicaParser.Result parse(MathematicaParser parser) {
        final PsiBuilder.Marker stringMark = parser.mark();
        boolean parsedQ = true;
        parser.advanceLexer();
        while (parser.testToken(MathematicaElementTypes.STRING_LITERAL)) {
            parser.advanceLexer();
        }
        if (!parser.testToken(MathematicaElementTypes.STRING_LITERAL_END)) {
            parser.error("\" expected");
            parsedQ = false;
        } else {
            parser.advanceLexer();
        }
        stringMark.done(MathematicaElementTypes.STRING_EXPRESSION);
        return parser.result(stringMark, MathematicaElementTypes.STRING_EXPRESSION, parsedQ);
    }
}
