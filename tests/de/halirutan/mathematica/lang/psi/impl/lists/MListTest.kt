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

package de.halirutan.mathematica.lang.psi.impl.lists

import com.intellij.testFramework.PsiTestCase
import de.halirutan.mathematica.lang.psi.api.lists.MList
import de.halirutan.mathematica.lang.psi.util.MathematicaPsiElementFactory
import org.junit.Assert
import org.junit.Test

/**
 * We need to carefully test, that [MList] can extract its elements even when it contains whitespace, comments or
 * implicit multiplications.
 * @author patrick (30.10.17).
 */
class MListTest : PsiTestCase() {

  private val testCases = mapOf(
      "{1,2,3}" to arrayOf("1", "2", "3"),
      "{1 1, 2, 3}" to arrayOf("1 1", "2", "3"),
      "{x (*inline comment*), 2 ,3}" to arrayOf("x", "2", "3"),
      "{x,      \n\n\ny}" to arrayOf("x", "y"),
      "{\n\n(*inline comment*)x(*inline comment*)" to arrayOf("x"),
      // here we silently ignore, that no Null is created because the parser will mark it as error anyway
      "{x,,y}" to arrayOf("x", "y")
  )

  @Test
  @Throws(Exception::class)
  fun testGetListElements() {
    val factory = MathematicaPsiElementFactory(myProject)
    for (case in testCases) {
      val expr = factory.createExpressionFromText(case.key)
      Assert.assertTrue(expr is MList)
      val listElements = (expr as MList).listElements
      Assert.assertTrue(listElements.size == case.value.size)
      listElements.forEachIndexed({ i, e ->
        Assert.assertEquals(e.text, case.value[i])
      })
      for (listElement in listElements) {

      }
    }
  }

}