package de.halirutan.mathematica.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.MathematicaBundle;

/**
 * @author patrick (08.12.17).
 */
public class MathematicaApplicationModule extends MathematicaModuleBuilder {
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
