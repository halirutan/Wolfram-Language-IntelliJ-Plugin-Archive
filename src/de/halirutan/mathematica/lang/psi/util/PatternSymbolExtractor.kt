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

package de.halirutan.mathematica.lang.psi.util

import com.google.common.collect.Lists
import com.intellij.psi.PsiElement
import de.halirutan.mathematica.lang.psi.MathematicaVisitor
import de.halirutan.mathematica.lang.psi.api.Expression
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.pattern.Blank
import de.halirutan.mathematica.lang.psi.api.pattern.BlankNullSequence
import de.halirutan.mathematica.lang.psi.api.pattern.BlankSequence
import de.halirutan.mathematica.lang.psi.api.pattern.Condition
import de.halirutan.mathematica.lang.psi.api.pattern.Optional
import de.halirutan.mathematica.lang.psi.api.pattern.Pattern

/**
 * @author patrick (10/10/13)
 */
class PatternSymbolExtractor : MathematicaVisitor() {

  val patternSymbols = HashSet<Symbol>()
  /* Except | Longest | Optional | PatternTest | Repeated | RepeatedNull | Shortest
* HoldPattern | IgnoringInactive | KeyValuePattern | Literal | Longest | Optional | Repeated | RepeatedNull | Shortest
* */
  private val myDiveInFirstChild = Lists.newArrayList("Longest", "Shortest", "Repeated", "Optional", "PatternTest", "Condition")
  private val myDoNotDiveIn = Lists.newArrayList("Verbatim")

  override fun visitBlank(blank: Blank) {
    if (blank.firstChild is Symbol) {
      patternSymbols.add(blank.firstChild as Symbol)
    }
  }

  override fun visitBlankSequence(blankSequence: BlankSequence) {
    if (blankSequence.firstChild is Symbol) {
      patternSymbols.add(blankSequence.firstChild as Symbol)
    }
  }

  override fun visitBlankNullSequence(blankNullSequence: BlankNullSequence) {
    if (blankNullSequence.firstChild is Symbol) {
      patternSymbols.add(blankNullSequence.firstChild as Symbol)
    }
  }

  override fun visitOptional(optional: Optional) {
    val firstChild = optional.firstChild
    firstChild?.accept(this)
  }

  override fun visitCondition(condition: Condition) {
    val firstChild = condition.firstChild
    firstChild?.accept(this)
  }

  override fun visitPattern(pattern: Pattern) {
    val firstChild = pattern.firstChild
    if (firstChild is Symbol) {
      patternSymbols.add(firstChild)
    }
    pattern.lastChild.accept(this)
  }

  override fun visitFunctionCall(functionCall: FunctionCall) {
    val head = functionCall.firstChild
    if (head is Symbol) {
      val functionName = head.symbolName
      if (myDiveInFirstChild.contains(functionName)) {
        val args = functionCall.children.filter { it is Expression }
        if (args.isNotEmpty()) {
          args[0].accept(this)
        }
      } else if (!myDoNotDiveIn.contains(functionName)) {
        functionCall.acceptChildren(this)
      }
    } else {
      functionCall.acceptChildren(this)
    }
  }

  override fun visitElement(element: PsiElement?) {
    element?.acceptChildren(this)
  }
}

