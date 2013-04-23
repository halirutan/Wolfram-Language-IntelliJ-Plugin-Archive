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

import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * Utility class for parsing.
 *
 * @author patrick (3/30/13)
 */
public final class ParserUtil {

    private ParserUtil() {
    }

    /**
     * Parses an expression sequence. These sequences are pretty common in function calls like f[a,b,c,d] or in lists
     * {1,2,3,4} and are just a comma separated list of expressions which go as long as we don't find the right
     * delimiter. Note that we do not accept {1,2,,,3} which would be valid Mathematica syntax but is not used in
     * real applications. Therefore, we will give an error instead because the user most probably did a mistake.
     *
     * @param parser   Parser which provides the token-stream, the builder, etc
     * @param rightDel Token where we will stop the sequence parsing
     * @return The parsing result which is true iff all sub-expressions were successfully parsed.
     * @throws CriticalParserError
     */
    static MathematicaParser.Result parseSequence(MathematicaParser parser, IElementType rightDel) throws CriticalParserError {

        MathematicaParser.Result result = MathematicaParser.notParsed();
        boolean sequenceParsed = true;

        // The following is not correct regarding the syntax of the Mathematica language because f[a,,b] is equivalent to
        // f[a, Null, b] but most people don't know this and just made an error when they typed
        // a comma with no expression. Therefore we will only regard f[a,b,c,d,..] as correct function calls.
        // Note, that it is always possible to write f[a, Null, b] explicitly, so we don't loose expression power.
        while (true) {
            while (parser.matchesToken(MathematicaElementTypes.COMMA)) {
                parser.advanceLexer();
                parser.error("Expression expected before ','");
                sequenceParsed = false;
            }
            if (parser.matchesToken(rightDel)) {
                break;
            }
            result = parser.parseExpression();
            sequenceParsed &= result.isParsed();

            // if we couldn't parseSequence the argument expression and the next token is neither a comma nor
            // a closing bracket, then we are lost at this point and should not try further to parseSequence something.
            if (!result.isParsed() && !(parser.matchesToken(MathematicaElementTypes.COMMA) || parser.matchesToken(rightDel))) {
                sequenceParsed = false;
                break;
            }

            if (parser.matchesToken(MathematicaElementTypes.COMMA)) {
                if (parser.matchesToken(rightDel)) {
                    parser.advanceLexer();
                    parser.error("unexpected ','");
                    // now advance over the closing bracket
                    break;
                }
                parser.advanceLexer();
            }

            if (parser.matchesToken(rightDel)) {
                break;
            }
        }
        return MathematicaParser.result(result.getMark(), result.getToken(), sequenceParsed);
    }

}
