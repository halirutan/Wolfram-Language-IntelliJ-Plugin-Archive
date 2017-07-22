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

package de.halirutan.mathematica.codeinsight.highlighting;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides the highlighting of paired braces. This works as follows: When you navigate the cursor near a
 * brace/bracket/parenthesis then its corresponding closing (or opening, if you are over the closing one) brace changes
 * its color, so that you see which portion of the code is embraced.
 *
 * @author patrick (4/2/13)
 */
public class MathematicaBraceMatcher implements PairedBraceMatcher {

  private static final BracePair[] PAIRS = new BracePair[]{
      new BracePair(MathematicaElementTypes.LEFT_BRACE, MathematicaElementTypes.RIGHT_BRACE, true),
      new BracePair(MathematicaElementTypes.LEFT_PAR, MathematicaElementTypes.RIGHT_PAR, false),
      new BracePair(MathematicaElementTypes.LEFT_ASSOCIATION, MathematicaElementTypes.RIGHT_ASSOCIATION, false),
      new BracePair(MathematicaElementTypes.LEFT_BRACKET, MathematicaElementTypes.RIGHT_BRACKET, true),
      new BracePair(MathematicaElementTypes.PART_BEGIN, MathematicaElementTypes.RIGHT_BRACKET, true)
  };

  /**
   * This functions returns a list of {@link IElementType} pairs which correspond to the opening and closing brace lexer
   * tokens.
   *
   * @return List of the matching brace lexer token pairs.
   */
  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
    return true;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
