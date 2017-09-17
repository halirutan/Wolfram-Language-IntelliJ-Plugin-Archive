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