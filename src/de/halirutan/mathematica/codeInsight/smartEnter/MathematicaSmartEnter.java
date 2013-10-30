package de.halirutan.mathematica.codeInsight.smartEnter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.text.CharArrayUtil;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (10/21/13)
 */
public class MathematicaSmartEnter extends SmartEnterProcessorWithFixers {

  static int MAX_UPWALK = 10;

  public MathematicaSmartEnter() {
    addFixers(new FunctionCallFixer());
  }


  @Nullable
  @Override
  protected PsiElement getStatementAtCaret(Editor editor, PsiFile psiFile) {
    int caret = editor.getCaretModel().getOffset();

    final Document doc = editor.getDocument();
    CharSequence chars = doc.getCharsSequence();
    int offset = caret == 0 ? 0 : CharArrayUtil.shiftBackward(chars, caret - 1, " \t");
    if (doc.getLineNumber(offset) < doc.getLineNumber(caret)) {
      offset = CharArrayUtil.shiftForward(chars, caret, " \t");
    }

    PsiElement current = psiFile.findElementAt(offset);
    int steps = 0;

    while (current != null && steps++ < MAX_UPWALK) {
      if (current instanceof de.halirutan.mathematica.parsing.psi.api.lists.List ||
          current instanceof CompoundExpression ||
          current instanceof FunctionCall) {
        return current;
      }
      current = current.getParent();
    }
    return null;
  }
}
