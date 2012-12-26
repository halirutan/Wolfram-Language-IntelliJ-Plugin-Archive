package org.ipcu.mathematicaPlugin;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/23/12
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class MathematicaFileType extends LanguageFileType{

    public static final MathematicaFileType INSTANCE = new MathematicaFileType();

    public static final Language LANGUAGE = INSTANCE.getLanguage();

    public static final String DEFAULT_EXTENSION = "mma";
    public static final String NOTEBOOK_EXTENSION = "nb";

    protected MathematicaFileType(){
        super(new MathematicaLanguage());
    }

    @NotNull
    @Override
    public String getName() {
        return Mathematica.NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return Mathematica.DESCRIPTION;
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return MathematicaFileType.DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MathematicaIcons.FILE_ICON;
    }
}
