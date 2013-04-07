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

import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * @author patrick (3/27/13)
 *
 */
public class DerivativeParselet implements InfixParselet {

    private final int precedence;

    public DerivativeParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public de.halirutan.mathematica.parsing.prattParser.MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) {
        return parser.notParsed();
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
