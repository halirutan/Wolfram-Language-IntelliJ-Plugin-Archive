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
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.codeInsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.fileTypes.MathematicaFileType;
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
public class SymbolPsiReference extends CachingReference implements PsiReference {

  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();
  private final Symbol variable;

  public SymbolPsiReference(Symbol element) {
    variable = element;
  }

  @Nullable
  @Override
  public PsiElement resolveInner() {
    MathematicaVariableProcessor processor = new MathematicaVariableProcessor(variable);
    PsiTreeUtil.treeWalkUp(processor, variable, variable.getContainingFile(), ResolveState.initial());
    final List<PsiElement> result = processor.getSymbols();
    return result != null && !result.isEmpty() ? result.get(0) : null;
  }


  @Override
  public Symbol getElement() {
    return variable;
  }

  @Override
  public TextRange getRangeInElement() {
    return TextRange.from(0, variable.getFirstChild().getNode().getTextLength());
  }

//  @Override
//  protected TextRange calculateDefaultRangeInElement() {
//    return super.calculateDefaultRangeInElement();
//  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return variable.getMathematicaContext()+"`"+variable.getSymbolName();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return variable.setName(newElementName);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    if (isReferenceTo(element)) {
      return variable;
    }
    return handleElementRename(element.getText());
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    if (NAMES.contains(variable.getSymbolName())) {
      return false;
    }
    if (element instanceof Symbol && ((Symbol) element).getSymbolName().equals(variable.getSymbolName())) {
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
//    final MathematicaVariantProcessor processor = new MathematicaVariantProcessor(getElement());
//    PsiTreeUtil.treeWalkUp(processor, getElement(), containingFile, ResolveState.initial());
//
//    variants.addAll(processor.getSymbols());
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
