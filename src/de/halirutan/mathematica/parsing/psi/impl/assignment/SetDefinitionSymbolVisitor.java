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

package de.halirutan.mathematica.parsing.psi.impl.assignment;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.SymbolAssignmentType;
import de.halirutan.mathematica.parsing.psi.api.*;
import de.halirutan.mathematica.parsing.psi.api.lists.List;
import de.halirutan.mathematica.parsing.psi.api.pattern.Condition;
import de.halirutan.mathematica.parsing.psi.api.pattern.Pattern;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author patrick (7/3/14)
 */
public class SetDefinitionSymbolVisitor extends MathematicaVisitor {
  private static final HashMap<String, SymbolAssignmentType> ourHeadAssignmentMapping;
  static {
    ourHeadAssignmentMapping = new HashMap<String, SymbolAssignmentType>(6);
    ourHeadAssignmentMapping.put("Options", SymbolAssignmentType.OPTIONS_ASSIGNMENT);
    ourHeadAssignmentMapping.put("Attributes", SymbolAssignmentType.ATTRIBUTES_ASSIGNMENT);
    ourHeadAssignmentMapping.put("MessageName", SymbolAssignmentType.MESSAGE_ASSIGNMENT);
    ourHeadAssignmentMapping.put("Default", SymbolAssignmentType.DEFAULT_ASSIGNMENT);
    ourHeadAssignmentMapping.put("Format", SymbolAssignmentType.FORMAT_ASSIGNMENT);
    ourHeadAssignmentMapping.put("N", SymbolAssignmentType.N_ASSIGNMENT);
    ourHeadAssignmentMapping.put("SyntaxInformation", SymbolAssignmentType.SYNTAX_INFORMATION_ASSIGNMENT);
  }
  private final LinkedHashSet<Symbol> myUnboundSymbols = Sets.newLinkedHashSet();
  private final PsiElement myStartElement;
  private SymbolAssignmentType myAssignmentType = null;
  private boolean myFoundAssignmentType = false;

  public SetDefinitionSymbolVisitor(final PsiElement startElement) {
    myStartElement = startElement;
  }

  /**
   * Creates an instance and supplies as second argument the type of the assignment. Since you call this with
   * startElement being the lhs of the assignment, inside here it is usually not known whether we have been called by a
   * Set or a SetDelayed.
   *
   * @param startElement
   *     element of the whole lhs (left hand side) of the assignment. Used to extract cases like <code
   *     >Options[f]=...</code>
   * @param assignmentType
   *     type of the assignment. Is only changed in cases like <code >Attributes[f]=</code>
   */
  public SetDefinitionSymbolVisitor(final PsiElement startElement, SymbolAssignmentType assignmentType) {
    this(startElement);
    myAssignmentType = assignmentType;
  }

  public Set<Symbol> getUnboundSymbols() {
    return myUnboundSymbols;
  }

  public SymbolAssignmentType getAssignmentType() {
    return myAssignmentType;
  }

  @Override
  public void visitCondition(Condition condition) {
    PsiElement firstChild = condition.getFirstChild();
    if (firstChild != null) {
      firstChild.accept(this);
    }
  }

  private void setMyAssignmentType(final String head) {
    if (ourHeadAssignmentMapping.containsKey(head)) {
      myAssignmentType = ourHeadAssignmentMapping.get(head);
    } else {
      myAssignmentType = SymbolAssignmentType.UNKNOWN;
    }
  }

  @Override
  public void visitFunctionCall(FunctionCall functionCall) {
    final PsiElement head = functionCall.getHead();
    if (head instanceof Symbol) {
      // The next set are symbols that are just ignored and we have to check their first argument for a symbol
      // which is defined
      if (functionCall.matchesHead("HoldPattern|Longest|Shortest|Repeated")) {
        final PsiElement arg1 = functionCall.getArgument(1);
        if (arg1 != null) {
          arg1.accept(this);
        }
      }
      // check if we have an assignment of the form Options[sym] = {...}
      if (functionCall.equals(myStartElement) && functionCall.matchesHead("Options|Attributes|MessageName|Default|Format|N|SyntaxInformation")) {
        if (myFoundAssignmentType) {
          // we already saw eg Options[..] and this cannot be handled any further
          return;
        }
        setMyAssignmentType(functionCall.getHead().getText());
        myFoundAssignmentType = true;
        PsiElement arg1 = functionCall.getArgument(1);
        if (arg1 != null) {
          if (functionCall.matchesHead("Options|Attributes|MessageName|Default|SyntaxInformation")) {
            if (arg1 instanceof Symbol) myUnboundSymbols.add((Symbol) arg1);
          } else {
            //if we have for instance  N[e : poly[cp_], pa_] := ... where the argument itself can be a complicated
            // patter, then we just go on with the visitor, but we remember that we already know the assignment type
            arg1.accept(this);
          }
        }
      } else {
        myUnboundSymbols.add((Symbol) head);
      }
    } else {
      // situations like this (g : fff)[x_^2] := Hold[g, x] where the head contains something more complex
      head.accept(this);
    }
  }

  /**
   * Need this for situations like <code>p:f[x_]:=x^2</code>
   */
  @Override
  public void visitPattern(final Pattern pattern) {
    final PsiElement lastChild = pattern.getLastChild();
    if (lastChild != null) {
      lastChild.accept(this);
    }
  }

  @Override
  public void visitMessageName(final MessageName messageName) {
    final Expression symbol = messageName.getSymbol();
    if (symbol instanceof Symbol) {
      myUnboundSymbols.add((Symbol) symbol);
      myAssignmentType = SymbolAssignmentType.MESSAGE_ASSIGNMENT;
    }
  }

  @Override
  public void visitGroup(Group group) {
    group.acceptChildren(this);
  }

  @Override
  public void visitList(List list) {
    list.acceptChildren(this);
  }


  @Override
  public void visitSymbol(final Symbol symbol) {
    myUnboundSymbols.add(symbol);
  }

}
