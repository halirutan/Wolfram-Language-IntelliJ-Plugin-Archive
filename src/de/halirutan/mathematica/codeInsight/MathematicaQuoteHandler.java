package de.halirutan.mathematica.codeInsight;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 *
 * @author patrick (4/8/13)
 */
public class MathematicaQuoteHandler extends SimpleTokenSetQuoteHandler {
    public MathematicaQuoteHandler() {
        super(STRING_LITERAL, STRING_LITERAL_BEGIN, STRING_LITERAL_END);
    }

}
