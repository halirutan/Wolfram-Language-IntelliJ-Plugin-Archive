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
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.SPAN;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.SPAN_EXPRESSION;
import static de.halirutan.mathematica.parsing.prattParser.ParseletProvider.getPrefixParselet;

/**
 * Parses <code>Span</code> constructs. This parselet handles not situations like <code>list[[;;]]</code> or
 * <code>list[[;;i]]</code> because this is a prefix operator and not infix.
 *
 * @author patrick (3/27/13)
 */
public class SpanParselet implements InfixParselet {
  private final int m_precedence;

  public SpanParselet(int precedence) {
    this.m_precedence = precedence;
  }


  // Parses things like expr1;;expr2, expr0;; ;;expr1 or expr0;;expr1;;expr2.
  @Override
  public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
    PsiBuilder.Marker spanMark = left.getMark().precede();
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

    // since here we met either expr0;; or expr0;;;;. By testing whether the next token is start of a new expression
    // (which requires to start with a prefix operator) we can tell whether we met the form list[[expr0;;]] or
    // which is a syntax error list[expr0;;;;]].
    PrefixParselet nextPrefix = getPrefixParselet(parser.getTokenType());
    if (nextPrefix == null) {
      if (skipped) {
        spanMark.error("Expression expected after  \"expr0;; ;;\"");
      } else {
        spanMark.done(SPAN_EXPRESSION);
      }
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && !skipped);
    }

    MathematicaParser.Result expr1 = parser.parseExpression(m_precedence);

    // if we had expr0;;;;expr1
    if (skipped) {
      spanMark.done(SPAN_EXPRESSION);
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && expr1.isParsed());
    }

    if (parser.matchesToken(SPAN)) {
      parser.advanceLexer();
      MathematicaParser.Result expr2 = parser.parseExpression(m_precedence);
      if (expr2.isParsed()) {
        spanMark.done(SPAN_EXPRESSION);
      } else
        spanMark.error("Expression expected after \"expr0;;expr1;;\"");
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && expr1.isParsed() && expr2.isParsed());
    } else {
      // we have the form expr0;;expr1
      spanMark.done(SPAN_EXPRESSION);
      return MathematicaParser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && expr1.isParsed());
    }
  }

  @Override
  public int getMyPrecedence() {
    return m_precedence;
  }
}
