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

package de.halirutan.mathematica.codeinsight.completion.rendering;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.template.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider.SymbolInformation;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.settings.MathematicaSettings;
import de.halirutan.mathematica.settings.MathematicaSettings.SmartEnterResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * One particular lookup element of a built-in function or symbol. It handles the insert and lets users inserts
 * completions ins various ways.
 * @author patrick (30.11.16).
 */
@SuppressWarnings("AnonymousClassVariableHidesContainingMethodVariable")
public class BuiltinSymbolLookupElement extends LookupElement {

  private final SymbolInformation myInfo;
  private static final char OPEN_BRACKET = '[';
  private static final char CLOSING_BRACKET = ']';

  public BuiltinSymbolLookupElement(SymbolInformation info) {
    myInfo = info;
  }

  @NotNull
  @Override
  public String getLookupString() {
    if ("System`".equals(myInfo.context)) {
      return myInfo.nameWithoutContext;
    }
    return myInfo.name;
  }

  @Override
  public void renderElement(LookupElementPresentation presentation) {
    presentation.setItemText(getLookupString());
    presentation.setItemTextForeground(JBColor.blue);
    presentation.setItemTextBold(myInfo.function);
    presentation.setTailText(myInfo.function ? "[" + myInfo.getCallPattern() + "]" : "", true);
    presentation.setTypeText(myInfo.context);
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }

  @Override
  public void handleInsert(InsertionContext context) {
    final SmartEnterResult smartEnterSetting = MathematicaSettings.getInstance().getSmartEnterResult();
    Editor editor = context.getEditor();
    Document document = editor.getDocument();
    context.commitDocument();

    char completionChar = context.getCompletionChar();
    context.setAddCompletionChar(false);

    if (completionChar == Lookup.COMPLETE_STATEMENT_SELECT_CHAR) {
      if (myInfo.function) {
        if (smartEnterSetting.equals(SmartEnterResult.INSERT_BRACES)) {
          document.insertString(context.getTailOffset(), "[]");
          final int currentPosition = context.getTailOffset();
          editor.getCaretModel().moveToOffset(currentPosition - 1);
        } else if (smartEnterSetting.equals(SmartEnterResult.INSERT_CODE) || smartEnterSetting.equals(SmartEnterResult.INSERT_TEMPLATE)) {
          document.insertString(context.getTailOffset(), Character.toString(OPEN_BRACKET));
          final int currentPosition = context.getTailOffset();
          document.insertString(currentPosition, myInfo.getCallPattern());
          document.insertString(context.getTailOffset(), Character.toString(CLOSING_BRACKET));
          final int endOffset = getFirstArgumentRange(myInfo) + currentPosition;
          editor.getSelectionModel().setSelection(currentPosition, endOffset);
          editor.getCaretModel().moveToOffset(endOffset);

          if (smartEnterSetting.equals(SmartEnterResult.INSERT_TEMPLATE)) {

            PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
            final PsiElement headOfFunction = PsiTreeUtil.findElementOfClassAtRange(context.getFile(), context.getStartOffset(), context.getTailOffset(), PsiElement.class);
            final TemplateBuilderFactory factory = TemplateBuilderFactoryImpl.getInstance();
            final TemplateBuilderImpl builder = (TemplateBuilderImpl) factory.createTemplateBuilder(headOfFunction);
            if (headOfFunction instanceof FunctionCall) {
              final PsiElement[] children = headOfFunction.getChildren();
              for (int i = 1; i < children.length; i++) {
                final PsiElement child = children[i];

                builder.replaceElement(child, new Expression() {
                  @NotNull
                  @Override
                  public Result calculateResult(ExpressionContext context) {
                    return new TextResult(child.getText());
                  }

                  @NotNull
                  @Override
                  public Result calculateQuickResult(ExpressionContext context) {
                    return new TextResult(child.getText());
                  }

                  @Nullable
                  @Override
                  public LookupElement[] calculateLookupItems(ExpressionContext context) {
                    return LookupElement.EMPTY_ARRAY;
                  }
                });
              }
              builder.run(context.getEditor(), true);

            }

          }
        }
      } else {
        document.insertString(context.getTailOffset(), " ");
        editor.getCaretModel().moveToOffset(context.getTailOffset());
      }

    }

    if (completionChar == ' ' || completionChar == '[') {
      context.setAddCompletionChar(true);
    }

    final Project project = context.getProject();
    PsiDocumentManager.getInstance(project).commitDocument(document);
  }

  private int getFirstArgumentRange(SymbolInformation info) {
    final String callPattern = info.getCallPattern();
    final int firstComma = callPattern.indexOf(',');
    if (firstComma == -1) {
      return callPattern.length();
    }
    return firstComma;
  }
}
