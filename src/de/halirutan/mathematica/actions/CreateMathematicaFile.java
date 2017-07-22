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

package de.halirutan.mathematica.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog.Builder;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.util.MathematicaIcons;

import java.io.File;

/**
 * Provides the creation of new Mathematica files through the IDEA  <em >new...</em> action.
 *
 * @author patrick (4/8/13)
 */
public class CreateMathematicaFile extends CreateFileFromTemplateAction implements DumbAware {
  private static final String NEW_M_FILE = "New Mathematica file";

  public CreateMathematicaFile() {
    super(NEW_M_FILE, "Creates a new .m Mathematica package file", MathematicaIcons.FILE_ICON);
  }

  /**
   * This is stolen from here {@link http://stackoverflow.com/a/990492/1078614}
   *
   * @param s
   *     filename with possible extension
   * @return filename without extension
   */
  private static String removeExtension(String s) {

    String separator = File.separator;
    String filename;

    // Remove the path up to the filename.
    int lastSeparatorIndex = s.lastIndexOf(separator);
    if (lastSeparatorIndex == -1) {
      filename = s;
    } else {
      filename = s.substring(lastSeparatorIndex + 1);
    }

    // Remove the extension.
    int extensionIndex = filename.lastIndexOf(".");
    if (extensionIndex == -1)
      return filename;

    return filename.substring(0, extensionIndex);
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, Builder builder) {
    builder.setTitle(NEW_M_FILE).addKind("Package", MathematicaIcons.FILE_ICON, "Package");
    builder.setTitle(NEW_M_FILE).addKind("Plain", MathematicaIcons.FILE_ICON, "Plain");
    builder.setTitle(NEW_M_FILE).addKind("Test", MathematicaIcons.FILE_ICON, "Test");
    builder.setTitle(NEW_M_FILE).addKind("Notebook", MathematicaIcons.FILE_ICON, "Notebook");
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

  @Override
  protected PsiFile createFile(String name, String templateName, PsiDirectory dir) {
    final FileTemplate template = FileTemplateManager.getInstance(dir.getProject()).getInternalTemplate(templateName);
    String fileName = removeExtension(name);
    return createFileFromTemplate(fileName, template, dir);
  }

}
