/*
 * Copyright (c) 2013 Patrick Scheibe
 *
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
