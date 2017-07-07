/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;
import de.halirutan.mathematica.lang.lexer.MathematicaLexer;

/**
 * Test small, important code snippets for their lexer output.
 * @author patrick (31.10.16)
 */
public class MathematicaLexerTest extends LexerTestCase {
  @Override
  protected Lexer createLexer() {
    return new MathematicaLexer();
  }

  public void testSlots1() throws Exception {
    doTest("#[#Test&, #\"Test\"&]&;",
        "SLOT ('#')\n" +
            "LEFT_BRACKET ('[')\n" +
            "ASSOCIATION_SLOT ('#Test')\n" +
            "FUNCTION ('&')\n" +
            "COMMA (',')\n" +
            "WHITE_SPACE (' ')\n" +
            "ASSOCIATION_SLOT ('#\"Test\"')\n" +
            "FUNCTION ('&')\n" +
            "RIGHT_BRACKET (']')\n" +
            "FUNCTION ('&')\n" +
            "SEMICOLON (';')");
  }

  public void testNumbers() throws Exception {
    doTest("1", "NUMBER ('1')");
    doTest("1.0", "NUMBER ('1.0')");
    doTest("16^^abc", "NUMBER ('16^^abc')");
    doTest("16^^abc.1abc", "NUMBER ('16^^abc.1abc')");
    doTest("2*^3", "NUMBER ('2*^3')");
    doTest("2*^-3", "NUMBER ('2*^-3')");
    doTest("12`", "NUMBER ('12`')");
    doTest("12`12", "NUMBER ('12`12')");
    doTest("12``12", "NUMBER ('12``12')");
  }

  public void testRepeatedAmbiguity() throws Exception {
    // Actually, this is not what Mathematica parses which is Repeated[1]
    // but the documentation states that the point for the number 1. should bind stronger
    doTest("1..", "NUMBER ('1.')\n" + "POINT ('.')");

    // These are the workarounds to get Repeated
    doTest("1 ..", "NUMBER ('1')\n" + "WHITE_SPACE (' ')\n" + "REPEATED ('..')");
    doTest("(1)..", "LEFT_PAR ('(')\n" + "NUMBER ('1')\n" + "RIGHT_PAR (')')\n" + "REPEATED ('..')");
  }

  @Override
  protected String getDirPath() {
    return null;
  }

  /**
   * Helper method to create and check the lexer output of small code snippets.
   * @param inputCode String with the code to scan
   */
  @SuppressWarnings("unused")
  private void printLexerOutput(String inputCode) {
    System.out.println(printTokens(inputCode, 0));
  }
}
