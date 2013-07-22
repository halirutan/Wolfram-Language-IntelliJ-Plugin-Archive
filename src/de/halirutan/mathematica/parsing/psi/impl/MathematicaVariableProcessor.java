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
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.function.Function;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author patrick (5/22/13)
 */
public class MathematicaVariableProcessor extends BaseScopeProcessor {

  private final List<PsiElement> mySymbols = Lists.newLinkedList();
  private final Symbol myStartElement;

  public MathematicaVariableProcessor(Symbol myStartElement) {
    this.myStartElement = myStartElement;
  }

  @Override
  public boolean execute(@NotNull PsiElement element, ResolveState state) {
    if (element instanceof FunctionCall) {
      if (((FunctionCall) element).isScopingConstruct()) {
        final List<Symbol> vars = MathematicaPsiUtililities.extractLocalizedVariables(element);
        for (Symbol v : vars) {
          if (v.getSymbolName().equals(myStartElement.getSymbolName()) && v != myStartElement) {
            mySymbols.add(v);
            return false;
          }
        }
      }
    } else if (element instanceof Function) {
      if(myStartElement.getFirstChild().getNode().getElementType().equals(MathematicaElementTypes.SLOT)) {
        mySymbols.add(element);
        return false;
      }
    }
    return true;
  }


  /**
   * Returns the list of all symbols collected during a {@link de.halirutan.mathematica.parsing.psi.impl.SymbolPsiReference#getVariants()} run.
   * Before returning the list, it removes duplicates, so that no entry appears more than once in the autocompletion
   * window.
   *
   * @return Sorted and cleaned list of collected symbols.
   */
  public List<PsiElement> getSymbols() {
    return mySymbols;
  }


}
