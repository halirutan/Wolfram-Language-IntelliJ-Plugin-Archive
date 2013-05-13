package de.halirutan.mathematica.projectsAndModules;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import com.intellij.platform.templates.BuilderBasedTemplate;
import de.halirutan.mathematica.MathematicaIcons;
import de.halirutan.mathematica.projectsAndModules.MathematicaModuleBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * User: rsmenon
 * Date: 5/6/13
 * Time: 4:42 PM
 */

public class MathematicaProjectTemplatesFactory extends ProjectTemplatesFactory {

    public static final String MATHEMATICA = "Mathematica";
    public static final String BASIC_MODULE = "Basic Package";
    public static final String APPLICATION_MODULE = "Mathematica Application";
    public static final String TEST_MODULE = "Unit Test Module";
    public static final String DOCUMENTATION_MODULE = "Documentation Module";

    @NotNull
    @Override
    public String[] getGroups() {
        return new String[] {MATHEMATICA};
    }

    @Override
    public Icon getGroupIcon(String group) {
        return MathematicaIcons.FILE_ICON;
    }

    @NotNull
    @Override
    public ProjectTemplate[] createTemplates(String group, WizardContext context) {
        ProjectTemplate[] project_templates = {

                new MathematicaProjectTemplate(BASIC_MODULE,
                        "Basic Mathematica package",
                        new MathematicaModuleBuilder.Basic()),

                new MathematicaProjectTemplate(APPLICATION_MODULE,
                        "Mathematica application",
                        new MathematicaModuleBuilder.Application())
        };

        if (context.getProject() == null) {
            return project_templates;
        }
        else {
            ProjectTemplate[] module_templates = {

                    new MathematicaProjectTemplate(BASIC_MODULE,
                            "Basic Mathematica package",
                            new MathematicaModuleBuilder.Basic()),

                    new MathematicaProjectTemplate(TEST_MODULE,
                            "Unit test module",
                            new MathematicaModuleBuilder.Test()),

                    new MathematicaProjectTemplate(DOCUMENTATION_MODULE,
                            "Documentation module",
                            new MathematicaModuleBuilder.Documentation())
            };

            return module_templates;
        }
    }

    private static class MathematicaProjectTemplate extends BuilderBasedTemplate {

        private final String PROJECT_NAME;
        private final String PROJECT_DESCRIPTION;

        private MathematicaProjectTemplate(String name, String description, MathematicaModuleBuilder builder) {
            super(builder);
            PROJECT_NAME = name;
            PROJECT_DESCRIPTION = description;
        }

        @NotNull
        @Override
        public String getName() {
            return PROJECT_NAME;
        }

        @Nullable
        @Override
        public String getDescription() {
            return PROJECT_DESCRIPTION;
        }
    }

}
