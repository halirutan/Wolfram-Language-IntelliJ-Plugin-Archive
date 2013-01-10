package org.ipcu.mathematicaPlugin.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.ipcu.mathematicaPlugin.MathematicaLanguage;
import org.ipcu.mathematicaPlugin.fileTypes.MathematicaFileType;
import org.ipcu.mathematicaPlugin.psi.MathematicaPsiFile;
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
