package de.halirutan.mathematica.codeinsight.smartenter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.util.MathematicaPsiUtilities;
import org.jetbrains.annotations.NotNull;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.COMMA;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.RIGHT_BRACKET;

/**
 * @author patrick (30/10/13)
 */
public class FunctionCallFixer extends SmartEnterProcessorWithFixers.Fixer<MathematicaSmartEnter> {
  @Override
  public void apply(@NotNull Editor editor, @NotNull MathematicaSmartEnter processor, @NotNull PsiElement element) throws IncorrectOperationException {
    Document doc = editor.getDocument();
    if (element instanceof FunctionCall) {

      final PsiElement lastChild = element.getLastChild();
      if (!lastChild.getNode().getElementType().equals(RIGHT_BRACKET)) {
        PsiElement prevSibling = lastChild.getPrevSibling();
        if (prevSibling != null) {
          final int textOffset = prevSibling.getTextOffset();
          if (prevSibling.getNode().getElementType() == COMMA) {
            doc.replaceString(textOffset, textOffset + 1, "]");
          } else {
            doc.insertString(textOffset + prevSibling.getTextLength(), "]");
          }
        }
        return;
      }
      PsiElement prevSibling = lastChild.getPrevSibling();
      while (prevSibling != null && prevSibling instanceof PsiWhiteSpace) {
        prevSibling = prevSibling.getPrevSibling();
      }
      if (prevSibling != null && prevSibling.getNode().getElementType() == COMMA) {
        doc.insertString(prevSibling.getTextOffset() + 1, "\n\n");
        editor.getCaretModel().moveToOffset(prevSibling.getTextOffset() + 2);
        processor.commit(editor);
//          return;
      }
//      editor.getCaretModel().moveToOffset(lastChild.getTextOffset() + 1, true);

    }
  }
}
