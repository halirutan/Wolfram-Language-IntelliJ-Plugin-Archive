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

package de.halirutan.mathematica.codeinsight.navigation

import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.util.MathematicaIcons
import javax.swing.Icon

/**
 * A simple navigation item that is used in the GotoSymbol contributor
 * @author patrick (10.05.18).
 */

class SymbolNavigationItem(val symbol: Symbol, val context: String) : NavigationItem, ItemPresentation {
  override fun navigate(requestFocus: Boolean) {
    (symbol as NavigationItem).navigate(requestFocus)
  }

  override fun getPresentation(): ItemPresentation? {
    return object : ItemPresentation {
      override fun getLocationString(): String? {
        val file = symbol.containingFile
        val fileName = file?.let { "(${it.name})" } ?: ""
        return "$context $fileName"
      }

      override fun getIcon(unused: Boolean): Icon? {
        return MathematicaIcons.FILE_ICON
      }

      override fun getPresentableText(): String? {
        return name
      }
    }
  }

  override fun canNavigate(): Boolean {
    return true
  }

  override fun getName(): String? {
    return symbol.symbolName
  }

  override fun canNavigateToSource(): Boolean {
    return true
  }

  override fun getLocationString(): String? {
    return symbol.mathematicaContext
  }

  override fun getIcon(unused: Boolean): Icon? {
    return MathematicaIcons.FILE_ICON
  }

  override fun getPresentableText(): String? {
    return name
  }
}