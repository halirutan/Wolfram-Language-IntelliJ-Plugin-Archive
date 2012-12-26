package org.ipcu.mathematicaPlugin;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/23/12
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MathematicaLanguage extends Language {


    protected MathematicaLanguage() {
        super(Mathematica.NAME);
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
