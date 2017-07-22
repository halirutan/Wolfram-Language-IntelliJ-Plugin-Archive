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

package de.halirutan.mathematica.lang.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.PsiTestCase;
import de.halirutan.mathematica.MathematicaTestUtils;
import de.halirutan.mathematica.lang.psi.MathematicaRecursiveVisitor;
import de.halirutan.mathematica.lang.psi.api.Symbol;

/**
 * @author patrick (20.07.17).
 */
public class ResovePerformanceTest extends PsiTestCase {

  @Override
  protected String getTestDataPath() {
    return MathematicaTestUtils.getTestPath() + "/lang/resolve";
  }

  public void testLargeFile() throws Exception {
    final int[] numResolved = {0};
    final PsiFile file = createFile("LargeFile.m", loadFile("LargeFile.m"));
    file.accept(new MathematicaRecursiveVisitor(){
      @Override
      public void visitSymbol(Symbol symbol) {
        final PsiElement resolve = symbol.resolve();
        if (resolve != null) {
          numResolved[0]++;
        }
      }
    });
    System.out.println(numResolved[0]);
  }
}
