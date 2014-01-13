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
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;

/**
 * @author patrick (3/27/13)
 */
public class BlankParselet implements InfixParselet {
  private final int myPrecedence;

  public BlankParselet(int precedence) {
    this.myPrecedence = precedence;
  }

  @Override
  public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
    if (!left.isValid()) return MathematicaParser.notParsed();
    PsiBuilder.Marker blankMark = left.getMark().precede();
    IElementType token = MathematicaElementTypes.BLANK_EXPRESSION;
    parser.advanceLexer();
    if (!parser.isNextWhitespace() && !parser.eof() && parser.getTokenType().equals(MathematicaElementTypes.IDENTIFIER)) {
      parser.advanceLexer();
    }
//    MathematicaParser.Result expr = parser.parseExpression(myPrecedence);
    blankMark.done(token);
//    return MathematicaParser.result(blankMark, token, !expr.isValid() || expr.isMyParsed());
    return MathematicaParser.result(blankMark, token, true);
  }

  @Override
  public int getMyPrecedence() {
    return myPrecedence;
  }
}
