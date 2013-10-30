package de.halirutan.mathematica.codeInsight.smartEnter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (30/10/13)
 */
public class FunctionCallFixer extends SmartEnterProcessorWithFixers.Fixer<MathematicaSmartEnter> {
  @Override
  public void apply(@NotNull Editor editor, @NotNull MathematicaSmartEnter processor, @NotNull PsiElement element) throws IncorrectOperationException {
    Document doc = editor.getDocument();
    if (element instanceof FunctionCall) {

      if (!element.getLastChild().getNode().getElementType().equals(MathematicaElementTypes.RIGHT_BRACKET)) {
        PsiElement prevSibling = element.getLastChild().getPrevSibling();
        if (prevSibling != null) {
          doc.insertString(prevSibling.getTextOffset() + prevSibling.getTextLength(),"]");
        }
      }
    } else if (element instanceof CompoundExpression) {

    }
  }
}
