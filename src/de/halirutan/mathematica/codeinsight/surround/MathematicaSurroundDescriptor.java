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

package de.halirutan.mathematica.codeinsight.surround;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Extension point that defines available surrounders.
 * @author patrick (6/11/14)
 */
public class MathematicaSurroundDescriptor implements SurroundDescriptor {
  private static final Surrounder SURROUNDERS[] = {
      new FunctionCallSurrounder(),
      new ParenthesesSurrounder("(", ")", "Surround with ()"),
      new ParenthesesSurrounder("{", "}", "Surround with {}"),
      new ParenthesesSurrounder("[", "]", "Surround with []"),
      new AnonymousFunctionSurrounder()
  };

  @NotNull
  public Surrounder[] getSurrounders() {
    return SURROUNDERS;
  }

  @Override
  public boolean isExclusive() {
    return false;
  }

  @NotNull
  @Override
  public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
    if (file instanceof MathematicaPsiFile) {
      return findElementsInRange(file, startOffset, endOffset);
    }
    return PsiElement.EMPTY_ARRAY;
  }

  private PsiElement[] findElementsInRange(PsiFile file, int startOffset, int endOffset) {

    if (endOffset < startOffset) {
      int tmp = endOffset;
      endOffset = startOffset;
      startOffset = tmp;
    }

    final PsiElement elementAtStart = file.findElementAt(startOffset);
    final PsiElement elementAtEnd = file.findElementAt(endOffset - 1);
    if (elementAtStart != null && elementAtEnd != null) {

      final PsiElement parent = PsiTreeUtil.findCommonContext(elementAtStart, elementAtEnd);
      if (parent != null && parent.getTextRange().equalsToRange(startOffset, endOffset)) {
        return new PsiElement[]{parent};
      }

      return new PsiElement[]{elementAtStart, elementAtEnd};

    }
    return PsiElement.EMPTY_ARRAY;
  }
}
