package de.halirutan.mathematica.fileTypes;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lexer.MathematicaLexer;
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

        SyntaxHighlighterBase.fillMap(colors, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.COMMENT, MathematicaElementTypes.COMMENT);
        fillMap(colors, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.STRING, MathematicaElementTypes.STRING_LITERAL);
        fillMap(colors, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.STRING, MathematicaElementTypes.STRING_LITERAL_BEGIN);
        fillMap(colors, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.STRING, MathematicaElementTypes.STRING_LITERAL_END);
        fillMap(colors, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.IDENTIFIER, MathematicaElementTypes.IDENTIFIER);
        fillMap(colors, MathematicaElementTypes.OPERATORS, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.OPERATORS);
        fillMap(colors, MathematicaElementTypes.BRACES, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.BRACES);
        fillMap(colors, MathematicaElementTypes.LITERALS, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.LITERALS);

        fillMap(colors, de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter.BAD_CHARACTER, MathematicaElementTypes.BAD_CHARACTER);
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
