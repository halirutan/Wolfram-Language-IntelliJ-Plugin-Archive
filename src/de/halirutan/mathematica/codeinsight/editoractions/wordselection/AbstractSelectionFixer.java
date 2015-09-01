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

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author patrick (01.09.15)
 */
abstract public class AbstractSelectionFixer<T extends PsiElement> extends ExtendWordSelectionHandlerBase{

  public abstract boolean isType(final PsiElement t);

  public abstract TextRange getTextRange(final PsiElement element);

  @Override
  public abstract boolean canSelect(PsiElement psiElement);

  @Override
  public List<TextRange> select(PsiElement psiElement, CharSequence editorText, int cursorOffset, Editor editor) {

    T element = null;

    List<TextRange> result = new ArrayList<TextRange>();

    if (isType(psiElement.getParent().getParent())) {
      //noinspection unchecked
      element = (T) psiElement.getParent().getParent();
      result.add(psiElement.getTextRange());
      result.add(psiElement.getParent().getTextRange());

    } else if (isType(psiElement.getParent())) {
      //noinspection unchecked
      element = (T) psiElement.getParent();
      result.add(psiElement.getTextRange());
    }

    if (element == null) return result;

    final TextRange textRange = getTextRange(element);
    if (textRange != null && textRange.getLength() > 0) {
      result.add(textRange);
    }
    return result;
  }
}