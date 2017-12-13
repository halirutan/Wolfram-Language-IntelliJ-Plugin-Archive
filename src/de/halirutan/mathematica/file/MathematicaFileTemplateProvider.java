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

package de.halirutan.mathematica.file;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

/**
 * @author rsmenon (5/13/13)
 */
public class MathematicaFileTemplateProvider {
  @NonNls
  public static final String PACKAGE = "Package.m";

  @NonNls
  public static final String PACLET_INFO = "PacletInfo.m";

  @NonNls
  public static final String NOTEBOOK = "Notebook.nb";

  @NonNls
  public static final String PLAIN = "Plain.m";

  @NonNls
  public static final String TEST = "Test.mt";

  @NonNls
  public static final String INIT = "init.m";


  @Nullable
  public static PsiElement createFromTemplate(@NotNull Project project,
                                              @NotNull VirtualFile rootDir,
                                              @NotNull String templateName,
                                              @NotNull String fileName,
                                              @NotNull Properties properties) throws Exception {
    rootDir.refresh(false, false);
    PsiDirectory directory = PsiManager.getInstance(project).findDirectory(rootDir);
    if (directory != null) {
      return createFromTemplate(project, templateName, fileName, directory, properties);
    }
    return null;
  }

  @Nullable
  public static PsiElement createFromTemplate(@NotNull Project project,
                                              @NotNull VirtualFile rootDir,
                                              @NotNull String templateName,
                                              @NotNull String fileName) throws Exception {
    return createFromTemplate(project, rootDir, templateName, fileName, FileTemplateManager.getInstance(project).getDefaultProperties());
  }

  private static PsiElement createFromTemplate(final Project project, String templateName, String fileName, @NotNull PsiDirectory directory, Properties properties)
      throws Exception {
    FileTemplateManager manager = FileTemplateManager.getInstance(project);
    FileTemplate template = manager.getInternalTemplate(templateName);
    return FileTemplateUtil.createFromTemplate(template, fileName, properties, directory);
  }

}
