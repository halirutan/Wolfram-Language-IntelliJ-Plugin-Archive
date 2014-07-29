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

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.util.MathematicaPsiElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (7/28/14)
 */
public class ParenthesesSurrounder extends MathematicaExpressionSurrounder {

  private final String myOpener;
  private final String myCloser;
  private final String myDescription;

  public ParenthesesSurrounder(final String opener, final String closer, final String description) {
    this.myOpener = opener;
    this.myCloser = closer;
    this.myDescription = description;
  }

  /**
   * As long as we have a complete expression, we can surround
   */
  @Override
  protected boolean isApplicable(final Expression element) {
    return true;
  }

  @Nullable
  @Override
  public TextRange surroundElements(@NotNull final Project project, @NotNull final Editor editor, final Expression element) throws IncorrectOperationException {
    MathematicaPsiElementFactory factory = new MathematicaPsiElementFactory(project);
    CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);

    Expression parExpr = factory.createExpressionFromText(myOpener + element.getText() + myCloser);
    parExpr = (Expression) codeStyleManager.reformat(parExpr);
    final PsiElement replace = element.replace(parExpr);
    return TextRange.from(replace.getTextOffset() + replace.getTextLength(), 0);
  }

  @Override
  public String getTemplateDescription() {
    return myDescription;
  }
}
