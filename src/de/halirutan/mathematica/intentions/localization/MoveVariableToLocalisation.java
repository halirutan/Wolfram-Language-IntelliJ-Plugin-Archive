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

package de.halirutan.mathematica.intentions.localization;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.intentions.IntentionBundle;
import de.halirutan.mathematica.lang.psi.api.Expression;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.lists.List;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct.ConstructType;
import de.halirutan.mathematica.lang.psi.util.MathematicaPsiElementFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides the insertion of unlocalised variables into a Module/Block/With declaration list
 * @author patrick (27.12.16).
 */
public class MoveVariableToLocalisation implements IntentionAction {
  @Nls
  @NotNull
  @Override
  public String getText() {
    return IntentionBundle.message("localisation.name");
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return IntentionBundle.message("familyName");
  }



  /**
   * Checks if there is a symbol at the caret and if there is no selection. Then it checks whether the symbol
   * has already a reference to a local construct like Module. If not, then we check if there is a surrounding
   * Module/Block/With that can be used for the introduction.
   * @param project current project
   * @param editor editor
   * @param file file we are editing
   * @return true if we can introduce a symbol in a surround Module/Block/With
   */
  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    if (editor.getSelectionModel().hasSelection()) {
      return false;
    }
    PsiElement element = findElementAtCaret(file, editor);
    if (element != null) {
      PsiReference reference = element.getReference();
      if (reference != null) {
        PsiElement resolve = reference.resolve();
        if (resolve instanceof Symbol) {
          ConstructType elementConstruct = ((Symbol) resolve).getLocalizationConstruct();
          if (elementConstruct != ConstructType.NULL) {
            return false;
          }
        }
      }
      return !PsiTreeUtil.treeWalkUp(new FindLocalisationProcessor(), element, file, ResolveState.initial());
    }
    return false;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    final PsiElement element = findElementAtCaret(file, editor);
    if (element == null) {
      return;
    }
    assert element.isValid() : element;
    FindLocalisationProcessor processor = new FindLocalisationProcessor();
    boolean notFound = PsiTreeUtil.treeWalkUp(processor, element, file, ResolveState.initial());
    if (!notFound) {
      FunctionCall localisationElement = processor.myLocalisationElement;
      if (localisationElement != null && localisationElement.isValid()) {
        PsiElement initList = localisationElement.getArgument(1);
        if (initList instanceof List) {
          MathematicaPsiElementFactory factory = new MathematicaPsiElementFactory(project);
          String text = initList.getText();
          Expression newList = factory.createExpressionFromText(text.substring(0, text.length() - 1) +
              ", " + element.getText() + "}");
          initList.replace(newList);
        }
      }
    }
  }

  @Override
  public boolean startInWriteAction() {
    return true;
  }

  @Nullable
  private PsiElement findElementAtCaret(PsiFile file, Editor editor) {
    CaretModel caretModel = editor.getCaretModel();
    int offset = caretModel.getOffset();
    PsiElement element = file.findElementAt(offset);
    if (element instanceof PsiWhiteSpace) {
      element = file.findElementAt(Math.max(0, offset - 1));
    }
    if (element != null && element.getContext() instanceof Symbol) {
      return element.getContext();
    }
    return null;
  }

}
