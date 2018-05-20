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

package de.halirutan.mathematica.lang.psi.util

import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import de.halirutan.mathematica.lang.psi.api.MessageName
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.assignment.Set
import de.halirutan.mathematica.lang.psi.api.string.MString

/** Provides a the feature of searching for a usage element and extract its usage message string
 *
 * @author patrick (02.12.17).
 */

fun Symbol.extractUsageMessageString(): Pair<Symbol, List<String>> {
  var result: Pair<Symbol, List<String>> = Pair(this, emptyList())
  val resolve = resolve() ?: return result

  // if resolve is a real symbol, then already point to the usage message in another file
  // this might change in future
  // TODO: Keep above in mind
//  if (resolve is Symbol) {
//    return Pair(resolve, doUsageMessageExtract(resolve))
//  }
  val progressManager = ProgressManager.getInstance()
  val progressIndicator = progressManager.progressIndicator ?: return result
  progressManager.runProcess({
    // Find all references and look if one of them is a usage message
    ReferencesSearch.search(resolve).find { psiReference ->
      ProgressManager.checkCanceled()
      val elm = psiReference.element ?: return@find false
      if (elm is Symbol) {
        val messages = doUsageMessageExtract(elm)
        if (messages.isNotEmpty()) {
          result = Pair(elm, messages)
        }
      }
      return@find false
    }
  }, progressIndicator)
  return result
}

/**
 * Takes a symbol that is located at the code `symbol::usage = ...` and extracts the left string. The it splits the
 * string at "\n" because this often indicates a new usage for a different call pattern. In addition, it removes the
 * surrounding quotes and the ellipsis character with "..."
 * @Note The lhs can be a combination of `<>` and this function will process the joined string of all parts
 */
private fun doUsageMessageExtract(symbol: Symbol): List<String> {
  if (symbol.parent is MessageName) {
    val messageName = symbol.parent as MessageName
    if (messageName.isUsageMessage) {
      val set = messageName.parent
      if (set is Set) {
        var message = ""
        when {
          set.lastChild is MString -> message = set.lastChild.text.removeSurrounding("\"")
          else -> {
            val strings = PsiTreeUtil.findChildrenOfType(set.lastChild, MString::class.java)
            strings.forEach { result -> message += result.text.removeSurrounding("\"") }
          }
        }
        return message.replace("\\[Ellipsis]", "...").split("\\n")
      }
    }
  }
  return emptyList()
}