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

package de.halirutan.mathematica.codeinsight.completion.providers

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.formatting.blocks.prev
import com.intellij.lang.ASTNode
import com.intellij.openapi.components.ServiceManager
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider
import de.halirutan.mathematica.codeinsight.completion.rendering.BuiltinSymbolLookupElement
import de.halirutan.mathematica.information.SymbolInformation
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes
import de.halirutan.mathematica.settings.MathematicaSettings

/**
 * Provides completion for Mathematica built-in symbols. The underlying important file with all information can be
 * found in the resource directory de/halirutan/mathematica/codeinsight/completion.
 * @author hal (4/2/13)
 */
class BuiltinFunctionCompletion : MathematicaCompletionProvider() {

  private val symbolInfo: SymbolInformation = ServiceManager.getService(SymbolInformation::class.java)

  override fun addTo(contributor: CompletionContributor) {
    val psiElementCapture = psiElement().withElementType(MathematicaElementTypes.IDENTIFIER)
    contributor.extend(CompletionType.BASIC, psiElementCapture, this)
  }

  override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {

    val prefix = findCurrentText(parameters, parameters.position)
    val previousNode: ASTNode? = parameters.position.node.prev()
    if (parameters.invocationCount == 0 && (prefix.isEmpty() || Character.isLowerCase(prefix[0]))) {
      return
    }

    val matcher = CamelHumpMatcher(prefix, true)
    val result2 = result.withPrefixMatcher(matcher)

    // User started a named symbol
    if (previousNode is ASTNode && previousNode.elementType == MathematicaElementTypes.LEFT_BRACKET_ESCAPED) {
      result2.addAllElements(symbolInfo.namedCharacters.map { name ->
        LookupElementBuilder.create(name)
      })
      result2.stopHere()
      return
    }

    val symbols = SymbolInformationProvider.getSystemSymbolInformation()
    val sortByImportance = !MathematicaSettings.getInstance().isSortCompletionEntriesLexicographically

    for (info in symbols.values) {
      val lookup = BuiltinSymbolLookupElement(info)
      if (sortByImportance) {
        result2.addElement(PrioritizedLookupElement.withPriority(lookup, info.importance.toDouble()))
      } else {
        result2.addElement(lookup)
      }
    }

    if (parameters.isExtendedCompletion) {
      result2.addAllElements(SymbolInformationProvider.getAllContexts().map { name -> LookupElementBuilder.create(name) })
      result2.addAllElements(SymbolInformationProvider.getAuxSymbols().map { name -> LookupElementBuilder.create(name) })
    }
  }
}
