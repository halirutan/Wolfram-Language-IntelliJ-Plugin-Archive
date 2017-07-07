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
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lang.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.lang.parsing.prattparser.MathematicaParser;
import de.halirutan.mathematica.lang.parsing.prattparser.MathematicaParser.Result;

/**
 * @author patrick (3/27/13)
 */
public class CompoundExpressionParselet implements InfixParselet {
  private final int myPrecedence;

  public CompoundExpressionParselet(int precedence) {
    this.myPrecedence = precedence;
  }

  @Override
  public int getMyPrecedence() {
    return myPrecedence;
  }

  @Override
  public Result parse(MathematicaParser parser, Result left) throws CriticalParserError {
    if (!left.isValid()) return MathematicaParser.notParsed();
    Marker compoundExprMark = left.getMark().precede();
    IElementType token = MathematicaElementTypes.COMPOUND_EXPRESSION_EXPRESSION;
    parser.advanceLexer();

    boolean ok = true;

    while (true) {
      Result result = parser.parseExpression(myPrecedence);
      if (!result.isValid()) break;
      ok &= result.isParsed();
      if (parser.matchesToken(MathematicaElementTypes.SEMICOLON)) parser.advanceLexer();
      else break;
    }

    compoundExprMark.done(token);
    return MathematicaParser.result(compoundExprMark, token, ok);

  }
}
