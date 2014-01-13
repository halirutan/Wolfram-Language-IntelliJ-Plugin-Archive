package de.halirutan.mathematica.codeinsight.smartenter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import org.jetbrains.annotations.NotNull;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.SEMICOLON;

/**
 * @author patrick (11/12/13)
 */
public class CompoundExpressionFixer extends SmartEnterProcessorWithFixers.Fixer<MathematicaSmartEnter> {
  @Override
  public void apply(@NotNull Editor editor, @NotNull MathematicaSmartEnter processor, @NotNull PsiElement element) throws IncorrectOperationException {
    Document doc = editor.getDocument();
    if (element instanceof CompoundExpression) {
      final PsiElement lastChild = element.getLastChild();
      if (!lastChild.getNode().getElementType().equals(SEMICOLON)) {
        final int offset = lastChild.getTextOffset() + lastChild.getTextLength();
        doc.insertString(offset, ";\n");
        editor.getCaretModel().moveToOffset(offset + 2);
      }
    }
  }
}
