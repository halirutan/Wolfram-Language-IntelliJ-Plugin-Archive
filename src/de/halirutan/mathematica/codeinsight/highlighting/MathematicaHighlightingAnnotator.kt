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

package de.halirutan.mathematica.codeinsight.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes
import de.halirutan.mathematica.lang.psi.MathematicaRecursiveVisitor
import de.halirutan.mathematica.lang.psi.MathematicaVisitor
import de.halirutan.mathematica.lang.psi.api.MessageName
import de.halirutan.mathematica.lang.psi.api.StringifiedSymbol
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.function.Function
import de.halirutan.mathematica.lang.psi.api.slots.Slot
import de.halirutan.mathematica.lang.psi.api.slots.SlotExpression
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct

/**
 * Provides all highlighting except for the most basic one, which is already done after the lexical scanning (includes
 * string, number, operator highlighting if it is set). In this stage, all the fancy highlighting happens which means
 *   * coloring of built-in functions  * coloring of local variables like in Module  * coloring of
 * messages  * coloring of anonymous functions
 *
 * @author patrick (5/14/13)
 */
class MathematicaHighlightingAnnotator : MathematicaVisitor(), Annotator {
    private var myHolder: AnnotationHolder? = null

    private fun setHighlighting(element: PsiElement, holder: AnnotationHolder, key: TextAttributesKey) {
        val annotation = holder.createInfoAnnotation(element, null)
        annotation.textAttributes = key
        annotation.setNeedsUpdateOnTyping(true)
    }

    private fun setHighlightingStrict(element: PsiElement, holder: AnnotationHolder, key: TextAttributesKey) {
        val annotation = holder.createInfoAnnotation(element, null)
        annotation.enforcedTextAttributes = TextAttributes.ERASE_MARKER
        annotation.enforcedTextAttributes = EditorColorsManager.getInstance().globalScheme.getAttributes(key)
        annotation.setNeedsUpdateOnTyping(false)
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        assert(myHolder == null) { "unsupported concurrent annotator invocation" }
        try {
            myHolder = holder
            element.accept(this)
        } finally {
            myHolder = null
        }
    }

    override fun visitSymbol(symbol: Symbol) {
        symbol.resolve()
        val scope = symbol.localizationConstruct
        when (scope) {
            LocalizationConstruct.MScope.FILE -> setHighlighting(symbol, myHolder!!, MathematicaSyntaxHighlighterColors.IDENTIFIER)
            LocalizationConstruct.MScope.NULL -> setHighlighting(symbol, myHolder!!, CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES)
            LocalizationConstruct.MScope.BUILT_IN -> setHighlighting(symbol, myHolder!!, MathematicaSyntaxHighlighterColors.BUILTIN_FUNCTION)
            LocalizationConstruct.MScope.MODULE -> setHighlighting(symbol, myHolder!!, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
            LocalizationConstruct.MScope.BLOCK -> setHighlighting(symbol, myHolder!!, MathematicaSyntaxHighlighterColors.BLOCK_LOCALIZED)
            LocalizationConstruct.MScope.SETDELAYEDPATTERN -> setHighlighting(symbol, myHolder!!, MathematicaSyntaxHighlighterColors.PATTERN)
            else -> setHighlighting(symbol, myHolder!!, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
        }

    }

    override fun visitFunction(function: Function) {
        setHighlighting(function, myHolder!!, MathematicaSyntaxHighlighterColors.ANONYMOUS_FUNCTION)

        val slotVisitor = object : MathematicaRecursiveVisitor() {

            override fun visitSlot(slot: Slot) {
                setHighlighting(slot, myHolder!!, MathematicaSyntaxHighlighterColors.PATTERN)
            }

            override fun visitSlotExpression(slotExpr: SlotExpression) {

                val head = slotExpr.firstChild.node.elementType
                if (head == MathematicaElementTypes.ASSOCIATION_SLOT) {
                    setHighlighting(slotExpr, myHolder!!, MathematicaSyntaxHighlighterColors.PATTERN)
                } else {
                    setHighlighting(slotExpr.firstChild, myHolder!!, MathematicaSyntaxHighlighterColors.PATTERN)
                }
                slotExpr.acceptChildren(this)

            }
        }
        function.accept(slotVisitor)

    }

    override fun visitStringifiedSymbol(stringifiedSymbol: StringifiedSymbol) {
        setHighlighting(stringifiedSymbol, myHolder!!, MathematicaSyntaxHighlighterColors.MESSAGE)
    }

    override fun visitMessageName(messageName: MessageName) {
        val tag = messageName.tag
        var color = MathematicaSyntaxHighlighterColors.MESSAGE
        if (tag != null && "usage" == tag.text) {
            color = MathematicaSyntaxHighlighterColors.USAGE_MESSAGE
        }
        val children = messageName.node.getChildren(null)
        for (i in 1 until children.size) {
            setHighlightingStrict(children[i].psi, myHolder!!, color)
        }
    }
}
