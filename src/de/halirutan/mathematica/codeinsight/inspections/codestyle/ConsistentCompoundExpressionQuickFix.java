/*
 * Copyright (c) 2015 Patrick Scheibe
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

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.BatchQuickFix;
import com.intellij.codeInspection.CommonProblemDescriptor;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author patrick (10.07.15)
 */
public class ConsistentCompoundExpressionQuickFix extends LocalQuickFixBase implements HighPriorityAction {

  private static final Logger LOG = Logger.getInstance("#de.halirutan.mathematica.codeinsight.inspections.codestyle.ConsistentCompoundExpressionQuickFix");

  public ConsistentCompoundExpressionQuickFix() {
    super("Fix missing semicolon");
  }

  @Override
  public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
    final PsiElement elm = descriptor.getPsiElement();

    if (!FileModificationService.getInstance().prepareFileForWrite(elm.getContainingFile())) return;

    final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
    final Document doc = manager.getDocument(elm.getContainingFile());
    if (doc != null && doc.isWritable()) {
      doc.insertString(descriptor.getTextRangeInElement().getEndOffset(), ";");
    } else {
      LOG.warn("Document was null or not writable!");
    }
  }
}
