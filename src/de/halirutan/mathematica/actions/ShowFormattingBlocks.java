/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.actions;

import com.intellij.application.options.CodeStyle;
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
import com.intellij.psi.formatter.common.AbstractBlock;
import de.halirutan.mathematica.codeinsight.formatter.AbstractMathematicaBlock;
import de.halirutan.mathematica.codeinsight.formatter.MathematicaFormattingModelBuilder;
import de.halirutan.mathematica.lang.MathematicaLanguage;

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
    final CodeStyleSettings settings = CodeStyle.getDefaultSettings();
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
