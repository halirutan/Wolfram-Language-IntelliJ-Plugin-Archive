package de.halirutan.mathematica.codeInsight.editor;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import com.intellij.platform.templates.BuilderBasedTemplate;
import de.halirutan.mathematica.MathematicaIcons;
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
    public static final String APPLICATION_MODULE = "Application Project";
    public static final String EMPTY_MODULE = "Empty Module";
    public static final String TEST_MODULE = "Test Module";
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
        ProjectTemplate[] templates = {
                new BuilderBasedTemplate(new MathematicaModuleBuilder()),

                new MathematicaProjectTemplate(BASIC_MODULE,
                        "Basic Mathematica package",
                        new MathematicaModuleBuilder.Basic()),

                new MathematicaProjectTemplate(APPLICATION_MODULE,
                        "Mathematica application project",
                        new MathematicaModuleBuilder.Application()),

                new MathematicaProjectTemplate(EMPTY_MODULE,
                        "Empty Mathematica project",
                        new MathematicaModuleBuilder.Empty())
        };

        return templates;
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
