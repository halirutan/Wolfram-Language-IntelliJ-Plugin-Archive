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
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.Set;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author patrick (5/22/13)
 */
public class SymbolVariantProcessor extends BaseScopeProcessor {

  private final List<Symbol> mySymbols = Lists.newLinkedList();
  private final Symbol myStartElement;

  public SymbolVariantProcessor(Symbol myStartElement) {
    super();
    this.myStartElement = myStartElement;
  }

  @Override
  public boolean execute(@NotNull PsiElement element, ResolveState state) {
    if (element instanceof Set || element instanceof SetDelayed) {
      Symbol assignee = MathematicaPsiUtililities.getAssignmentSymbol(element);
      if (assignee != null) {
        mySymbols.add(assignee);
      }
    }

    if (element instanceof FunctionCall) {
      List<Symbol> declaredSymbols = MathematicaPsiUtililities.extractLocalizedVariables(element);
      if (declaredSymbols.size() > 0) {
        mySymbols.addAll(declaredSymbols);
      }
    }
    return true;
  }

  public List<Symbol> getSymbols() {
    mySymbols.remove(myStartElement);
    return mySymbols;
  }
}
