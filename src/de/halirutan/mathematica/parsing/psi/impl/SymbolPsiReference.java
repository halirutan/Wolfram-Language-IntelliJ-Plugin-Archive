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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.codeInsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.util.MathematicaVariableProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author patrick (5/8/13)
 */
public class SymbolPsiReference extends CachingReference implements PsiReference {

  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();
  private final Symbol myVariable;

  public SymbolPsiReference(Symbol element) {
    myVariable = element;
  }

  @Nullable
  @Override
  public PsiElement resolveInner() {
    if (myVariable.cachedResolve()) {
      if (myVariable.getSymbolName().equals(myVariable.getResolveElement().getSymbolName())) {
        return myVariable.getResolveElement();
      } else {
        myVariable.subtreeChanged();
      }

    }
    MathematicaVariableProcessor processor = new MathematicaVariableProcessor(myVariable);
    PsiTreeUtil.treeWalkUp(processor, myVariable, myVariable.getContainingFile(), ResolveState.initial());
    final PsiElement referringSymbol = processor.getMyReferringSymbol();
    if (referringSymbol instanceof Symbol) {
      myVariable.setReferringElement((Symbol) referringSymbol, processor.getMyLocalization(), processor.getMyLocalizationSymbol());
      return referringSymbol;
    }
    return null;
  }


  @Override
  public Symbol getElement() {
    return myVariable;
  }

  @Override
  public TextRange getRangeInElement() {
    return TextRange.from(0, myVariable.getFirstChild().getNode().getTextLength());
  }

//  @Override
//  protected TextRange calculateDefaultRangeInElement() {
//    return super.calculateDefaultRangeInElement();
//  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return myVariable.getMathematicaContext()+"`"+ myVariable.getSymbolName();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return myVariable.setName(newElementName);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    if (isReferenceTo(element)) {
      return myVariable;
    }
    return handleElementRename(element.getText());
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    if (NAMES.contains(myVariable.getSymbolName())) {
      return false;
    }
    if (element instanceof Symbol && ((Symbol) element).getSymbolName().equals(myVariable.getSymbolName())) {
      return super.isReferenceTo(element);
    }
    return false;
  }

  @Override
  public boolean isSoft() {
    return super.isSoft();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @NotNull
  @Override
  public String getUnresolvedMessagePattern() {
    return "unresolved var";
  }

  //  @NotNull
//  @Override
//  public Object[] getVariants() {
//    final PsiFile containingFile = myElement.getContainingFile();
//
//    List<Symbol> variants = Lists.newArrayList();
//
//    final MathematicaDefinedSymbolsProcessor processor = new MathematicaDefinedSymbolsProcessor(getElement());
//    PsiTreeUtil.treeWalkUp(processor, getElement(), containingFile, ResolveState.initial());
//
//    variants.addAll(processor.getMyReferringSymbol());
//
//
//    List<LookupElement> lookupElements = new ArrayList<LookupElement>();
//    for (Symbol currentSymbol : variants) {
//      if (!NAMES.contains(currentSymbol.getSymbolName())) {
//        lookupElements.add(LookupElementBuilder.create(currentSymbol));
//      }
//    }
//    return lookupElements.toArray();
//  }
}
