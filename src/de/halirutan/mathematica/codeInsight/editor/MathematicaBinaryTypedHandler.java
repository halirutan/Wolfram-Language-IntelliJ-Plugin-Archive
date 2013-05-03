/*
 * Copyright (c) 2013 Patrick Scheibe
 *
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

package de.halirutan.mathematica.codeInsight.editor;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.TypedHandler;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.codeInsight.highlighting.BraceMatcher;
import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.psi.api.MathematicaPsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Until I update this, it's an exact copy of what you find in the Erlang plugin. Thanks Sergey.
 *
 * @author patrick (4/4/13)
 */
public class MathematicaBinaryTypedHandler extends TypedHandlerDelegate

    {

        @Override
        public TypedHandlerDelegate.Result charTyped(char c, Project project, Editor editor, @NotNull PsiFile file) {
        if (!(file instanceof MathematicaPsiFile)) return super.charTyped(c, project, editor, file);

        if ((c != '<') || !CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET) {
            return TypedHandlerDelegate.Result.CONTINUE;
        }
        insertMatchedBinaryBraces(project, editor, file);
        return TypedHandlerDelegate.Result.CONTINUE;
    }

        /**
         * this is almost complete c'n'p from TypedHandler,
         * This code should be generalized into BraceMatchingUtil to support custom matching braces for plugin developers
         *
         * @see TypedHandler
         * @see BraceMatchingUtil
         */
    private static void insertMatchedBinaryBraces(Project project, Editor editor, PsiFile file) {
        if (!(file instanceof MathematicaPsiFile)) return;

        PsiDocumentManager.getInstance(project).commitAllDocuments();

        FileType fileType = file.getFileType();
        int offset = editor.getCaretModel().getOffset();
        HighlighterIterator iterator = ((EditorEx) editor).getHighlighter().createIterator(offset);
        boolean atEndOfDocument = offset == editor.getDocument().getTextLength();

        if (!atEndOfDocument) iterator.retreat();
        if (iterator.atEnd()) return;
        BraceMatcher braceMatcher = BraceMatchingUtil.getBraceMatcher(fileType, iterator);
        if (iterator.atEnd()) return;
        IElementType braceTokenType = iterator.getTokenType();
        final CharSequence fileText = editor.getDocument().getCharsSequence();
        if (!braceMatcher.isLBraceToken(iterator, fileText, fileType)) return;

        if (!iterator.atEnd()) {
            iterator.advance();

            if (!iterator.atEnd()) {
                if (!BraceMatchingUtil.isPairedBracesAllowedBeforeTypeInFileType(braceTokenType, iterator.getTokenType(), fileType)) {
                    return;
                }
                if (BraceMatchingUtil.isLBraceToken(iterator, fileText, fileType)) {
                    return;
                }
            }

            iterator.retreat();
        }

        int lparenOffset = BraceMatchingUtil.findLeftmostLParen(iterator, braceTokenType, fileText, fileType);
        if (lparenOffset < 0) lparenOffset = 0;

        iterator = ((EditorEx) editor).getHighlighter().createIterator(lparenOffset);

        if (!BraceMatchingUtil.matchBrace(fileText, fileType, iterator, true, true)) {
            editor.getDocument().insertString(offset, ">>");
        }
    }
}