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

package de.halirutan.mathematica.projectsAndModules;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import de.halirutan.mathematica.MathematicaFileTemplateProvider;
import de.halirutan.mathematica.MathematicaIcons;
import de.halirutan.mathematica.actions.CreateMathematicaFile;
import de.halirutan.mathematica.projectsAndModules.ProjectType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;

/**
 * @author patrick (4/8/13)
 */
public class MathematicaModuleBuilder extends JavaModuleBuilder {

    /**
     * Additions by rsmenon (5/6/13)
     * Will need some more reworking to implement checkboxes for Test/Documentation.
     */

    private String PROJECT_NAME;
    private final ProjectType PROJECT_TYPE;

    public MathematicaModuleBuilder(ProjectType type) {
        PROJECT_TYPE = type;
    }

    public void setupRootModel(final ModifiableRootModel rootModel) throws ConfigurationException {
        super.setupRootModel(rootModel);

        VirtualFile[] files = rootModel.getContentRoots();
        if (files.length > 0) {
            final VirtualFile contentRoot = files[0];

            final Project project = rootModel.getProject();
            PROJECT_NAME = project.getName();

            StartupManager.getInstance(project).runWhenProjectIsInitialized(new DumbAwareRunnable() {
                public void run() {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        public void run() {
                            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                public void run() {
                                    createProject(project, contentRoot);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void createProject(Project project, VirtualFile contentRoot) {

        switch (PROJECT_TYPE) {
            case APPLICATION:
                createKernelFiles(project, contentRoot);
                createProjectFiles(project, contentRoot);

            case BASIC:
                createProjectFiles(project, contentRoot);

            default:

        }
    }

    private void createKernelFiles(Project project, VirtualFile contentRoot) {
        try {
            final VirtualFile kernelRoot = contentRoot.createChildDirectory(this, "Kernel");
            MathematicaFileTemplateProvider.createFromTemplate(project, kernelRoot, MathematicaFileTemplateProvider.INIT, "init");
        }

        catch (IOException e) {
        }
        catch (Exception e) {
        }
    }

    private void createProjectFiles(Project project, VirtualFile contentRoot) {
        //Create a .m and .nb file with the project's name
        try {
            MathematicaFileTemplateProvider.createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.PACKAGE, PROJECT_NAME);
            MathematicaFileTemplateProvider.createFromTemplate(project, contentRoot, MathematicaFileTemplateProvider.NOTEBOOK, PROJECT_NAME);
        }

        catch (Exception e) {

        }
    }

    @Nullable
    @Override
    public ModuleWizardStep modifySettingsStep(final SettingsStep settingsStep) {
       return new MathematicaModifiedSettingsStep(this, settingsStep);
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

    public static class Test extends MathematicaModuleBuilder {
        public Test() {
            super(ProjectType.TEST);
        }

        @Override
        public String getBuilderId() {
            return "mathematica.test";
        }

        @Override
        public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext,
                                                    ModulesProvider modulesProvider) {
            return ModuleWizardStep.EMPTY_ARRAY;
        }

    }

    public static class Documentation extends MathematicaModuleBuilder {
        public Documentation() {
            super(ProjectType.DOCUMENTATION);
        }

        @Override
        public String getBuilderId() {
            return "mathematica.documentation";
        }

        @Override
        public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext,
                                                    ModulesProvider modulesProvider) {
            return ModuleWizardStep.EMPTY_ARRAY;
        }

    }

    @Override
    public Icon getBigIcon() {
        return MathematicaIcons.FILE_ICON;
    }

    @Override
    public Icon getNodeIcon() {
        return MathematicaIcons.FILE_ICON;
    }
}
