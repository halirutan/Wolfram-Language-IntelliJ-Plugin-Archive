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

package de.halirutan.mathematica.refactoring;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import de.halirutan.mathematica.file.MathematicaFileType;

/**
 * @author patrick (24.12.16).
 */
public class RenameTest extends LightCodeInsightFixtureTestCase{

  public void testNormalVariable() throws Exception {
    myFixture.configureByText(MathematicaFileType.INSTANCE,
        "var1;\n" +
            "var1::usage = \"var1 is equal to nothing..\";\n" +
            "var1<caret>=1;");
    myFixture.renameElementAtCaret("var2");
    myFixture.checkResult("var2;\n" +
        "var2::usage = \"var2 is equal to nothing..\";\n" +
        "var2=1;");
  }

  public void testModule() throws Exception {
    myFixture.configureByText(MathematicaFileType.INSTANCE,
        "var;\n" +
            "var::usage = \"var is equal to nothing..\";\n" +
            "var=1;\n" +
            "\n" +
            "Module[{var},\n" +
            "  var + var<caret>\n" +
            "  \n" +
            "]");
    myFixture.renameElementAtCaret("var2");
    myFixture.checkResult("var;\n" +
        "var::usage = \"var is equal to nothing..\";\n" +
        "var=1;\n" +
        "\n" +
        "Module[{var2},\n" +
        "  var2 + var2\n" +
        "  \n" +
        "]");
  }

  public void testUsage() throws Exception {
    myFixture.configureByText(MathematicaFileType.INSTANCE,
        "func::usage = \"func<caret> is a function called like func[]\";");
    myFixture.renameElementAtCaret("newFunc");
    myFixture.checkResult("newFunc::usage = \"newFunc is a function called like newFunc[]\";");
  }

  public void testUsage2() throws Exception {
    myFixture.configureByText(MathematicaFileType.INSTANCE,
        "func<caret>::usage = \"func is a function called like func[]\";");
    myFixture.renameElementAtCaret("newFunc");
    myFixture.checkResult("newFunc::usage = \"newFunc is a function called like newFunc[]\";");
  }

  //TODO: This seems to be a bug in the FindUsage framework of IDEA
  //I have to decide if I really want to do this work by myself and probably loose performance.
/*
  public void testDollarVariables() throws Exception {
    myFixture.configureByText(MathematicaFileType.INSTANCE,
        "var$ = 1;\n" +
            "var$<caret> + var$");
    myFixture.renameElementAtCaret("$var$");
    myFixture.checkResult("$var$ = 1;\n" +
        "$var$ + $var$");
  }
*/

}