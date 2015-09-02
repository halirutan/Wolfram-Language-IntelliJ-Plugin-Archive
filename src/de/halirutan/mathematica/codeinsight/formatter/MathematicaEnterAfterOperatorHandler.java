/*
 * Copyright (c) 2015 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.formatter;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.MathematicaLanguage;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (11/12/13)
 */
public class MathematicaEnterAfterOperatorHandler extends EnterHandlerDelegateAdapter {

  @SuppressWarnings("deprecation")
  @Override
  public Result preprocessEnter(@NotNull final PsiFile file,
                                @NotNull final Editor editor,
                                @NotNull final Ref<Integer> caretOffset,
                                @NotNull final Ref<Integer> caretAdvance,
                                @NotNull final DataContext dataContext,
                                final EditorActionHandler originalHandler) {

    Result res = skipWithResultQ(file, editor, dataContext);
    if (res != null) {
      return res;
    }

    Document document = editor.getDocument();
    final Project project = editor.getProject();

    final int offset = caretOffset.get();
    final int lineNumber = document.getLineNumber(offset);
    final int lineStartOffset = document.getLineStartOffset(lineNumber);
    final int prevLineStartOffset = lineNumber > 0 ? document.getLineStartOffset(lineNumber - 1) : lineStartOffset;

    if (project == null || offset <= 0) {
      return Result.Continue;
    }

    final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();
    final HighlighterIterator iterator = highlighter.createIterator(caretOffset.get() - 1);
    final IElementType type = getNonWhitespaceElementType(iterator, lineStartOffset, prevLineStartOffset);

    if (MathematicaElementTypes.INDENTABLE_OPERATORS.contains(type)) {
      final CodeStyleSettings currentSettings = CodeStyleSettingsManager.getInstance(project).getCurrentSettings();
      final int contIndentSize = currentSettings.getIndentOptionsByFile(file).CONTINUATION_INDENT_SIZE;

      final int indentInPrevLine = EditorActionUtil.findFirstNonSpaceColumnOnTheLine(editor, lineNumber);

      final String newIndent = StringUtil.repeatSymbol(' ', contIndentSize + Math.max(0,indentInPrevLine));
      EditorModificationUtil.insertStringAtCaret(editor, "\n" + newIndent);
      PsiDocumentManager.getInstance(project).commitDocument(document);
      return Result.Stop;
    }
    return Result.Continue;
  }


  @Nullable
  protected IElementType getNonWhitespaceElementType(final HighlighterIterator iterator, int curLineStart, final int prevLineStartOffset) {
    while (!iterator.atEnd() && iterator.getEnd() >= curLineStart && iterator.getStart() >= prevLineStartOffset) {
      final IElementType tokenType = iterator.getTokenType();
      if (!MathematicaElementTypes.WHITE_SPACE_OR_COMMENTS.contains(tokenType)) {
        return tokenType;
      }
      iterator.retreat();
    }
    return null;
  }

  protected Result skipWithResultQ(@NotNull final PsiFile file, @NotNull final Editor editor, @NotNull final DataContext dataContext) {
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


}
