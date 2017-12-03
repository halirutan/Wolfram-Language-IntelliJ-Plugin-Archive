package de.halirutan.mathematica.lang.psi.util

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbService
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import de.halirutan.mathematica.lang.psi.api.MessageName
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.assignment.Set
import de.halirutan.mathematica.lang.psi.api.string.MString

/**
 *
 * @author patrick (02.12.17).
 */

fun extractUsageMessageString(symbol: Symbol): Pair<Symbol, List<String>> {
  val resolve = symbol.resolve() ?: return Pair(symbol, emptyList())

  if (resolve is Symbol) {
    return Pair(resolve, doUsageMessageExtract(resolve))
  }

  var result: Pair<Symbol, List<String>> = Pair(symbol, emptyList())

  DumbService.getInstance(symbol.project).runReadActionInSmartMode {
    val elements = ReferencesSearch.search(resolve)
    for (elm in elements) {
      ProgressManager.checkCanceled()
      if (elm != null && elm.element is Symbol) {
        val sym = elm.element as Symbol
        val r = doUsageMessageExtract(sym)
        if (r.isNotEmpty()) {
          result = Pair(sym, r)
          break
        }
      }
    }
  }
  return result
}

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