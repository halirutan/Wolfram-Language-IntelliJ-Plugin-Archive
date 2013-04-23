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

import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

/**
 * Interface for all parselets that implement an infix operation.
 *
 * @author patrick (3/27/13)
 */
public interface InfixParselet {

    /**
     * Parses infix operations. Everything which has a left operand is an infix operation. This includes things like
     * function calls, because the [ in f[x] is an infix operator which has f as left side and which parses up to the
     * matching ]. Note that even postfix operations are implemented through InfixParselets. They just don't have a
     * second (right) operand.
     *
     * @param parser The main parser object which is needed to get tokens from the stream, advance the lexer and create
     *               AST marks and result objects carrying information about the parser results.
     * @param left   The left operand of the infix operation. This needs to be passed because it is parsed earlier in
     *               the process.
     * @return Information about the success of the parsing as well as the element types which was parsed.
     */
    MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError;

    int getPrecedence();
}
