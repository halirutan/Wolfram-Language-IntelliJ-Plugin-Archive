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

package de.halirutan.mathematica.actions;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.internal.statistic.UsageTrigger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.ex.FileTypeChooser;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.MathematicaIcons;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (4/8/13)
 */
public class CreateMathematicaFile extends CreateFileFromTemplateAction implements DumbAware{
    private static final String NEW_M_FILE = "New Mathematica file";

    public CreateMathematicaFile() {
        super(NEW_M_FILE, "Creates a new .m Mathematica package file", MathematicaIcons.FILE_ICON);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle(NEW_M_FILE).addKind("Package", MathematicaIcons.FILE_ICON, "Mathematica Package").
        addKind("Package", MathematicaIcons.FILE_ICON, "Mathematica Package");


    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return NEW_M_FILE;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CreateMathematicaFile;
    }

}
