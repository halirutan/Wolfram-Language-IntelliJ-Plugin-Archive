/*
 * Copyright (c) 2017 Patrick Scheibe
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

package de.halirutan.mathematica.lang.parsing.prattparser.parselets;

import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.lang.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.lang.parsing.prattparser.MathematicaParser;
import de.halirutan.mathematica.lang.parsing.prattparser.MathematicaParser.Result;

import static de.halirutan.mathematica.lang.parsing.MathematicaElementTypes.*;

/**
 * Parselet for symbols (identifier). Here, I currently parse not only simple identifiers like Sqrt, variable or
 * $MaxRecursion. Additionally, I parse slot expressions like #1, ## or #abc into symbols and I parse stringified
 * expressions like the rhs of << into one STRINGIFIED_SYMBOL_EXPRESSION node.
 *
 * @author patrick (3/27/13)
 */
public class SymbolParselet implements PrefixParselet {

  private final int myPrecedence;

  public SymbolParselet(int precedence) {
    this.myPrecedence = precedence;
  }

  @Override
  public Result parse(MathematicaParser parser) throws CriticalParserError {
    Marker symbolMark = parser.mark();
    IElementType finalExpressionType;
    final IElementType tokenType = parser.getTokenType();
    if (tokenType.equals(IDENTIFIER)) {
      finalExpressionType = SYMBOL_EXPRESSION;
    } else if (tokenType.equals(STRINGIFIED_IDENTIFIER)) {
      finalExpressionType = STRINGIFIED_SYMBOL_EXPRESSION;
    } else {
      return MathematicaParser.notParsed();
    }
    parser.advanceLexer();
    symbolMark.done(finalExpressionType);
    return MathematicaParser.result(symbolMark, finalExpressionType, true);
  }

  public int getPrecedence() {
    return myPrecedence;
  }
}
