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

package de.halirutan.mathematica.codeinsight.completion;

import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.MathematicaLanguage;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (7/7/14)
 */
public class MathematicaCharFilter extends CharFilter {
  @Nullable
  @Override
  public Result acceptChar(final char c, final int prefixLength, final Lookup lookup) {
    if(!lookup.isFocused()) return null;
    final PsiFile psiFile = lookup.getPsiFile();

    if (psiFile != null && !psiFile.getViewProvider().getLanguages().contains(MathematicaLanguage.INSTANCE))
      return null;

    switch (c) {
      case '[':
      case '(':
      case '{':
        ((LookupImpl) lookup).finishLookup('\n');
        return Result.SELECT_ITEM_AND_FINISH_LOOKUP;
      default:
        return null;
    }
  }
}
