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

import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.lang.psi.MathematicaRecursiveVisitor;
import de.halirutan.mathematica.lang.psi.SymbolAssignmentType;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.assignment.*;
import de.halirutan.mathematica.lang.psi.impl.assignment.SetDefinitionSymbolVisitor;
import de.halirutan.mathematica.lang.psi.impl.assignment.UpSetDefinitionSymbolVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static de.halirutan.mathematica.lang.psi.SymbolAssignmentType.*;

/**
 * @author patrick (1/6/14)
 */
public class GlobalDefinitionCollector {

  private final Map<String, HashSet<AssignmentProperty>> myAssignments;

  public GlobalDefinitionCollector(PsiElement startElement) {
    myAssignments = new HashMap<>();
    final CollectorVisitor myVisitor = new CollectorVisitor();
    startElement.accept(myVisitor);
  }

  @NotNull
  public Map<String, HashSet<AssignmentProperty>> getAssignments() {
    return myAssignments;
  }


  private void addAssignment(Symbol symbol, PsiElement lhs, SymbolAssignmentType type) {
    String key = symbol.getSymbolName();
    HashSet<AssignmentProperty> assignment;
    if (myAssignments.containsKey(key)) {
      assignment = myAssignments.get(key);
    } else {
      assignment = new HashSet<>(1);
      myAssignments.put(key, assignment);
    }
    assignment.add(new AssignmentProperty(symbol, lhs, type));
  }

  private class CollectorVisitor extends MathematicaRecursiveVisitor {

    @Override
    public void visitSetDelayed(final SetDelayed setDelayed) {
      final PsiElement lhs = setDelayed.getFirstChild();
      SetDefinitionSymbolVisitor visitor = new SetDefinitionSymbolVisitor(lhs, SET_DELAYED_ASSIGNMENT);
      lhs.accept(visitor);
      final java.util.Set<Symbol> unboundSymbols = visitor.getUnboundSymbols();
      for (Symbol symbol : unboundSymbols) {
        addAssignment(symbol, lhs, visitor.getAssignmentType());
      }
    }

    @Override
    public void visitSet(final Set set) {
      final PsiElement lhs = set.getFirstChild();
      SetDefinitionSymbolVisitor visitor = new SetDefinitionSymbolVisitor(lhs, SET_ASSIGNMENT);
      lhs.accept(visitor);
      final java.util.Set<Symbol> unboundSymbols = visitor.getUnboundSymbols();
      for (Symbol symbol : unboundSymbols) {
        PsiElement context = lhs;
        if (visitor.getAssignmentType() == ATTRIBUTES_ASSIGNMENT || visitor.getAssignmentType() == OPTIONS_ASSIGNMENT) {
//          context = set.getLastChild();
          context = set.getFirstChild();
        }
        addAssignment(symbol, context, visitor.getAssignmentType());
      }
    }

    @Override
    public void visitTagSet(final TagSet tagSet) {
      final PsiElement symbol = tagSet.getFirstChild();
      if (symbol instanceof Symbol) {
        addAssignment((Symbol) symbol, tagSet, TAG_SET_ASSIGNMENT);
      }
    }

    @Override
    public void visitTagSetDelayed(final TagSetDelayed tagSetDelayed) {
      final PsiElement symbol = tagSetDelayed.getFirstChild();
      if (symbol instanceof Symbol) {
        addAssignment(((Symbol) symbol), tagSetDelayed, TAG_SET_DELAYED_ASSIGNMENT);
      }
    }

    @Override
    public void visitUpSet(final UpSet upSet) {
      final PsiElement lhs = upSet.getFirstChild();
      if (lhs != null) {
        UpSetDefinitionSymbolVisitor visitor = new UpSetDefinitionSymbolVisitor();
        lhs.accept(visitor);
        final java.util.Set<Symbol> unboundSymbols = visitor.getUnboundSymbols();
        for (Symbol symbol : unboundSymbols) {
          addAssignment(symbol, lhs, UP_SET_ASSIGNMENT);
        }
      }
    }

    @Override
    public void visitUpSetDelayed(final UpSetDelayed upSetDelayed) {
      final PsiElement lhs = upSetDelayed.getFirstChild();
      if (lhs != null) {
        UpSetDefinitionSymbolVisitor visitor = new UpSetDefinitionSymbolVisitor();
        lhs.accept(visitor);
        final java.util.Set<Symbol> unboundSymbols = visitor.getUnboundSymbols();
        for (Symbol symbol : unboundSymbols) {
          addAssignment(symbol, lhs, UP_SET_DELAYED_ASSIGNMENT);
        }
      }
    }

    @Override
    public void visitFunctionCall(final FunctionCall functionCall) {
      final PsiElement arg1 = functionCall.getArgument(1);
      if (arg1 != null) {
        if (functionCall.matchesHead("Set|SetDelayed")) {
          SetDefinitionSymbolVisitor visitor = new SetDefinitionSymbolVisitor(arg1);
          arg1.accept(visitor);
          final java.util.Set<Symbol> symbols = visitor.getUnboundSymbols();
          for (Symbol symbol : symbols) {
            addAssignment(symbol, arg1,functionCall.matchesHead("Set") ? SET_ASSIGNMENT : SET_DELAYED_ASSIGNMENT);
          }
        } else if (functionCall.matchesHead("TagSet|TagSetDelayed")) {
          if (arg1 instanceof Symbol) {
            addAssignment((Symbol) arg1, functionCall,
                functionCall.matchesHead("TagSet") ? TAG_SET_ASSIGNMENT : TAG_SET_DELAYED_ASSIGNMENT);
          }
        } else if (functionCall.matchesHead("UpSet|UpSetDelayed")) {
          UpSetDefinitionSymbolVisitor visitor = new UpSetDefinitionSymbolVisitor();
          arg1.accept(visitor);
          for (Symbol symbol : visitor.getUnboundSymbols()) {
            addAssignment(symbol, arg1, functionCall.matchesHead("UpSet") ? UP_SET_ASSIGNMENT : UP_SET_DELAYED_ASSIGNMENT);
          }
        } else if (functionCall.matchesHead("SetAttributes")) {
          if (arg1 instanceof Symbol) {
            addAssignment((Symbol) arg1, functionCall, ATTRIBUTES_ASSIGNMENT);
          }
        } else if (functionCall.matchesHead("SetOptions")) {
          if (arg1 instanceof Symbol) {
            addAssignment((Symbol) arg1, functionCall, OPTIONS_ASSIGNMENT);
          }
        } else if (!functionCall.matchesHead("Module|With")) {
          ProgressIndicatorProvider.checkCanceled();
          functionCall.acceptChildren(this);
        }
      }
    }
  }

  public class AssignmentProperty {
    final public PsiElement myAssignmentSymbol;
    final public PsiElement myLhsOfAssignment;
    final public SymbolAssignmentType myAssignmentType;

    AssignmentProperty(final PsiElement assignmentSymbol, final PsiElement lhsOfAssignment, final SymbolAssignmentType assignmentType) {
      myAssignmentSymbol = assignmentSymbol;
      myLhsOfAssignment = lhsOfAssignment;
      myAssignmentType = assignmentType;
    }
  }

}
