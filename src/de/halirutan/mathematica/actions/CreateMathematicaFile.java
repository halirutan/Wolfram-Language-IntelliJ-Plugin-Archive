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
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.file.MathematicaFileType;
import de.halirutan.mathematica.file.MathematicaTemplateProperties;
import de.halirutan.mathematica.module.MathematicaLanguageLevelModuleExtension;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import de.halirutan.mathematica.sdk.MathematicaSdkType;
import de.halirutan.mathematica.util.MathematicaIcons;

import java.util.Properties;

/**
 * Provides the creation of new Mathematica files through the IDEA  <em >new...</em> action.
 * <p>
 * TODO: This should be reworked.
 * The global string identifier for the templates should use {@link de.halirutan.mathematica.file.MathematicaFileTemplateProvider}
 * instead. Furthermore, we don't use the framework of {@link CreateFileFromTemplateAction} which is ugly as shit.
 * Instead, the variables we set inside the file template could be live templates that are active as soon as the file
 * is created and we only fill it with the default values. This gives the user the ability to fix settings. Not a pressing
 * matter so I'll leave it for now.
 *
 * @author patrick (4/8/13)
 */
public class CreateMathematicaFile extends CreateFileFromTemplateAction implements DumbAware {
  private static final String NEW_M_FILE = "New Mathematica file";

  private static final String PACKAGE = "Package";
  private static final String PLAIN = "Plain";
  private static final String TEST = "Test";
  private static final String NOTEBOOK = "Notebook";
  private Project myProject = null;

  public CreateMathematicaFile() {
    super(NEW_M_FILE, "Creates a new .m Mathematica package file", MathematicaIcons.FILE_ICON);
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, Builder builder) {
    myProject = project;
    final MyNameValidator nameValidator = new MyNameValidator(MathematicaFileType.DEFAULT_EXTENSIONS);
    builder.setTitle(NEW_M_FILE).addKind(PACKAGE, MathematicaIcons.FILE_ICON, PACKAGE);
    builder.setTitle(NEW_M_FILE).addKind(PLAIN, MathematicaIcons.FILE_ICON, PLAIN);
    builder.setTitle(NEW_M_FILE).addKind(TEST, MathematicaIcons.FILE_ICON, TEST);
    builder.setTitle(NEW_M_FILE).addKind(NOTEBOOK, MathematicaIcons.FILE_ICON, NOTEBOOK);
    builder.setValidator(nameValidator);
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
    MathematicaLanguageLevel version = null;
    final String fileWithoutExtension = StringUtil.trimExtensions(name);
    final Module module = ModuleUtilCore.findModuleForFile(dir.getVirtualFile(), myProject);
    if (module != null) {
      final MathematicaLanguageLevelModuleExtension languageLevelModuleExtension =
          MathematicaLanguageLevelModuleExtension.getInstance(module);
      if (languageLevelModuleExtension != null) {
        version = languageLevelModuleExtension.getMathematicaLanguageLevel();
      } else {
        final Sdk projectSdk = ProjectRootManager.getInstance(myProject).getProjectSdk();
        if (projectSdk instanceof MathematicaSdkType) {
          version = MathematicaLanguageLevel.createFromSdk(projectSdk);
        }
      }
    }
    if (version == null) {
      version = MathematicaLanguageLevel.HIGHEST;
    }

    MathematicaTemplateProperties props = MathematicaTemplateProperties.create();
    props.setProperty(MathematicaTemplateProperties.MATHEMATICA_VERSION, version.getName());
    props.setProperty(MathematicaTemplateProperties.CONTEXT, fileWithoutExtension + "`");
    props.setProperty(MathematicaTemplateProperties.PACKAGE_NAME, fileWithoutExtension);
    props.setProperty(MathematicaTemplateProperties.PACKAGE_VERSION, "0.1");

    final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance(myProject);
    final Properties defaultProperties = fileTemplateManager.getDefaultProperties();
    defaultProperties.putAll(props.getProperties());

    final FileTemplate template = fileTemplateManager.getInternalTemplate(templateName);
    try {
      final PsiElement psiElement = FileTemplateUtil.createFromTemplate(template, name, defaultProperties, dir);
      if (psiElement instanceof PsiFile) {
        return (PsiFile) psiElement;
      }
    } catch (Exception e) {
      LOG.error("Error while creating new file", e);
    }
    LOG.error("Could not create file");
    return null;
  }

  /**
   * Provides a simple check for file extension
   */
  private class MyNameValidator implements InputValidator {

    private final String[] myExtensions;

    MyNameValidator(String[] myExtensions) {
      this.myExtensions = myExtensions;
    }

    @Override
    public boolean checkInput(String inputString) {
      return inputString != null && hasValidFileExtension(inputString, myExtensions);
    }

    private boolean hasValidFileExtension(String input, String... extensions) {
      if (FileUtilRt.getNameWithoutExtension(input).equals(input)) {
        return true;
      }
      for (String ext : extensions) {
        if (FileUtilRt.extensionEquals(input, ext)) return true;
      }
      return false;
    }

    @Override
    public boolean canClose(String inputString) {
      return checkInput(inputString);
    }
  }


}
