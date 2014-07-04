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

import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;

/**
 * Utility class for parsing.
 *
 * @author patrick (3/30/13)
 */
public final class ParserUtil {

  private ParserUtil() {
  }

  /**
   * Parses an expression sequence. These sequences are pretty common in function calls like f[a,b,c,d] or in lists
   * {1,2,3,4} and are just a comma separated list of expressions which go as long as we don't find the right delimiter.
   * Note that we do not accept {1,2,,,3} which would be valid Mathematica syntax but is not used in real applications.
   * Therefore, we will give an error instead because the user most probably did a mistake.
   *
   * @param parser
   *     Parser which provides the token-stream, the builder, etc
   * @param rightDel
   *     Token where we will stop the sequence parsing
   * @return The parsing result which is true iff all sub-expressions were successfully parsed.
   * @throws CriticalParserError
   */
  static MathematicaParser.Result parseSequence(MathematicaParser parser, IElementType rightDel) throws CriticalParserError {

    MathematicaParser.Result result = MathematicaParser.notParsed();
    boolean sequenceParsed = true;

    // The following is not correct regarding the syntax of the Mathematica language because f[a,,b] is equivalent to
    // f[a, Null, b] but most people don't know this and just made an error when they typed
    // a comma with no expression. Therefore we will only regard f[a,b,c,d,..] as correct function calls.
    // Note, that it is always possible to write f[a, Null, b] explicitly, so we don't loose expression power.
    while (true) {
      while (parser.matchesToken(MathematicaElementTypes.COMMA)) {
        parser.advanceLexer();
        parser.error("Expression expected before ','");
        sequenceParsed = false;
      }
      if (parser.matchesToken(rightDel)) {
        break;
      }
      result = parser.parseExpression();
      sequenceParsed &= result.isMyParsed();

      // if we couldn't parseSequence the argument expression and the next token is neither a comma nor
      // a closing brace/bracket, then we are lost at this point and should not try further to parseSequence something.
      if (!result.isMyParsed() && !(parser.matchesToken(MathematicaElementTypes.COMMA) || parser.matchesToken(rightDel))) {
        sequenceParsed = false;
        break;
      }

      // Jump over a comma after an expression was parsed in e.g. f[expr1, expr2, ...]
      // Note, when the comma is directly followed by the closing ] (or }) then we mark it with an error since
      // something like f[expr1,expr2,] is not considered to be a bug.
      if (parser.matchesToken(MathematicaElementTypes.COMMA)) {
        if (parser.matchesToken(MathematicaElementTypes.COMMA, rightDel)) {
          parser.error("unexpected ','");
          parser.advanceLexer();
          break;
        }
        parser.advanceLexer();
      }

      if (parser.matchesToken(rightDel)) {
        break;
      }
    }
    return MathematicaParser.result(result.getMark(), result.getToken(), sequenceParsed);
  }

}
