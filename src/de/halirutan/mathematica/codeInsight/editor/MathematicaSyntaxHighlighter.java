package de.halirutan.mathematica.codeInsight.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.lexer.MathematicaLexer;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a syntax highlighter for the Mathematica language. This class is registered through
 * {@link MathematicaSyntaxHighlighterFactory} so it can be used in the custom language plugin.
 * @author patrick (1/3/13)
 */
public class MathematicaSyntaxHighlighter extends SyntaxHighlighterBase {

    private final MathematicaLexer lexer;
    private static final Map<IElementType, TextAttributesKey> colors = new HashMap<IElementType, TextAttributesKey>();

    public MathematicaSyntaxHighlighter() {
        lexer = new MathematicaLexer();

        SyntaxHighlighterBase.fillMap(colors, MathematicaSyntaxHighlighterColors.COMMENT, MathematicaElementTypes.COMMENT);
        fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL);
        fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL_BEGIN);
        fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL_END);
        fillMap(colors, MathematicaSyntaxHighlighterColors.IDENTIFIER, MathematicaElementTypes.IDENTIFIER);
        fillMap(colors, MathematicaElementTypes.OPERATORS, MathematicaSyntaxHighlighterColors.OPERATORS);
        fillMap(colors, MathematicaElementTypes.BRACES, MathematicaSyntaxHighlighterColors.BRACES);
        fillMap(colors, MathematicaElementTypes.LITERALS, MathematicaSyntaxHighlighterColors.LITERALS);

        fillMap(colors, MathematicaSyntaxHighlighterColors.BAD_CHARACTER, MathematicaElementTypes.BAD_CHARACTER);
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
