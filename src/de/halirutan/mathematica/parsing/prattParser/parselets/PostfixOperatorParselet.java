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
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;
import de.halirutan.mathematica.parsing.prattParser.ParseletProvider;

/**
 * @author patrick (3/27/13)
 *
 */
public class PostfixOperatorParselet implements InfixParselet {

    private final int precedence;

    public PostfixOperatorParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) {
        IElementType token = parser.getTokenType();
        IElementType psiElement = ParseletProvider.getInfixPsiElement(this);
        PsiBuilder.Marker postfixMarker = left.getMark().precede();
        parser.advanceLexer();
        postfixMarker.done(psiElement);
        return parser.result(postfixMarker, token, true);
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
