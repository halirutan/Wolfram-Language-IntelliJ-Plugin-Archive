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

package de.halirutan.mathematica.parsing.psi.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Group;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.pattern.Condition;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author patrick (7/3/14)
 */
public class UnboundSymbolExtractingVisitor extends MathematicaVisitor {


  private final LinkedHashSet<Symbol> myUnboundSymbols = Sets.newLinkedHashSet();
  private final List<String> myDiveInFirstChild = Lists.newArrayList("Longest", "Shortest", "Repeated", "Optional", "PatternTest", "Condition");
  private final List<String> myDoNotDiveIn = Lists.newArrayList("Verbatim");

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
      final String headName = ((Symbol) head).getSymbolName();
      //Todo: Exract first argument to Options[..]
      if (headName.matches("Options")) {
      }


      myUnboundSymbols.add((Symbol) head);
      final String functionName = ((Symbol) head).getSymbolName();
      if (myDiveInFirstChild.contains(functionName)) {
        List<PsiElement> args = MathematicaPsiUtilities.getArguments(functionCall);
        if (args.size() > 0) {
          args.get(0).accept(this);
        }
      } else if (!myDoNotDiveIn.contains(functionName)) {
        functionCall.acceptChildren(this);
      }
    } else {
      functionCall.acceptChildren(this);
    }
  }

  @Override
  public void visitGroup(Group group) {
    group.acceptChildren(this);
  }

  @Override
  public void visitList(de.halirutan.mathematica.parsing.psi.api.lists.List list) {
    list.acceptChildren(this);
  }


  @Override
  public void visitSymbol(final Symbol symbol) {
    myUnboundSymbols.add(symbol);
  }

}
