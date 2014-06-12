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

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.filetypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (6/12/14)
 */
public class AnnonymousFunctionSurrounder implements Surrounder {
  @Override
  public String getTemplateDescription() {
    return "Anonymous function";
  }

  @Override
  public boolean isApplicable(@NotNull PsiElement[] elements) {
    return elements.length == 1 && elements[0] != null && elements[0] instanceof Expression;
  }

  @Nullable
  @Override
  public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] elements) throws IncorrectOperationException {
    assert (elements.length == 1 && elements[0] != null) || PsiTreeUtil.findCommonParent(elements) == elements[0].getParent();
    final PsiElement e = elements[0];

    final PsiFileFactory factory = PsiFileFactory.getInstance(project);
    final StringBuilder stringBuilder = new StringBuilder("(");
    stringBuilder.append(e.getText());
    stringBuilder.append(")&");

    final PsiFile file = factory.createFileFromText("dummy.m", MathematicaFileType.INSTANCE, stringBuilder);
    final FunctionCall[] func = PsiTreeUtil.getChildrenOfType(file, FunctionCall.class);
    assert func != null && func[0] != null;
    e.replace(func[0]);
//    final PsiElement head = newElement.getFirstChild();
//    return head == null ? null : head.getTextRange();
    return null;
  }
}
