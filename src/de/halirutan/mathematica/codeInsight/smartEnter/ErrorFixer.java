package de.halirutan.mathematica.codeInsight.smartEnter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (10/21/13)
 */
public class ErrorFixer extends MathematicaSmartEnter.Fixer {
  @Override
  public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element) throws IncorrectOperationException {
    if (element instanceof PsiErrorElement) {

    }
  }
}
