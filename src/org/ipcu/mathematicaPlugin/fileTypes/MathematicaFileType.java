package org.ipcu.mathematicaPlugin.fileTypes;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.ipcu.mathematicaPlugin.Mathematica;
import org.ipcu.mathematicaPlugin.MathematicaIcons;
import org.ipcu.mathematicaPlugin.MathematicaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/23/12
 * Time: 9:55 PM
 * Purpose:
 */
public class MathematicaFileType extends LanguageFileType{

    public static final LanguageFileType INSTANCE = new MathematicaFileType();
    public static final String[] DEFAULT_EXTENSIONS= new String[]{"m","mma","nb"};

    protected MathematicaFileType(){
        super(MathematicaLanguage.INSTANCE);
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
        return MathematicaFileType.DEFAULT_EXTENSIONS[0];
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MathematicaIcons.FILE_ICON;
    }
}
