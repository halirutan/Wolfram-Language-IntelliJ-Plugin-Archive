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

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement

/**
 * User: rsmenon (5/17/13)
 */

object MathematicaSyntaxHighlighterColors {

    val COMMENT = TextAttributesKey.createTextAttributesKey("MMA.COMMENT")
    val COMMENT_SPECIAL = TextAttributesKey.createTextAttributesKey("MMA.COMMENT_SPECIAL")
    val STRING = TextAttributesKey.createTextAttributesKey("MMA.STRING")
    val OPERATORS = TextAttributesKey.createTextAttributesKey("MMA.OPERATORS")
    val LITERAL = TextAttributesKey.createTextAttributesKey("MMA.LITERAL")
    val IDENTIFIER = TextAttributesKey.createTextAttributesKey("MMA.IDENTIFIER")
    val BUILTIN_FUNCTION = TextAttributesKey.createTextAttributesKey("MMA.BUILTIN_FUNCTION")
    val BRACE = TextAttributesKey.createTextAttributesKey("MMA.BRACE")
    val PATTERN = TextAttributesKey.createTextAttributesKey("MMA.PATTERN")
    val SLOT = TextAttributesKey.createTextAttributesKey("MMA.SLOT")
    val ANONYMOUS_FUNCTION = TextAttributesKey.createTextAttributesKey("MMA.ANONYMOUS_FUNCTION")
    val MESSAGE = TextAttributesKey.createTextAttributesKey("MMA.MESSAGE")
    val USAGE_MESSAGE = TextAttributesKey.createTextAttributesKey("MMA.USAGE_MESSAGE")
    val MODULE_LOCALIZED = TextAttributesKey.createTextAttributesKey("MMA.MODULE_LOCALIZED")
    val BLOCK_LOCALIZED = TextAttributesKey.createTextAttributesKey("MMA.BLOCK_LOCALIZED")
    val BAD_CHARACTER = HighlighterColors.BAD_CHARACTER

    fun setHighlighting(element: PsiElement, holder: AnnotationHolder, key: TextAttributesKey) {
        val annotation = holder.createInfoAnnotation(element, null)
        annotation.textAttributes = key
        annotation.setNeedsUpdateOnTyping(true)
    }

    fun setHighlightingStrict(element: PsiElement, holder: AnnotationHolder, key: TextAttributesKey) {
        val annotation = holder.createInfoAnnotation(element, null)
        annotation.enforcedTextAttributes = TextAttributes.ERASE_MARKER
        annotation.enforcedTextAttributes = EditorColorsManager.getInstance().globalScheme.getAttributes(key)
        annotation.setNeedsUpdateOnTyping(false)
    }
}
