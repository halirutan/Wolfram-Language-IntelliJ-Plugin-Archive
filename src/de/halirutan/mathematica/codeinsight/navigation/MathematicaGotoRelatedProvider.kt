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

package de.halirutan.mathematica.codeinsight.navigation

import com.intellij.navigation.GotoRelatedItem
import com.intellij.navigation.GotoRelatedProvider
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.containers.SortedList
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes
import de.halirutan.mathematica.lang.psi.api.Symbol
import java.util.*

/**
 * Provides functionality to navigate through different usages of the symbol under the caret.
 * It will take the correct scope which means that it won't stupidly highlight symbols with the same name.
 * Rather, it will find out if the symbol is globally defined, in a Module, or in any other scoping construct.
 * Then, the list of suggestions contains only correct places which really refer to usages of the current symbol.
 * @author patrick (28.12.16).
 */
class MathematicaGotoRelatedProvider : GotoRelatedProvider() {

    override fun getItems(psiElement: PsiElement): List<GotoRelatedItem> {
        // I want the entries in the suggestion window to be sorted by line number!
        val symbol = when {
            psiElement is Symbol -> psiElement
            psiElement is LeafPsiElement && psiElement.elementType == MathematicaElementTypes.IDENTIFIER -> psiElement.parent
            else -> psiElement
        }
        val declarations = SortedList(Comparator.comparingInt<GotoSymbolItem>({ it.lineNumber }))
        if (symbol is Symbol) {
            val containingFile = symbol.getContainingFile()
            val resolve = symbol.resolve() ?: return declarations
            val usages = ReferencesSearch.search(resolve, GlobalSearchScope.fileScope(containingFile)).findAll()
            val project = symbol.getProject()
            val documentManager = PsiDocumentManager.getInstance(project)
            val fileName = containingFile.name
            val document = documentManager.getDocument(containingFile) ?: return declarations
            for (usage in usages) {
                val usageElement = usage.element
                if (usageElement is Symbol && usageElement.isValid) {
                    // What follows is that want to collect code around the found element.
                    // I will collect neighbouring PsiElements but not more than 20 characters to the right and
                    // to the left of the current usageElement.
                    val elementTextOffset = usageElement.getTextOffset()
                    val lineNumber = document.getLineNumber(elementTextOffset)
                    val lineStartOffset = document.getLineStartOffset(lineNumber)
                    val lineEndOffset = document.getLineEndOffset(lineNumber)
                    assert(lineStartOffset <= lineEndOffset)

                    var textToShow = document.getText(TextRange.create(lineStartOffset, lineEndOffset)).trim { it <= ' ' }
                    textToShow = if (textToShow.length > 80) textToShow.substring(0, 80) else textToShow

                    val item = GotoSymbolItem(
                            usageElement,
                            textToShow,
                            "(line ${lineNumber + 1} in $fileName)",
                            lineNumber)
                    declarations.add(item)
                }
            }
        }
        return declarations
    }

}
