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

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;
import de.halirutan.mathematica.parsing.psi.api.Symbol;

/**
 * Parselet for MessageName's like blub::usage or Sin::argx. There are some specialties about this because the left
 * operand is required to be a symbol. The right operand can be a symbol or a string.
 *
 * @author patrick (7/1/13)
 */
public class PatternParselet implements InfixParselet {
  private static final TokenSet PATTERN_ELEMENTS = TokenSet.create(
      MathematicaElementTypes.BLANK_EXPRESSION,
      MathematicaElementTypes.BLANK_SEQUENCE_EXPRESSION,
      MathematicaElementTypes.BLANK_NULL_SEQUENCE_EXPRESSION,
      MathematicaElementTypes.PATTERN_EXPRESSION
  );
  private final int myPrecedence;

  public PatternParselet(int precedence) {
    this.myPrecedence = precedence;
  }

  @Override
  public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
    if (!left.isValid()) {
      return MathematicaParser.notParsed();
    }

    PsiBuilder.Marker patternOptional = left.getMark().precede();
    parser.advanceLexer();
    MathematicaParser.Result result = parser.parseExpression(myPrecedence);

    IElementType expressionType = left.getToken().equals(MathematicaElementTypes.SYMBOL_EXPRESSION) ?
        MathematicaElementTypes.PATTERN_EXPRESSION : MathematicaElementTypes.OPTIONAL_EXPRESSION;

    if (!result.isParsed()) {
      parser.error("Could not parse Pattern/Optional expression.");
    }

    patternOptional.done(expressionType);
    return MathematicaParser.result(patternOptional, expressionType, result.isParsed());
  }

  @Override
  public int getMyPrecedence() {
    return myPrecedence;
  }
}
