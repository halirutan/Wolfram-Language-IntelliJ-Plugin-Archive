/*
 * Mathematica Plugin for Jetbrains IDEA
 * Copyright (C) 2013 Patrick Scheibe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.halirutan.mathematica.codeInsight.completion;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.Nullable;

/**
 * This is almost completely stolen from {@link com.intellij.codeInsight.completion.util.ParenthesesInsertHandler}
 * @author patrick (4/3/13)
 */
public class MathematicaBracketInsertHandler implements InsertHandler<LookupElement> {

    private final static char OPEN_BRACKET = '[';
    private final static char CLOSING_BRACKET = ']';



    public static final MathematicaBracketInsertHandler INSTANCE = new MathematicaBracketInsertHandler();

    public static MathematicaBracketInsertHandler getInstance() {
        return INSTANCE;
    }

    protected MathematicaBracketInsertHandler() {}

    private static boolean isToken(@Nullable final PsiElement element, final String text) {
        return element != null && text.equals(element.getText());
    }


    public void handleInsert(final InsertionContext context, final LookupElement item) {
        final Editor editor = context.getEditor();
        final Document document = editor.getDocument();
        context.commitDocument();

        final char completionChar = context.getCompletionChar();
        context.setAddCompletionChar(false);

        if (completionChar == Lookup.NORMAL_SELECT_CHAR ) {
            document.insertString(context.getTailOffset(), " ");
            editor.getCaretModel().moveToOffset(context.getTailOffset());
        }

        if (completionChar == Lookup.COMPLETE_STATEMENT_SELECT_CHAR ) {
            final SymbolInformationProvider.SymbolInformation symbol = SymbolInformationProvider.getSymbolNames().get(item.getLookupString());
            boolean insertBrackets = symbol!=null && symbol.isFunction;
            if (insertBrackets) {
                document.insertString(context.getTailOffset(), Character.toString(OPEN_BRACKET));
                editor.getCaretModel().moveToOffset(context.getTailOffset());
                document.insertString(context.getTailOffset(), Character.toString(CLOSING_BRACKET));
            }
        }
   }

}
