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

package de.halirutan.mathematica.lang.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiElementProcessor;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.assignment.*;
import de.halirutan.mathematica.lang.psi.impl.assignment.SetDefinitionSymbolVisitor;
import de.halirutan.mathematica.lang.psi.impl.assignment.UpSetDefinitionSymbolVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (1/6/14)
 */
@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
class GlobalDefinitionResolveProcessor implements PsiElementProcessor {
  private final Symbol myStartElement;
  private PsiElement myReferringSymbol;

  public GlobalDefinitionResolveProcessor(Symbol startElement) {
    this.myStartElement = startElement;
    this.myReferringSymbol = null;
  }

  @Override
  public boolean execute(@NotNull PsiElement element) {
    if (element instanceof Set || element instanceof SetDelayed) {
      return visitSetDefinition(element.getFirstChild());
    }

    if (element instanceof TagSet || element instanceof TagSetDelayed) {
      return visitTagSetDefinition(element.getFirstChild());
    }
    if (element instanceof UpSet || element instanceof UpSetDelayed) {
      return visitUpSetDefinition(element.getFirstChild());
    }

    if (element instanceof FunctionCall) {
      final FunctionCall call = (FunctionCall) element;
      final PsiElement lhs = ((FunctionCall) element).getArgument(1);
      if (call.hasHead("Set") || call.hasHead("SetDelayed")) {
        return visitSetDefinition(lhs);
      } else if (call.hasHead("TagSet") || call.hasHead("TagSetDelayed")) {
        return visitTagSetDefinition(lhs);
      } else if (call.hasHead("UpSet")|| call.hasHead("UpSetDelayed")) {
        return visitUpSetDefinition(lhs);
      } else if ((call.hasHead("SetAttributes") || call.hasHead("SetOptions")) && lhs instanceof Symbol) {
        return visitSymbol((Symbol) lhs);
      }
    }
    return true;
  }

  /**
   * Check if a symbol has the same name and if yes, it is my point of definition.
   *
   * @param symbol
   *     symbol to check
   * @return true if the names are equal
   */
  private boolean visitSymbol(final Symbol symbol) {
    if (myStartElement.getFullSymbolName().equals(symbol.getFullSymbolName())) {
      myReferringSymbol = symbol;
      return false;
    }
    return true;
  }


  private boolean visitUpSetDefinition(final PsiElement lhs) {
    if (lhs != null) {
      UpSetDefinitionSymbolVisitor definitionVisitor = new UpSetDefinitionSymbolVisitor();
      lhs.accept(definitionVisitor);
      final java.util.Set<Symbol> definitionSymbols = definitionVisitor.getUnboundSymbols();
      for (Symbol next : definitionSymbols) {
        if (next.getFullSymbolName().equals(myStartElement.getFullSymbolName())) {
          myReferringSymbol = next;
          return false;
        }
      }
    }
    return true;
  }

  /**
   * TagSet should be trivial. In f /: g[a,b,..,f,..] = .., f is always expected to be a symbol.
   */
  private boolean visitTagSetDefinition(final PsiElement defSymbol) {
    if (defSymbol instanceof Symbol && ((Symbol) defSymbol).getFullSymbolName().matches(myStartElement.getFullSymbolName())) {
      myReferringSymbol = defSymbol;
      return false;
    }
    return true;
  }

  private boolean visitSetDefinition(final PsiElement lhs) {
    if (lhs != null) {
      SetDefinitionSymbolVisitor definitionVisitor = new SetDefinitionSymbolVisitor(lhs);
      lhs.accept(definitionVisitor);
      final java.util.Set<Symbol> definitionSymbols = definitionVisitor.getUnboundSymbols();
      for (Symbol next : definitionSymbols) {
        if (next.getFullSymbolName().equals(myStartElement.getFullSymbolName())) {
          myReferringSymbol = next;
          return false;
        }
      }
    }
    return true;
  }

  @Nullable
  public PsiElement getMyReferringSymbol() {
    return myReferringSymbol;
  }

}
