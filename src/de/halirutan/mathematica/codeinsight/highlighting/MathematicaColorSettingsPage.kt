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

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.io.StreamUtil
import de.halirutan.mathematica.util.MathematicaIcons
import gnu.trove.THashMap
import java.io.IOException
import javax.swing.Icon

/**
 * @author patrick (4/7/13)
 */
class MathematicaColorSettingsPage : ColorSettingsPage {

    override fun getIcon(): Icon? {
        return MathematicaIcons.FILE_ICON
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return MathematicaSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        val resource = javaClass.classLoader.getResourceAsStream("/colors/demoText.txt")
        val demoText: String
        try {
            demoText = StreamUtil.readText(resource, "UTF-8")
        } catch (e: IOException) {
            throw RuntimeException("Mathematica Plugin could not load the syntax highlighter demo text.", e)
        }

        return demoText
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
        val map = THashMap<String, TextAttributesKey>()
        map.put("i", MathematicaSyntaxHighlighterColors.IDENTIFIER)
        map.put("k", MathematicaSyntaxHighlighterColors.BUILTIN_FUNCTION)
        map.put("pat", MathematicaSyntaxHighlighterColors.PATTERN)
        map.put("usg", MathematicaSyntaxHighlighterColors.USAGE_MESSAGE)
        map.put("msg", MathematicaSyntaxHighlighterColors.MESSAGE)
        map.put("mod", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
        map.put("blk", MathematicaSyntaxHighlighterColors.BLOCK_LOCALIZED)
        map.put("fn", MathematicaSyntaxHighlighterColors.ANONYMOUS_FUNCTION)
        map.put("slot", MathematicaSyntaxHighlighterColors.SLOT)
        map.put("s", MathematicaSyntaxHighlighterColors.STRING)
        map.put("c", MathematicaSyntaxHighlighterColors.COMMENT)
        map.put("cs", MathematicaSyntaxHighlighterColors.COMMENT_SPECIAL)
        map.put("o", MathematicaSyntaxHighlighterColors.OPERATORS)
        map.put("b", MathematicaSyntaxHighlighterColors.BRACE)
        map.put("l", MathematicaSyntaxHighlighterColors.LITERAL)
        return map
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return arrayOf(
                AttributesDescriptor("Symbols", MathematicaSyntaxHighlighterColors.IDENTIFIER),
                AttributesDescriptor("Built-In Function", MathematicaSyntaxHighlighterColors.BUILTIN_FUNCTION),
                AttributesDescriptor("Number", MathematicaSyntaxHighlighterColors.LITERAL),
                AttributesDescriptor("Operator Sign", MathematicaSyntaxHighlighterColors.OPERATORS),
                AttributesDescriptor("Parenthesis", MathematicaSyntaxHighlighterColors.BRACE),
                AttributesDescriptor("String", MathematicaSyntaxHighlighterColors.STRING),
                AttributesDescriptor("Comment", MathematicaSyntaxHighlighterColors.COMMENT),
                AttributesDescriptor("Comment Special", MathematicaSyntaxHighlighterColors.COMMENT_SPECIAL),
                AttributesDescriptor("Module Variables", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED),
                AttributesDescriptor("Block Variables", MathematicaSyntaxHighlighterColors.BLOCK_LOCALIZED),
                AttributesDescriptor("Patterns and Arguments", MathematicaSyntaxHighlighterColors.PATTERN),
                AttributesDescriptor("Anonymous functions", MathematicaSyntaxHighlighterColors.ANONYMOUS_FUNCTION),
                AttributesDescriptor("Slots", MathematicaSyntaxHighlighterColors.SLOT),
                AttributesDescriptor("Usage message", MathematicaSyntaxHighlighterColors.USAGE_MESSAGE),
                AttributesDescriptor("Other message", MathematicaSyntaxHighlighterColors.MESSAGE)
        )
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "Mathematica"
    }

}
