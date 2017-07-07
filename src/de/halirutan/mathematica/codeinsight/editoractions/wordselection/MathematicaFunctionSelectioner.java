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

package de.halirutan.mathematica.codeinsight.editoractions.wordselection;

import com.intellij.codeInsight.editorActions.wordSelection.BasicSelectioner;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;

import java.util.ArrayList;
import java.util.List;

/**
 * @author patrick (01.09.15)
 */
public class MathematicaFunctionSelectioner extends BasicSelectioner {
  @Override
  public boolean canSelect(PsiElement e) {
    return e instanceof FunctionCall;
  }

  @Override
  public List<TextRange> select(PsiElement psiElement, CharSequence editorText, int cursorOffset, Editor editor) {

    int start;
    int end;

    if (psiElement instanceof FunctionCall) {
      final FunctionCall funcCall = (FunctionCall) psiElement;
      List<TextRange> result = new ArrayList<>();
      start = funcCall.getTextOffset() + funcCall.getHead().getTextLength() + 1;
      end = funcCall.getTextOffset() + funcCall.getTextLength() - 1;
      if (start < end && start != 0)
        result.add(new TextRange(start, end));
      result.add(funcCall.getTextRange());
      return result;
    }
    return null;
  }
}