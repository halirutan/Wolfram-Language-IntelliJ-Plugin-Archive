package de.halirutan.mathematica;

import com.intellij.ide.fileTemplates.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

/**
 * User: rsmenon
 * Date: 5/13/13
 * Time: 5:22 PM
 */
public class MathematicaFileTemplateProvider {
    @NonNls
    public static final String PACKAGE = "Package.m";

    @NonNls
    public static final String NOTEBOOK = "Notebook.nb";

    @NonNls
    public static final String PLAIN = "Plain.m";

    @NonNls
    public static final String TEST = "Test.mt";

    @NonNls
    public static final String INIT = "init.m";

    @Nullable
    public static PsiElement createFromTemplate(@NotNull Project project,
                                                @NotNull VirtualFile rootDir,
                                                @NotNull String templateName,
                                                @NotNull String fileName,
                                                @NotNull Properties properties) throws Exception {
        rootDir.refresh(false, false);
        PsiDirectory directory = PsiManager.getInstance(project).findDirectory(rootDir);
        if (directory != null) {
            return createFromTemplate(templateName, fileName, directory, properties);
        }
        return null;
    }

    @Nullable
    public static PsiElement createFromTemplate(@NotNull Project project,
                                                @NotNull VirtualFile rootDir,
                                                @NotNull String templateName,
                                                @NotNull String fileName) throws Exception {
        return createFromTemplate(project, rootDir, templateName, fileName, FileTemplateManager.getInstance().getDefaultProperties(project));
    }

    public static PsiElement createFromTemplate(String templateName, String fileName, @NotNull PsiDirectory directory, Properties properties)
            throws Exception {
        FileTemplateManager manager = FileTemplateManager.getInstance();
        FileTemplate template = manager.getInternalTemplate(templateName);
        return FileTemplateUtil.createFromTemplate(template, fileName, properties, directory);
    }

}
