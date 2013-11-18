package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.CodeDocumentationUtil;
import com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (11/12/13)
 */
public class MathematicaEnterBetweenBracesHandler extends EnterBetweenBracesHandler {

  private static final Logger LOG = Logger.getInstance("#com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesHandler");

  @Override
  public Result preprocessEnter(@NotNull final PsiFile file, @NotNull final Editor editor, @NotNull final Ref<Integer> caretOffsetRef, @NotNull final Ref<Integer> caretAdvance,
                                @NotNull final DataContext dataContext, final EditorActionHandler originalHandler) {
    Document document = editor.getDocument();
    CharSequence text = document.getCharsSequence();
    int caretOffset = caretOffsetRef.get();
    if (!CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
      return Result.Continue;
    }

    if (caretOffset <= 0 || caretOffset >= text.length() || !isBracePair(text.charAt(caretOffset - 1), text.charAt(caretOffset))) {
      return Result.Continue;
    }

    final int line = document.getLineNumber(caretOffset);
    final int start = document.getLineStartOffset(line);
//    final CodeDocumentationUtil.CommentContext commentContext =
//        CodeDocumentationUtil.tryParseCommentContext(file, text, caretOffset, start);
//
//    // special case: enter inside "()" or "{}"
//    String indentInsideJavadoc = commentContext.docAsterisk
//        ? CodeDocumentationUtil.getIndentInsideJavadoc(document, caretOffset)
//        : null;

    originalHandler.execute(editor, dataContext);

//    Project project = editor.getProject();
//    if (indentInsideJavadoc != null && project != null && CodeStyleSettingsManager.getSettings(project).JD_LEADING_ASTERISKS_ARE_ENABLED) {
//      document.insertString(editor.getCaretModel().getOffset(), "*" + indentInsideJavadoc);
//    }

    PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
    try {
      CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, editor.getCaretModel().getOffset());
    } catch (IncorrectOperationException e) {
      LOG.error(e);
    }
    return Result.Continue;
  }


  protected boolean isBracePair(char c1, char c2) {
    return (c1 == '(' && c2 == ')') || (c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']');
  }
}
