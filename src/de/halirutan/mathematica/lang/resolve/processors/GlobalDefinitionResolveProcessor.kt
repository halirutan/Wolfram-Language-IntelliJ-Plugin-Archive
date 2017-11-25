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

package de.halirutan.mathematica.lang.resolve.processors

import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiElementProcessor
import de.halirutan.mathematica.lang.psi.LocalizationConstruct
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.assignment.Set
import de.halirutan.mathematica.lang.psi.api.assignment.SetDelayed
import de.halirutan.mathematica.lang.psi.api.assignment.TagSet
import de.halirutan.mathematica.lang.psi.api.assignment.TagSetDelayed
import de.halirutan.mathematica.lang.psi.api.assignment.UpSet
import de.halirutan.mathematica.lang.psi.api.assignment.UpSetDelayed
import de.halirutan.mathematica.lang.psi.impl.assignment.SetDefinitionSymbolVisitor
import de.halirutan.mathematica.lang.psi.impl.assignment.UpSetDefinitionSymbolVisitor
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult

/**
 * @author patrick (1/6/14)
 */
class GlobalDefinitionResolveProcessor(private val myStartElement: Symbol) : PsiElementProcessor<PsiElement> {

    var resolveResult: SymbolResolveResult? = null

    override fun execute(element: PsiElement): Boolean {
        if (element is Set || element is SetDelayed) {
            return visitSetDefinition(element.firstChild)
        }
        if (element is TagSet || element is TagSetDelayed) {
            return visitTagSetDefinition(element.firstChild)
        }
        if (element is UpSet || element is UpSetDelayed) {
            return visitUpSetDefinition(element.firstChild)
        }

        if (element is FunctionCall) {
            val lhs = element.getArgument(1)
            if (element.hasHead("Set") || element.hasHead("SetDelayed")) {
                return visitSetDefinition(lhs)
            } else if (element.hasHead("TagSet") || element.hasHead("TagSetDelayed")) {
                return visitTagSetDefinition(lhs)
            } else if (element.hasHead("UpSet") || element.hasHead("UpSetDelayed")) {
                return visitUpSetDefinition(lhs)
            } else if ((element.hasHead("SetAttributes") || element.hasHead("SetOptions")) && lhs is Symbol) {
                return visitSymbol(lhs)
            }
        }
        return true
    }

    /**
     * Check if a symbol has the same name and if yes, it is my point of definition.
     *
     * @param symbol
     * symbol to check
     * @return true if the names are equal
     */
    private fun visitSymbol(symbol: Symbol): Boolean {
        return if (symbol.localizationConstruct == LocalizationConstruct.MScope.NULL_SCOPE && myStartElement.fullSymbolName == symbol.fullSymbolName) {
            checkIfFound(symbol)
        } else true
    }


    private fun visitUpSetDefinition(lhs: PsiElement?): Boolean {
        if (lhs != null) {
            val definitionVisitor = UpSetDefinitionSymbolVisitor()
            lhs.accept(definitionVisitor)
            val definitionSymbols = definitionVisitor.unboundSymbols
            definitionSymbols
                    .filter { it.fullSymbolName == myStartElement.fullSymbolName }
                    .forEach { return checkIfFound(it) }
        }
        return true
    }

    /**
     * TagSet should be trivial. In f /: g[a,b,..,f,..] = .., f is always expected to be a symbol.
     */
    private fun visitTagSetDefinition(defSymbol: PsiElement?): Boolean {
        return if (defSymbol is Symbol && defSymbol.fullSymbolName.matches(myStartElement.fullSymbolName.toRegex())) {
            checkIfFound(defSymbol)
        } else true
    }

    private fun visitSetDefinition(lhs: PsiElement?): Boolean {
        if (lhs != null) {
            val definitionVisitor = SetDefinitionSymbolVisitor(lhs)
            lhs.accept(definitionVisitor)
            val definitionSymbols = definitionVisitor.unboundSymbols
            definitionSymbols
                    .filter { it.fullSymbolName == myStartElement.fullSymbolName }
                    .forEach { return checkIfFound(it) }
        }
        return true
    }

    private fun checkIfFound(possibleDefinition: Symbol): Boolean {
        if (!possibleDefinition.isLocallyBound) {
            resolveResult = SymbolResolveResult(possibleDefinition, LocalizationConstruct.MScope.FILE_SCOPE, possibleDefinition.containingFile, true)
            return false
        }
        return true
    }

}
