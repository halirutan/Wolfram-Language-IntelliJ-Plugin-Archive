package de.halirutan.mathematica.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.MathematicaBundle;

/**
 * A module builder that creates a package structure containing a separate folder for the source code. Under this folder
 * it creates PacletInfo.m, package and notebook file and a Kernel directory with an init.m
 */
public class MathematicaApplicationModule extends MathematicaModuleBuilder {

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
  protected void createModuleStructure(Project project, VirtualFile contentRoot) {
    createKernelFiles(project, contentRoot);
    createProjectFiles(project, contentRoot);
    createPacletInfoFile(project, contentRoot);
  }

  @Override
  protected boolean isAvailable() {
    return true;
  }
}
