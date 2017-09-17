package de.halirutan.mathematica.codeinsight.highlighting.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors
import de.halirutan.mathematica.lang.psi.api.MessageName

/**
 *
 * @author patrick (13.09.17).
 */
class MessageNameAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is MessageName) {
            val tag = element.tag
            tag?.let {
                val color = when {
                    "usage" == tag.text -> MathematicaSyntaxHighlighterColors.USAGE_MESSAGE
                    else -> MathematicaSyntaxHighlighterColors.MESSAGE
                }
                val children = element.node.getChildren(null)
                for (i in 1 until children.size) {
                    MathematicaSyntaxHighlighterColors.setHighlightingStrict(children[i].psi, holder, color)
                }
            }
        }
    }
}