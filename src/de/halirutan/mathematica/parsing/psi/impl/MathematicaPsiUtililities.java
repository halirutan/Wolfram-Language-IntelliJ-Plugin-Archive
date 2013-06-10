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
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.Set;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author patrick (5/21/13)
 */
public class MathematicaPsiUtililities {

  public static String getSymbolName(Symbol element) {
    ASTNode symbolNode = element.getNode().getFirstChildNode();
    if (symbolNode != null) {
      return symbolNode.getText();
    }
    return null;
  }

  //TODO: Node types are wrong.
  public static PsiElement setSymbolName(Symbol element, String newName) {
    ASTNode symbolNode = element.getNode();
    if (symbolNode != null) {
      Symbol newSymbol = MathematicaSymbolFactory.createSymbol(element.getProject(), newName);
      ASTNode newSymbolNode = newSymbol.getNode();
      element.getNode().replaceChild(symbolNode, newSymbolNode);
    }
    return element;
  }

  /**
   * Extracts the assignment symbol from assignment operations, <code>g[x_]:=x^2</code> should return the x and <code>a
   * = 2</code> returns a. Note that vector assignments like <code>{a,{b,c}} = {1,{2,3}}</code> return a list of
   * variables.
   *
   * @param element PsiElement of the assignment
   * @return List of symbols which are assigned.
   */
  @Nullable
  public static List<Symbol> getAssignmentSymbols(PsiElement element) {
    final PsiElement firstChild = element.getFirstChild();
    final List<Symbol> assignees = Lists.newArrayList();

    if (element instanceof SetDelayed || element instanceof Set) {
      if (firstChild instanceof Symbol) {
        assignees.add((Symbol) firstChild);
      }
      if (firstChild instanceof FunctionCall) {
        if (firstChild.getFirstChild() instanceof Symbol) {
          assignees.add((Symbol) firstChild.getFirstChild());
        }
      }
      if (firstChild instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
        assignees.addAll(getSymbolsFromNestedList(firstChild));
      }

    }
    return assignees;
  }

  /**
   * Simple version to extract Symbols from a nested list up to level 2. For the following examples all symbols a,b,c,d
   * would be extracted successfully: <ul > <li ><code>{a,b,c,d}</code></li> <li ><code>{{a,b},c,d}</code></li> <li
   * ><code>{{a},b,{c},d}</code></li> </ul>
   *
   * @param listHead List to extract from
   * @return List of extracted {@link Symbol} PsiElement's.
   */
  @NotNull
  private static List<Symbol> getSymbolsFromNestedList(PsiElement listHead) {
    List<Symbol> assignees = Lists.newArrayList();
    PsiElement children[] = listHead.getChildren();

    for (PsiElement child : children) {
      if (child instanceof Symbol) {
        assignees.add((Symbol) child);
      }
      if (child instanceof List) {
        for (PsiElement childLevel2 : child.getChildren()) {
          if (childLevel2 instanceof Symbol) {
            assignees.add((Symbol) child);
          }
        }
      }
    }
    return assignees;
  }

  /**
   * This function tries to extract all localized variables inside a <code>Module</code>, <code>Block</code> or <code
   * >With</code> construct. The variables can either be just declared like in <code >Block[{a,b,c},..]</code>.
   *
   * @param element The {@link FunctionCall} PsiElement containing the scoping construct.
   * @return List of localized variables. For <code >Module[{a,b:=3,c=2}</code> the list contains the PsiElements for a,
   *         b, and c.
   */
  public static List<Symbol> extractLocalizedVariables(PsiElement element) {
    List<Symbol> localVariables = Lists.newArrayList();

    // Do we have a function call and is the first child a symbol like f[..]
    if (element instanceof FunctionCall && element.getFirstChild() instanceof Symbol) {
      String scopingConstructName = ((Symbol) element.getFirstChild()).getSymbolName();
      // Do we have Module[..] Block[..] or With[..]
      if (scopingConstructName.equals("Module") || scopingConstructName.equals("Block") || scopingConstructName.equals("With")) {
        // The general structure in the parse tree is FunctionCall(Module, [, ...,])
        final PsiElement openingBracket = element.getFirstChild().getNextSibling();
        if (openingBracket != null) {
          final PsiElement initList = openingBracket.getNextSibling();
          if (initList instanceof List) {
            if (initList.getChildren().length > 0) {
              for (PsiElement child : initList.getChildren()) {
                if (child instanceof Symbol) {
                  localVariables.add((Symbol) child);
                } else {
                  List<Symbol> tmp = getAssignmentSymbols(child);
                  if (tmp != null) {
                    localVariables.addAll(tmp);
                  }
                }
              }
            }
          }
        }
      }
    }
    return localVariables;
  }

  public static List<Symbol> extractFunctionVariables(PsiElement element) {
    return Collections.emptyList();
  }

}
