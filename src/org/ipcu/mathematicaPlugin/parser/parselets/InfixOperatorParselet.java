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
import org.ipcu.mathematicaPlugin.parser.ParseletProvider;

/**
 * @author patrick (3/27/13)
 *
 */
public class InfixOperatorParselet implements InfixParselet {
    private final int precedence;
    private final boolean isRight;

    public InfixOperatorParselet(int precedence, boolean right) {
        this.precedence = precedence;
        isRight = right;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) {
        if (!left.valid()) return parser.notParsed();
        final PsiBuilder.Marker infixOperationMarker = left.getMark().precede();
        parser.advanceLexer();
        MathematicaParser.Result result = parser.parseExpression(precedence - (isRight ? 1 : 0));
        if (!result.parsed()) {
            infixOperationMarker.error("More input expected.");
        } else {
            final IElementType token = ParseletProvider.getInfixPsiElement(this);
            infixOperationMarker.done(token);
            result = parser.result(infixOperationMarker, token, true);
        }
        return result;
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }

}
