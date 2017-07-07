/*
 * Copyright (c) 2014 Patrick Scheibe
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

package de.halirutan.mathematica.lang.psi.impl.assignment;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.Group;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.lists.List;
import de.halirutan.mathematica.lang.psi.api.pattern.Condition;
import de.halirutan.mathematica.lang.psi.api.pattern.Pattern;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author patrick (7/3/14)
 */
public class UpSetDefinitionSymbolVisitor extends MathematicaVisitor {
  private final LinkedHashSet<Symbol> myUnboundSymbols = Sets.newLinkedHashSet();
  private boolean isZeroLevel = true;

  public Set<Symbol> getUnboundSymbols() {
    return myUnboundSymbols;
  }

  @Override
  public void visitCondition(Condition condition) {
    PsiElement firstChild = condition.getFirstChild();
    if (firstChild != null) {
      firstChild.accept(this);
    }
  }

  @Override
  public void visitFunctionCall(FunctionCall functionCall) {
    if (isZeroLevel) {
      isZeroLevel = false;
      final PsiElement[] children = functionCall.getChildren();
      boolean skipHead = true;
      for (PsiElement child : children) {
        if (skipHead) {
          skipHead = false;
          continue;
        }
        child.accept(this);
      }
    } else {
      final PsiElement head = functionCall.getHead();
      if (head instanceof Symbol) {
        myUnboundSymbols.add((Symbol) head);
      } else {
        // situations like this a[p:b, c, d] ^:= ... where the patterns or something else is involved.
        head.accept(this);
      }
    }
  }

  /**
   * Need this for situations like <code>p:f[x_]:=x^2</code>
   */
  @Override
  public void visitPattern(final Pattern pattern) {
    final PsiElement lastChild = pattern.getLastChild();
    if (lastChild != null) {
      lastChild.accept(this);
    }
  }

  @Override
  public void visitGroup(Group group) {
    group.acceptChildren(this);
  }

  @Override
  public void visitList(List list) {
    list.acceptChildren(this);
  }


  @Override
  public void visitSymbol(final Symbol symbol) {
    myUnboundSymbols.add(symbol);
  }

}
