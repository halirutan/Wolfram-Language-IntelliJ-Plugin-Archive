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

package de.halirutan.mathematica.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.CharFilter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.file.MathematicaFileTemplateProvider;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import de.halirutan.mathematica.sdk.MathematicaSdkType;
import de.halirutan.mathematica.util.MathematicaIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The base-class for all builders that create a Mathematica module with varying content structure.
 * It is called when a new module is going to be created. This usually happens through the {@link ModuleType} but for
 * Mathematica we always use extensions of this class that are instantiated in {@link MathematicaProjectTemplatesFactory}.
 * @author patrick (4/8/13)
 */
public class MathematicaModuleBuilder extends ModuleBuilder {

  private static final Logger LOG = Logger.getLogger("#de.halirutan.mathematica.module.MathematicaModuleBuilder");

  private String myPackageName = null;
  private MathematicaLanguageLevel myLanguageLevel;

  MathematicaModuleBuilder() {
    myLanguageLevel = MathematicaLanguageLevel.HIGHEST;
  }

  public void setLanguageLevel(MathematicaLanguageLevel level) {
    myLanguageLevel = level;
  }

  /**
   * Called by IntelliJ when the module structure is constructed. We set up only the language level we obtained from
   * {@link MathematicaModifiedSettingsStep} and extract the name of the package. The rest of the work of creating a
   * directory structure and file templates is done in {@link #createModuleStructure(Project, VirtualFile)} that can be
   * overridden by classes extending from this.
   */
  @Override
  public void setupRootModel(final ModifiableRootModel rootModel) {
    ContentEntry contentEntry = doAddContentEntry(rootModel);
    final MathematicaLanguageLevelModuleExtensionImpl moduleExtension =
        rootModel.getModuleExtension(MathematicaLanguageLevelModuleExtensionImpl.class);
    moduleExtension.setMathematicaLanguageLevel(myLanguageLevel);
    myPackageName = StringUtil.strip(getName(), CharFilter.NOT_WHITESPACE_FILTER);
    if (contentEntry != null) {
      final File packageDir = new File(getContentEntryPath() + File.separator + myPackageName);
      //noinspection ResultOfMethodCallIgnored
      packageDir.mkdirs();
      final VirtualFile sourceRoot = LocalFileSystem.getInstance()
                                                    .refreshAndFindFileByPath(
                                                        FileUtil.toSystemIndependentName(packageDir.getAbsolutePath()));
      if (sourceRoot != null) {
        contentEntry.addSourceFolder(sourceRoot, false);
        final Project project = rootModel.getProject();
        StartupManager.getInstance(project).runWhenProjectIsInitialized(
            (DumbAwareRunnable) () -> ApplicationManager.getApplication().invokeLater(
                () -> ApplicationManager.getApplication()
                                        .runWriteAction(() -> createModuleStructure(project, sourceRoot))));
      }
    }
  }

  /**
   * Provides a callback where implementations can set up directory structure under the contentRoot
   *
   * @param project     Project to which the module is attached
   * @param contentRoot Content root that is the top-level folder of this module
   */
  protected void createModuleStructure(Project project, VirtualFile contentRoot) {
  }

  /**
   * Filters out all other SDK's than valid Mathematica SDK's
   */
  @Override
  public boolean isSuitableSdkType(SdkTypeId sdkType) {
    return sdkType instanceof MathematicaSdkType;
  }

  /**
   * Template method that can be used by implementations of this class to create a Kernel dir and an init.m
   */
  void createKernelFiles(Project project, VirtualFile contentRoot) {
    try {
      final VirtualFile kernelRoot = contentRoot.createChildDirectory(this, "Kernel");
      MathematicaFileTemplateProvider.createFromTemplate(project, kernelRoot, MathematicaFileTemplateProvider.INIT,
          "init");
    } catch (Exception ignored) {
      LOG.warning("Could not create Kernel files and init.m");
    }
  }

  /**
   * Template method that can be used by implementations of this class to create a package file and a notebook.
   */
  void createProjectFiles(Project project, VirtualFile contentRoot) {
    //Create a .m and .nb file with the project's name
    try {
      MathematicaFileTemplateProvider.createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.PACKAGE,
          myPackageName);
      MathematicaFileTemplateProvider.createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.NOTEBOOK,
          myPackageName);
    } catch (Exception ignored) {
      LOG.warning("Could not create project files");
    }
  }

  /**
   * Template method that can be used by implementations of this class to create a PacletInfo.m
   */
  void createPacletInfoFile(Project project, VirtualFile contentRoot) {
    try {
      Properties props = new Properties();
      props.put("MathematicaVersion", myLanguageLevel.getName() + "+");
      props.put("MathematicaContext", myPackageName);

      MathematicaFileTemplateProvider
          .createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.PACLET_INFO, "PacletInfo.m", props);
    } catch (Exception ignored) {
      LOG.warning("Could not create PacletInfo.m");
    }
  }

  @Override
  public String getGroupName() {
    return MathematicaBundle.message("project.template.group.name");
  }

  @Override
  public String getPresentableName() {
    return getGroupName();
  }

  @Override
  public boolean isTemplateBased() {
    return true;
  }

  /**
   * This base-class should not be available to construct modules. Instead, use one of the implementing classes.
   * @return Always false
   */
  @Override
  protected boolean isAvailable() {
    return false;
  }

  @Nullable
  @Override
  public ModuleWizardStep modifySettingsStep(@NotNull final SettingsStep settingsStep) {
    return new MathematicaModifiedSettingsStep(this, settingsStep);
  }

  @Override
  public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep) {
    return new MathematicaModifiedSettingsStep(this, settingsStep);
  }

  @Override
  public Icon getNodeIcon() {
    return MathematicaIcons.FILE_ICON;
  }


  @Override
  public ModuleType getModuleType() {
    return MathematicaModuleType.getInstance();
  }

}
