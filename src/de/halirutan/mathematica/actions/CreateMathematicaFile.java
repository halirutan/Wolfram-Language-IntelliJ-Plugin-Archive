/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import de.halirutan.mathematica.MathematicaIcons;

/**
 * @author patrick (4/8/13)
 */
public class CreateMathematicaFile extends CreateFileFromTemplateAction implements DumbAware {
  private static final String NEW_M_FILE = "New Mathematica file";

  public CreateMathematicaFile() {
    super(NEW_M_FILE, "Creates a new .m Mathematica package file", MathematicaIcons.FILE_ICON);
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
    builder.setTitle(NEW_M_FILE).addKind("Notebook", MathematicaIcons.FILE_ICON, "Notebook");
    builder.setTitle(NEW_M_FILE).addKind("Package", MathematicaIcons.FILE_ICON, "Package");
    builder.setTitle(NEW_M_FILE).addKind("Plain", MathematicaIcons.FILE_ICON, "Plain");
    builder.setTitle(NEW_M_FILE).addKind("Test", MathematicaIcons.FILE_ICON, "Test");
  }

  @Override
  protected String getActionName(PsiDirectory directory, String newName, String templateName) {
    return NEW_M_FILE;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CreateMathematicaFile;
  }

}
