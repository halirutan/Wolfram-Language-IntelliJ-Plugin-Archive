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

package de.halirutan.mathematica.codeInsight.surroundWith;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import org.intellij.lang.regexp.psi.RegExpAtom;
import org.intellij.lang.regexp.psi.RegExpBranch;
import org.intellij.lang.regexp.psi.RegExpElement;
import org.intellij.lang.regexp.psi.RegExpPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author patrick (11/21/13)
 */
public class MathematicaSurroundWithDescriptor implements SurroundDescriptor {
  private static final Surrounder[] SURROUNDERS = { };

  @NotNull
  public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
    return findElementsInRange(file, startOffset, endOffset);
  }

  @NotNull
  public Surrounder[] getSurrounders() {
    return SURROUNDERS;
  }

  @Override
  public boolean isExclusive() {
    return false;
  }

  private PsiElement[] findElementsInRange(PsiFile file, int startOffset, int endOffset) {
    // adjust start/end
    PsiElement element1 = file.findElementAt(startOffset);
    PsiElement element2 = file.findElementAt(endOffset - 1);
    if (element1 instanceof PsiWhiteSpace) {
      startOffset = element1.getTextRange().getEndOffset();
    }
    if (element2 instanceof PsiWhiteSpace) {
      endOffset = element2.getTextRange().getStartOffset();
    }

    final Expression expression = findElementAtStrict(file, startOffset, endOffset, Expression.class);
    if (expression != null) return new Expression[]{ expression };

    return PsiElement.EMPTY_ARRAY;
  }

  @Nullable
  private static <T extends Expression> T findElementAtStrict(PsiFile file, int startOffset, int endOffset, Class<T> clazz) {
    T element = PsiTreeUtil.findElementOfClassAtRange(file, startOffset, endOffset, clazz);
    if (element == null || element.getTextRange().getEndOffset() < endOffset) return null;
    return element;
  }
}
