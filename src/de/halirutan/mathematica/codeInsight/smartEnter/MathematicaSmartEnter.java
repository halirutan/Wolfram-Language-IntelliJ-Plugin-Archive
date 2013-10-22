package de.halirutan.mathematica.codeInsight.smartEnter;

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (10/21/13)
 */
public class MathematicaSmartEnter extends SmartEnterProcessor {

  @Override
  public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
    PsiElement statementAtCaret = getStatementAtCaret(editor, psiFile);
    return false;

  }

}
