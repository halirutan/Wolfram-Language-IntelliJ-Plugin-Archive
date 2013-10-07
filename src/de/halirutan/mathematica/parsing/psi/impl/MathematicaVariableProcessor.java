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

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.parsing.psi.api.function.Function;
import de.halirutan.mathematica.parsing.psi.api.rules.RuleDelayed;
import de.halirutan.mathematica.parsing.psi.util.MathematicaPsiUtililities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author patrick (5/22/13)
 */
public class MathematicaVariableProcessor extends BaseScopeProcessor {

  private final Symbol myStartElement;
  private PsiElement myReferringSymbol;
  private LocalizationConstruct.ConstructType myLocalization;
  private PsiElement myLocalizationSymbol;

  public MathematicaVariableProcessor(Symbol myStartElement) {
    this.myStartElement = myStartElement;
    this.myReferringSymbol = null;
    this.myLocalization = LocalizationConstruct.ConstructType.NULL;

  }

  @Override
  public boolean execute(@NotNull PsiElement element, ResolveState state) {
    if (element instanceof FunctionCall) {
      if (((FunctionCall) element).isScopingConstruct()) {
        final List<Symbol> vars = MathematicaPsiUtililities.extractLocalizedVariables(element);
        for (Symbol v : vars) {
          if (v.getSymbolName().equals(myStartElement.getSymbolName())) {
            myReferringSymbol = v;
            myLocalizationSymbol = element.getFirstChild();
            myLocalization = ((FunctionCall) element).getScopingConstruct();
            return false;
          }
        }
      }
    } else if (element instanceof Function) {
      if (myStartElement.getFirstChild().getNode().getElementType().equals(MathematicaElementTypes.SLOT)) {
        myReferringSymbol = element.getLastChild();
        return false;
      }
    } else if (element instanceof SetDelayed) {
      final List<Symbol> patterns = MathematicaPsiUtililities.getPatternSymbols(element);
      if (patterns != null) {
        for (Symbol p : patterns) {
          if (p.getSymbolName().equals(myStartElement.getSymbolName())) {
            myReferringSymbol = p;
            myLocalization = LocalizationConstruct.ConstructType.SETDELAYEDPATTERN;
            return false;
          }
        }
      }
    } else if (element instanceof RuleDelayed) {
      PsiElement lhs = element.getFirstChild();
      List<Symbol> symbolsFromArgumentPattern = MathematicaPsiUtililities.getSymbolsFromArgumentPattern(lhs);
      for (Symbol symbol : symbolsFromArgumentPattern) {
        if (symbol.getSymbolName().equals(myStartElement.getSymbolName())) {
          myReferringSymbol = symbol;
          myLocalization = LocalizationConstruct.ConstructType.RULEDELAYED;
          return false;
        }
      }


    }
    return true;
  }

  /**
   * Returns the list of all symbols collected during a {@link SymbolPsiReference#getVariants()} run. Before returning
   * the list, it removes duplicates, so that no entry appears more than once in the autocompletion window.
   *
   * @return Sorted and cleaned list of collected symbols.
   */
  @Nullable
  public PsiElement getMyReferringSymbol() {
    return myReferringSymbol;
  }

  public LocalizationConstruct.ConstructType getMyLocalization() {
    return myLocalization;
  }

  public PsiElement getMyLocalizationSymbol() {
    return myLocalizationSymbol;
  }
}
