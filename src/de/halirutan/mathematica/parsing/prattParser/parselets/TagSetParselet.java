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
public class TagSetParselet implements InfixParselet {

    private final int precedence;

    public TagSetParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        final PsiBuilder.Marker tagSetMark = left.getMark().precede();
        if (parser.testToken(TAG_SET)) {
            parser.advanceLexer();
        } else {
            tagSetMark.drop();
            throw new CriticalParserError("Expected token TAG_SET");
        }
        // In the next line we parse expr1 of expr0/:expr1 and we reduce the precedence by one because it is
        // right associative. Using SetDelayed (:=) which has the same precedence the following expression:
        // a /: b := c := d is then correctly parsed as a /: b := (c := d)
        final MathematicaParser.Result expr1 = parser.parseExpression(getPrecedence());

        IElementType setOrSetDelayedOrUnset = parser.getTokenType();

        // Form expr0 /: expr1 =. where nothing needs to be parsed right of the =.
        if (setOrSetDelayedOrUnset == UNSET) {
            parser.advanceLexer();
            tagSetMark.done(TAG_SET_EXPRESSION);
            return parser.result(tagSetMark, TAG_SET_EXPRESSION, expr1.parsed());
        }

        // Form expr0 /: expr1 := expr2 or expr0 /: expr1 = expr2 where we need to parse expr2
        if (setOrSetDelayedOrUnset == SET || setOrSetDelayedOrUnset == SET_DELAYED) {
            parser.advanceLexer();
            final MathematicaParser.Result expr2 = parser.parseExpression(getPrecedence());
            tagSetMark.done(TAG_SET_EXPRESSION);
            return parser.result(tagSetMark, TAG_SET_EXPRESSION, expr1.parsed() && expr2.parsed());
        }

        // if we are here, the second operator (:=, = or =.) is missing and we give up
        parser.error("Missing ':=','=' or '=.' to complete TagSet");
        tagSetMark.done(TAG_SET_EXPRESSION);
        return parser.result(tagSetMark, TAG_SET_EXPRESSION, false);
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
