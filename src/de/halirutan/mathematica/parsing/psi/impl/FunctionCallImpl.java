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
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct;
import org.jetbrains.annotations.NotNull;

public class FunctionCallImpl extends ExpressionImpl implements FunctionCall {

  private final Key<Object> myScopeKey = Key.create("SCOPING_CONSTRUCT");


  public FunctionCallImpl(@NotNull ASTNode node) {
    super(node);
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
  public void subtreeChanged() {
    clearUserData();
  }

  /**
   * Extracts the head of the function call and looks whether it is in the list {@link #SCOPING_CONSTRUCTS}. This can
   * lead to various false negatives. E.g. <code >(Block)[{..},..]</code> returns false, although after <em
   * >evaluating</em> the code in Mathematica, it's of course found to be a correct scoping construct. Btw, the
   * Mathematica front end has the same issues.
   *
   * @return True iff the head is a symbol defining the function as scoping construct like <code
   *         >Block[{..},..]</code>.
   */

  @Override
  public boolean isScopingConstruct() {

    if (isUserDataEmpty()) {
      cacheScopingConstruct();
    }
    LocalizationConstruct.ConstructType type = (LocalizationConstruct.ConstructType) getUserData(myScopeKey);
    return type != null && !type.equals(LocalizationConstruct.ConstructType.NULL);
  }

  public LocalizationConstruct.ConstructType getScopingConstruct() {
    if (isUserDataEmpty()) {
      cacheScopingConstruct();
    }
    return (LocalizationConstruct.ConstructType) getUserData(myScopeKey);
  }

  private void cacheScopingConstruct() {
    PsiElement head = getFirstChild();
    if (head instanceof Symbol) {
      cacheScopingConstruct(((Symbol) head).getSymbolName());
    } else {
      putUserData(myScopeKey, LocalizationConstruct.ConstructType.NULL);
    }
  }

  private void cacheScopingConstruct(String functionName) {
    if (isUserDataEmpty()) {
      putUserData(myScopeKey, LocalizationConstruct.getType(functionName));
    }
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MathematicaVisitor) {
      ((MathematicaVisitor) visitor).visitFunctionCall(this);
    } else {
      super.accept(visitor);
    }
  }
}
