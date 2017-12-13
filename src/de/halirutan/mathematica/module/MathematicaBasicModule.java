package de.halirutan.mathematica.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import de.halirutan.mathematica.MathematicaBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (08.12.17).
 */
public class MathematicaBasicModule extends MathematicaModuleBuilder {
  public MathematicaBasicModule() {
  }

  @Override
  public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
    return ModuleWizardStep.EMPTY_ARRAY;
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
