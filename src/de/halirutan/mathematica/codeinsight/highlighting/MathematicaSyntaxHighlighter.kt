/*
 * Copyright (c) 2017 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import de.halirutan.mathematica.lang.lexer.MathematicaLexer
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes

import java.util.HashMap

/**
 * Provides a basic syntax highlighter for the Mathematica language.
 *
 * @author patrick (1/3/13)
 */
class MathematicaSyntaxHighlighter : SyntaxHighlighterBase() {
    private val myLexer: MathematicaLexer = MathematicaLexer()

    init {

        SyntaxHighlighterBase.fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL)
        SyntaxHighlighterBase.fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_NAMED_CHARACTER)
        SyntaxHighlighterBase.fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL_BEGIN)
        SyntaxHighlighterBase.fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL_END)
        SyntaxHighlighterBase.fillMap(colors, MathematicaSyntaxHighlighterColors.IDENTIFIER, MathematicaElementTypes.IDENTIFIER)
        SyntaxHighlighterBase.fillMap(colors, MathematicaElementTypes.OPERATORS, MathematicaSyntaxHighlighterColors.OPERATORS)
        SyntaxHighlighterBase.fillMap(colors, MathematicaElementTypes.BRACES, MathematicaSyntaxHighlighterColors.BRACE)
        SyntaxHighlighterBase.fillMap(colors, MathematicaElementTypes.LITERALS, MathematicaSyntaxHighlighterColors.LITERAL)
        SyntaxHighlighterBase.fillMap(colors, MathematicaElementTypes.COMMENTS, MathematicaSyntaxHighlighterColors.COMMENT)
        SyntaxHighlighterBase.fillMap(colors, MathematicaSyntaxHighlighterColors.BAD_CHARACTER, MathematicaElementTypes.BAD_CHARACTER)
    }

    override fun getHighlightingLexer(): Lexer {
        return myLexer
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return SyntaxHighlighterBase.pack(colors[tokenType])
    }

    companion object {
        private val colors = HashMap<IElementType, TextAttributesKey>()
    }

}
