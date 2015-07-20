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

package de.halirutan.mathematica.parsing.prattparser.parselets;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.ParserBundle;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser.Result;

/**
 * Parsing a string.
 *
 * @author patrick (3/27/13)
 */
public class StringParselet implements PrefixParselet {
  private final int myPrecedence;

  public StringParselet(int precedence) {
    myPrecedence = precedence;
  }

  /**
   * Tries to parse a string consisting of beginning ", string content and final ".
   *
   * @param parser
   *     The main parser object.
   * @return Information about the success of the parsing.
   */
  @Override
  public Result parse(MathematicaParser parser) throws CriticalParserError {
    Marker stringMark = parser.mark();
    boolean parsedQ = true;
    parser.advanceLexer();
    while (parser.matchesToken(MathematicaElementTypes.STRING_LITERAL)) {
      parser.advanceLexer();
    }
    if (parser.matchesToken(MathematicaElementTypes.STRING_LITERAL_END)) {
      parser.advanceLexer();
    } else {
      parser.error(ParserBundle.message("General.expected.character", "\""));
      parsedQ = false;
    }
    stringMark.done(MathematicaElementTypes.STRING_LITERAL_EXPRESSION);
    return MathematicaParser.result(stringMark, MathematicaElementTypes.STRING_LITERAL_EXPRESSION, parsedQ);
  }

  public int getPrecedence() {
    return myPrecedence;
  }
}
