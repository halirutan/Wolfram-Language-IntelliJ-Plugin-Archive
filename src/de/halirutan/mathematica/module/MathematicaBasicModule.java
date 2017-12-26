package de.halirutan.mathematica.module;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.file.MathematicaFileTemplateProvider;
import de.halirutan.mathematica.file.MathematicaTemplateProperties;

/**
 * A module builder that adds a simple package and a notebook file and nothing more
 */
public class MathematicaBasicModule extends MathematicaModuleBuilder {

  private static final Logger LOG = Logger.getInstance("#de.halirutan.mathematica.module.MathematicaBasicModule");

  @SuppressWarnings("WeakerAccess")
  public MathematicaBasicModule() {
  }

  @Override
  protected void createModuleStructure(Project project, VirtualFile contentRoot, MathematicaTemplateProperties properties) {
    String name = properties.getProperties().getProperty(MathematicaTemplateProperties.PACKAGE_NAME);
    try {
      MathematicaFileTemplateProvider.createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.PACKAGE,
          name, properties.getProperties());
      MathematicaFileTemplateProvider.createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.NOTEBOOK,
          name);
    } catch (Exception exception) {
      LOG.warn("Cannot create files for basic module.", exception);
    }
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
