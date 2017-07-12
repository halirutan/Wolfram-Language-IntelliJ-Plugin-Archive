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

package de.halirutan.mathematica.lang.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct.MScope;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (08.07.17).
 */
public class SymbolResolveResult implements ResolveResult {

  private final PsiElement myElement;
  private final boolean myIsValid;
  private MScope myLocalization;

  SymbolResolveResult(PsiElement element, LocalizationConstruct.MScope scope, boolean isValid) {
    this.myElement = element;
    this.myLocalization = scope;
    this.myIsValid = isValid;
  }

  @Nullable
  @Override
  public PsiElement getElement() {
    return myElement;
  }

  public LocalizationConstruct.MScope getLocalization() {
    return myLocalization;
  }

  @Override
  public boolean isValidResult() {
    return myIsValid;
  }
}
