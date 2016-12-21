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

package de.halirutan.mathematica.codeinsight.completion;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.string.MString;

import java.util.HashSet;

/**
 * @author patrick (19.12.16).
 */
public class ImportedContextVisitor extends MathematicaVisitor {

  private final HashSet<String > myImportedContexts;

  public ImportedContextVisitor() {
    myImportedContexts = new HashSet<String>();
  }

  public HashSet<String> getImportedContexts() {
    return myImportedContexts;
  }

  @Override
  public void visitFile(PsiFile file) {
    file.acceptChildren(this);
  }

  @Override
  public void visitCompoundExpression(CompoundExpression compoundExpression) {
    compoundExpression.acceptChildren(this);
  }

  @Override
  public void visitFunctionCall(FunctionCall functionCall) {
    if (functionCall.matchesHead("Needs")) {
      final PsiElement context = functionCall.getArgument(1);
      if (context instanceof MString) {
        final String text = context.getText();
        myImportedContexts.add(text.substring(1, text.length() - 1));
      }
    }
  }
}
