/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.codeinsight.highlighting.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors
import de.halirutan.mathematica.lang.psi.LocalizationConstruct
import de.halirutan.mathematica.lang.psi.api.Symbol

/**
 * Provides all highlighting except for the most basic one, which is already done after the lexical scanning (includes
 * string, number, operator highlighting if it is set). In this stage, all the fancy highlighting happens which means
 *   * coloring of built-in functions  * coloring of local variables like in Module  * coloring of
 * messages  * coloring of anonymous functions
 *
 * @author patrick (5/14/13)
 */
class SymbolAnnotator : Annotator {

  /** Annotates a [symbol] by checking its localization */
  override fun annotate(symbol: PsiElement, holder: AnnotationHolder) {
    if (symbol is Symbol) {
      val resolve = symbol.advancedResolve()
      val scope = resolve.localization
      val scopeType = scope.type
      if (!resolve.isValidResult || LocalizationConstruct.MScope.NULL_SCOPE == scopeType) {
        return
      }
      scopeType?.let {
        when (it) {
          LocalizationConstruct.ScopeType.MODULE_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
          LocalizationConstruct.ScopeType.RULE_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.PATTERN)
          LocalizationConstruct.ScopeType.FUNCTION_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.PATTERN)
          LocalizationConstruct.ScopeType.TABLE_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
          LocalizationConstruct.ScopeType.COMPILE_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
          LocalizationConstruct.ScopeType.ANONYMOUS_FUNCTION_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
          LocalizationConstruct.ScopeType.LIMIT_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
          LocalizationConstruct.ScopeType.MANIPULATE_LIKE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
          else -> when (scope) {
            LocalizationConstruct.MScope.FILE_SCOPE -> MathematicaSyntaxHighlighterColors.setHighlightingStrict(symbol, holder, MathematicaSyntaxHighlighterColors.SYMBOL)
            LocalizationConstruct.MScope.IMPORT_SCOPE -> MathematicaSyntaxHighlighterColors.setHighlightingStrict(symbol, holder, MathematicaSyntaxHighlighterColors.IMPORTED_SYMBOL)
            LocalizationConstruct.MScope.KERNEL_SCOPE -> MathematicaSyntaxHighlighterColors.setHighlighting(symbol, holder, MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL)
            else -> return
          }
        }
      }
    }
  }
}
