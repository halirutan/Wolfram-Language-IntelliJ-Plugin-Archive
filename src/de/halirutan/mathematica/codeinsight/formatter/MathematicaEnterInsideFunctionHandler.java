package de.halirutan.mathematica.codeinsight.formatter;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlCodeStyleSettings;
import com.intellij.ui.docking.DockManager;
import de.halirutan.mathematica.codeinsight.formatter.settings.MathematicaCodeStyleSettings;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import org.jetbrains.annotations.NotNull;

/**
 * Provide the ability to press enter inside function calls to move the closing bracket even one line further
 * down and indenting the cursor in the middle correctly.
 * @author patrick (11/12/13)
 */
public class MathematicaEnterInsideFunctionHandler extends EnterHandlerDelegateAdapter {

  private static final Logger LOG = Logger.getInstance("#de.halirutan.mathematica.codeinsight.formatter.MathematicaEnterBetweenBracesHandler");


  @Override
  public Result preprocessEnter(@NotNull final PsiFile file,
                                @NotNull final Editor editor,
                                @NotNull final Ref<Integer> caretOffset,
                                @NotNull final Ref<Integer> caretAdvance,
                                @NotNull final DataContext dataContext,
                                final EditorActionHandler originalHandler) {
    final Project project = editor.getProject();

    if (project == null) {
      return Result.Continue;
    }


    if (!CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
      return Result.Continue;
    }

    PsiElement atCaret = file.findElementAt(caretOffset.get());
    if (atCaret == null) {
      return Result.Continue;
    }

    if (!(atCaret.getParent() instanceof FunctionCall && atCaret.getNode().getElementType() == MathematicaElementTypes.RIGHT_BRACKET)) {
      return Result.Continue;
    }
    originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);
    return Result.DefaultForceIndent;
  }

  @Override
  public Result postProcessEnter(@NotNull final PsiFile file, @NotNull final Editor editor, @NotNull final DataContext dataContext) {
    return super.postProcessEnter(file, editor, dataContext);
  }
}
