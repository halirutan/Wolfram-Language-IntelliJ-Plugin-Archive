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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct;
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (10.07.17).
 */
public class LightSymbol extends LightElement implements Symbol {
  private final PsiFile myFile;
  private String myName;


  public LightSymbol(@NotNull final Symbol symbol) {
    super(symbol.getManager(), MathematicaLanguage.INSTANCE);
    myName = symbol.getText();
    myFile = symbol.getContainingFile();
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor){
      visitor.visitElement(this);
  }

  @Override
  public String toString() {
    return "LightSymbol[" + myName + "]";
  }

  @Override
  public PsiFile getContainingFile() {
    return myFile;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof LightSymbol) {
      return ((LightSymbol) obj).getSymbolName().equals(myName);
    }
    return false;
  }

  @Override
  public boolean isEquivalentTo(PsiElement another) {
    if (another instanceof PsiReference) {
      final PsiElement resolve = ((PsiReference) another).resolve();
      return resolve != null && resolve.equals(this);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 1;
    hash = hash * 31 + myName.hashCode();
    return hash;
  }

  @Override
  public String getMathematicaContext() {
    return null;
  }

  public String getSymbolName() {
    return myName;
  }

  @Override
  public String getFullSymbolName() {
    return myName;
  }

  @Override
  public LocalizationConstruct.MScope getLocalizationConstruct() {
    return null;
  }

  @Override
  public PsiElement[] getElementsReferencingToMe() {
    return new PsiElement[0];
  }

  @Override
  public SymbolResolveResult advancedResolve() {
    return null;
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return this;
  }

  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    myName = name;
    return this;
  }

  @Override
  public PsiElement getElement() {
    return this;
  }

  @Override
  public TextRange getRangeInElement() {
    return TextRange.create(0, getTextLength());
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return this;
  }

  @Override
  public String getText() {
    return myName;
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return getText();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return setName(newElementName);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return null;
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    if (element == this) {
      return true;
    }
    if (element instanceof PsiReference) {
      return ((PsiReference) element).resolve() == this;
    }
    return false;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @Override
  public boolean isSoft() {
    return true;
  }
}
