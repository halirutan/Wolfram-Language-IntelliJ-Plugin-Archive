/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.completion;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider.SymbolInformation;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (30.11.16).
 */
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
      Editor editor = context.getEditor();
      Document document = editor.getDocument();
      context.commitDocument();

      char completionChar = context.getCompletionChar();
      context.setAddCompletionChar(false);

      if (completionChar == Lookup.COMPLETE_STATEMENT_SELECT_CHAR) {
        if (myInfo.function) {
          document.insertString(context.getTailOffset(), Character.toString(OPEN_BRACKET));
          editor.getCaretModel().moveToOffset(context.getTailOffset());
          document.insertString(context.getTailOffset(), Character.toString(CLOSING_BRACKET));
        } else {
          document.insertString(context.getTailOffset(), " ");
          editor.getCaretModel().moveToOffset(context.getTailOffset());
        }

      }

      if (completionChar == ' ') {
        context.setAddCompletionChar(true);
      }

    final Project project = context.getProject();
    PsiDocumentManager.getInstance(project).commitDocument(document);
  }
}
