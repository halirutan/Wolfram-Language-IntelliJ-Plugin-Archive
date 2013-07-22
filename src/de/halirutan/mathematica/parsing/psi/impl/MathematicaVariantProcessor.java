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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author patrick (5/22/13)
 */
public class MathematicaVariantProcessor extends BaseScopeProcessor {

  private final List<Symbol> mySymbols = Lists.newLinkedList();
  private final Symbol myStartElement;

  public MathematicaVariantProcessor(Symbol myStartElement) {
    super();
    this.myStartElement = myStartElement;
  }

  @Override
  public boolean execute(@NotNull PsiElement element, ResolveState state) {
    if (element instanceof Set || element instanceof SetDelayed) {
      List<Symbol> assignee = MathematicaPsiUtililities.getSymbolsFromFunctionCallPattern(element);
      if (assignee != null) {
        mySymbols.addAll(assignee);
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

  /**
   * Returns the list of all symbols collected during a {@link SymbolPsiReference#getVariants()} run.
   * Before returning the list, it removes duplicates, so that no entry appears more than once in the autocompletion
   * window.
   * @return Sorted and cleaned list of collected symbols.
   */
  public List<Symbol> getSymbols() {

    Collections.sort(mySymbols, new SymbolComparator());
    Pattern pattern = Pattern.compile(myStartElement.getSymbolName().substring(0,1)+".*");
    Symbol tmp = null;
    for (Iterator<Symbol> symbolIterator = mySymbols.iterator(); symbolIterator.hasNext(); ) {
      Symbol next = symbolIterator.next();

      if(!pattern.matcher(next.getSymbolName()).matches()) {
        symbolIterator.remove();
        continue;
      }

      if (tmp == null) {
        tmp = next;
        continue;
      }

      if (tmp.getSymbolName().equals(next.getSymbolName())) {
        symbolIterator.remove();
      } else {
        tmp = next;
      }

    }

    mySymbols.remove(myStartElement);
    return mySymbols;
  }

  class SymbolComparator implements Comparator<Symbol> {

    @Override
    public int compare(Symbol o1, Symbol o2) {
      return (o1.getSymbolName().compareTo(o2.getSymbolName()));
    }

  }

}
