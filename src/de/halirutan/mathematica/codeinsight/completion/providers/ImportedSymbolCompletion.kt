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
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.ProgressManager
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import de.halirutan.mathematica.codeinsight.completion.MathematicaCompletionContributor.IMPORT_VARIABLE_PRIORITY
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes
import de.halirutan.mathematica.lang.psi.api.Symbol


/**
 * Accesses the file index to provide completion for functions that are defined in other packages.
 */
class ImportedSymbolCompletion : MathematicaCompletionProvider() {

  override fun addTo(contributor: CompletionContributor) {
    val symbolPattern = psiElement().withElementType(MathematicaElementTypes.IDENTIFIER)
    contributor.extend(CompletionType.BASIC, symbolPattern, this)
  }

  override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
    val callingSymbol = parameters.position.parent
    val project = callingSymbol.project

    val prefix = findCurrentText(parameters, parameters.position)
    if (parameters.invocationCount == 0 && prefix.isEmpty() || callingSymbol !is Symbol) {
      return
    }
    val originalFile = parameters.originalFile
    val module = ModuleUtilCore.findModuleForFile(originalFile.virtualFile, project)
    if (module != null) {
      val moduleScope = module.getModuleWithDependenciesAndLibrariesScope(true)
      val index = FileBasedIndex.getInstance()
      val indexID = MathematicaPackageExportIndex.INDEX_ID
      index.getAllKeys(indexID, project).forEach {
        it?.let {
          val inScope = !index.processValues(
              indexID,
              it,
              null,
              { file, _ -> file == originalFile.virtualFile },
              moduleScope
          )
          ProgressManager.checkCanceled()
          if (inScope && it.isExported) {
            result.addElement(PrioritizedLookupElement.withPriority(
                LookupElementBuilder.create(it.symbol).withTypeText("(" + it.fileName + ")", true),
                IMPORT_VARIABLE_PRIORITY))
          }
        }
      }
    }
  }
}
