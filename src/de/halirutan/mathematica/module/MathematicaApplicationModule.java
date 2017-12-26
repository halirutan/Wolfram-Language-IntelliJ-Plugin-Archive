package de.halirutan.mathematica.module;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.file.MathematicaFileTemplateProvider;
import de.halirutan.mathematica.file.MathematicaTemplateProperties;

/**
 * A module builder that creates a package structure containing a separate folder for the source code. Under this folder
 * it creates PacletInfo.m, package and notebook file and a Kernel directory with an init.m
 */
public class MathematicaApplicationModule extends MathematicaModuleBuilder {

  private static final Logger LOG = Logger.getInstance("#de.halirutan.mathematica.module.MathematicaApplicationModule");

  @SuppressWarnings("WeakerAccess")
  public MathematicaApplicationModule() {
  }

  @Override
  public String getPresentableName() {
    return MathematicaBundle.message("project.template.application");
  }

  @Override
  public String getDescription() {
    return MathematicaBundle.message("project.template.application.description");
  }

  @Override
  protected void createModuleStructure(Project project, VirtualFile contentRoot, MathematicaTemplateProperties properties) {
    try {
      createKernelFiles(project, contentRoot, properties);
      MathematicaFileTemplateProvider.createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.PACKAGE,
          properties.getProperties().getProperty(MathematicaTemplateProperties.PACKAGE_NAME),
          properties.getProperties());
      createPacletInfoFile(project, contentRoot);
    } catch (Exception exception) {
      LOG.warn("Could not create template package file.", exception);
    }
  }

  @Override
  protected boolean isAvailable() {
    return true;
  }
}
