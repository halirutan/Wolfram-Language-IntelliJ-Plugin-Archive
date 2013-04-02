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

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.BLANK_EXPRESSION;

/**
 * @author patrick (3/27/13)
 *
 */
public class BlankParselet implements InfixParselet {
    final int precedence;
    public BlankParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) {
        if (!left.valid()) return parser.notParsed();
        final PsiBuilder.Marker blankMark = left.getMark().precede();
        final IElementType token = BLANK_EXPRESSION;
        parser.advanceLexer();
        MathematicaParser.Result result = parser.parseExpression(precedence);
        blankMark.done(token);
        return parser.result(blankMark, token, result.valid()? result.parsed(): true);
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
