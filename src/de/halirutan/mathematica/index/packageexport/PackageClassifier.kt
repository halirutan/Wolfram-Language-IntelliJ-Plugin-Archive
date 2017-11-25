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

package de.halirutan.mathematica.index.packageexport

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import de.halirutan.mathematica.index.PackageUtil
import de.halirutan.mathematica.lang.psi.MathematicaVisitor
import de.halirutan.mathematica.lang.psi.api.CompoundExpression
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.MessageName
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.assignment.Set
import de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities
import java.util.*

/**
 * Building a symbol export index for package files.
 * Only symbols with a usage message at file level (not nested in deeper code) will be recognized
 * @author patrick (17.12.16).
 */
class PackageClassifier internal constructor() : MathematicaVisitor() {

  val myExportInfo: HashSet<PackageExportSymbol> = HashSet()
  private val myContextStack: Stack<String> = Stack()
  private var myFileName: String = ""

  override fun visitFile(file: PsiFile) {
    myFileName = file.name
    file.acceptChildren(this)
  }

  override fun visitCompoundExpression(compoundExpression: CompoundExpression) {
    compoundExpression.acceptChildren(this)
  }

  override fun visitFunctionCall(functionCall: FunctionCall) {
    if (functionCall.matchesHead("BeginPackage")) {
      val beginPackageContext = MathematicaPsiUtilities.getBeginPackageContext(functionCall)
      myContextStack.push(beginPackageContext ?: "")
    } else if (functionCall.matchesHead("Begin")) {
      val beginContext = MathematicaPsiUtilities.getBeginContext(functionCall)
      myContextStack.push(beginContext ?: "")
    } else if (functionCall.matchesHead("End") || functionCall.matchesHead("EndPackage")) {
      if (!myContextStack.empty()) {
        myContextStack.pop()
      }
    }
  }

  override fun visitSet(set: Set) {
    val lhs = set.firstChild
    if (lhs is MessageName) {
      val tag = lhs.tag
      if ("usage" == (if (tag != null) tag.text else "")) {
        val symbol = lhs.symbol
        if (symbol is Symbol) {
          val context = PackageUtil.buildContext(myContextStack)
          myExportInfo.add(PackageExportSymbol(myFileName, context, (symbol as Symbol).symbolName, true, (symbol as PsiElement).textOffset))
        }
      }
    }
  }
}
