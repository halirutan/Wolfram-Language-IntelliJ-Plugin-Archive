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
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * Parselet for derivative expression like f''[x] or g'
 * I expect the left operand to be a symbol. Don't know whether this is a requirement, but I cannot think of another
 * form.
 *
 * @author patrick (3/27/13)
 */
public class DerivativeParselet implements InfixParselet {

    private final int m_precedence;

    public DerivativeParselet(int precedence) {
        m_precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) {
        PsiBuilder.Marker derivativeMark = left.getMark().precede();
        boolean result = true;

        if (left.getToken().equals(SYMBOL_EXPRESSION)) {
            parser.error("Derivative expects symbol");
            result = false;
        }

        while (parser.getTokenType().equals(DERIVATIVE)) {
            parser.advanceLexer();
        }
        derivativeMark.done(DERIVATIVE_EXPRESSION);

        return MathematicaParser.result(derivativeMark, DERIVATIVE_EXPRESSION, result);
    }

    @Override
    public int getPrecedence() {
        return m_precedence;
    }
}
