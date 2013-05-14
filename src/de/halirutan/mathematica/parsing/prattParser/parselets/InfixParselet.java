/*
 * Copyright (c) 2013 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
