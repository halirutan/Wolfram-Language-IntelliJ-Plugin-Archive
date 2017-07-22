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

import com.intellij.lang.SmartEnterProcessorWithFixers.Fixer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.text.CharArrayUtil;
import de.halirutan.mathematica.lang.psi.api.CompoundExpression;
import org.jetbrains.annotations.NotNull;

import static de.halirutan.mathematica.lang.parsing.MathematicaElementTypes.SEMICOLON;

/**
 * @author patrick (11/12/13)
 */
class CompoundExpressionFixer extends Fixer<MathematicaSmartEnter> {
  @Override
  public void apply(@NotNull Editor editor, @NotNull MathematicaSmartEnter processor, @NotNull PsiElement element) throws IncorrectOperationException {
    Document doc = editor.getDocument();
    final int caretOffset = editor.getCaretModel().getOffset();
    if (element instanceof CompoundExpression) {
      final PsiElement lastChild = element.getLastChild();
      final TextRange lastChildRange = lastChild.getTextRange();

      if (!lastChild.getNode().getElementType().equals(SEMICOLON)) {
        if (lastChildRange.containsOffset(caretOffset) || lastChildRange.getEndOffset() < caretOffset) {
          int offset = lastChild.getTextOffset() + lastChild.getTextLength();
          offset = CharArrayUtil.shiftBackward(editor.getDocument().getCharsSequence(), offset - 1, " \t\n") + 1;
          doc.insertString(offset, ";");
          editor.getCaretModel().moveToOffset(offset + 1);
          processor.commit(editor);
        }
      }
    }
  }
}
