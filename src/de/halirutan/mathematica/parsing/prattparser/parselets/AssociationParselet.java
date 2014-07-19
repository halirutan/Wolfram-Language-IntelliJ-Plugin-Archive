/*
 * Copyright (c) 2014 Patrick Scheibe
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
import de.halirutan.mathematica.parsing.ParserBundle;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * Provides functionality to parse Association in Mathematica Created by rsmenon on 3/28/14.
 */
public class AssociationParselet implements PrefixParselet {

  private final int myPrecedence;

  public AssociationParselet(int precedence) {
    myPrecedence = precedence;
  }

  @Override
  public MathematicaParser.Result parse(MathematicaParser parser) throws CriticalParserError {
    PsiBuilder.Marker associationMarker = parser.mark();
    boolean result = true;

    if (parser.matchesToken(LEFT_ASSOCIATION)) {
      parser.advanceLexer();
    } else {
      associationMarker.drop();
      throw new CriticalParserError(ParserBundle.message("Association.critical.error"));
    }

    MathematicaParser.Result seqResult = ParserUtil.parseSequence(parser, RIGHT_ASSOCIATION);

    if (parser.matchesToken(RIGHT_ASSOCIATION)) {
      parser.advanceLexer();
    } else {
      parser.error(ParserBundle.message("General.closing", "'|>'"));
      result = false;
    }
    associationMarker.done(ASSOCIATION_EXPRESSION);
    return MathematicaParser.result(associationMarker, ASSOCIATION_EXPRESSION, result && seqResult.isParsed());
  }

  public int getPrecedence() {
    return myPrecedence;
  }
}
