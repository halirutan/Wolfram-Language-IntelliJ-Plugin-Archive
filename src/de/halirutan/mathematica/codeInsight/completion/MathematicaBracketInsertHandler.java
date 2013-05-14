/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.codeInsight.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * This is almost completely stolen from {@link com.intellij.codeInsight.completion.util.ParenthesesInsertHandler}
 *
 * @author patrick (4/3/13)
 */
public class MathematicaBracketInsertHandler implements InsertHandler<LookupElement> {

    public static final MathematicaBracketInsertHandler INSTANCE = new MathematicaBracketInsertHandler();
    private static final char OPEN_BRACKET = '[';
    private static final char CLOSING_BRACKET = ']';

    protected MathematicaBracketInsertHandler() {
    }

    public static MathematicaBracketInsertHandler getInstance() {
        return INSTANCE;
    }

    private static boolean isToken(@Nullable PsiElement element, String text) {
        return (element != null) && text.equals(element.getText());
    }

    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
        Editor editor = context.getEditor();
        Document document = editor.getDocument();
        context.commitDocument();

        char completionChar = context.getCompletionChar();
        context.setAddCompletionChar(false);

//        if (completionChar == Lookup.NORMAL_SELECT_CHAR) {
////            editor.getCaretModel().moveToOffset(context.getTailOffset());
//        }

        if (completionChar == Lookup.COMPLETE_STATEMENT_SELECT_CHAR) {
            SymbolInformationProvider.SymbolInformation symbol = SymbolInformationProvider.getSymbolNames().get(item.getLookupString());
            boolean insertBrackets = (symbol != null) && symbol.function;
            if (insertBrackets) {
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
    }

}
