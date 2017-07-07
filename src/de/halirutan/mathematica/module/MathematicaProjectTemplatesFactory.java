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

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import com.intellij.platform.templates.BuilderBasedTemplate;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.util.MathematicaIcons;
import de.halirutan.mathematica.module.MathematicaModuleBuilder.Application;
import de.halirutan.mathematica.module.MathematicaModuleBuilder.Basic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author rsmenon (5/6/13)
 */
public class MathematicaProjectTemplatesFactory extends ProjectTemplatesFactory {

  public static final String MATHEMATICA = MathematicaBundle.message("project.template.mathematica");
  private static final String BASIC_MODULE = MathematicaBundle.message("project.template.basic");
  private static final String APPLICATION_MODULE = MathematicaBundle.message("project.template.application");
  public static final String TEST_MODULE = MathematicaBundle.message("project.template.test");
  public static final String DOCUMENTATION_MODULE = MathematicaBundle.message("project.template.documentation");

  @NotNull
  @Override
  public String[] getGroups() {
    return new String[]{MATHEMATICA};
  }

  @Override
  public Icon getGroupIcon(String group) {
    return MathematicaIcons.FILE_ICON;
  }

  @NotNull
  @Override
  public ProjectTemplate[] createTemplates(String group, WizardContext context) {
    ProjectTemplate[] projectTemplates = {

        new MathematicaProjectTemplate(BASIC_MODULE,
            MathematicaBundle.message("project.template.basic.description"),
            // The descriptions should be reworked when the plugin is more mature
            new Basic()),

        new MathematicaProjectTemplate(APPLICATION_MODULE,
            MathematicaBundle.message("project.template.application.description"),
            new Application())
    };
    return projectTemplates;
//    if (context.getProject() == null) {
//      return projectTemplates;
//    } else {
//      return new ProjectTemplate[]{
//
//          new MathematicaProjectTemplate(TEST_MODULE,
//              MathematicaBundle.message("project.template.test.description"),
//              new MathematicaModuleBuilder.Test()),
//
//          new MathematicaProjectTemplate(DOCUMENTATION_MODULE,
//              MathematicaBundle.message("project.template.documentation.description"),
//              new MathematicaModuleBuilder.Documentation())
//      };
//    }
  }

  private static class MathematicaProjectTemplate extends BuilderBasedTemplate {

    private final String myProjectName;
    private final String myProjectDescription;

    private MathematicaProjectTemplate(String name, String description, MathematicaModuleBuilder builder) {
      super(builder);
      myProjectName = name;
      myProjectDescription = description;
    }

    @NotNull
    @Override
    public String getName() {
      return myProjectName;
    }

    @Nullable
    @Override
    public String getDescription() {
      return myProjectDescription;
    }
  }

}
