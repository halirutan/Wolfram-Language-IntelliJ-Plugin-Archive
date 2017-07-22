package de.halirutan.mathematica.actions;

import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.Indent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.formatter.common.AbstractBlock;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import de.halirutan.mathematica.codeinsight.formatter.AbstractMathematicaBlock;
import de.halirutan.mathematica.codeinsight.formatter.MathematicaFormattingModelBuilder;

import javax.swing.*;
import java.util.List;

/**
 * @author patrick (11/11/13)
 */
public class ShowFormattingBlocks extends AnAction {
  private static String printBlock(AbstractBlock block, String text) {
    String result = "";
    final TextRange range = block.getTextRange();
    result += block.getNode() + "(" + text.substring(range.getStartOffset(), Math.min(range.getStartOffset() + 3, range.getEndOffset())) +
        "..." + text.substring(Math.max(range.getEndOffset() - 3, range.getStartOffset()), range.getEndOffset()) + range +
        ")" + printIndent(block.getIndent()) + " " + block.getAlignment() + "\n";
    final List<Block> subBlocks = block.getSubBlocks();
    for (Block subBlock : subBlocks) {
      result += printBlock((AbstractMathematicaBlock) subBlock, text);
    }
    return result;
  }

  private static String printIndent(Indent indent) {
    return indent.toString();
  }

  public void actionPerformed(AnActionEvent event) {
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    Project project = event.getData(PlatformDataKeys.PROJECT);

    DialogBuilder dialogBuilder = new DialogBuilder(project);
    dialogBuilder.setTitle("Formatting Block structure");

    final String text = editor != null ? editor.getDocument().getText() : "";
    final CodeStyleSettings settings = CodeStyleSettingsManager.getInstance(project).getCurrentSettings();
    MathematicaFormattingModelBuilder modelBuilder = new MathematicaFormattingModelBuilder();
    final PsiFile file = PsiFileFactory.getInstance(project).createFileFromText("a.m", MathematicaLanguage.INSTANCE, text);
    final FormattingModel model = modelBuilder.createModel(file.getNode().getPsi(), settings);
    final Block rootBlock = model.getRootBlock();
    final String blockText = printBlock((AbstractBlock) rootBlock, text);

    JTextArea textArea = new JTextArea(blockText);
    final JScrollPane pane = new JScrollPane(textArea);
    dialogBuilder.setCenterPanel(pane);
    dialogBuilder.show();
  }
}
