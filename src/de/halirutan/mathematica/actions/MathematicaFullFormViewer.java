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

import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.MathematicaNotification;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import de.halirutan.mathematica.lang.psi.util.MathematicaFullFormCreator;
import de.halirutan.mathematica.lang.psi.util.MathematicaPsiElementFactory;

/**
 * @author patrick (03.09.17).
 */
public class MathematicaFullFormViewer extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Editor editor = e.getData(CommonDataKeys.EDITOR);
    final Project project = e.getProject();
    assert project != null;
    final FileEditorManagerEx editorManagerEx = FileEditorManagerEx.getInstanceEx(project);
    final VirtualFile currentFile = editorManagerEx.getCurrentFile();
    if (currentFile == null) {
      MathematicaNotification.error(MathematicaBundle.message("fullform.viewer.no.file"));
      return;
    }
    final PsiFile psiFile = PsiManager.getInstance(project).findFile(currentFile);
    if (psiFile != null && MathematicaLanguage.INSTANCE.equals(psiFile.getLanguage())) {
      MathematicaFullFormCreator fullFormCreator = new MathematicaFullFormCreator();
      PsiElement expression = null;
      if (editor != null) {
        final SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
          final String selectedText = selectionModel.getSelectedText();
          if (selectedText != null) {
            MathematicaPsiElementFactory factory = new MathematicaPsiElementFactory(project);
            expression = factory.createDummyFile(selectedText);
          }
        } else {
          expression = psiFile;
        }
      }

      if (expression != null) {
        String fullForm = fullFormCreator.getFullForm(expression);

        final VirtualFile scratchFile = ScratchRootType.getInstance().createScratchFile(project, "FullForm.m", MathematicaLanguage.INSTANCE,
            fullForm);
        if (scratchFile != null) {
          editorManagerEx.openFile(scratchFile, true);
          WriteCommandAction.runWriteCommandAction(project,
              () -> {
                final PsiFile file = PsiManager.getInstance(project).findFile(scratchFile);
                assert file != null;
                CodeStyleManager.getInstance(project).reformat(file);
              }
              );
        }
      }

    }
  }
}

