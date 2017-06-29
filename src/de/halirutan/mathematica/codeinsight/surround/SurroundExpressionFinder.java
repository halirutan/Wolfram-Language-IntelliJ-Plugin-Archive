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

package de.halirutan.mathematica.codeinsight.surround;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.Number;
import de.halirutan.mathematica.parsing.psi.api.StringifiedSymbol;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.slots.Slot;
import de.halirutan.mathematica.parsing.psi.api.slots.SlotExpression;

/**
 * When SurroundWith is pressed without having an explicit selection, we need to find the best expression the user
 * might want to surround. This visitor will try to achieve this by walking upwards in the syntax tree and ignoring
 * useless candidates. For instance when the caret is over a symbol it is often not wanted to surround this symbol.
 * Instead we are looking for the wider context which often is a function call, a list, an association, a rule, or any
 * other non-trivial expression.
 * <p>
 * Be aware that this will only be called when the user did not explicitly made a selection and really asks us to make
 * an educated guess!
 *
 * @author patrick (04.06.17).
 */
public class SurroundExpressionFinder extends MathematicaVisitor {

  private PsiElement myBestExpression = null;

  PsiElement getBestExpression() {
    return myBestExpression;
  }

  private void walkUp(PsiElement element) {
    if (element.getContext() != null) {
      element.getContext().accept(this);
    }
  }

  @Override
  public void visitElement(PsiElement element) {
    if (element instanceof LeafPsiElement) {
      walkUp(element);
    } else {
      myBestExpression = element;
    }
  }

  @Override
  public void visitFile(PsiFile file) {
  }

  @Override
  public void visitCompoundExpression(CompoundExpression compoundExpression) {
    walkUp(compoundExpression);
  }

  @Override
  public void visitSymbol(Symbol symbol) {
    walkUp(symbol);
  }

  @Override
  public void visitStringifiedSymbol(StringifiedSymbol stringifiedSymbol) {
    walkUp(stringifiedSymbol);
  }

  @Override
  public void visitSlot(Slot slot) {
    walkUp(slot);
  }

  @Override
  public void visitSlotExpression(SlotExpression slotExpr) {
    walkUp(slotExpr);
  }

  @Override
  public void visitWhiteSpace(PsiWhiteSpace space) {
    walkUp(space);
  }

  @Override
  public void visitNumber(Number number) {
    walkUp(number);
  }
}
