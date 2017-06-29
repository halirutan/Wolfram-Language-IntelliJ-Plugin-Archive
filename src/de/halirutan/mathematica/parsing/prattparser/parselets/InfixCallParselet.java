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
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.ParserBundle;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser.Result;

/**
 * This parses infix calls of functions. Usually, functions are called like this f[a,b]. In Mathematica you can write
 * this as a~f~b. The difference to other infix operators is that it always has to be a pair of ~. Therefore, the first
 * ~ in a~f~b triggers the call of {@link InfixCallParselet#parse} which needs to ensure that we find a second ~ and the
 * expression b.
 *
 * @author patrick (3/27/13)
 */
public class InfixCallParselet implements InfixParselet {

  private final int myPrecedence;

  public InfixCallParselet(int precedence) {
    myPrecedence = precedence;
  }

  @Override
  public Result parse(MathematicaParser parser, Result left) throws CriticalParserError {
    Marker infixCall = left.getMark().precede();
    parser.advanceLexer();
    Result operator = parser.parseExpression(myPrecedence);

    if (parser.matchesToken(MathematicaElementTypes.INFIX_CALL)) {
      parser.advanceLexer();
      Result operand2 = parser.parseExpression(myPrecedence);
      if (!operand2.isParsed()) {
        parser.error(ParserBundle.message("Infix.missing.arg2"));
      }
      infixCall.done(MathematicaElementTypes.INFIX_CALL_EXPRESSION);
      return MathematicaParser.result(infixCall, MathematicaElementTypes.INFIX_CALL_EXPRESSION, operator.isParsed() && operand2.isParsed());
    } else {
      // if the operator was not parsed successfully we will not display a parsing error
      if (operator.isParsed()) {
        parser.error(ParserBundle.message("Infix.missing.tilde"));
      } else {
        parser.error(ParserBundle.message("Infix.operator.missing"));
      }
      infixCall.done(MathematicaElementTypes.INFIX_CALL_EXPRESSION);
      return MathematicaParser.result(infixCall, MathematicaElementTypes.INFIX_CALL_EXPRESSION, false);
    }
  }

  @Override
  public int getMyPrecedence() {
    return myPrecedence;
  }
}
