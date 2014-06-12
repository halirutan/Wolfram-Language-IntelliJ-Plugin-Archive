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

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.MathematicaLanguage;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (6/11/14)
 */
public class MathematicaSurroundDescriptor implements SurroundDescriptor {
  private static final Surrounder SURROUNDERS[] = {
      new FunctionCallSurrounder(),
      new AnnonymousFunctionSurrounder(),
      new LocalizationSurrounder("Module"),
      new LocalizationSurrounder("With"),
      new LocalizationSurrounder("Block"),
      new LocalizationSurrounder("Function"),
      new LocalizationSurrounder("Compile")
  };

  @NotNull
  @Override
  public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
    final PsiElement elementInRange = CodeInsightUtilCore.findElementInRange(file, startOffset, endOffset, Expression.class, MathematicaLanguage.INSTANCE);
    return new PsiElement[]{elementInRange};
  }

  @NotNull
  @Override
  public Surrounder[] getSurrounders() {
    return SURROUNDERS;
  }

  @Override
  public boolean isExclusive() {
    return false;
  }
}
