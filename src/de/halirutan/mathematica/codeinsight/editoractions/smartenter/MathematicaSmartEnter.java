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

  private static final int MAX_UPWALK = 10;

  @SuppressWarnings("unchecked")
  public MathematicaSmartEnter() {
    final Fixer[] fixers = {new FunctionCallFixer(), new CompoundExpressionFixer()};
    addFixers(fixers);
    addEnterProcessors(new FunctionCallEnterProcessor());
  }

  private static PsiElement findNextElement(Editor editor, PsiFile psiFile) {
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

  @Override
  public boolean doNotStepInto(PsiElement element) {
    return true;
  }

  private class FunctionCallEnterProcessor extends FixEnterProcessor {
    @Override
    public boolean doEnter(PsiElement atCaret, PsiFile file, @NotNull Editor editor, boolean modified) {
      final CaretModel caretModel = editor.getCaretModel();
      if (modified && file.isValid()) {
        CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, caretModel.getOffset());
        reformat(atCaret);
        commit(editor);
        return true;
      }
      if (atCaret instanceof PsiFile || atCaret instanceof CompoundExpression) {
        return false;
      } else {
        caretModel.moveToOffset(atCaret.getTextOffset() + atCaret.getTextLength());
        return true;
      }
    }
  }
}
