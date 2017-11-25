/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.lang.resolve;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.ResolveTestCase;

import java.io.File;

/**
 * @author patrick (20.07.17).
 */
abstract public class AbstractResolveTest extends ResolveTestCase {

  @Override
  protected String getTestDataPath() {
    return new File("testData/resolve/").getAbsolutePath();
  }

  PsiReference configure() throws Exception {
    final String fileName = getTestDataPath() + File.pathSeparator + getTestName(false) + ".m";
    return configureByFileText(FileUtil.loadFile(new File(fileName)));
  }

  protected PsiReference configureByFileText(String fileText) throws Exception {
    return super.configureByFileText(fileText, getTestName(false) + ".m");
  }
}
