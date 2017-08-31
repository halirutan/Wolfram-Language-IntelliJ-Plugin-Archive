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

package de.halirutan.mathematica.lang.resolve

import com.intellij.openapi.util.TextRange
import junit.framework.TestCase

/**
 * @author patrick (20.07.17).
 */
class ModuleResolveTestCase : AbstractResolveTest() {

    fun testSimpleModule() {
        val psiReference = configure()
        val resolve = psiReference.resolve()
        TestCase.assertNotNull(resolve)
        TestCase.assertEquals(TextRange.create(8, 9), resolve?.textRange)
    }

    fun testNestedModule() {
        val ref = configure()
        val resolve = ref.resolve()
        TestCase.assertNotNull(resolve)
        TestCase.assertEquals(TextRange.create(24,25), resolve?.textRange)
    }

    fun testCompileSingle() {
        val ref = configure()
        val resolve = ref.resolve()
        TestCase.assertNotNull(resolve)
        TestCase.assertEquals(TextRange.create(8,11), resolve?.textRange)
    }

    fun testCompileWithTypes() {
        val ref = configure()
        val resolve = ref.resolve()
        TestCase.assertNotNull(resolve)
        TestCase.assertEquals(TextRange.create(25,26), resolve?.textRange)
    }

    fun testCompileWithMixedTypes() {
        val ref = configure()
        val resolve = ref.resolve()
        TestCase.assertNotNull(resolve)
        TestCase.assertEquals(TextRange.create(21,22), resolve?.textRange)
    }

}
