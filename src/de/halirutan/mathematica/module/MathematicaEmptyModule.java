package de.halirutan.mathematica.module;

import de.halirutan.mathematica.MathematicaBundle;

/**
 * Module builder for an empty module that does not create any folder structure or files
 */
public class MathematicaEmptyModule extends MathematicaModuleBuilder {

  @SuppressWarnings("WeakerAccess")
  public MathematicaEmptyModule() {
  }

  @Override
  public String getPresentableName() {
    return MathematicaBundle.message("project.template.empty");
  }

  @Override
  public String getDescription() {
    return MathematicaBundle.message("project.template.empty.description");
  }

  @Override
  protected boolean isAvailable() {
    return true;
  }
}
