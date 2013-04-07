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
import de.halirutan.mathematica.parsing.prattParser.ParseletProvider;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

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
    public de.halirutan.mathematica.parsing.prattParser.MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        if (!left.valid()) return parser.notParsed();
        final PsiBuilder.Marker infixOperationMarker = left.getMark().precede();
        final IElementType token = ParseletProvider.getInfixPsiElement(this);

        // if we have an implicit times, we don't advance the lexer since we are already over the second operand
        // token
        if ( !parser.isImplicitTimesPosition() ) {
            parser.advanceLexer();
        }

        MathematicaParser.Result result = parser.parseExpression(precedence - (isRight ? 1 : 0));
        if (!result.parsed()) {
            parser.getBuilder().error("More input expected.");
            infixOperationMarker.done(token);
        } else {
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
