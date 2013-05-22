/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.parsing.psi.impl;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.codeInsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author patrick (5/8/13)
 */
public class SymbolPsiReference extends PsiReferenceBase<Symbol> implements PsiPolyVariantReference{

  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();


  public SymbolPsiReference(Symbol element, TextRange range) {
    super(element, range);
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    List<Symbol> result = null;
    final PsiFile file = myElement.getContainingFile();
    Symbol[] allSymbols = PsiTreeUtil.getChildrenOfType(file, Symbol.class);
    if (allSymbols != null) {
      for (Symbol currentSymbol : allSymbols) {
        if (getValue().equals(currentSymbol.getSymbolName())) {
          if (result == null) {
            result = new ArrayList<Symbol>();
          }
          result.add(currentSymbol);
        }
      }
    }

    List<ResolveResult> resolveResults = new ArrayList<ResolveResult>();
    if (result != null) {
      for (Symbol symbol : result) {
        resolveResults.add(new PsiElementResolveResult(symbol));
      }
    }
    return resolveResults.toArray(new ResolveResult[resolveResults.size()]);

  }

  @Nullable
  @Override
  public PsiElement resolve() {
    ResolveResult[] resolveResults = multiResolve(false);
    return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
  }

  @Override
  public void setRangeInElement(TextRange range) {
    super.setRangeInElement(range);
  }

  @NotNull
  @Override
  public String getValue() {
    return super.getValue();
  }

  @Override
  public Symbol getElement() {
    return super.getElement();
  }

  @Override
  public TextRange getRangeInElement() {
    return super.getRangeInElement();
  }

  @Override
  protected TextRange calculateDefaultRangeInElement() {
    return super.calculateDefaultRangeInElement();
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return super.getCanonicalText();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return super.handleElementRename(newElementName);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return super.bindToElement(element);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return super.isReferenceTo(element);
  }

  @Override
  public boolean isSoft() {
    return super.isSoft();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final PsiFile containingFile = myElement.getContainingFile();

    List<Symbol> variants = Lists.newArrayList();

    final SymbolVariantProcessor processor = new SymbolVariantProcessor(getElement());
    PsiTreeUtil.treeWalkUp(processor, getElement(), containingFile, ResolveState.initial());

    variants.addAll(processor.getSymbols());

    List<LookupElement> lookupElements = new ArrayList<LookupElement>();
    for (Symbol currentSymbol : variants) {
      if (!NAMES.contains(currentSymbol.getSymbolName())) {
        lookupElements.add(LookupElementBuilder.create(currentSymbol));
      }
    }
    return lookupElements.toArray();
  }
}
