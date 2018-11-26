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

package de.halirutan.mathematica.codeinsight.navigation

import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex
import de.halirutan.mathematica.index.packageexport.PackageExportSymbol
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.resolve.GlobalDefinitionCollector

/**
 * Provides GotoSymbol for all file level definitions that have a usage message.
 * @author patrick (10.05.18).
 */
class GotoSymbolContributor : ChooseByNameContributor {

  private val packageIndex = MathematicaPackageExportIndex.INDEX_ID

  override fun getItemsByName(name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean): Array<NavigationItem> {
    val project1 = project ?: return emptyArray()
    val scope = GlobalSearchScope.projectScope(project1)
    val name1 = name ?: return emptyArray()
    val keys = getPackageExportKeys(project1, includeNonProjectItems)
    val result = keys.filter { k -> k.symbol == name1 }.fold(ArrayList<NavigationItem>()) { accumulator, key ->
      val filesByName = FilenameIndex.getFilesByName(project1, key.fileName, scope)
      filesByName.forEach { file ->
        file?.let {
          val symbol = it.findElementAt(key.offset)?.parent
          if (symbol is Symbol && symbol.symbolName == key.symbol) {
            val c = GlobalDefinitionCollector(symbol.getContainingFile())
            if (c.assignments.containsKey(symbol.symbolName)) {
              c.assignments[symbol.symbolName]?.forEach { assignmentProperty ->
                if (assignmentProperty.myAssignmentSymbol is Symbol && assignmentProperty.myLhsOfAssignment is PsiElement) {
                  accumulator.add(SymbolNavigationItem(assignmentProperty.myAssignmentSymbol, assignmentProperty.myLhsOfAssignment.text))
                }
              }
            }
          }
        }
      }
      accumulator
    }
    return result.toTypedArray()
  }

  override fun getNames(project: Project?, includeNonProjectItems: Boolean): Array<String> {
    val nameArray = MathematicaPackageExportIndex.getSymbolNames(project).toHashSet()
    return nameArray.toTypedArray()
  }

  private fun getPackageExportKeys(project: Project, includeNonProjectItems: Boolean): ArrayList<PackageExportSymbol> {
    val fileIndex = FileBasedIndex.getInstance() ?: return arrayListOf()
    val scope = if (includeNonProjectItems) GlobalSearchScope.projectScope(project) else GlobalSearchScope.allScope(project)
    val result: ArrayList<PackageExportSymbol> = arrayListOf()
    fileIndex.getAllKeys(packageIndex, project).forEach { symbol ->
      symbol?.let {
        val fileForKey = arrayListOf<VirtualFile>()
        val inScope = !fileIndex.processValues(
            packageIndex,
            it,
            null,
            { file, _ -> fileForKey.add(file); false },
            scope
        )

        if (fileForKey.isNotEmpty() && inScope) {
          result.add(it)
        }
      }
    }
    return result
  }
}