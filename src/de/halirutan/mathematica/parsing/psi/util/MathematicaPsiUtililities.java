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

import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.fileTypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.Set;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.parsing.psi.api.pattern.*;
import de.halirutan.mathematica.parsing.psi.impl.MathematicaPsiFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author patrick (5/21/13)
 */
public class MathematicaPsiUtililities {

  public static final java.util.Set<String> MODULE_LIKE_CONSTRUCTS = new HashSet<String>(Arrays.asList(
      new String[]{"Module", "Block", "With"}));
  public static final java.util.Set<String> TABLE_LIKE_CONSTRUCTS = new HashSet<String>(Arrays.asList(
      new String[]{"Table", "Integrate", "NIntegrate", "Sum", "NSum"}));

  public static String getSymbolName(Symbol element) {
    ASTNode symbolNode = element.getNode().getFirstChildNode();
    if (symbolNode != null) {
      return symbolNode.getText();
    }
    return null;
  }

  public static PsiElement setSymbolName(Symbol element, String newName) {
    ASTNode identifierNode = element.getNode().findChildByType(MathematicaElementTypes.IDENTIFIER);
    final PsiFileFactory fileFactory = PsiFileFactory.getInstance(element.getProject());
    final MathematicaPsiFileImpl file = (MathematicaPsiFileImpl) fileFactory.createFileFromText("dummy.m", MathematicaFileType.INSTANCE, newName);
    ASTNode newElm = file.getFirstChild().getNode().findChildByType(MathematicaElementTypes.IDENTIFIER);
    if (identifierNode != null && newElm != null) {
      element.getNode().replaceChild(identifierNode, newElm);
    }
    return element;
  }

  /**
   * Extracts the assignment symbol from assignment operations, <code>g[x_]:=x^2</code> should return the g and  x <code>a
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

//        final List<PsiElement> arguments = getArguments(firstChild);
//        for (PsiElement currentArgument : arguments) {
//          assignees.addAll(getSymbolsFromArgumentPattern(currentArgument));
//        }

      }
      if (firstChild instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
        assignees.addAll(getSymbolsFromNestedList(firstChild));
      }

    }
    return assignees;
  }

  @Nullable
  public static List<Symbol> getSymbolsFromFunctionCallPattern(PsiElement element) {
    final PsiElement firstChild = element.getFirstChild();
    final List<Symbol> assignees = Lists.newArrayList();

    if (element instanceof SetDelayed || element instanceof Set) {
      if (firstChild instanceof FunctionCall) {
        final List<PsiElement> arguments = getArguments(firstChild);
        for (PsiElement currentArgument : arguments) {
          assignees.addAll(getSymbolsFromArgumentPattern(currentArgument));
        }
      }
    }
    return assignees;
  }


  public static List<Symbol> getSymbolsFromArgumentPattern(@Nullable PsiElement element) {
    final LinkedList<Symbol> result = new LinkedList<Symbol>();
    if (element == null) {
      return result;
    }

    PsiElementVisitor patternVisitor = new PsiRecursiveElementVisitor() {

      private final List<String> myDiveInFirstChild = Lists.newArrayList("Longest", "Shortest", "Repeated", "Optional", "PatternTest", "Condition");

      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof Blank ||
            element instanceof BlankSequence ||
            element instanceof BlankNullSequence ||
            element instanceof Pattern) {
          PsiElement possibleSymbol = element.getFirstChild();
          if (possibleSymbol instanceof Symbol) {
            result.add((Symbol) possibleSymbol);
          }
        } else if (element instanceof Optional || element instanceof Condition || element instanceof PatternTest) {
          PsiElement firstChild = element.getFirstChild();
          if (firstChild != null) {
            firstChild.accept(this);
          }
        } else if (element instanceof FunctionCall) {
          PsiElement head = element.getFirstChild();
          if (myDiveInFirstChild.contains(head.getNode().getText())) {
            List<PsiElement> args = getArguments(element);
            if (args.size() > 0) {
              args.get(0).accept(this);
            }
          }
        } else {
          element.acceptChildren(this);
        }
      }
    };

    patternVisitor.visitElement(element);
    return result;
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
            assignees.add((Symbol) childLevel2);
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

    // Do we have a function call and is the fi1rst child a symbol like f[..]
    if (element instanceof FunctionCall && element.getFirstChild() instanceof Symbol) {
      String scopingConstructName = ((Symbol) element.getFirstChild()).getSymbolName();
      // Do we have Module[..] Block[..] or With[..]
      if (MODULE_LIKE_CONSTRUCTS.contains(scopingConstructName)) {
        // The general structure in the parse tree is FunctionCall(Module, [, ...,])
        final PsiElement openingBracket = getNextSiblingSkippingWhitespace(element.getFirstChild());
        if (openingBracket != null) {
          final PsiElement initList = getNextSiblingSkippingWhitespace(openingBracket);
          if (initList instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
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

      if (TABLE_LIKE_CONSTRUCTS.contains(scopingConstructName)) {
        final PsiElement openingBracket = getNextSiblingSkippingWhitespace(element.getFirstChild());
        if (openingBracket != null) {
          final PsiElement body = getNextSiblingSkippingWhitespace(openingBracket);
          PsiElement initList = body != null ? getNextArgument(body) : null;
          while (initList instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
            PsiElement arg1 = getFirstListElement(initList);
            if (arg1 instanceof Symbol) {
              localVariables.add((Symbol) arg1);

            }
            initList = getNextArgument(initList);
          }
        }
      }




    }
    return localVariables;
  }

  public static PsiElement getNextSiblingSkippingWhitespace(@Nullable PsiElement elm) {
    if (elm == null) return null;
    PsiElement sibling = elm.getNextSibling();
    while (sibling instanceof PsiWhiteSpace) {
      sibling = sibling.getNextSibling();
    }
    return sibling;
  }

  public static PsiElement getFirstListElement(@Nullable PsiElement list) {
    if (list == null) {
      return null;
    }
    PsiElement brace = list.getFirstChild();
    if (brace == null || !brace.getNode().getText().equals("{")) {
      return null;
    }
    return getNextSiblingSkippingWhitespace(brace);
  }

  public static List<PsiElement> getArguments(@Nullable PsiElement func) {
    if (func == null) {
      return null;
    }

    PsiElement head = func.getFirstChild();
    if (head == null) {
      return null;
    }

    PsiElement bracket = head.getNextSibling();
    if (bracket == null || !bracket.getNode().getElementType().equals(MathematicaElementTypes.LEFT_BRACKET)) {
      return null;
    }

    List<PsiElement> allArguments = new LinkedList<PsiElement>();
    boolean skipHead = true;
    for (PsiElement child : func.getChildren()) {
      final IElementType type = child.getNode().getElementType();
      if (MathematicaElementTypes.WHITE_SPACE_OR_COMMENTS.contains(type) || type.equals(MathematicaElementTypes.COMMA)) {
        continue;
      }
      if (skipHead) {
        skipHead = false;
        continue;
      }

      allArguments.add(child);
    }

    return allArguments;
  }

  public static PsiElement getNextArgument(@Nullable PsiElement arg) {
    if (arg == null) {
      return null;
    }

    PsiElement comma = getNextSiblingSkippingWhitespace(arg);
    if (comma != null && comma.getNode().getElementType().equals(MathematicaElementTypes.COMMA)) {
      return getNextSiblingSkippingWhitespace(comma);
    }

    return null;
  }


}
