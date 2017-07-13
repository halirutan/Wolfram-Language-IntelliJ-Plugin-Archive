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

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.file.MathematicaFileType;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct.MScope;
import de.halirutan.mathematica.lang.resolve.MathematicaSymbolResolver;
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of Mathematica symbols which are probably the most important elements of a parse tree. Symbols in
 * Mathematica are not only the variables you use. Due to the <em>data is code</em> paradigm of Mathematica, even the
 * functions you call like <code>Sqrt[2]</code> are expression having a symbol as head (the <code>Sqrt</code>).
 * <p/>
 * Symbols with explicit context like <code>Developer`ToPackedArray</code> are parsed as one symbol and this class
 * provides methods to separate the parts.
 * <br/>
 *  Provides functionality to resolve where a certain symbol is defined in code. For this, the SymbolPsiReference class
 * uses several processors which scan the local scope and global file scope. Note that GlobalDefinitionResolveProcessor
 * does not scan the whole file because this would be too slow. Instead, it expects that global symbol definitions are
 * done at file-scope. The class uses caching to speed up the resolve process. Once a definition for a symbol is found,
 * it is stored as long as the code in the concerning areas is not edited.
 *
 * @author patrick (3/28/13)
 */
public class SymbolImpl extends ExpressionImpl implements Symbol {

  private static final MathematicaSymbolResolver RESOLVER = new MathematicaSymbolResolver();

  public SymbolImpl(ASTNode node) {
    super(node);
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
    String context = "";
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


  public MScope getLocalizationConstruct() {
//    if (myIsUpToDate && myLocalization != null) {
//      return myLocalization;
//    }
    return MScope.NULL;
  }

  @Override
  public PsiElement[] getElementsReferencingToMe() {
//    if (myReferringElements.isEmpty()) return PsiElement.EMPTY_ARRAY;
//    return myReferringElements.toArray(new Symbol[myReferringElements.size()]);
    return PsiElement.EMPTY_ARRAY;
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MathematicaVisitor) {
      ((MathematicaVisitor) visitor).visitSymbol(this);
    } else {
      super.accept(visitor);
    }
  }

  @Override
  public String toString() {
    return "Symbol";
  }

  @Override
  public PsiElement getElement() {
    return this;
  }

  @Override
  public TextRange getRangeInElement() {
    return TextRange.create(0, getTextLength());
  }

  @Override
  public PsiReference getReference() {
    return this;
  }


  @Nullable
  @Override
  public PsiElement resolve() {
    final SymbolResolveResult symbolResolveResult = advancedResolve();
    if (symbolResolveResult != null) {
      return symbolResolveResult.getElement();
    }
    return null;
  }

  @Override
  public SymbolResolveResult advancedResolve() {
    ResolveCache resolveCache = ResolveCache.getInstance(getProject());
    return resolveCache.resolveWithCaching(this, RESOLVER, true, false);
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return getFullSymbolName();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return setName(newElementName);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return null;
  }

  /**
   * This method is used by {@link PsiManager#areElementsEquivalent(PsiElement, PsiElement)}
   * @param another the other element which is tested to be equal to this element
   * @return true if the full symbol name is the same.
   */
  @Override
  public boolean isEquivalentTo(PsiElement another) {
    if (super.isEquivalentTo(another)) {
      return true;
    }
    if (another instanceof PsiReference) {
      final PsiElement myDef = resolve();
      final PsiElement otherDef = ((PsiReference) another).resolve();
      return myDef != null && otherDef != null && myDef == otherDef;
    }
    return false;
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return getManager().areElementsEquivalent(resolve(), element);
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
