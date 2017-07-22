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
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.resolve.MathematicaSymbolResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Provides functionality to resolve where a certain symbol is defined in code. For this, the SymbolPsiReference class
 * uses several processors which scan the local scope and global file scope. Note that GlobalDefinitionResolveProcessor
 * does not scan the whole file because this would be too slow. Instead, it expects that global symbol definitions are
 * done at file-scope. The class uses caching to speed up the resolve process. Once a definition for a symbol is found,
 * it is stored as long as the code in the concerning areas is not edited.
 *
 * @author patrick (5/8/13)
 */
public class SymbolPsiReference implements PsiReference {

  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();
  private final Symbol mySymbol;

  SymbolPsiReference(Symbol element) {
    mySymbol = element;
  }


  @Override
  public Symbol getElement() {
    return mySymbol;
  }

  @Override
  public TextRange getRangeInElement() {
    return TextRange.from(0, mySymbol.getTextLength());
  }

  private static final MathematicaSymbolResolver RESOLVER = new MathematicaSymbolResolver();

  @Nullable
  @Override
  public PsiElement resolve() {
//    ResolveCache resolveCache = ResolveCache.getInstance(mySymbol.getProject());
//    resolveCache.clearCache(true);
//    return resolveCache.resolveWithCaching(this, RESOLVER, false, false);
    return null;
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return mySymbol.getFullSymbolName();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return mySymbol.setName(newElementName);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    if (isReferenceTo(element)) {
      return mySymbol;
    }
    return handleElementRename(element.getText());
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    final PsiReference reference = element.getReference();
    if (reference != null) {
      final PsiElement resolve = reference.resolve();
      return resolve instanceof Symbol && ((Symbol) resolve).getFullSymbolName().equals(mySymbol.getFullSymbolName());
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
    return false;
  }

}
