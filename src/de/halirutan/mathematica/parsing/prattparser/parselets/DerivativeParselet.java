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

package de.halirutan.mathematica.parsing.prattparser.parselets;

import com.intellij.lang.PsiBuilder.Marker;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser.Result;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.DERIVATIVE;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.DERIVATIVE_EXPRESSION;

/**
 * Parselet for derivative expression like f''[x] or g' I expect the left operand to be a symbol. Don't know whether
 * this is a requirement, but I cannot think of another form.
 *
 * @author patrick (3/27/13)
 */
public class DerivativeParselet implements InfixParselet {

  private final int myPrecedence;

  public DerivativeParselet(int precedence) {
    myPrecedence = precedence;
  }

  @Override
  public Result parse(MathematicaParser parser, Result left) throws CriticalParserError {
    Marker derivativeMark = left.getMark().precede();

    while (!parser.eof() && parser.getTokenType().equals(DERIVATIVE)) {
      parser.advanceLexer();
    }
    derivativeMark.done(DERIVATIVE_EXPRESSION);

    return MathematicaParser.result(derivativeMark, DERIVATIVE_EXPRESSION, true);
  }

  @Override
  public int getMyPrecedence() {
    return myPrecedence;
  }
}
