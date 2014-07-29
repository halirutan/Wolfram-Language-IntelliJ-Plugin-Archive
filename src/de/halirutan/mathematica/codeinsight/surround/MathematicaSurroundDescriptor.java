/*
 * Copyright (c) 2014 Patrick Scheibe
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
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (6/11/14)
 */
public class MathematicaSurroundDescriptor implements SurroundDescriptor {
  private static final Surrounder SURROUNDERS[] = {
      new FunctionCallSurrounder(),
      new ParenthesesSurrounder("(", ")", "Surround with ()"),
      new ParenthesesSurrounder("{", "}", "Surround with {}"),
      new AnonymousFunctionSurrounder()
//      new LocalizationSurrounder("Module"),
//      new LocalizationSurrounder("With"),
//      new LocalizationSurrounder("Block"),
//      new LocalizationSurrounder("Function"),
//      new LocalizationSurrounder("Compile")
  };

  @Nullable
  private static Expression findElementAtStrict(PsiFile file, int startOffset, int endOffset) {
    Expression element = PsiTreeUtil.findElementOfClassAtRange(file, startOffset, endOffset, Expression.class);
    if (element == null) return null;
    PsiElement result = element;
    // Here I'm still unsure about the best approach to select the final PsiElements if the
    // text-range goes over several scopes.
//    while (result.getTextRange().getEndOffset() < endOffset) {
//      result = result.getParent();
//    }
    //noinspection ConstantConditions
    if (result instanceof Expression) {
      return (Expression) result;
    }
    return null;
  }

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

    if (endOffset < startOffset) {
      int tmp = endOffset;
      endOffset = startOffset;
      startOffset = tmp;
    }

    // adjust start/end
    PsiElement element1 = file.findElementAt(startOffset);
    PsiElement element2 = file.findElementAt(endOffset - 1);
    if (element1 instanceof PsiWhiteSpace) {
      startOffset = element1.getTextRange().getEndOffset();
    }
    if (element2 instanceof PsiWhiteSpace) {
      endOffset = element2.getTextRange().getStartOffset();
    }
    final PsiElement elementAtStart = file.findElementAt(startOffset);
    final PsiElement elementAtEnd = file.findElementAt(endOffset);
    if (elementAtStart != null && elementAtEnd != null) {

      final PsiElement commonContext = PsiTreeUtil.findCommonParent(elementAtStart, elementAtEnd);
      if (commonContext != null && commonContext instanceof Expression && !(commonContext instanceof PsiFile))
        return new PsiElement[]{commonContext};

//      final Expression expression = findElementAtStrict(file, startOffset, endOffset);
//      if (expression != null) return new Expression[]{expression};

    }
    return PsiElement.EMPTY_ARRAY;
  }
}
