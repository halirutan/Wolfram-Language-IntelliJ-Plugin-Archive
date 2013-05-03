/*
 * Copyright (c) 2013 Patrick Scheibe
 *
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

package de.halirutan.mathematica.fileTypes;

import com.intellij.openapi.fileTypes.LanguageFileType;
import de.halirutan.mathematica.MathematicaLanguage;
import de.halirutan.mathematica.Mathematica;
import de.halirutan.mathematica.MathematicaIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;


/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/23/12
 * Time: 9:55 PM
 * Purpose:
 */
public class MathematicaFileType extends LanguageFileType{

    public static final LanguageFileType INSTANCE = new MathematicaFileType();
    private static final String[] DEFAULT_EXTENSIONS= {"m","mma","nb"};

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
