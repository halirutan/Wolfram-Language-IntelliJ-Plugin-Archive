package de.halirutan.mathematica.codeinsight.inspections.symbol

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import de.halirutan.mathematica.codeinsight.inspections.AbstractInspection
import de.halirutan.mathematica.codeinsight.inspections.InspectionBundle
import de.halirutan.mathematica.lang.psi.MathematicaVisitor
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.impl.LightUndefinedSymbol

/**
 *
 * @author patrick (13.09.17).
 */
class UnresolvedSymbolInspection : AbstractInspection() {


    override fun getDisplayName(): String {
        return InspectionBundle.message("symbol.unresolved.name")
    }

    override fun getStaticDescription(): String? {
        return InspectionBundle.message("symbol.unresolved.description")
    }

    override fun getGroupDisplayName(): String {
        return InspectionBundle.message("group.symbol")
    }

    override fun getDefaultLevel(): HighlightDisplayLevel {
        return HighlightDisplayLevel.ERROR
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
        return object: MathematicaVisitor(){
            override fun visitSymbol(symbol: Symbol?) {
                symbol?.let {
                    val resolve = symbol.resolve()
                    if (resolve is LightUndefinedSymbol) {
                        holder.registerProblem(symbol, InspectionBundle.message("symbol.unresolved.message"), ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, TextRange.create(0,symbol.textLength))
                    }

                }
            }
        }

    }
}