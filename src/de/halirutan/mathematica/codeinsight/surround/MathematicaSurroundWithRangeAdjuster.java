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

package de.halirutan.mathematica.codeinsight.surround;

import com.intellij.codeInsight.generation.surroundWith.SurroundWithRangeAdjuster;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiUtilBase;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import de.halirutan.mathematica.lang.psi.api.Expression;
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile;
import org.jetbrains.annotations.Nullable;

/**
 * The range adjuster has two purposes: If a user called surroundWith having an explicit selection, we just remove the
 * whitespace before and after the selection. If the user has not selected anything it is up to us to find a good
 * expression near the caret that is going to be surrounded. For this, we use {@link SurroundExpressionFinder} to inspect
 * the syntax tree and we return the range hopefully a useful expression.
 *
 * @author patrick (04.06.17).
 */
public class MathematicaSurroundWithRangeAdjuster implements SurroundWithRangeAdjuster {

  @Nullable
  @Override
  public TextRange adjustSurroundWithRange(PsiFile file, TextRange selectedRange) {
    if (file instanceof MathematicaPsiFile) {
      return adjustSurroundWithRange(file, selectedRange, true);
    }
    return selectedRange;
  }

  @Nullable
  @Override
  public TextRange adjustSurroundWithRange(PsiFile file, TextRange selectedRange, boolean hasSelection) {
    if (!(file instanceof MathematicaPsiFile)) {
      return selectedRange;
    }
    int startOffset = selectedRange.getStartOffset();
    int endOffset = selectedRange.getEndOffset();
    if (endOffset < startOffset) {
      int tmp = endOffset;
      endOffset = startOffset;
      startOffset = tmp;
    }

    if (hasSelection) {
      final FileViewProvider viewProvider = file.getViewProvider();
      PsiElement element1 = viewProvider.findElementAt(startOffset, MathematicaLanguage.INSTANCE);
      PsiElement element2 = viewProvider.findElementAt(endOffset - 1, MathematicaLanguage.INSTANCE);
      if (element1 instanceof PsiWhiteSpace) {
        startOffset = element1.getTextRange().getEndOffset();
        element1 = file.findElementAt(startOffset);
      }
      if (element2 instanceof PsiWhiteSpace) {
        endOffset = element2.getTextRange().getStartOffset();
        element2 = file.findElementAt(endOffset - 1);
      }
      if (element1 != null && element2 != null) {
        final int startOffset1 = element1.getTextRange().getStartOffset();
        final int endOffset1 = element2.getTextRange().getEndOffset();
        if (startOffset1 < endOffset1) {
          return TextRange.create(startOffset1, endOffset1);
        }
      }
      return null;
    } else {
      final Project project = file.getProject();
      final Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
      if (selectedTextEditor == null) {
        return null;
      }

      PsiElement element = PsiUtilBase.getElementAtCaret(selectedTextEditor);
      if (element == null) {
        return null;
      }

      SurroundExpressionFinder expressionFinder = new SurroundExpressionFinder();
      element.accept(expressionFinder);
      PsiElement bestExpression = expressionFinder.getBestExpression();
      if (bestExpression instanceof Expression) {
        final TextRange textRange = bestExpression.getTextRange();
        selectedTextEditor.getSelectionModel().setSelection(textRange.getStartOffset(), textRange.getEndOffset());
        return bestExpression.getTextRange();
      }
    }
    return selectedRange;
  }
}
