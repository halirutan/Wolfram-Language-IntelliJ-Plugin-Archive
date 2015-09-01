/*
 * Copyright (c) 2015 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.editoractions.wordselection;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;

import java.util.ArrayList;
import java.util.List;

/**
 * @author patrick (01.09.15)
 */
public class ArgumentSelectioner extends AbstractSelectionFixer<FunctionCall>{

  @Override
  public boolean canSelect(final PsiElement psiElement) {
    return ((psiElement instanceof Expression) && psiElement.getParent().getParent() instanceof FunctionCall) ||
        (psiElement.getParent() instanceof FunctionCall);
  }

  @Override
  public boolean isType(final PsiElement psiElement) {
    return psiElement instanceof FunctionCall;
  }

  @Override
  public TextRange getTextRange(final PsiElement psiElement) {
    if (psiElement instanceof FunctionCall) {
      FunctionCall funcCall = (FunctionCall) psiElement;
      final int headLength = funcCall.getHead().getTextLength();
      return new TextRange(
          funcCall.getTextOffset() + headLength + 1,
        funcCall.getTextOffset() + funcCall.getTextLength() - 1
      );
    }
    return null;
  }

}
