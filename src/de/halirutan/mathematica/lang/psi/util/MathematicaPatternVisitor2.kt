/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.lang.psi.util

import com.intellij.psi.PsiElement
import de.halirutan.mathematica.lang.psi.MathematicaVisitor
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.assignment.SetDelayed
import de.halirutan.mathematica.lang.psi.api.assignment.TagSet
import de.halirutan.mathematica.lang.psi.api.assignment.TagSetDelayed
import de.halirutan.mathematica.lang.psi.api.pattern.Condition
import de.halirutan.mathematica.lang.psi.api.rules.RuleDelayed

/**
 * @author patrick (10/10/13)
 */
class MathematicaPatternVisitor2 : MathematicaVisitor() {

  val patternSymbols = HashSet<Symbol>()


  private fun extractPatternVariables(element: PsiElement) {
    val extractor = PatternSymbolExtractor()
    element.accept(extractor)
    patternSymbols.addAll(extractor.patternSymbols)
  }

  override fun visitSetDelayed(setDelayed: SetDelayed) {
    val lhs = setDelayed.firstChild
    extractPatternVariables(lhs)
  }

  override fun visitSet(set: de.halirutan.mathematica.lang.psi.api.assignment.Set) {
    val lhs = set.firstChild
    extractPatternVariables(lhs)
  }

  override fun visitTagSet(tagSet: TagSet) {
    val firstChild = tagSet.firstChild ?: return
    val operator = MathematicaPsiUtilities.getNextSiblingSkippingWhitespace(firstChild) ?: return
    val pattern = MathematicaPsiUtilities.getNextSiblingSkippingWhitespace(operator)
    extractPatternVariables(pattern)
  }


  override fun visitTagSetDelayed(tagSetDelayed: TagSetDelayed) {
    val firstChild = tagSetDelayed.firstChild
    val operator = MathematicaPsiUtilities.getNextSiblingSkippingWhitespace(firstChild) ?: return
    val pattern = MathematicaPsiUtilities.getNextSiblingSkippingWhitespace(operator)
    extractPatternVariables(pattern)
  }

  override fun visitRuleDelayed(ruleDelayed: RuleDelayed) {
    val lhs = ruleDelayed.firstChild
    extractPatternVariables(lhs)
  }

  override fun visitCondition(condition: Condition) {
    condition.firstChild?.let {
      extractPatternVariables(it)
    }
  }
}

