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
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.ParserBundle;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;

/**
 * This parselet is special, because it is <em>not</em> bound to a special operator. Basically, this is called when
 * there is no infix operator between two prefix operators and it would lead to an error otherwise. This seems fragile
 * to me but at the moment it does a reasonable job.
 *
 * @author patrick (4/13/13)
 */
public class ImplicitMultiplicationParselet implements InfixParselet {

  private static final int PRECEDENCE = 42;

  @Override
  public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
    if (!left.isValid()) return MathematicaParser.notParsed();

    PsiBuilder.Marker timesMarker = left.getMark().precede();


    MathematicaParser.Result result = parser.parseExpression(PRECEDENCE);
    if (result.isParsed()) {
      timesMarker.done(MathematicaElementTypes.TIMES_EXPRESSION);
      result = MathematicaParser.result(timesMarker, MathematicaElementTypes.TIMES_EXPRESSION, true);
    } else {
      parser.error(ParserBundle.message("General.eof"));
      timesMarker.done(MathematicaElementTypes.TIMES_EXPRESSION);
    }
    return result;
  }

  @Override
  public int getMyPrecedence() {
    return PRECEDENCE;
  }

}
