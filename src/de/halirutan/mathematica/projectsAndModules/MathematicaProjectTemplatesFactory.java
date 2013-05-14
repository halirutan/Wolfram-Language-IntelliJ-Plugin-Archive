/*
 * Copyright (c) 2013 Patrick Scheibe
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
                        "A basic Mathematica package provides a simple package file and a notebook. " +
                                "Use this for simple or moderately sized Mathematica packages. ",
                                // The descriptions should be reworked when the plugin is more mature
                        new MathematicaModuleBuilder.Basic()),

                new MathematicaProjectTemplate(APPLICATION_MODULE,
                        "A Mathematica application is used for large packages and more complicated applications. ",
                        new MathematicaModuleBuilder.Application())
        };

        if (context.getProject() == null) {
            return project_templates;
        }
        else {
            return new ProjectTemplate[]{

                    new MathematicaProjectTemplate(TEST_MODULE,
                            "Unit test module",
                            new MathematicaModuleBuilder.Test()),

                    new MathematicaProjectTemplate(DOCUMENTATION_MODULE,
                            "Documentation module",
                            new MathematicaModuleBuilder.Documentation())
            };
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
