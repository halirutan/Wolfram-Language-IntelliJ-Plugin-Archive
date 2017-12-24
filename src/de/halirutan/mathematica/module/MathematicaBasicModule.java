package de.halirutan.mathematica.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.MathematicaBundle;

/**
 * A module builder that adds a simple package and a notebook file and nothing more
 */
public class MathematicaBasicModule extends MathematicaModuleBuilder {

  @SuppressWarnings("WeakerAccess")
  public MathematicaBasicModule() {
  }

  @Override
  protected void createModuleStructure(Project project, VirtualFile contentRoot) {
    createProjectFiles(project, contentRoot);
  }

  @Override
  public String getPresentableName() {
    return MathematicaBundle.message("project.template.basic");
  }

  @Override
  public String getDescription() {
    return MathematicaBundle.message("project.template.basic.description");
  }

  @Override
  protected boolean isAvailable() {
    return true;
  }
}
