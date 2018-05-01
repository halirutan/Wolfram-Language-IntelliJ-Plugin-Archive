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

package de.halirutan.mathematica.lang.resolve

import com.intellij.openapi.util.TextRange
import junit.framework.TestCase

/**
 * Tests if local variables are resolved correctly
 * @author patrick (20.07.17).
 */
class LocalVariableResolveTestCase : AbstractResolveTest() {

  private val moduleLike = mapOf(
      "Module[{x}, <ref>x]" to TextRange.create(8, 9),
      "Module[{x,y,z}, Module[{x}, y+<ref>x]]" to TextRange.create(24, 25),
      // the second x should refer to the outside x
      "Module[{x},Module[{x = <ref>x}, y+x]]" to TextRange.create(8, 9)
  )

  private val compileLike = mapOf(
      "Compile[var, Sin[var] + <ref>var];" to TextRange.create(8, 11),
      "Compile[{{x, _Real, 0}, {y, _Integer, 1}}, x+<ref>y]" to TextRange.create(25, 26),
      "Compile[{{x, _Real}, y, {z, _Real, 0}}, x+<ref>y+z ]" to TextRange.create(21, 22)
  )

  private val ruleDelayedLike = mapOf(
      "{test_, test_} :> <ref>test" to TextRange.create(1, 5),
      "HoldComplete[ReturnPacket[{expr_ :> expr_}]] :> HoldComplete[<ref>expr]" to TextRange.create(27, 31)
  )

  private val functionLike = mapOf(
      "Function[{val, key}, val + <ref>key]" to TextRange.create(15, 18),
      "Function[val, <ref>val]" to TextRange.create(9, 12)

  )

  fun testModule() {
    for ((key, value) in moduleLike) {
      val psiReference = configureByFileText(key)
      val resolve = psiReference.resolve()
      TestCase.assertNotNull(resolve)
      TestCase.assertEquals(value, resolve?.textRange)
    }
  }

  fun testCompile() {
    for ((key, value) in compileLike) {
      val ref = configureByFileText(key)
      val resolve = ref.resolve()
      TestCase.assertNotNull(resolve)
      TestCase.assertEquals(value, resolve?.textRange)
    }
  }

  fun testRuleDelayed() {
    for ((key, value) in ruleDelayedLike) {
      val ref = configureByFileText(key)
      val resolve = ref.resolve()
      TestCase.assertNotNull(resolve)
      TestCase.assertEquals(value, resolve?.textRange)
    }
  }

  fun testFunction() {
    for ((key, value) in functionLike) {
      val ref = configureByFileText(key)
      val resolve = ref.resolve()
      TestCase.assertNotNull(resolve)
      TestCase.assertEquals(value, resolve?.textRange)
    }
  }


}
