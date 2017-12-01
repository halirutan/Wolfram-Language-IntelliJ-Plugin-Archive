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

package de.halirutan.mathematica.lang.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (10.07.17).
 */
public class LightSymbol extends LightElement implements PsiNamedElement {
  private String myName;
  private final PsiFile myFile;

  public LightSymbol(@NotNull Symbol symbol) {
    super(symbol.getManager(), MathematicaLanguage.INSTANCE);
    myName = symbol.getText();
    myFile = symbol.getContainingFile();
  }

  @Override
  public String toString() {
    return myName;
  }

  @NotNull
  @Override
  public String getName() {
    return myName;
  }

  @Override
  public PsiFile getContainingFile() {
    return myFile;
  }

  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    myName = name;
    return this;
  }

  @NotNull
  @Override
  public SearchScope getUseScope() {
    return myFile.getUseScope();
  }

  @Override
  public int hashCode() {
    int hash = 1;
    hash = hash*17 + myName.hashCode();
    hash = hash * 31 + myFile.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof LightSymbol && obj.hashCode() == hashCode();
  }

  @Override
  public boolean isEquivalentTo(PsiElement another) {
    return another instanceof LightSymbol && another.hashCode() == hashCode();
  }
}
