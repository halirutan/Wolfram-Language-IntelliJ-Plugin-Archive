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
import org.ipcu.mathematicaPlugin.MathematicaElementTypes;
import org.ipcu.mathematicaPlugin.parser.MathematicaParser;
import org.ipcu.mathematicaPlugin.parser.ParseletProvider;

/**
 * @author patrick (3/27/13)
 *
 */
public class GroupParselet implements PrefixParselet {
    private final int precendence;
    public GroupParselet(int precedence) {
        this.precendence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser) {
        // should never happen
        if (parser.getTokenType() != MathematicaElementTypes.LEFT_PAR) {
            return parser.notParsed();
        }
        final IElementType token = ParseletProvider.getPrefixPsiElement(this);
        final PsiBuilder.Marker groupMark = parser.mark();
        parser.advanceLexer();

        if (parser.getBuilder().eof()) {
            parser.getBuilder().error("More input expected");
            groupMark.drop();
            return parser.notParsed();
        }

        MathematicaParser.Result result = parser.parseExpression();
        if (parser.testToken(MathematicaElementTypes.RIGHT_PAR)) {
            parser.advanceLexer();
            groupMark.done(token);

            // if we find a closing ) we return the group as parsed successful, no matter whether
            // the containing expression was parsed. Errors in the expression are marked there anyway.
            result = parser.result(groupMark, token, true);
        } else {
            // when the grouped expr was parsed successfully and we just don't find the closing parenthesis we
            // create an error mark there. Otherwise we just return "not parsed" since something seems to be really
            // broken.
            if (result.parsed()) {
                parser.getBuilder().error("')' expected");
                groupMark.done(token);
                result = parser.result(groupMark, token,false);
            } else {
                result = parser.notParsed();
                groupMark.drop();
            }
        }
        return result;
    }

}
