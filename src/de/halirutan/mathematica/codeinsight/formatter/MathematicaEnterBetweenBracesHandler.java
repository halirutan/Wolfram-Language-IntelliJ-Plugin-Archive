package de.halirutan.mathematica.codeinsight.formatter;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (11/12/13)
 */
public class MathematicaEnterBetweenBracesHandler extends EnterHandlerDelegateAdapter {

  private static final Logger LOG = Logger.getInstance("#com.intellij.codeinsight.editorActions.enter.EnterBetweenBracesHandler");

  @SuppressWarnings("deprecation")
  @Override
  public Result preprocessEnter(@NotNull final PsiFile file,
                                @NotNull final Editor editor,
                                @NotNull final Ref<Integer> caretOffset,
                                @NotNull final Ref<Integer> caretAdvance,
                                @NotNull final DataContext dataContext,
                                final EditorActionHandler originalHandler) {
    Document document = editor.getDocument();
    CharSequence text = document.getCharsSequence();
    int offset = caretOffset.get();
    if (!CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
      return Result.Continue;
    }

    if (offset <= 0 || offset >= text.length() || !isBracePair(text.charAt(offset - 1), text.charAt(offset))) {
      return Result.Continue;
    }


    originalHandler.execute(editor, dataContext);
    PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
    try {
      CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, editor.getCaretModel().getOffset());
    } catch (IncorrectOperationException e) {
      LOG.error(e);
    }
    return Result.Continue;
  }


  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  protected boolean isBracePair(char c1, char c2) {
    return (c1 == '(' && c2 == ')') || (c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']');
  }
}
