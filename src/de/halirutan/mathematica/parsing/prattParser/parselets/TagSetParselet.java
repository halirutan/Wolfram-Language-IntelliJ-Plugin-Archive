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
 * Parselet for all <em>tag setting operations</em> like a /: b = c or a /: b =.
 *
 * @author patrick (3/27/13)
 */
public class TagSetParselet implements InfixParselet {

    private final int m_precedence;

    public TagSetParselet(int precedence) {
        this.m_precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        PsiBuilder.Marker tagSetMark = left.getMark().precede();
        if (parser.matchesToken(MathematicaElementTypes.TAG_SET)) {
            parser.advanceLexer();
        } else {
            tagSetMark.drop();
            throw new CriticalParserError("Expected token TAG_SET");
        }
        // In the next line we parse expr1 of expr0/:expr1 and we reduce the precedence by one because it is
        // right associative. Using SetDelayed (:=) which has the same precedence the following expression:
        // a /: b := c := d is then correctly parsed as a /: b := (c := d)
        MathematicaParser.Result expr1 = parser.parseExpression(m_precedence);

        IElementType tokenType = parser.getTokenType();

        // Form expr0 /: expr1 =. where nothing needs to be parsed right of the =.
        if (tokenType.equals(MathematicaElementTypes.UNSET)) {
            parser.advanceLexer();
            tagSetMark.done(MathematicaElementTypes.TAG_UNSET_EXPRESSION);
            return MathematicaParser.result(tagSetMark, MathematicaElementTypes.TAG_UNSET_EXPRESSION, expr1.isParsed());
        }

        // Form expr0 /: expr1 := expr2 or expr0 /: expr1 = expr2 where we need to parse expr2
        if ((tokenType.equals(MathematicaElementTypes.SET)) || (tokenType.equals(MathematicaElementTypes.SET_DELAYED))) {

            parser.advanceLexer();
            MathematicaParser.Result expr2 = parser.parseExpression(m_precedence);
            IElementType endType = tokenType.equals(MathematicaElementTypes.SET) ? MathematicaElementTypes.TAG_SET_EXPRESSION : MathematicaElementTypes.TAG_SET_DELAYED_EXPRESSION;
            tagSetMark.done(endType);
            return MathematicaParser.result(tagSetMark, endType, expr1.isParsed() && expr2.isParsed());
        }

        // if we are here, the second operator (:=, = or =.) is missing and we give up
        parser.error("Missing ':=','=' or '=.' to complete TagSet");
        tagSetMark.done(MathematicaElementTypes.TAG_SET_EXPRESSION);
        return MathematicaParser.result(tagSetMark, MathematicaElementTypes.TAG_SET_EXPRESSION, false);
    }

    @Override
    public int getPrecedence() {
        return m_precedence;
    }
}
