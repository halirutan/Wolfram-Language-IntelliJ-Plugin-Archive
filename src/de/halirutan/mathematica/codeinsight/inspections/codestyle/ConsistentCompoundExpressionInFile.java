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

package de.halirutan.mathematica.codeinsight.inspections.codestyle;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import de.halirutan.mathematica.codeinsight.inspections.AbstractInspection;
import de.halirutan.mathematica.codeinsight.inspections.MathematicaInspectionBundle;
import de.halirutan.mathematica.filetypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import static de.halirutan.mathematica.parsing.psi.util.MathematicaPsiUtilities.getNextSiblingSkippingWhitespace;

/**
 * @author patrick (7/8/14)
 */
public class ConsistentCompoundExpressionInFile extends AbstractInspection {


  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return MathematicaInspectionBundle.message("consistent.compound.expression.in.file.name");
  }

  @NotNull
  @Override
  public String getStaticDescription() {
    return MathematicaInspectionBundle.message("consistent.compound.expression.in.file.description");
  }

  @Nls
  @NotNull
  @Override
  public String getGroupDisplayName() {
    return MathematicaInspectionBundle.message("group.codestyle");
  }

  @SuppressWarnings("OverlyComplexAnonymousInnerClass")
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly,@NotNull LocalInspectionToolSession session) {
    if(session.getFile().getFileType() instanceof MathematicaFileType) {
      return new MathematicaVisitor() {
        @Override
        public void visitFile(final PsiFile file) {
          PsiElement child = file.getFirstChild();
          while (child instanceof PsiWhiteSpace || child instanceof PsiComment) {
            child = child.getNextSibling();
          }
          while (child != null) {
            if (getNextSiblingSkippingWhitespace(child) != null) {
              final PsiElement lastChild = child.getLastChild();
              if (lastChild != null && lastChild.getTextLength() > 0) {
                holder.registerProblem(
                    lastChild,
                    TextRange.from(lastChild.getTextLength()-1, 1),
                    MathematicaInspectionBundle.message("consistent.compound.expression.in.file.message"),
                    new ConsistentCompoundExpressionQuickFix());
              }
            }
            child = getNextSiblingSkippingWhitespace(child);
          }
        }
      };
    }
    return PsiElementVisitor.EMPTY_VISITOR;
  }
}
