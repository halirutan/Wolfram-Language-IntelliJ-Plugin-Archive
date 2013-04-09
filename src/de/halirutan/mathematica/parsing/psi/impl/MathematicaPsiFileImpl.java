package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import de.halirutan.mathematica.MathematicaLanguage;
import de.halirutan.mathematica.fileTypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.psi.api.MathematicaPsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13
 * Time: 12:09 PM
 * Purpose:
 */
public class MathematicaPsiFileImpl extends PsiFileBase implements MathematicaPsiFile {

    public MathematicaPsiFileImpl(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, MathematicaLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return MathematicaFileType.INSTANCE;
    }
}
