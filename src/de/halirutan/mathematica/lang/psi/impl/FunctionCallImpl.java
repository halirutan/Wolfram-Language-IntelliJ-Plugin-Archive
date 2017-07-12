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
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct.MScope;
import org.jetbrains.annotations.NotNull;

public class FunctionCallImpl extends ExpressionImpl implements FunctionCall {

  private final Key<Object> myScopeKey = Key.create("SCOPING_CONSTRUCT");
  private boolean myIsUpToDate;
  private String myHead;


  public FunctionCallImpl(@NotNull ASTNode node) {
    super(node);
    myIsUpToDate = false;
    myHead = node.getFirstChildNode().getText();
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    final PsiElement head = getFirstChild();
    if (head instanceof Symbol) {
      // In a tree-up-walk, we only consider declarations of Module, Block, .. when we come from inside this Module.
      // Therefore, we need to check whether our last position was inside and if not, we don't consider the declarations
      // of this.
      if (lastParent.getParent() != this) {
        return true;
      }
      final String symbolName = ((Symbol) head).getSymbolName();
      cacheScopingConstruct(symbolName);
      if (isScopingConstruct()) {
        return processor.execute(this, state);
      }
    }
    return true;
  }

  @Override
  public PsiElement getHead() {
    return getFirstChild();
  }

  @Override
  public boolean matchesHead(final String head) {
    return myHead != null && head != null && myHead.matches(head);
  }

  public boolean hasHead(@NotNull final String otherHead) {
    return myHead.equals(otherHead);
  }

  public boolean hasHead(@NotNull final String[] heads) {
    for (String head : heads) {
      if (head.equals(myHead)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public PsiElement getArgument(int n) {
    for (PsiElement child : getChildren()) {
      if (n > 0) {
        n--;
        continue;
      }
      return child;
    }
    return null;
  }

  @Override
  public void subtreeChanged() {
    super.subtreeChanged();
    myIsUpToDate = false;
  }

  /**
   * Extracts the head of the function call and looks whether it is in the list {@link #SCOPING_CONSTRUCTS}. This can
   * lead to various false negatives. E.g. <code >(Block)[{..},..]</code> returns false, although after <em
   * >evaluating</em> the code in Mathematica, it's of course found to be a correct scoping construct. Btw, the
   * Mathematica front end has the same issues.
   *
   * @return True iff the head is a symbol defining the function as scoping construct like <code >Block[{..},..]</code>.
   */

  @Override
  public boolean isScopingConstruct() {

    if (!myIsUpToDate) {
      cacheScopingConstruct();
    }
    MScope type = (LocalizationConstruct.MScope) getUserData(myScopeKey);
    return type != null && !type.equals(MScope.NULL);
  }

  public LocalizationConstruct.MScope getScopingConstruct() {
    if (!myIsUpToDate) {
      cacheScopingConstruct();
    }
    return (LocalizationConstruct.MScope) getUserData(myScopeKey);
  }

  private void cacheScopingConstruct() {
    if (myIsUpToDate) return;
    PsiElement head = getFirstChild();
    if (head instanceof Symbol) {
      cacheScopingConstruct(((Symbol) head).getSymbolName());
    } else {
      putUserData(myScopeKey, MScope.NULL);
    }
    myIsUpToDate = true;
  }

  private void cacheScopingConstruct(String functionName) {
    putUserData(myScopeKey, LocalizationConstruct.getType(functionName));
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MathematicaVisitor) {
      ((MathematicaVisitor) visitor).visitFunctionCall(this);
    } else {
      super.accept(visitor);
    }
  }

  @Override
  public boolean headMatches(final Class<?> clazz) {
    final String head = getFirstChild().getText();
    return clazz.getSimpleName().matches(head);
  }
}
