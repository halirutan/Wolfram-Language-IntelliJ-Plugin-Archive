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

package de.halirutan.mathematica.parsing.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.parsing.psi.api.assignment.TagSet;
import de.halirutan.mathematica.parsing.psi.api.assignment.TagSetDelayed;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author patrick (9/27/13)
 */
public class MathematicaTopLevelFunctionVisitor extends MathematicaVisitor {

  private final Set<String> myCollectedFunctionNames;
  private final Set<Symbol> myCollectedFunctions;

  public MathematicaTopLevelFunctionVisitor() {
    super();
    myCollectedFunctionNames = new HashSet<String>();
    myCollectedFunctions = new HashSet<Symbol>();
  }

  public void visitFile(PsiFile file) {
    file.acceptChildren(this);
  }

  public void visitCompoundExpression(CompoundExpression compoundExpression) {
    compoundExpression.acceptChildren(this);
  }

  public void visitSetDelayed(SetDelayed setDelayed) {
    cacheAssignedSymbols(setDelayed);
  }

  public void visitSet(de.halirutan.mathematica.parsing.psi.api.assignment.Set set) {
    cacheAssignedSymbols(set);
  }

  public void visitTagSet(TagSet element) {
    cacheAssignedSymbols(element);
  }

  public void visitTagSetDelayed(TagSetDelayed tagSetDelayed) {
    cacheAssignedSymbols(tagSetDelayed);
  }

  public Set<String> getFunctionsNames() {
    return myCollectedFunctionNames;
  }

  public Set<Symbol> getAssignedSymbols() {
    return myCollectedFunctions;
  }

  private void cacheAssignedSymbols(PsiElement element) {
    final List<Symbol> assignmentSymbols = MathematicaPsiUtilities.getAssignmentSymbols(element);
    if (assignmentSymbols != null) {
      for (Symbol assignmentSymbol : assignmentSymbols) {
        String symbolName = assignmentSymbol.getSymbolName();
        myCollectedFunctionNames.add(symbolName);
        myCollectedFunctions.add(assignmentSymbol);
      }
    }
  }
}
