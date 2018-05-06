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

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import de.halirutan.mathematica.lang.psi.MathematicaRecursiveVisitor
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.Group
import de.halirutan.mathematica.lang.psi.api.Number
import de.halirutan.mathematica.lang.psi.api.StringifiedSymbol
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.pattern.Blank
import de.halirutan.mathematica.lang.psi.api.pattern.BlankNullSequence
import de.halirutan.mathematica.lang.psi.api.pattern.BlankSequence
import de.halirutan.mathematica.lang.psi.api.string.MString

/**
 * Aims to provide a FullForm that is close to what Mathematica has.
 * We cannot completely reach this goal but this function is helpful to inspect parser errors since it makes precedences
 * explicit.
 * @author patrick (03.09.17).
 */
class MathematicaFullFormCreator : MathematicaRecursiveVisitor() {

  private val myBuilder = StringBuilder()
  private var myCharCount = 0

  override fun visitElement(element: PsiElement) {
    ProgressIndicatorProvider.checkCanceled()
    if (element is PsiWhiteSpace || element is PsiComment) {
      return
    }
    addContent(element)
    createEnclosedExpression(element.children, element.textLength > 40 || myCharCount > 120)
  }

  override fun visitFile(file: PsiFile) {
    val children = file.children
    for (child in children) {
      if (child is PsiWhiteSpace || child is PsiComment) {
        continue
      }
      child.accept(this)
      addLineFeed()
      addLineFeed()
    }
  }

  override fun visitFunctionCall(functionCall: FunctionCall) {
    functionCall.head.accept(this)
    val parameters = functionCall.parameters
    val parameterArray = parameters.toTypedArray<PsiElement>()
    createEnclosedExpression(parameterArray, functionCall.textLength > 30 || myCharCount > 120)
  }

  override fun visitSymbol(symbol: Symbol) {
    addContent(symbol.text)
  }

  override fun visitNumber(number: Number) {
    addContent(number.text)
  }

  override fun visitString(mString: MString) {
    addContent(mString.text)
  }

  override fun visitBlank(blank: Blank) {
    visitBlankLikeElement(blank)
  }

  override fun visitBlankSequence(blankSequence: BlankSequence) {
    visitBlankLikeElement(blankSequence)
  }

  override fun visitBlankNullSequence(blankNullSequence: BlankNullSequence) {
    visitBlankLikeElement(blankNullSequence)
  }

  private fun visitBlankLikeElement(blank: PsiElement) {
    if (blank is Blank || blank is BlankSequence || blank is BlankNullSequence) {
      val head = blank.toString()
      val children = blank.children
      when (children.size) {
        0 -> addContent("$head[]")
        1, 2 -> {
          addContent("Pattern[")
          children[0].accept(this)
          if (children.size == 2) {
            addContent(",$head[")
            children[1].accept(this)
            addContent("]")
          }
          addContent("]")
        }
      }
    }
  }

  override fun visitGroup(group: Group) {
    val children = group.children
    if (children.size == 1) {
      children[0].accept(this)
    }
  }

  override fun visitStringifiedSymbol(stringifiedSymbol: StringifiedSymbol) {
    addContent("\"" + stringifiedSymbol.text + "\"")
  }

  private fun createEnclosedExpression(elements: Array<PsiElement>?, makeLineBreak: Boolean) {
    if (elements == null) {
      addContent("[]")
    } else {
      addContent("[")
      if (makeLineBreak) {
        addLineFeed()
      }
      myCharCount++
      for (i in elements.indices) {
        if (elements[i] is PsiWhiteSpace || elements[i] is PsiComment) {
          continue
        }
        elements[i].accept(this)
        if (i != elements.size - 1) {
          addContent(",")
          if (makeLineBreak) {
            addLineFeed()
          }
        }
      }
      if (makeLineBreak) {
        addLineFeed()
      }
      addContent("]")
    }
  }

  private fun addContent(element: PsiElement) {
    addContent(element.toString())
  }

  private fun addContent(content: String) {
    myBuilder.append(content)
    myCharCount += content.length
  }

  private fun addLineFeed() {
    myBuilder.append("\n")
    myCharCount = 0
  }

  fun getFullForm(element: PsiElement): String {
    element.accept(this)
    return myBuilder.toString()
  }


}
