/*
 * Copyright (c) 2017 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.editoractions.smartenter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.lang.psi.api.CompoundExpression;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.lists.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (10/21/13)
 */
public class MathematicaSmartEnter extends SmartEnterProcessorWithFixers {

  @SuppressWarnings("unchecked")
  public MathematicaSmartEnter() {
    final Fixer[] fixers = {new FunctionCallFixer(), new CompoundExpressionFixer(), new CommentFixer()};
    addFixers(fixers);
    addEnterProcessors(new FunctionCallEnterProcessor());
  }

  @SuppressWarnings("UnnecessaryLocalVariable")
  @Nullable
  @Override
  protected PsiElement getStatementAtCaret(Editor editor, PsiFile psiFile) {
    PsiElement atCaret = super.getStatementAtCaret(editor, psiFile);

    if (atCaret instanceof PsiWhiteSpace) {
      atCaret = PsiTreeUtil.skipSiblingsBackward(atCaret, PsiWhiteSpace.class);
    }

    if (atCaret instanceof PsiComment) {
      return atCaret;
    }

    final PsiElement expressionAtCaret = PsiTreeUtil.getParentOfType(atCaret,
        FunctionCall.class,
        CompoundExpression.class,
        List.class
    );

    if (expressionAtCaret != null && PsiTreeUtil.hasErrorElements(expressionAtCaret)) {
      final PsiErrorElement[] errors = PsiTreeUtil.getChildrenOfType(expressionAtCaret, PsiErrorElement.class);
      if (errors != null && errors.length > 0) {
        final int errorOffset = errors[0].getTextOffset();
        final PsiElement errorElement = psiFile.findElementAt(errorOffset);
        if (errorElement != null) {
          registerUnresolvedError(errorElement.getTextOffset() + errorElement.getTextLength());
        } else {
          registerUnresolvedError(errorOffset);
        }
      }
    }

    if (expressionAtCaret instanceof List) {
      if (atCaret.getText().equals("}") || !PsiTreeUtil.hasErrorElements(expressionAtCaret))
        return expressionAtCaret.getParent();
    }

    if (expressionAtCaret instanceof FunctionCall) {
      if (atCaret.getText().equals("]")) {
        PsiElement parent = PsiTreeUtil.getParentOfType(atCaret.getParent(),
            FunctionCall.class,
            CompoundExpression.class,
            List.class
        );
        return parent;
      }

    }

    return expressionAtCaret;
//    return findNextElement(editor, psiFile);
  }

  @Override
  protected boolean collectChildrenRecursively(@NotNull PsiElement atCaret) {
    return false;
  }

  @Override
  public boolean doNotStepInto(PsiElement element) {
    return true;
  }

  @Override
  protected void collectAdditionalElements(@NotNull PsiElement element, @NotNull java.util.List<PsiElement> result) {
  }

  private class FunctionCallEnterProcessor extends FixEnterProcessor {
    @Override
    public boolean doEnter(PsiElement atCaret, PsiFile file, @NotNull Editor editor, boolean modified) {
      final CaretModel caretModel = editor.getCaretModel();
      CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, caretModel.getOffset());
      reformat(atCaret);
      commit(editor);
      if (atCaret instanceof FunctionCall && modified && file.isValid()) {
        return true;
      }
      if (atCaret instanceof CompoundExpression) {
        super.plainEnter(editor);
        return true;
      }
      if (atCaret instanceof PsiComment) {
        return true;
      }
      if (atCaret instanceof PsiFile) {
        return true;
      } else {
        caretModel.moveToOffset(atCaret.getTextOffset() + atCaret.getTextLength());
        return true;
      }
    }
  }
}
