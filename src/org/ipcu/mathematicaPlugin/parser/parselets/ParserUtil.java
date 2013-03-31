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

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.COMMA;

/**
 * @author patrick (3/30/13)
 */
public class ParserUtil {

    static MathematicaParser.Result parseEnclosedExpressionSequence(MathematicaParser parser, PsiBuilder.Marker markerToClose,
                                                                    IElementType typeToCloseExpr, IElementType leftDel, IElementType rightDel,
                                                                    String expectedCloseMessage) {
        MathematicaParser.Result result = parser.notParsed();
        boolean hasClosingBracked = false;

        if (parser.getTokenType() != leftDel) throw new AssertionError("Delimiter wrong");
        parser.advanceLexer();

        // handle Function[] as well!!
        // The following is not correct regarding the syntax of the Mathematica language because f[a,,b] is equivalent to
        // f[a, Null, b] but most people don't know this and just made an error when they typed
        // a comma with no expression. Therefore we will only regard f[a,b,c,d,..] as correct function calls.
        // Note, that it is always possible to write f[a, Null, b] explicitly, so we don't loose expression power.
        if (!parser.testToken(rightDel)) {
            while(true) {
                while (parser.testToken(COMMA)) {
                    final PsiBuilder.Marker nullComma = parser.mark();
                    parser.advanceLexer();
                    nullComma.error("Expression expected");
                }
                if (parser.testToken(rightDel)) {
                    break;
                }
                result = parser.parseExpression();

                // if we couldn't parseEnclosedExpressionSequence the argument expression and the next token is neither a comma nor
                // a closing bracket, then we are lost at this point and should not try further to parseEnclosedExpressionSequence something.
                if (!result.parsed() && !(parser.testToken(COMMA) || parser.testToken(rightDel))) {
                    break;
                }

                if (parser.testToken(COMMA)) {
                    if (parser.getBuilder().lookAhead(1) == rightDel) {
                        final PsiBuilder.Marker wrongComma = parser.mark();
                        parser.advanceLexer();
                        wrongComma.error("unexpected ','");
                        // now advance over the closing bracket
                        parser.advanceLexer();
                        hasClosingBracked = true;
                        break;
                    }
                    parser.advanceLexer();
                }

                if (parser.testToken(rightDel)) {
                    hasClosingBracked = true;
                    parser.advanceLexer();
                    break;
                }

            }
        }
        // if we had the closing bracket of f[]
        else {
            hasClosingBracked = true;
            parser.advanceLexer();
        }

        if (result.parsed() || hasClosingBracked) {
            markerToClose.done(typeToCloseExpr);
            result = parser.result(markerToClose, typeToCloseExpr, true);
        } else {
            if (parser.getBuilder().eof()) {
                markerToClose.done(typeToCloseExpr);
                parser.getBuilder().error(expectedCloseMessage);
            } else {
                // if argument parsing went wrong and we couldn't find a closing bracket even with skipping all commas
                // AND we are not at the file end so that the user probably just writes more input, then I don't know what
                // else to do than just returning a syntax error.
                markerToClose.error("Syntax error.");
            }
        }
        return result;

    }

}
