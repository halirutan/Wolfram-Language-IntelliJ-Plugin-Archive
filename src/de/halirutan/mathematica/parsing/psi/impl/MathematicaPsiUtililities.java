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
import de.halirutan.mathematica.parsing.psi.api.lists.List;
import org.jetbrains.annotations.Nullable;

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
   * Extracts the assignment symbol from assignment operations, g[x_]:=x^2 should return the x
   * a = 2 returns a
   * @param element PsiElement of the assignment
   * @return Symbol which is assigned
   */
  @Nullable
  public static Symbol getAssignmentSymbol(PsiElement element) {
    final PsiElement firstChild = element.getFirstChild();

    if (element instanceof SetDelayed || element instanceof Set) {
      if (firstChild instanceof Symbol) {
        return (Symbol) firstChild;
      } else if (firstChild instanceof FunctionCall) {
        if (firstChild.getFirstChild() instanceof Symbol) {
          return (Symbol) firstChild.getFirstChild();
        }
      }
    }
    return null;
  }

  public static java.util.List<Symbol> extractLocalizedVariables(PsiElement element) {
    java.util.List<Symbol> localVariables = Lists.newArrayList();

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
                  Symbol tmp = getAssignmentSymbol(child);
                  if (tmp != null) {
                    localVariables.add(tmp);
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
}
