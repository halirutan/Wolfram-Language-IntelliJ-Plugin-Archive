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

package de.halirutan.mathematica.lang.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import de.halirutan.mathematica.file.MathematicaFileType;
import de.halirutan.mathematica.lang.psi.api.Symbol;

/**
 * @author patrick (5/21/13)
 */
class MathematicaSymbolFactory {
  public static Symbol createSymbol(Project project, String name) {
    final MathematicaPsiFileImpl file = createFile(project, name);
    return (Symbol) file.getFirstChild();
  }

  public static PsiElement createExpression(Project project, String name) {
    final MathematicaPsiFileImpl file = createFile(project, name);
    return file.getFirstChild();
  }


  private static MathematicaPsiFileImpl createFile(Project project, String symbolName) {
    String fileName = "dummy.m";
    final PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
    return (MathematicaPsiFileImpl) psiFileFactory.createFileFromText(fileName, MathematicaFileType.INSTANCE, symbolName);
  }


}
