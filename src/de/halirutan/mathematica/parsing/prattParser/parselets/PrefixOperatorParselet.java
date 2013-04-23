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
import de.halirutan.mathematica.parsing.prattParser.ParseletProvider;

/**
 * Parselet for a typical prefix operator like ++a or !a which does not need special care.
 *
 * @author patrick (3/27/13)
 */
public class PrefixOperatorParselet implements PrefixParselet {
    private final int m_precedence;

    public PrefixOperatorParselet(int precedence) {
        this.m_precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser) throws CriticalParserError {
        PsiBuilder.Marker mark = parser.mark();
        parser.advanceLexer();
        IElementType token = ParseletProvider.getPrefixPsiElement(this);
        MathematicaParser.Result result = parser.parseExpression(m_precedence);
        mark.done(token);
        return MathematicaParser.result(mark, token, result.isParsed());

    }
}
