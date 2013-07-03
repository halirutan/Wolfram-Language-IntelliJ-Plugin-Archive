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
import de.halirutan.mathematica.parsing.psi.api.function.Apply;
import org.jetbrains.annotations.NotNull;

/**
 * This is the base class for all psi elements which are operations and have an operation-sign. It is used to check and
 * find the correct element if we want documentation of an operator like @@@ or /@
 *
 * @author patrick (5/5/13)
 */
public class OperatorNameProvider extends ExpressionImpl {

  public OperatorNameProvider(@NotNull ASTNode node) {
    super(node);
  }

  /**
   * Checks whether operatorSignElement is the correct sign for our operation. Example: You wrote code like a <code >@@</code> b and
   * you are inside <code >@@@</code> with the cursor and call QuickDocumentation. Then PsiElement is "PsiElement(APPLY)" and the
   * parent element in the parse tree is an instance of {@link Apply}. Now the method checks this and concludes, that
   * <code >@@</code> is indeed the operator sign to Apply.
   * <p/>
   * On the contrary, say you are beside a comma in the code {a,b,c} then the comma is not the operator sign of List
   * and the function returns false.
   *
   * @param operatorSignElement Operator sign to check
   * @return true if operatorSignElement is the operator sign of this expression
   */
  public boolean isOperatorSign(PsiElement operatorSignElement) {
    String name = "PsiElement(" + this.toString() + ')';
    return operatorSignElement.toString().replace("_", "").toLowerCase().equals(name.toLowerCase());
  }

  /**
   * Returns the Mathematica name of this operator: @@ is Apply (and @@@ too!) or #2 is Slot
   *
   * @return Mathematica operator name
   */
  public String getOperatorName() {
    return this.toString();
  }

}
