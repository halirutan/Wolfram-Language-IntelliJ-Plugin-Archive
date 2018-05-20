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

package de.halirutan.mathematica.lang.resolve

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities.isBuiltInSymbol
import de.halirutan.mathematica.lang.resolve.processors.GlobalDefinitionResolveProcessor
import java.util.*

/**
 * The symbol resolver works currently in 3 steps to find a possible definition of a symbol that appears in the code.
 * It will check if the symbol is a built-in symbol
 * It will make a tree-walk upwards to check if the symbol is in any localization construct
 * It will check the file, if the symbol is defined as a global symbol like a function at file-scope
 * It will check the file-index and look for symbols that are exported from other files
 *
 * @author patrick (08.07.17).
 */
class MathematicaGlobalSymbolResolver {

  private val packageIndex = MathematicaPackageExportIndex.INDEX_ID

  fun resolve(ref: Symbol, containingFile: PsiFile): Array<ResolveResult> {

    if (containingFile != ref.containingFile) {
      return ResolveResult.EMPTY_ARRAY
    }

    val symbolCache = MathematicaGlobalResolveCache.getInstance(containingFile.project)

    if (symbolCache.containsSymbol(ref)) {
      return arrayOf(symbolCache.getValue(ref))
    }

    if (isBuiltInSymbol(ref)) {
      val result = symbolCache.cacheBuiltInSymbol(ref)
      return arrayOf(result)
    }

    val globalProcessor = GlobalDefinitionResolveProcessor(ref)
    PsiTreeUtil.processElements(containingFile, globalProcessor)

    val resolveResult = globalProcessor.resolveResult
    resolveResult?.let {
      val result = symbolCache.cacheFileSymbol(resolveResult, containingFile)
      if (containingFile is MathematicaPsiFile) {
        containingFile.cacheLocalDefinition(result)
      }
      return arrayOf(result)
    }

    val cacheInvalidResult: () -> Array<ResolveResult> = { arrayOf(symbolCache.cacheInvalidFileSymbol(ref, containingFile)) }

    val project = containingFile.project
    containingFile.virtualFile?.let { virtualFile ->
      val module = ModuleUtilCore.findModuleForFile(virtualFile, project) ?: return@let
      val references = ArrayList<SymbolResolveResult>()
      val psiManager = PsiManager.getInstance(project)
      val fileIndex = FileBasedIndex.getInstance() ?: return@let
      val moduleScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
      fileIndex.getAllKeys(packageIndex, project).forEach {
        it?.let {
          val fileForKey = arrayListOf<VirtualFile>()
          val inScope = !fileIndex.processValues(
              packageIndex,
              it,
              null,
              { file, _ -> fileForKey.add(file); false },
              moduleScope
          )

          if (fileForKey.isNotEmpty() && inScope && it.isExported && it.symbol == ref.fullSymbolName) {
            val psiFile = psiManager.findFile(fileForKey.first()) ?: return@forEach
            val externalSymbol = PsiTreeUtil.findElementOfClassAtOffset(psiFile, it.offset, Symbol::class.java, true)
                ?: return@forEach
            val result = symbolCache.cacheExternalSymbol(ref, externalSymbol, psiFile)
            references.add(result)
            return references.toTypedArray()
          }
        }
      }
    }

    if (isAuxSymbol(ref)) {
      val result = symbolCache.cacheBuiltInSymbol(ref)
      return arrayOf(result)
    }
    return cacheInvalidResult()
  }

  private fun isAuxSymbol(symbol: Symbol): Boolean {
    val context = symbol.fullSymbolName ?: return false
    return SymbolInformationProvider.getAuxSymbols()?.contains(context) ?: false
  }

}
