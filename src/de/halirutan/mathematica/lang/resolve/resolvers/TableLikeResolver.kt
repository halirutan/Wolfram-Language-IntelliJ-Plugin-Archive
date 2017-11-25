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

import com.intellij.psi.ResolveState
import de.halirutan.mathematica.lang.psi.LocalizationConstruct
import de.halirutan.mathematica.lang.psi.api.Expression
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.lists.MList
import de.halirutan.mathematica.lang.resolve.SymbolResolveHint
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult

/** Resolve variables that in localization constructs that behave like a Table */
class TableLikeResolver : Resolver {

  override fun resolve(symbol: Symbol, scopingElement: FunctionCall, state: ResolveState): SymbolResolveResult? {

    val lastParent = state.get(SymbolResolveHint.LAST_PARENT)
    val parameters: List<Expression> = scopingElement.parameters
    val scope = scopingElement.scopingConstruct

    if (!scopingElement.isScopingConstruct || !LocalizationConstruct.isTableLike(scope) || parameters.size < 2) {
      return null
    }

    val body = getBodyElement(parameters, scope) ?: return null
    val defLists = getLocalizationParameters(parameters, scope).takeUnless { it.isEmpty() } ?: return null

    // if we're coming from the body of the table
    if (lastParent == body) {
      defLists
          .filterIsInstance<MList>()
          .map { it.listElements }
          .filter { !it.isEmpty() && it.first() is Symbol }
          .map { it.first() }
          .forEach { s ->
            if (s is Symbol && s.fullSymbolName == symbol.fullSymbolName) {
              return SymbolResolveResult(s, scope, scopingElement, true)
            }
          }
    }
    // if we are inside one of the definition lists itself
    else {
      val index = defLists.indexOf(lastParent).takeUnless { it == -1 } ?: return null
      for (defList in defLists.subList(0, index + 1).filterIsInstance<MList>()) {
        val iterator = defList.listElements.takeIf { it.size > 0 } ?: continue
        val s = iterator.first()
        if (s is Symbol && s.fullSymbolName == symbol.fullSymbolName) {
          if (s == symbol || lastParent != defList) {
            return SymbolResolveResult(s, scope, scopingElement, true)
          }
        }

      }
    }
    return null
  }
}