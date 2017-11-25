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
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes
import de.halirutan.mathematica.lang.psi.MathematicaRecursiveVisitor
import de.halirutan.mathematica.lang.psi.api.function.Function
import de.halirutan.mathematica.lang.psi.api.slots.Slot
import de.halirutan.mathematica.lang.psi.api.slots.SlotExpression

/**
 * Provides highlighting of anonymous functions.
 * @author patrick (03.03.15)
 */
class FunctionAnnotator : Annotator {

  override fun annotate(function: PsiElement, holder: AnnotationHolder) {
    if (function is Function) {
      MathematicaSyntaxHighlighterColors.setHighlighting(function, holder, MathematicaSyntaxHighlighterColors.ANONYMOUS_FUNCTION)

      val slotVisitor = object : MathematicaRecursiveVisitor() {

        override fun visitSlot(slot: Slot) {
          MathematicaSyntaxHighlighterColors.setHighlighting(slot, holder, MathematicaSyntaxHighlighterColors.PATTERN)
        }

        override fun visitSlotExpression(slotExpr: SlotExpression) {
          val head = slotExpr.firstChild.node.elementType
          if (head == MathematicaElementTypes.ASSOCIATION_SLOT) {
            MathematicaSyntaxHighlighterColors.setHighlighting(slotExpr, holder, MathematicaSyntaxHighlighterColors.PATTERN)
          } else {
            MathematicaSyntaxHighlighterColors.setHighlighting(slotExpr.firstChild, holder, MathematicaSyntaxHighlighterColors.PATTERN)
          }
          slotExpr.acceptChildren(this)

        }
      }

      function.accept(slotVisitor)
    }
  }
}