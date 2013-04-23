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
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * Parselet for symbols (identifier).
 *
 * @author patrick (3/27/13)
 */
public class SymbolParselet implements PrefixParselet {

    private final int m_precedence;

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser) {
        PsiBuilder.Marker symbolMark = parser.mark();
        parser.advanceLexer();
        symbolMark.done(MathematicaElementTypes.SYMBOL_EXPRESSION);
        return parser.result(symbolMark, MathematicaElementTypes.SYMBOL_EXPRESSION, true);
    }

    public SymbolParselet(int precedence) {
        this.m_precedence = precedence;
    }

    public int getPrecedence() {
        return m_precedence;
    }
}
