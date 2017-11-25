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

package de.halirutan.mathematica.lang.resolve.resolvers

import com.intellij.psi.PsiElement
import de.halirutan.mathematica.lang.psi.LocalizationConstruct
import de.halirutan.mathematica.lang.psi.api.Expression
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.assignment.Set
import de.halirutan.mathematica.lang.psi.api.assignment.SetDelayed
import de.halirutan.mathematica.lang.psi.api.lists.MList

/**
 * Finds the symbol element in `{x, x=1, x:=y}`
 */
fun findSymbolInAssignment(elm: PsiElement): Symbol? {
  when {
    elm is Symbol -> return elm
    (elm is Set || elm is SetDelayed) && elm.firstChild is Symbol -> return elm.firstChild as Symbol
  }
  return null
}

/** Compile variable definitions can look like x, {x,y,z}, {{x,_Real},{y,_Real,3}} */
fun findCompileDeclarationSymbol(elm: PsiElement): Symbol? {
  if (elm is Symbol) {
    return elm
  }
  if (elm is MList) {
    elm.listElements.takeUnless { it.isEmpty() }?.let {
      if (it[0] is Symbol) {
        return it[0] as Symbol
      }
      if (it[0] is MList) {
        (it[0] as MList).listElements.takeUnless { it.isEmpty() && it[0] is Symbol }?.let {
          return it[0] as Symbol
        }
      }
    }
  }
  return null
}

/** Returns the body element from a list of parameters of a localization construct.
 * The position of the body parameter is defined by the type of scoping.
 * */
fun getBodyElement(parameters: List<Expression>, scoping: LocalizationConstruct.MScope): Expression? {
  var bodyPosition = scoping.bodyPosition
  if (bodyPosition == -1) {
    bodyPosition = parameters.lastIndex
  }
  return parameters.elementAtOrNull(bodyPosition)
}

/**
 * Returns a list of expressions. Each expression is a possible place of declaring a local variable.
 * */
fun getLocalizationParameters(parameters: List<Expression>, scoping: LocalizationConstruct.MScope): List<Expression> {
  val start = scoping.scopePositionStart
  val end = when {
    scoping.scopePositionEnd < 0 -> parameters.size + scoping.scopePositionEnd + 1
    scoping.scopePositionEnd > parameters.lastIndex -> parameters.size
    else -> scoping.scopePositionEnd + 1
  }
  if (start < end && end <= parameters.size) {
    return parameters.subList(start, end)
  }
  return emptyList()
}

