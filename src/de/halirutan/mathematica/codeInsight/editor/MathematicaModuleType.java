/*
 * Mathematica Plugin for Jetbrains IDEA
 * Copyright (C) 2013 Patrick Scheibe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.halirutan.mathematica.codeInsight.editor;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.util.ArrayUtil;
import de.halirutan.mathematica.MathematicaIcons;

import javax.swing.*;
import java.util.ArrayList;

/**
 * @author patrick (4/8/13)
 */
public class MathematicaModuleType extends ModuleType<MathematicaModuleBuilder> {
    public static final String MATHEMATICA_TYPE_ID = "MATHEMATICA_MODULE";

    public MathematicaModuleType() {
        super(MATHEMATICA_TYPE_ID);
    }

    public static MathematicaModuleType getInstance() {
        return (MathematicaModuleType) ModuleTypeManager.getInstance().findByID(MATHEMATICA_TYPE_ID);
    }

    @Override
    public String getName() {
        return "Mathematica Module";
    }

    @Override
    public String getDescription() {
        return "Mathematica module for developing Mathematica packages";
    }

    @Override
    public Icon getBigIcon() {
        return MathematicaIcons.FILE_ICON;
    }

    @Override
    public MathematicaModuleBuilder createModuleBuilder() {
        return new MathematicaModuleBuilder();
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return MathematicaIcons.FILE_ICON;
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, MathematicaModuleBuilder moduleBuilder, ModulesProvider modulesProvider) {
        final ProjectWizardStepFactory wizFactory = ProjectWizardStepFactory.getInstance();
        ArrayList<ModuleWizardStep> steps = new ArrayList<ModuleWizardStep>();
        steps.add(wizFactory.createNameAndLocationStep(wizardContext));
        final ModuleWizardStep[] wizardSteps = steps.toArray(new ModuleWizardStep[steps.size()]);
        return ArrayUtil.mergeArrays(wizardSteps, super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider));
    }
}
