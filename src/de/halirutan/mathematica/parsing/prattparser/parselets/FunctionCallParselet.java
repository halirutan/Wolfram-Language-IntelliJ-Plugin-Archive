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
 * Parses functions calls like f[x] and array element access like l[[i]] since both start with an opening bracket.
 *
 * @author patrick (3/27/13)
 */
public class FunctionCallParselet implements InfixParselet {

  private final int myPrecedence;

  public FunctionCallParselet(int precedence) {
    myPrecedence = precedence;
  }

  @Override
  public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
    // should never happen
    if ((!parser.getTokenType().equals(MathematicaElementTypes.LEFT_BRACKET)) && !left.isValid()) {
      return MathematicaParser.notParsed();
    }

    PsiBuilder.Marker mainMark = left.getMark().precede();

    // parse the start. Either a Part expression like list[[ or a function call f[
    boolean isPartExpr = false;
    if (parser.matchesToken(MathematicaElementTypes.LEFT_BRACKET, MathematicaElementTypes.LEFT_BRACKET)) {
      isPartExpr = true;
      parser.advanceLexer();
      parser.advanceLexer();
    } else {
      parser.advanceLexer();
    }

    MathematicaParser.Result exprSeq = MathematicaParser.notParsed();
    boolean hasArgs = false;
    if (!parser.matchesToken(MathematicaElementTypes.RIGHT_BRACKET)) {
      exprSeq = ParserUtil.parseSequence(parser, MathematicaElementTypes.RIGHT_BRACKET);
      hasArgs = true;
    }

    if (parser.matchesToken(MathematicaElementTypes.RIGHT_BRACKET)) {
      if (isPartExpr && parser.matchesToken(MathematicaElementTypes.RIGHT_BRACKET, MathematicaElementTypes.RIGHT_BRACKET)) {
        if (!hasArgs) {
          parser.error("Part expression cannot be empty");
        }
        parser.advanceLexer();
        parser.advanceLexer();
        mainMark.done(MathematicaElementTypes.PART_EXPRESSION);
        return MathematicaParser.result(mainMark, MathematicaElementTypes.PART_EXPRESSION, exprSeq.isMyParsed() && hasArgs);
      } else if (isPartExpr) {
        parser.advanceLexer();
        parser.error("Closing ']' expected");
        mainMark.done(MathematicaElementTypes.PART_EXPRESSION);
        return MathematicaParser.result(mainMark, MathematicaElementTypes.PART_EXPRESSION, false);
      } else {
        parser.advanceLexer();
        mainMark.done(MathematicaElementTypes.FUNCTION_CALL_EXPRESSION);
        return MathematicaParser.result(mainMark, MathematicaElementTypes.FUNCTION_CALL_EXPRESSION, true);
      }
    }

    parser.error("Closing ']' expected");
    IElementType expressionType = isPartExpr ? MathematicaElementTypes.PART_EXPRESSION : MathematicaElementTypes.FUNCTION_CALL_EXPRESSION;
    mainMark.done(expressionType);
    return MathematicaParser.result(mainMark, expressionType, false);

  }

  @Override
  public int getMyPrecedence() {
    return myPrecedence;
  }
}
