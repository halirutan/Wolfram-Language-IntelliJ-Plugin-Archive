/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.highlighting.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct

/**
 * Provides all highlighting except for the most basic one, which is already done after the lexical scanning (includes
 * string, number, operator highlighting if it is set). In this stage, all the fancy highlighting happens which means
 *   * coloring of built-in functions  * coloring of local variables like in Module  * coloring of
 * messages  * coloring of anonymous functions
 *
 * @author patrick (5/14/13)
 */
class LocalizedSymbolAnnotator : Annotator {

    override fun annotate(symbol: PsiElement, holder: AnnotationHolder) {
        if (symbol is Symbol) {
            symbol.resolve()
            val scope = symbol.localizationConstruct
            if (scope == LocalizationConstruct.MScope.NULL_SCOPE) {
                return
            }
            when (scope) {
                LocalizationConstruct.MScope.FILE_SCOPE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.IDENTIFIER)
                LocalizationConstruct.MScope.KERNEL_SCOPE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.BUILTIN_FUNCTION)
                LocalizationConstruct.MScope.MODULE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
                LocalizationConstruct.MScope.BLOCK -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.BLOCK_LOCALIZED)
                LocalizationConstruct.MScope.SETDELAYED_SCOPE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.PATTERN)
                else -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
            }
        }
    }
}
