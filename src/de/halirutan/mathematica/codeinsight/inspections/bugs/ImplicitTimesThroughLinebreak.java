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

package de.halirutan.mathematica.codeinsight.inspections.bugs;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiErrorElement;
import de.halirutan.mathematica.codeinsight.inspections.AbstractInspection;
import de.halirutan.mathematica.codeinsight.inspections.MathematicaInspectionBundle;
import de.halirutan.mathematica.filetypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.lists.List;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.halirutan.mathematica.parsing.psi.util.MathematicaPsiUtilities.getFirstListElement;
import static de.halirutan.mathematica.parsing.psi.util.MathematicaPsiUtilities.getNextSiblingSkippingWhitespace;

/**
 *
 * @author halirutan
 */
public class ImplicitTimesThroughLinebreak extends AbstractInspection {

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return MathematicaInspectionBundle.message("bugs.implicit.times.through.linebreak.name");
  }

  @Nullable
  @Override
  public String getStaticDescription() {
    return MathematicaInspectionBundle.message("bugs.implicit.times.through.linebreak.description");
  }

  @Nls
  @NotNull
  @Override
  public String getGroupDisplayName() {
    return MathematicaInspectionBundle.message("group.bugs");
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
    if(session.getFile().getFileType() instanceof MathematicaFileType) {
      return new ImplicitTimesVisitor(holder);
    } else return PsiElementVisitor.EMPTY_VISITOR;
  }

  private static class ImplicitTimesVisitor extends MathematicaVisitor {


    private final ProblemsHolder myHolder;

    public ImplicitTimesVisitor(final ProblemsHolder holder) {
      this.myHolder = holder;
    }

    @Override
    public void visitFunctionCall(final FunctionCall functionCall) {
      final PsiElement head = functionCall.getFirstChild();
      if (head == null) {
        return;
      }
      PsiElement elm = getNextSiblingSkippingWhitespace(head);
      int argsWithoutComma = 0;
      while ((elm = getNextSiblingSkippingWhitespace(elm)) != null &&
          elm.getNode().getElementType() != MathematicaElementTypes.RIGHT_BRACKET) {
        if (elm instanceof PsiErrorElement) continue;
        if (elm.getNode().getElementType() == MathematicaElementTypes.COMMA) {
          argsWithoutComma = 0;
        } else {
          argsWithoutComma++;
          if (argsWithoutComma > 1) {
            myHolder.registerProblem(elm, TextRange.from(0, 1), MathematicaInspectionBundle.message("bugs.implicit.times.through.linebreak.message"));
          }
        }
      }
    }

    @Override
    public void visitList(final List list) {
      PsiElement elm = getFirstListElement(list);
      int argsWithoutComma = 1;
      while ((elm = getNextSiblingSkippingWhitespace(elm)) != null &&
          elm.getNode().getElementType() != MathematicaElementTypes.RIGHT_BRACE) {
        if (elm instanceof PsiErrorElement) continue;
        if (elm.getNode().getElementType() == MathematicaElementTypes.COMMA) {
          argsWithoutComma = 0;
        } else {
          argsWithoutComma++;
          if (argsWithoutComma > 1) {
            myHolder.registerProblem(elm, TextRange.from(0, 1), MathematicaInspectionBundle.message("bugs.implicit.times.through.linebreak.message"));
          }
        }
      }
    }
  }
}

