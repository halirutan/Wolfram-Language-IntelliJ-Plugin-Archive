/*
 * Copyright (c) 2017 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.halirutan.mathematica.lang.resolve.processors

import com.intellij.psi.ResolveState
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.lists.MList
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult

/**
 *
 * @author patrick (31.08.17).
 */
fun resolveLocalCompileLikeVariables(myStartElement: Symbol, functionCall: FunctionCall, state: ResolveState): SymbolResolveResult? {

    val lastParent = state.get(SymbolResolveHint.LAST_PARENT)
    val arguments = functionCall.parameters
    if (arguments.size == 0) {
        return null
    }

    val firstArgument = arguments[0]
    val inDef = firstArgument == lastParent


    val scopingConstruct = functionCall.scopingConstruct
    if (firstArgument is Symbol) {
        if (firstArgument.fullSymbolName == myStartElement.fullSymbolName) {
            return SymbolResolveResult(firstArgument, scopingConstruct, true)
        }
    } else if (firstArgument is MList) {
        val children = firstArgument.listElements
        for (child in children) {
            var currentChild = child
            if (currentChild is MList) {
                val listElements = currentChild.listElements
                if (listElements.size != 0) {
                    currentChild = listElements[0]
                } else {
                    continue
                }
            }
            if (currentChild is Symbol) {
                if (inDef) {
                    if (currentChild == myStartElement) {
                        return SymbolResolveResult(currentChild, scopingConstruct, true)
                    }
                } else {
                    if (currentChild.fullSymbolName == myStartElement.fullSymbolName) {
                        return SymbolResolveResult(currentChild, scopingConstruct, true)
                    }
                }
            }
        }
    }
    return null
}