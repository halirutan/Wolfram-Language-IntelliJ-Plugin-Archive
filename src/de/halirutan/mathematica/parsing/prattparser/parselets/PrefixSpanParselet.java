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
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.SPAN;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.SPAN_EXPRESSION;
import static de.halirutan.mathematica.parsing.prattparser.ParseletProvider.getPrefixParselet;

/**
 * Parses <code>Span</code> constructs like <code>list[[;;]]</code> or <code>list[[;;i]]</code> where <code>;;</code> is
 * a prefix operator.
 *
 * @author patrick (3/27/13)
 */
public class PrefixSpanParselet implements PrefixParselet {
  private final int myPrecedence;

  public PrefixSpanParselet(int precedence) {
    this.myPrecedence = precedence;
  }

  @Override
  public MathematicaParser.Result parse(MathematicaParser parser) throws CriticalParserError {
    final PsiBuilder.Marker spanMark = parser.mark();
    boolean skipped = false;

    if (parser.matchesToken(SPAN)) {
      parser.advanceLexer();
    } else {
      spanMark.drop();
      throw new CriticalParserError("SPAN token ';;' expected");
    }

    // if we meet a second ;; right after the first ;; we just skip it
    if (parser.matchesToken(SPAN)) {
      skipped = true;
      parser.advanceLexer();
    }

    PrefixParselet nextPrefix = getPrefixParselet(parser.getTokenType());
    if (nextPrefix == null) {
      if (skipped) {
        spanMark.error("Expression expected after  \";; ;;\"");
      } else {
        spanMark.done(SPAN_EXPRESSION);
      }
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, !skipped);
    }

    MathematicaParser.Result expr1 = parser.parseExpression(myPrecedence);

    // if we had ;;;;expr1
    if (skipped) {
      spanMark.done(SPAN_EXPRESSION);
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, expr1.isMyParsed());
    }

    if (parser.matchesToken(SPAN)) {
      parser.advanceLexer();
      MathematicaParser.Result expr2 = parser.parseExpression(myPrecedence);
      if (expr2.isMyParsed()) {
        spanMark.done(SPAN_EXPRESSION);
      } else
        spanMark.error("Expression expected after \";;expr1;;\"");
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, expr1.isMyParsed() && expr2.isMyParsed());
    } else {
      // we have the form expr0;;expr1
      spanMark.done(SPAN_EXPRESSION);
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, expr1.isMyParsed());
    }
  }

  public int getPrecedence() {
    return myPrecedence;
  }

}
