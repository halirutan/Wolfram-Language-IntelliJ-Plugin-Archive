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
 * @author patrick (3/30/13)
 */
public class ParserUtil {

    static MathematicaParser.Result parseSequence(MathematicaParser parser, IElementType rightDel) throws CriticalParserError {

        MathematicaParser.Result result = parser.notParsed();
        boolean sequenceParsed = true;

        // The following is not correct regarding the syntax of the Mathematica language because f[a,,b] is equivalent to
        // f[a, Null, b] but most people don't know this and just made an error when they typed
        // a comma with no expression. Therefore we will only regard f[a,b,c,d,..] as correct function calls.
        // Note, that it is always possible to write f[a, Null, b] explicitly, so we don't loose expression power.
        while(true) {
            while (parser.testToken(MathematicaElementTypes.COMMA)) {
                parser.advanceLexer();
                parser.error("Expression expected before ','");
                sequenceParsed = false;
            }
            if (parser.testToken(rightDel)) {
                break;
            }
            result = parser.parseExpression();
            sequenceParsed &= result.parsed();

            // if we couldn't parseSequence the argument expression and the next token is neither a comma nor
            // a closing bracket, then we are lost at this point and should not try further to parseSequence something.
            if (!result.parsed() && !(parser.testToken(MathematicaElementTypes.COMMA) || parser.testToken(rightDel))) {
                sequenceParsed = false;
                break;
            }

            if (parser.testToken(MathematicaElementTypes.COMMA)) {
                if (parser.getBuilder().lookAhead(1) == rightDel) {
                    parser.advanceLexer();
                    parser.error("unexpected ','");
                    // now advance over the closing bracket
                    break;
                }
                parser.advanceLexer();
            }

            if (parser.testToken(rightDel)) {
                break;
            }

        }
        return parser.result(result.getMark(), result.getToken(), sequenceParsed);

    }

}
