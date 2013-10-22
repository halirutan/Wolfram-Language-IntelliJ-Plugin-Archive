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

package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (10/20/13)
 */
public class MathematicaFormattingModelBuilder implements FormattingModelBuilder {
  private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
    TokenSet assignments = TokenSet.create(
        MathematicaElementTypes.SET_DELAYED,
        MathematicaElementTypes.SET,
        MathematicaElementTypes.ADD_TO,
        MathematicaElementTypes.TIMES_BY,
        MathematicaElementTypes.SUBTRACT_FROM,
        MathematicaElementTypes.DIVIDE_BY);


    return new SpacingBuilder(settings)
        .around(assignments).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
        .after(MathematicaElementTypes.COMMA).spaceIf(true)
        .before(MathematicaElementTypes.LEFT_BRACKET).none()
        .after(MathematicaElementTypes.RIGHT_BRACKET).none();
  }

  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    return FormattingModelProvider.createFormattingModelForPsiFile(
        element.getContainingFile(),
        new MathematicaBlock(element.getNode(), Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(),
            createSpaceBuilder(settings)), settings
    );
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }
}
