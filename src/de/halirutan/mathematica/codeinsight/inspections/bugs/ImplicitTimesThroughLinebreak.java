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
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.halirutan.mathematica.codeinsight.inspections.AbstractInspection;
import de.halirutan.mathematica.codeinsight.inspections.MathematicaInspectionBundle;
import de.halirutan.mathematica.file.MathematicaFileType;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.lists.List;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities.getFirstListElement;
import static de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities.getNextSiblingSkippingWhitespace;

/**
 * Provides warnings when commas or semicolons are missing. Unlike in other languages, Mathematica regards whitespaces
 * between expressions as multiplication to make a more mathematical input style possible. Therefore, {a, b c} is
 * syntactically correct and means {a, b*c}. While it is easy to see such mistakes when they happen in one line,
 * it can be hard to notice when list entries are separated through linebreak.
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

    private void registerProblem(final PsiElement element) {
      myHolder.registerProblem(
          element,
          TextRange.from(element.getTextLength()-1,1),
          MathematicaInspectionBundle.message("bugs.implicit.times.through.linebreak.message"));
    }

    @Override
    public void visitFunctionCall(final FunctionCall functionCall) {
      final PsiElement head = functionCall.getFirstChild();
      if (head == null) {
        return;
      }
      PsiElement elm = getNextSiblingSkippingWhitespace(head);
      PsiElement next;
      int argsWithoutComma = 0;
      while ((next = getNextSiblingSkippingWhitespace(elm)) != null &&
          !(next instanceof LeafPsiElement && next.getText().equals("]"))) {
        if (next instanceof PsiErrorElement) return;
        if (next instanceof LeafPsiElement && next.getText().equals(",")) {
          argsWithoutComma = 0;
        } else {
          argsWithoutComma++;
          if (argsWithoutComma > 1) {
            registerProblem(elm);
          }
        }
        elm = next;
      }
    }

    @Override
    public void visitList(final List list) {
      PsiElement elm = getFirstListElement(list);
      PsiElement next;
      int argsWithoutComma = 1;
      while ((next = getNextSiblingSkippingWhitespace(elm)) != null &&
          !(next instanceof LeafPsiElement && next.getText().equals("}"))) {
        if (next instanceof PsiErrorElement) return;
        if (next instanceof LeafPsiElement && next.getText().equals(",")) {
          argsWithoutComma = 0;
        } else {
          argsWithoutComma++;
          if (argsWithoutComma > 1) {
            registerProblem(elm);
          }
        }
        elm = next;
      }
    }
  }
}

