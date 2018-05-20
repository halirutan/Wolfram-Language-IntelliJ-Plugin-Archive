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

package de.halirutan.mathematica.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import de.halirutan.mathematica.MathematicaBundle
import de.halirutan.mathematica.lang.psi.LocalizationConstruct
import de.halirutan.mathematica.lang.psi.api.OperatorNameProvider
import de.halirutan.mathematica.lang.psi.api.StringifiedSymbol
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.slots.Slot
import de.halirutan.mathematica.lang.psi.impl.LightBuiltInSymbol
import de.halirutan.mathematica.lang.psi.impl.LightFileSymbol
import de.halirutan.mathematica.lang.psi.util.MathematicaPsiElementFactory
import de.halirutan.mathematica.lang.psi.util.extractUsageMessageString
import java.util.*
import java.util.regex.Pattern

/**
 * Provides documentation aka rendered usage messages for built-in functions, operators and user-defined functions
 *
 * @author patrick (4/4/13)
 */
class MathematicaDocumentationProvider : AbstractDocumentationProvider() {

  private val logger = Logger.getInstance(MathematicaDocumentationProvider::class.java)

  /**
   * Generates the documentation (if available) for element. This does three things:
   *
   *
   *
   *  * it provides documentation if the symbol near the cursor is a built in function
   *  * it provides documentation when it is called over operators like ++
   *  * it checks if user functions have a usage message and presents this
   *
   *
   * @param element         Element which was possibly altered by [.getCustomDocumentationElement] or by
   * [.getDocumentationElementForLookupItem] if the lookup was active
   * @param originalElement The original element for which the doc was called (possibly whitespace)
   *
   * @return The html string of the usage message or null if it could not be loaded
   */
  override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {

    val slotPattern = Pattern.compile("#[0-9]*.*")
    val slotSequencePattern = Pattern.compile("##[0-9]*.*")

    fun buildPath(name: String, context: String = ""): String = "usages/$context${name.replace('`', '/')}.html"
    if (!(element is Symbol || element is LightBuiltInSymbol || element is OperatorNameProvider || element is Slot)) {
      return null
    }

    val path: String = when (element) {
      is OperatorNameProvider -> buildPath(element.operatorName, "System/")
      is Symbol -> {
        val name = element.fullSymbolName
        when {
          name.contains('`') -> buildPath(name)
          else -> buildPath(name, "System/")
        }
      }
      is Slot -> {
        val name = element.text
        when {
          slotPattern.matcher(name).matches() -> buildPath("Slot", "System/")
          slotSequencePattern.matcher(name).matches() -> buildPath("SlotSequence", "System/")
          else -> ""
        }
      }
      else -> ""
    }

    if (path.isNotEmpty()) {
      MathematicaDocumentationProvider::class.java.getResourceAsStream(path)?.let {
        val usage = Scanner(it, "UTF-8").useDelimiter("\\A").next()
        if (!usage.isEmpty()) {
          return usage
        }
      }
    }

    // Inject the usage message of functions that are declared inside the package
    if (element is Symbol) {
      renderCustomUsageMessage(element).let {
        if (it.isNotEmpty()) return it
      }
    }
    return null
  }

  /**
   * Provides a html form of the usage message for custom user functions.
   * @param symbol a file symbol for which the usage message should be found and rendered
   * @return the first found usage message
   */
  private fun renderCustomUsageMessage(symbol: Symbol): String {
    val usages = symbol.extractUsageMessageString()
    if (usages.component2().isEmpty()) {
      return ""
    }
    val fileName = usages.component1().containingFile.name
    val symbolName = symbol.symbolName
    val result = StringBuilder("<h3>")
    result.append(symbolName)
    result.append(" (")
    result.append(fileName)
    result.append(")</h3><ul>")
    for (usg in usages.component2()) {
      result.append("<li>")
      result.append(usg.replace("($symbolName)".toRegex(), "<b>$1</b>"))
//      result.append(usg)
      result.append("</li>")
    }
    result.append("</ul>")
    return result.toString()
  }

  /**
   * Calculates the correct element for which the user wants documentation.
   *
   * @param editor         The editor of the file
   * @param file           The file which is edited and where the doc call was made
   * @param contextElement The element where the caret was when the doc was called
   *
   * @return The element for which the user wants documentation. If an item of the completion list is currently
   * highlighted, then this element. If the cursor is over/beside an identifier, then the symbol element. As last thing
   * it is determined whether the PsiElement is the operator-sign of an operation, then we get the corresponding
   * operation psi implementation element back.
   */
  override fun getCustomDocumentationElement(editor: Editor, file: PsiFile, contextElement: PsiElement?): PsiElement? {
    var docElement: PsiElement?

    if (contextElement != null) {
      docElement = contextElement.parent
    } else {
      var offset = editor.caretModel.currentCaret.offset
      offset = if (offset > 0) offset - 1 else offset
      docElement = PsiTreeUtil.findElementOfClassAtOffset(file, offset, Symbol::class.java, false)
      docElement = docElement ?: file.findElementAt(offset)
    }

    return if (docElement != null && docElement !is Symbol && docElement !is OperatorNameProvider && docElement !is Slot) {
      docElement.parent
    } else {
      docElement
    }
  }

  /**
   * This makes it possible to have the documentation for each function while scrolling through the completion
   * suggestion list.
   *
   * @param psiManager access to Psi related things
   * @param object     the current lookup object
   * @param element    the element, the documentation was initially called for. Note that this is typically not a valid built-in
   * function, because you start typing Plo then the completion box pops up and when you call documentation on one
   * of the selected lookup entries, the elements name is still Plo, while you want to check the documentation for
   * the lookup element.
   *
   * @return The Symbol which was created from the string of the lookup element or null if it wasn't possible.
   */
  override fun getDocumentationElementForLookupItem(psiManager: PsiManager?, `object`: Any?, element: PsiElement?): PsiElement? {
    val manager = psiManager ?: return null
    val elementFactory = MathematicaPsiElementFactory(manager.project)
    `object`?.let {
      logger.debug("Looking up doc for completion element $it")
      try {
        elementFactory.createExpressionFromText(it.toString())?.let {
          return when (it) {
            is Symbol -> it
            is StringifiedSymbol -> it
            else -> null
          }
        }
      } catch (exception: IncorrectOperationException) {
        logger.debug("Invalid lookup element expression")
      }
    }
    return null
  }

  /**
   * Provides a quick info when the user hovers code with CTRL pressed
   * @param element element to show quick navigation. This is the resolved element which means we can check which kind of light symbol it is
   * @param originalElement the original symbol in file
   * @return quick info what happens when the user performs a mouse click
   */
  override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
    if (element is LightBuiltInSymbol) {
      return MathematicaBundle.message("doc.navi.builtin", element.name)
    }
    if (element is LightFileSymbol) {
      return if (originalElement is Symbol && originalElement.localizationConstruct == LocalizationConstruct.MScope.NULL_SCOPE) {
        MathematicaBundle.message("doc.navi.invalid", element.name)
      } else MathematicaBundle.message("doc.navi.navto", element.name)
    }
    return if (element is Symbol) {
      MathematicaBundle.message("doc.navi.navto", element.fullSymbolName)
    } else super.getQuickNavigateInfo(element, originalElement)
  }
}
