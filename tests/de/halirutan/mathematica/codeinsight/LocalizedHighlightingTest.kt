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

package de.halirutan.mathematica.codeinsight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors
import de.halirutan.mathematica.file.MathematicaFileType
import junit.framework.TestCase

class LocalizedHighlightingTest : LightCodeInsightFixtureTestCase() {

  fun testModule() {
    checkMathematicaHighlighting("Module[{variable = 3}, variable]",
        listOf(
            Pair("Module", MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL),
            Pair("variable", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED),
            Pair("variable", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
        ))
  }

  fun testWith() {
    checkMathematicaHighlighting("With[{variable = 3}, variable]",
        listOf(
            Pair("With", MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL),
            Pair("variable", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED),
            Pair("variable", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
        )
    )

    checkMathematicaHighlighting("With[{variable = 3}, {var2 = 4}, variable + var2]",
        listOf(
            Pair("With", MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL),
            Pair("variable", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED),
            Pair("var2", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED),
            Pair("variable", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED),
            Pair("var2", MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED)
        )
    )
  }

  fun testFunction() {
    checkMathematicaHighlighting("Function[x, x]",
        listOf(
            Pair("Function", MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL),
            Pair("x", MathematicaSyntaxHighlighterColors.PATTERN),
            Pair("x", MathematicaSyntaxHighlighterColors.PATTERN)
        ))

    checkMathematicaHighlighting("Function[{x, y}, x + y]",
        listOf(
            Pair("Function", MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL),
            Pair("x", MathematicaSyntaxHighlighterColors.PATTERN),
            Pair("y", MathematicaSyntaxHighlighterColors.PATTERN),
            Pair("x", MathematicaSyntaxHighlighterColors.PATTERN),
            Pair("y", MathematicaSyntaxHighlighterColors.PATTERN)
        ))

    checkMathematicaHighlighting("#1 + #2 &",
        listOf(
            Pair("#1", MathematicaSyntaxHighlighterColors.SLOT),
            Pair("#1 + #2 &", MathematicaSyntaxHighlighterColors.ANONYMOUS_FUNCTION),
            Pair("#2", MathematicaSyntaxHighlighterColors.SLOT)
        ))


    checkMathematicaHighlighting("Function[Null, #]",
        listOf(
            Pair("Function", MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL),
            Pair("Null", MathematicaSyntaxHighlighterColors.KERNEL_SYMBOL),
            Pair("#", MathematicaSyntaxHighlighterColors.SLOT)
        ))
  }


  private fun checkMathematicaHighlighting(code: String, highlights: List<Pair<String, TextAttributesKey>>) {
    myFixture.configureByText(MathematicaFileType.INSTANCE, code)
    val result = myFixture.doHighlighting()
    TestCase.assertEquals("Mismatch in number of highlighted symbols", result.size, highlights.size)
    result.forEachIndexed { i, info ->
      TestCase.assertNotNull(info)
      TestCase.assertEquals("Mismatch in text that is highlighted", highlights[i].first, info.text)
      TestCase.assertEquals("Mismatch in assigned highlighter color", highlights[i].second, info.forcedTextAttributesKey)
    }

  }

}