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

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import de.halirutan.mathematica.filetypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct.ConstructType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

/**
 * Implementation of Mathematica symbols which are probably the most important elements of a parse tree. Symbols in
 * Mathematica are not only the variables you use. Due to the <em>data is code</em> paradigm of Mathematica, even the
 * functions you call like <code>Sqrt[2]</code> are expression having a symbol as head (the <code>Sqrt</code>).
 * <p/>
 * Symbols with explicit context like <code>Developer`ToPackedArray</code> are parsed as one symbol and this class
 * provides methods to separate the parts.
 *
 * @author patrick (3/28/13)
 */
public class SymbolImpl extends ExpressionImpl implements Symbol {

  private final HashSet<Symbol> myReferringElements = new HashSet<Symbol>();
  private boolean myIsUpToDate;
  private ConstructType myLocalization;
  private Symbol myDefinitionElement;
  private PsiElement myLocalizationElement;

  public SymbolImpl(@NotNull ASTNode node) {
    super(node);
    myLocalization = ConstructType.NULL;
    myDefinitionElement = null;
    myLocalizationElement = null;
    myIsUpToDate = false;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) {
    ASTNode identifierNode = getNode().findChildByType(MathematicaElementTypes.IDENTIFIER);
    final PsiFileFactory fileFactory = PsiFileFactory.getInstance(getProject());
    final MathematicaPsiFileImpl file = (MathematicaPsiFileImpl) fileFactory.createFileFromText("dummy.m", MathematicaFileType.INSTANCE, name);
    ASTNode newElm = file.getFirstChild().getNode().findChildByType(MathematicaElementTypes.IDENTIFIER);
    if (identifierNode != null && newElm != null) {
      getNode().replaceChild(identifierNode, newElm);
    }
    return this;
  }

  @Override
  public String getName() {
    return getText();
  }

  @Override
  public String getMathematicaContext() {
    String myName = getName();
    String context = "System`";
    if (myName != null) {
      if (myName.contains("`")) {
        context = myName.substring(0, myName.lastIndexOf('`') + 1);
      }
    }
    return context;
  }

  @Override
  public String getSymbolName() {
    String myName = getName();
    if (myName == null) return "";
    if (myName.lastIndexOf('`') == -1) {
      return myName;
    } else {
      return myName.substring(myName.lastIndexOf('`') + 1, myName.length());
    }
  }

  @Override
  public String getFullSymbolName() {
    return getName();
  }

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return this;
  }

  @Override
  public PsiReference getReference() {
    return new SymbolPsiReference(this);
  }

  @Override
  public void subtreeChanged() {
    for (Symbol myReferringElement : myReferringElements) {
      myReferringElement.subtreeChanged();
    }
    myReferringElements.clear();
    if (myLocalizationElement instanceof SymbolImpl) {
      ((SymbolImpl) myLocalizationElement).subtreeChanged();
    }
    myIsUpToDate = false;
    myLocalizationElement = null;
    myLocalization = ConstructType.NULL;
  }

  public boolean cachedResolve() {
    return myIsUpToDate;
  }

  public Symbol getResolveElement() {
    if (myIsUpToDate) {
      return myDefinitionElement;
    }
    return null;
  }

  public ConstructType getLocalizationConstruct() {
    if (myIsUpToDate && myLocalization != null) {
      return myLocalization;
    }
    return ConstructType.NULL;
  }

  @Override
  public void setReferringElement(Symbol referringSymbol, ConstructType type, PsiElement localizationElement) {
    myDefinitionElement = referringSymbol;
    referringSymbol.addElementReferencingToMe(this);
    myLocalizationElement = localizationElement;
    myLocalization = type;
    myIsUpToDate = true;
  }

  @Override
  public void addElementReferencingToMe(Symbol reference) {
    if (!reference.equals(this)) myReferringElements.add(reference);
  }

  @Override
  public PsiElement[] getElementsReferencingToMe() {
    return myReferringElements.toArray(new Symbol[myReferringElements.size()]);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MathematicaVisitor) {
      ((MathematicaVisitor) visitor).visitSymbol(this);
    } else {
      super.accept(visitor);
    }
  }

  @NotNull
  @Override
  public PsiReference[] getReferences() {
    return ReferenceProvidersRegistry.getReferencesFromProviders(this);
  }

}
