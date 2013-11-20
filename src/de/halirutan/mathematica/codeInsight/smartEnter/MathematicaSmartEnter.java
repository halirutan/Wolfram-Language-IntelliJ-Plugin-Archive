package de.halirutan.mathematica.codeInsight.smartEnter;

import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.text.CharArrayUtil;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.lists.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (10/21/13)
 */
public class MathematicaSmartEnter extends SmartEnterProcessorWithFixers {

  static int MAX_UPWALK = 10;

  public MathematicaSmartEnter() {
    addFixers(new FunctionCallFixer());
    addFixers(new CompoundExpressionFixer());
    addEnterProcessors(new FunctionCallEnterProcessor());
  }

  public static PsiElement findNextElement(Editor editor, PsiFile psiFile) {
    int caret = editor.getCaretModel().getOffset();

    final Document doc = editor.getDocument();
    CharSequence chars = doc.getCharsSequence();
    int offset = caret == 0 ? 0 : CharArrayUtil.shiftBackward(chars, caret - 1, " \t");
    if (doc.getLineNumber(offset) < doc.getLineNumber(caret)) {
      offset = CharArrayUtil.shiftForward(chars, caret, " \t");
    }

    PsiElement current = psiFile.findElementAt(offset);
    PsiElement saveOriginal = current;
    int steps = 0;

    while (current != null && steps++ < MAX_UPWALK) {
      if (current instanceof List ||
          current instanceof CompoundExpression ||
          current instanceof FunctionCall ||
          current instanceof PsiFile) {

        if (current instanceof List && saveOriginal.getNode().getElementType() == RIGHT_BRACE ||
            current instanceof FunctionCall && saveOriginal.getNode().getElementType() == RIGHT_BRACKET ||
            current instanceof CompoundExpression && saveOriginal.getNode().getElementType() == SEMICOLON) {
          offset += 1;
          current = psiFile.findElementAt(offset);
          saveOriginal = current;
          steps = 0;
          continue;
        }
        return current;
      }
      current = current.getParent();
    }
    return null;
  }

  @Nullable
  @Override
  protected PsiElement getStatementAtCaret(Editor editor, PsiFile psiFile) {
    return findNextElement(editor, psiFile);
  }

  private class FunctionCallEnterProcessor extends FixEnterProcessor {
    @Override
    public boolean doEnter(PsiElement atCaret, PsiFile file, @NotNull Editor editor, boolean modified) {
      final CaretModel caretModel = editor.getCaretModel();
      if (modified) {
        CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, caretModel.getOffset());
        reformat(atCaret);
        commit(editor);
        return true;
      }
      if (atCaret instanceof PsiFile) {
        return false;
      } else {
        caretModel.moveToOffset(atCaret.getTextOffset() + atCaret.getTextLength());
        return true;
      }
    }
  }
}
