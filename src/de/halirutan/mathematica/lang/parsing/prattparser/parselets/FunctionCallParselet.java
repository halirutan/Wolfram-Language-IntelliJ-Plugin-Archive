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
import de.halirutan.mathematica.lang.parsing.ParserBundle;
import de.halirutan.mathematica.lang.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.lang.parsing.prattparser.MathematicaParser;
import de.halirutan.mathematica.lang.parsing.prattparser.MathematicaParser.Result;

/**
 * Parses functions calls like f[x] or slot expressions like #["name"] or #[name] (used often with Associations)
 * and array element access like l[[i]] since they all start with an opening bracket.
 *
 * @author patrick (3/27/13)
 */
public class FunctionCallParselet implements InfixParselet {

  private final int myPrecedence;

  public FunctionCallParselet(int precedence) {
    myPrecedence = precedence;
  }

  @Override
  public Result parse(MathematicaParser parser, Result left) throws CriticalParserError {
    // should never happen
    if ((!parser.getTokenType().equals(MathematicaElementTypes.LEFT_BRACKET)) && !left.isValid()) {
      return MathematicaParser.notParsed();
    }

    Marker mainMark = left.getMark().precede();

    // parse the start. Could be one of the following:
    //   1. a Part expression like list[[
    //   2. a function call f[
    //   3. a slot expression like #[ which could be a function call or an Association lookup
    boolean isPartExpr = false;
    boolean isAssociationSlot = false;
    if (parser.matchesToken(MathematicaElementTypes.LEFT_BRACKET, MathematicaElementTypes.LEFT_BRACKET)) {
      isPartExpr = true;
      parser.advanceLexer();
      parser.advanceLexer();
    } else if (left.getToken().equals(MathematicaElementTypes.SLOT)) {
      isAssociationSlot = true;
      parser.advanceLexer();
    } else {
      parser.advanceLexer();
    }

    Result exprSeq = MathematicaParser.notParsed();
    boolean hasArgs = false;
    if (!parser.matchesToken(MathematicaElementTypes.RIGHT_BRACKET)) {
      exprSeq = ParserUtil.parseSequence(parser, MathematicaElementTypes.RIGHT_BRACKET);
      hasArgs = true;
    }

    if (parser.matchesToken(MathematicaElementTypes.RIGHT_BRACKET)) {
      if (isPartExpr && parser.matchesToken(MathematicaElementTypes.RIGHT_BRACKET, MathematicaElementTypes.RIGHT_BRACKET)) {
        if (!hasArgs) {
          parser.error(ParserBundle.message("Part.empty"));
        }
        parser.advanceLexer();
        parser.advanceLexer();
        mainMark.done(MathematicaElementTypes.PART_EXPRESSION);
        return MathematicaParser.result(mainMark, MathematicaElementTypes.PART_EXPRESSION, exprSeq.isParsed() && hasArgs);
      } else if (isPartExpr) {
        parser.advanceLexer();
        parser.error(ParserBundle.message("General.closing", "']]'"));
        mainMark.done(MathematicaElementTypes.PART_EXPRESSION);
        return MathematicaParser.result(mainMark, MathematicaElementTypes.PART_EXPRESSION, false);
      } else if (isAssociationSlot) {
        parser.advanceLexer();
        mainMark.done(MathematicaElementTypes.SLOT_EXPRESSION);
        return MathematicaParser.result(mainMark, MathematicaElementTypes.SLOT_EXPRESSION, true);
      } else {
        parser.advanceLexer();
        mainMark.done(MathematicaElementTypes.FUNCTION_CALL_EXPRESSION);
        return MathematicaParser.result(mainMark, MathematicaElementTypes.FUNCTION_CALL_EXPRESSION, true);
      }
    }

    parser.error(ParserBundle.message("General.closing", "']'"));
    IElementType expressionType = isPartExpr ? MathematicaElementTypes.PART_EXPRESSION : MathematicaElementTypes.FUNCTION_CALL_EXPRESSION;
    mainMark.done(expressionType);
    return MathematicaParser.result(mainMark, expressionType, false);

  }

  @Override
  public int getMyPrecedence() {
    return myPrecedence;
  }
}
