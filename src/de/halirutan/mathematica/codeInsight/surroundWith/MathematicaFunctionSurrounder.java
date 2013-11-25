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

package de.halirutan.mathematica.codeInsight.surroundWith;

import com.intellij.codeInsight.generation.surroundWith.SurroundWithUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.fileTypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.psi.impl.MathematicaPsiFileImpl;
import org.intellij.lang.regexp.RegExpFileType;
import org.intellij.lang.regexp.psi.RegExpAtom;
import org.intellij.lang.regexp.psi.RegExpPattern;
import org.intellij.lang.regexp.psi.impl.RegExpElementImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (11/21/13)
 */
public class MathematicaFunctionSurrounder implements Surrounder {
  @Override
  public String getTemplateDescription() {
    return "Surround with function";
  }

  @Override
  public boolean isApplicable(@NotNull PsiElement[] elements) {
      return true;
    }
  }

  @Nullable
  public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] elements) throws IncorrectOperationException {
    assert elements.length == 1 || PsiTreeUtil.findCommonParent(elements) == elements[0].getParent();
    final PsiElement e = elements[0];
    final ASTNode node = e.getNode();
    assert node != null;

    final ASTNode parent = node.getTreeParent();

    final StringBuilder s = new StringBuilder();
    for (int i = 0; i < elements.length; i++) {
      final PsiElement element = elements[i];

        s.append(element.getText());
      if (i > 0) {
        final ASTNode child = element.getNode();
        assert child != null;
        parent.removeChild(child);
      }
    }
    final PsiFileFactory factory = PsiFileFactory.getInstance(project);

    final PsiFile f = factory.createFileFromText("dummy.m", MathematicaFileType.INSTANCE, makeReplacement(s));
    final RegExpPattern pattern = PsiTreeUtil.getChildOfType(f, RegExpPattern.class);
    assert pattern != null;

    final RegExpAtom element = pattern.getBranches()[0].getAtoms()[0];

    if (isInsideStringLiteral(e)) {
      final Document doc = editor.getDocument();
      PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(doc);
      final TextRange tr = e.getTextRange();
      doc.replaceString(tr.getStartOffset(), tr.getEndOffset(),
          StringUtil.escapeStringCharacters(element.getText()));

      return TextRange.from(e.getTextRange().getEndOffset(), 0);
    } else {
      final PsiElement n = e.replace(element);
      return TextRange.from(n.getTextRange().getEndOffset(), 0);
    }
  }

  private static boolean isInsideStringLiteral(PsiElement context) {
    while (context != null) {
      if (RegExpElementImpl.isLiteralExpression(context)) {
        return true;
      }
      context = context.getContext();
    }
    return false;
  }

  protected String makeReplacement(StringBuilder s) {
    return myGroupStart + s + ")";
  }
}
