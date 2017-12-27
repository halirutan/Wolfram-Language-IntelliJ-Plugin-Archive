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

package de.halirutan.mathematica.codeinsight.editoractions.enter;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provide the ability to press enter inside function calls to move the closing bracket even one line further down and
 * indenting the cursor in the middle correctly.
 *
 * @author patrick (11/12/13)
 */
public class MathematicaEnterInsideFunctionHandler extends EnterHandlerDelegateAdapter {

  private final static TokenSet separators = TokenSet.create(
      MathematicaElementTypes.COMMA,
      MathematicaElementTypes.SEMICOLON
  );

  private Result skipWithResultQ(@NotNull final PsiFile file, @NotNull final Editor editor, @NotNull final DataContext dataContext) {
    final Project project = CommonDataKeys.PROJECT.getData(dataContext);
    if (project == null) {
      return Result.Continue;
    }

    if (!file.getViewProvider().getLanguages().contains(MathematicaLanguage.INSTANCE)) {
      return Result.Continue;
    }

    if (editor.isViewer()) {
      return Result.Continue;
    }

    final Document document = editor.getDocument();
    if (!document.isWritable()) {
      return Result.Continue;
    }

    if (!CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
      return Result.Continue;
    }

    PsiDocumentManager.getInstance(project).commitDocument(document);

    int caret = editor.getCaretModel().getOffset();
    if (caret == 0) {
      return Result.DefaultSkipIndent;
    }
    if (caret <= 0) {
      return Result.Continue;
    }
    return null;
  }

  @Override
  public Result preprocessEnter(@NotNull final PsiFile file,
                                @NotNull final Editor editor,
                                @NotNull final Ref<Integer> caretOffset,
                                @NotNull final Ref<Integer> caretAdvance,
                                @NotNull final DataContext dataContext,
                                final EditorActionHandler originalHandler) {

    final Result result = skipWithResultQ(file, editor, dataContext);
    if (result != null) {
      return result;
    }

    Document document = editor.getDocument();
    final Project project = editor.getProject();


    PsiElement atCaret = file.findElementAt(caretOffset.get());
    if (atCaret == null) {
      return Result.Continue;
    }

    if (!(atCaret.getParent() instanceof FunctionCall && atCaret.getNode().getElementType() == MathematicaElementTypes.RIGHT_BRACKET)) {
      return Result.Continue;
    }

    final int offset = caretOffset.get();
    final int lineNumber = document.getLineNumber(offset);
    final int lineStartOffset = document.getLineStartOffset(lineNumber);
    final int prevLineStartOffset = lineNumber > 0 ? document.getLineStartOffset(lineNumber - 1) : lineStartOffset;

    final EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
    final HighlighterIterator iterator = highlighter.createIterator(caretOffset.get() - 1);
    final IElementType type = getNonWhitespaceElementType(iterator, lineStartOffset, prevLineStartOffset);

    if (!separators.contains(type)) {
      return Result.Continue;
    }

    originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);
    if (project != null) {
      CodeStyleManager.getInstance(project).adjustLineIndent(editor.getDocument(), caretOffset.get() + 1);
    }
    PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
    return Result.DefaultForceIndent;
  }

  @Nullable
  private IElementType getNonWhitespaceElementType(final HighlighterIterator iterator, int curLineStart, final int prevLineStartOffset) {
    while (!iterator.atEnd() && iterator.getEnd() >= curLineStart && iterator.getStart() >= prevLineStartOffset) {
      final IElementType tokenType = iterator.getTokenType();
      if (!MathematicaElementTypes.WHITE_SPACE_OR_COMMENTS.contains(tokenType)) {
        return tokenType;
      }
      iterator.retreat();
    }
    return null;
  }

}
