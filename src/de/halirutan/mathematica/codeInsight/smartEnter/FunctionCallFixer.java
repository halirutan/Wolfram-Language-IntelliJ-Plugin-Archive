package de.halirutan.mathematica.codeInsight.smartEnter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (10/21/13)
 */
public class FunctionCallFixer extends SmartEnterProcessorWithFixers.Fixer {

  private int startLine(Editor editor, PsiElement psiElement) {
    return editor.getDocument().getLineNumber(psiElement.getTextRange().getStartOffset());
  }

  @Override
  public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element) throws IncorrectOperationException {
    PsiElement args = null;

  }
}
