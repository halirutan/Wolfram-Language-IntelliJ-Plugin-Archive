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

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import de.halirutan.mathematica.codeinsight.inspections.AbstractInspection;
import de.halirutan.mathematica.codeinsight.inspections.MathematicaInspectionBundle;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (7/8/14)
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
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
    return new MathematicaVisitor() {
      @Override
      public void visitCompoundExpression(final CompoundExpression compoundExpression) {
        // todo: Implement locic that checks whether implicit multiplication could be unwanted.
      }
    };
  }
}

