package org.ipcu.mathematicaPlugin.fileTypes;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.ipcu.mathematicaPlugin.MathematicaElementTypes;
import org.ipcu.mathematicaPlugin.editor.MathematicaHighlightingColors;
import org.ipcu.mathematicaPlugin.lexer.MathematicaLexer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13
 * Time: 6:12 AM
 * Purpose:
 */
public class MathematicaSyntaxHighlighter extends SyntaxHighlighterBase {

    private final MathematicaLexer lexer;
    private final Map<IElementType, TextAttributesKey> colors = new HashMap<IElementType, TextAttributesKey>();

    public MathematicaSyntaxHighlighter() {
        lexer = new MathematicaLexer();

        fillMap(colors, MathematicaHighlightingColors.COMMENT, MathematicaElementTypes.COMMENT);
        fillMap(colors, MathematicaHighlightingColors.STRING, MathematicaElementTypes.STRING_LITERAL);
        fillMap(colors, MathematicaHighlightingColors.IDENTIFIER, MathematicaElementTypes.IDENTIFIER);
        fillMap(colors, MathematicaElementTypes.OPERATORS,MathematicaHighlightingColors.OPERATORS);
        fillMap(colors, MathematicaElementTypes.BRACES, MathematicaHighlightingColors.BRACES);
        fillMap(colors, MathematicaElementTypes.LITERALS, MathematicaHighlightingColors.LITERALS);

        fillMap(colors, MathematicaHighlightingColors.BAD_CHARACTER, MathematicaElementTypes.BAD_CHARACTER);
    }



    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return lexer;
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(colors.get(tokenType));
    }


}
