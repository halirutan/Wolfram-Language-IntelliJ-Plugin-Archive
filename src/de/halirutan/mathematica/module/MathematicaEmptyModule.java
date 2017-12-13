package de.halirutan.mathematica.module;

import de.halirutan.mathematica.MathematicaBundle;

/**
 * @author patrick (08.12.17).
 */
public class MathematicaEmptyModule extends MathematicaModuleBuilder {
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
