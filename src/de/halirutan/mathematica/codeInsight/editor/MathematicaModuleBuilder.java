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

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;

/**
 * @author patrick (4/8/13)
 */
public class MathematicaModuleBuilder extends JavaModuleBuilder {

    public MathematicaModuleBuilder() {
        this(ProjectType.APPLICATION);
    }

    @Override
    public ModuleType getModuleType() {
        return MathematicaModuleType.getInstance();
    }

    /**
     * Additions by rsmenon (5/6/13)
     * Will need rework to unify with the rest.
     */

    private String PROJECT_NAME;
    private final ProjectType PROJECT_TYPE;

    public MathematicaModuleBuilder(ProjectType type) {
        PROJECT_TYPE = type;
    }

    public static class Basic extends MathematicaModuleBuilder {
        public Basic() {
            super(ProjectType.BASIC);
        }

        @Override
        public String getBuilderId() {
            return "mathematica.basic";
        }

        @Override
        public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext,
                                                    ModulesProvider modulesProvider) {
            return ModuleWizardStep.EMPTY_ARRAY;
        }

    }

    public static class Application extends MathematicaModuleBuilder {
        public Application() {
            super(ProjectType.APPLICATION);
        }

        @Override
        public String getBuilderId() {
            return "mathematica.application";
        }

        @Override
        public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext,
                                                    ModulesProvider modulesProvider) {
            return ModuleWizardStep.EMPTY_ARRAY;
        }

    }

    public static class Empty extends MathematicaModuleBuilder {
        public Empty() {
            super(ProjectType.EMPTY);
        }

        @Override
        public String getBuilderId() {
            return "mathematica.empty";
        }

        @Override
        public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext,
                                                    ModulesProvider modulesProvider) {
            return ModuleWizardStep.EMPTY_ARRAY;
        }

    }

}
