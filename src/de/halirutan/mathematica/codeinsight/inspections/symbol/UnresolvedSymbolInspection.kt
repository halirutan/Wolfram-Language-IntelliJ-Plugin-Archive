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

package de.halirutan.mathematica.codeinsight.inspections.symbol

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import de.halirutan.mathematica.codeinsight.inspections.AbstractInspection
import de.halirutan.mathematica.codeinsight.inspections.InspectionBundle
import de.halirutan.mathematica.lang.psi.LocalizationConstruct
import de.halirutan.mathematica.lang.psi.MathematicaVisitor
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.resolve.MathematicaGlobalResolveCache

/**
 * Provides warning annotations to symbols that could not be resolved to some place of definition.
 */
class UnresolvedSymbolInspection : AbstractInspection() {

  override fun getDisplayName(): String = InspectionBundle.message("symbol.unresolved.name")

  override fun getStaticDescription(): String? = InspectionBundle.message("symbol.unresolved.description")

  override fun getGroupDisplayName(): String = InspectionBundle.message("group.symbol")

  override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.WEAK_WARNING

  override fun runForWholeFile(): Boolean = false

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
    return object : MathematicaVisitor() {

      override fun visitFile(file: PsiFile?) {
        if (file is MathematicaPsiFile) {
          MathematicaGlobalResolveCache.getInstance(file.project)
          PsiTreeUtil.processElements(file, PsiElementProcessor { symbol: PsiElement ->
            if (symbol is Symbol && symbol.localizationConstruct == LocalizationConstruct.MScope.NULL_SCOPE) {
              holder.registerProblem(symbol, InspectionBundle.message("symbol.unresolved.message"))
            }
            return@PsiElementProcessor true
          }
          )
        }
      }
    }
  }

}