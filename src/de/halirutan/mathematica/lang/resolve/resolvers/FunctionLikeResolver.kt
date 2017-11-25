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
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.resolve.SymbolResolveHint
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult

/**  Resolves local variables from Function-like local variables
 * */
class FunctionLikeResolver : Resolver {

  override fun resolve(symbol: Symbol, scopingElement: FunctionCall, state: ResolveState): SymbolResolveResult? {
    val lastParent = state.get(SymbolResolveHint.LAST_PARENT)
    val scope = scopingElement.scopingConstruct
    val parameters = scopingElement.parameters

    // when resolving doesn't make sense
    if (!scopingElement.isScopingConstruct || !LocalizationConstruct.isFunctionLike(scope) || parameters.size < 2) {
      return null
    }

    val body = getBodyElement(parameters, scope) ?: return null
    val defLists = getLocalizationParameters(parameters, scope).takeUnless { it.isEmpty() } ?: return null

    // Symbol to resolve is located in the body of Compile
    if (lastParent == body) {
      for (defList in defLists) {
        if (defList is Symbol && defList.hasSameName(symbol)) {
          return SymbolResolveResult(defList, scope, scopingElement, true)
        }
      }
    } else {
      defLists.indexOf(lastParent).takeUnless { it == -1 }?.let {
        val defList = defLists[it]
        if (defList is Symbol && defList == symbol) {
          return SymbolResolveResult(defList, scope, scopingElement, true)
        }
      }
    }
    return null
  }
}
