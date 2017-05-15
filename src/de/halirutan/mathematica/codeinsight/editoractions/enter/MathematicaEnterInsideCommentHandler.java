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
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import de.halirutan.mathematica.MathematicaLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Provides the ability to press Enter inside comments and jump to the next line again being inside a new comment.
 * This is a very convenient functionality. I plan to implement two types, the first one leaves the comment-closing on
 * the line and makes a new (* *) on the next line. The second option is to actually move the comment-closing to the next
 * line and just put a '*' at the beginning.
 * <p>
 * In any way, we should try to be intelligent about the indenting done in the next line so that all comments are nicely
 * aligned. But this is future music since currently all comments are indented to the first column in each line.
 *
 * @author patrick (25/05/2017)
 */
public class MathematicaEnterInsideCommentHandler extends EnterHandlerDelegateAdapter {

  /**
   * This tests if we are in a valid Mathematica file and can operate on the document.
   *
   * @param file        the PsiFile we are editing
   * @param editor      the editor of this file
   * @param dataContext context
   * @return null if we should indeed take some action. Otherwise we do not know what to do and leave it to other
   * Enter handlers.
   */
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

    if (!(atCaret instanceof PsiComment)) {
      return Result.Continue;
    }

    final int offset = caretOffset.get();
    final int lineNumber = document.getLineNumber(offset);
    final int lineEndOffset = document.getLineEndOffset(lineNumber);

    // Test if the only thing that comes after the caret is the comment-closing with optional white space.
    // If someone is pressing Enter in the middle of a comment he just gets a usual Enter moving everything right to the
    // caret to the next line.
    final String textToEndOL = document.getText(TextRange.create(offset, lineEndOffset));
    if (!textToEndOL.matches("\\s*\\*\\)\\s*")) {
      return Result.Continue;
    }

    // Moving the caret to the end of the line, doing an Enter and inserting a new comment.
    editor.getCaretModel().moveToOffset(lineEndOffset);
    originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);
    EditorModificationUtil.insertStringAtCaret(editor, "(*  *)");
    EditorModificationUtil.moveCaretRelatively(editor, -3);
    if (project != null) {
      CodeStyleManager.getInstance(project).adjustLineIndent(editor.getDocument(), caretOffset.get() + 1);
    }
    return Result.Stop;
  }
}
