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

package de.halirutan.mathematica.codeinsight.structureview;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.parsing.psi.api.assignment.TagSet;
import de.halirutan.mathematica.parsing.psi.api.assignment.TagSetDelayed;
import de.halirutan.mathematica.parsing.psi.util.MathematicaPatternVisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <p> This visitor goes through the AST and extracts all top-level definitions. It's purpose is to collect the
 * information specifically for the StructureView. This means it will bundle several definitions of the same symbol so
 * that the StructureView contains one node for one defined functions, no matter how many different definition patterns
 * exist. </p> <p> The node for one function can be <em>opened</em> to see (and be able to navigate) the different
 * definitions for the symbol. </p>
 *
 * @author patrick (6/14/14)
 */
public class MathematicaViewElementExtractingVisitor extends MathematicaVisitor {


  private HashMap<String, List<SymbolDefinition>> myDefinedSymbols;

  public MathematicaViewElementExtractingVisitor() {
    super();
    myDefinedSymbols = new HashMap<String, List<SymbolDefinition>>(20);
  }

  public void visitFile(PsiFile file) {
    file.acceptChildren(this);
  }

  public void visitCompoundExpression(CompoundExpression compoundExpression) {
    // we only want to dive in if we are in the top level
    if (compoundExpression.getParent() instanceof MathematicaPsiFile) {
      compoundExpression.acceptChildren(this);
    }
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
  public HashMap<String, List<SymbolDefinition>> getDefinedSymbols() {
    return myDefinedSymbols;
  }

  @Override
  public void visitFunctionCall(final FunctionCall functionCall) {
    if (!functionCall.isScopingConstruct()) {
      functionCall.acceptChildren(this);
    }
  }

  private void cacheAssignedSymbols(PsiElement setType) {
    MathematicaPatternVisitor patternVisitor = new MathematicaPatternVisitor();
    setType.accept(patternVisitor);
    final Set<Symbol> unboundSymbols = patternVisitor.getUnboundSymbols();
    if (unboundSymbols.size() > 0) {
      final Symbol symbol = unboundSymbols.iterator().next();
      String symbolName = symbol.getSymbolName();
      if (!myDefinedSymbols.containsKey(symbolName)) {
        myDefinedSymbols.put(symbolName, new LinkedList<SymbolDefinition>());
      }
      final List<SymbolDefinition> symbolDefinitions = myDefinedSymbols.get(symbolName);
      symbolDefinitions.add(new SymbolDefinition(symbol, setType));
    }
  }

}