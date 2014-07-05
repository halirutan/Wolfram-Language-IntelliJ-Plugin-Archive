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

package de.halirutan.mathematica.parsing.psi.impl.assignment;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Group;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.lists.List;
import de.halirutan.mathematica.parsing.psi.api.pattern.Condition;
import de.halirutan.mathematica.parsing.psi.api.pattern.Pattern;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author patrick (7/3/14)
 */
public class SetDefinitionSymbolVisitor extends MathematicaVisitor {
  private final LinkedHashSet<Symbol> myUnboundSymbols = Sets.newLinkedHashSet();
  private final PsiElement myStartElement;

  public SetDefinitionSymbolVisitor(final PsiElement startElement) {
    this.myStartElement = startElement;
  }

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
    final PsiElement head = functionCall.getHead();
    if (head instanceof Symbol) {
      // check if we have an assignment of the form Options[sym] = {...}
      if (functionCall.equals(myStartElement) && functionCall.matchesHead("Options|Attributes|MessageName|Default|Format|N")) {
        PsiElement arg1 = functionCall.getArgument(1);
        if (arg1 instanceof Symbol) arg1.accept(this);
      } else {
        myUnboundSymbols.add((Symbol) head);
      }
    } else {
      // situations like this (g : fff)[x_^2] := Hold[g, x] where the head contains something more complex
      head.accept(this);
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
