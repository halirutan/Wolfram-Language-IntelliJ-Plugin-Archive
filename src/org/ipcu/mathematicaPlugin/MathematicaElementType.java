package org.ipcu.mathematicaPlugin;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/27/12
 * Time: 2:35 AM
 * Purpose:
 */
public class MathematicaElementType extends IElementType {

    public MathematicaElementType(@NotNull @NonNls String debugName) {
        super(debugName, MathematicaLanguage.INSTANCE);
    }


}
