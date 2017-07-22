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

package de.halirutan.mathematica.codeinsight.completion;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.CompoundExpression;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.assignment.*;
import de.halirutan.mathematica.lang.psi.impl.assignment.SetDefinitionSymbolVisitor;
import de.halirutan.mathematica.lang.psi.impl.assignment.UpSetDefinitionSymbolVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author patrick (9/27/13)
 */
class GlobalDefinitionCompletionProvider extends MathematicaVisitor {

  private final Set<String> myCollectedFunctionNames;

  public GlobalDefinitionCompletionProvider() {
    myCollectedFunctionNames = new HashSet<>();
  }

  public void visitFile(PsiFile file) {
    file.acceptChildren(this);
  }

  public void visitCompoundExpression(CompoundExpression compoundExpression) {
    compoundExpression.acceptChildren(this);
  }

  public void visitSetDelayed(SetDelayed setDelayed) {
    cacheFromSetAssignment(setDelayed);
  }

  public void visitSet(de.halirutan.mathematica.lang.psi.api.assignment.Set set) {
    cacheFromSetAssignment(set);
  }

  public void visitTagSet(TagSet tagSet) {
    final PsiElement tag = tagSet.getFirstChild();
    if (tag instanceof Symbol) {
      myCollectedFunctionNames.add(((Symbol) tag).getFullSymbolName());
    }
  }

  public void visitTagSetDelayed(TagSetDelayed tagSetDelayed) {
    final PsiElement tag = tagSetDelayed.getFirstChild();
    if (tag instanceof Symbol) {
      myCollectedFunctionNames.add(((Symbol) tag).getFullSymbolName());
    }
  }

  @Override
  public void visitUpSet(final UpSet upSet) {
    cacheFromUpSetAssignment(upSet);
  }

  @Override
  public void visitUpSetDelayed(final UpSetDelayed upSetDelayed) {
    cacheFromUpSetAssignment(upSetDelayed);
  }

  @Override
  public void visitFunctionCall(final FunctionCall functionCall) {
    final String head = functionCall.getHead().getText();
    if (head.matches("Set|SetDelayed")) {
      final PsiElement lhs = functionCall.getArgument(1);
      if (lhs != null) {
        final SetDefinitionSymbolVisitor visitor = new SetDefinitionSymbolVisitor(lhs);
        lhs.accept(visitor);
        cacheAssignedSymbols(visitor.getUnboundSymbols());
      }
    } else if (head.matches("TagSet|TagSetDelayed|SetAttributes|SetOptions")) {
      final PsiElement arg1 = functionCall.getArgument(1);
      if (arg1 instanceof Symbol) {
        myCollectedFunctionNames.add(((Symbol) arg1).getFullSymbolName());
      }
    }
  }

  public Set<String> getFunctionsNames() {
    return myCollectedFunctionNames;
  }

  private void cacheFromSetAssignment(PsiElement element) {
    final PsiElement lhs = element.getFirstChild();
    SetDefinitionSymbolVisitor visitor = new SetDefinitionSymbolVisitor(lhs);
    lhs.accept(visitor);
    cacheAssignedSymbols(visitor.getUnboundSymbols());
  }

  private void cacheFromUpSetAssignment(final PsiElement upSet) {
    final PsiElement lhs = upSet.getFirstChild();
    UpSetDefinitionSymbolVisitor visitor = new UpSetDefinitionSymbolVisitor();
    lhs.accept(visitor);
    cacheAssignedSymbols(visitor.getUnboundSymbols());
  }

  private void cacheAssignedSymbols(Set<Symbol> symbolSet) {
    if (symbolSet != null) {
      for (Symbol symbol : symbolSet) {
        myCollectedFunctionNames.add(symbol.getFullSymbolName());
      }
    }
  }

}
