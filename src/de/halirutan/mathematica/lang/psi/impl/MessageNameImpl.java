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

package de.halirutan.mathematica.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.Expression;
import de.halirutan.mathematica.lang.psi.api.MessageName;
import de.halirutan.mathematica.lang.psi.api.StringifiedSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created with IntelliJ IDEA. User: patrick Date: 3/27/13 Time: 11:25 PM Purpose:
 */
public class MessageNameImpl extends OperatorNameProviderImpl implements MessageName {
  public MessageNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public boolean isOperatorSign(PsiElement operatorSignElement) {
    return operatorSignElement.toString().contains("DOUBLE_COLON");
  }

  @Override
  public String getOperatorName() {
    return "MessageName";
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MathematicaVisitor) {
      ((MathematicaVisitor) visitor).visitMessageName(this);
    } else {
      super.accept(visitor);
    }
  }

  @Nullable
  @Override
  public Expression getSymbol() {
    return (Expression) getFirstChild();
  }

  @Nullable
  @Override
  public StringifiedSymbol getTag() {
    final PsiElement[] args = getChildren();
    if (args.length >= 2 && args[1] instanceof StringifiedSymbol) {
      return (StringifiedSymbol) args[1];
    }
    return null;
  }

  @Nullable
  @Override
  public StringifiedSymbol getLang() {
    final PsiElement[] args = getChildren();
    if (args.length == 3 && args[2] instanceof StringifiedSymbol) {
      return (StringifiedSymbol) args[2];
    }
    return null;
  }
}
