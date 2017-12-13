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

import com.intellij.ide.util.newProjectWizard.SourcePathsStep;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.util.MathematicaIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author patrick (4/8/13)
 */

public class MathematicaModuleType extends ModuleType<MathematicaModuleBuilder> {
  private static final String MATHEMATICA_TYPE_ID = "MATHEMATICA_MODULE";

  public MathematicaModuleType() {
    super(MATHEMATICA_TYPE_ID);
  }

  public static MathematicaModuleType getInstance() {
    return (MathematicaModuleType) ModuleTypeManager.getInstance().findByID(MATHEMATICA_TYPE_ID);
  }

  @NotNull
  @Override
  public String getName() {
    return MathematicaBundle.message("module.type.name");
  }

  @NotNull
  @Override
  public String getDescription() {
    return MathematicaBundle.message("module.type.description");
  }

  @NotNull
  @Override
  public MathematicaModuleBuilder createModuleBuilder() {
    return new MathematicaModuleBuilder();
  }

  @Override
  public Icon getNodeIcon(@Deprecated boolean isOpened) {
    return MathematicaIcons.FILE_ICON;
  }

  @NotNull
  @Override
  public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull MathematicaModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
    return new ModuleWizardStep[]{
        new SourcePathsStep(moduleBuilder, MathematicaIcons.SET_DELAYED_ICON, getId())
    };
  }
}
