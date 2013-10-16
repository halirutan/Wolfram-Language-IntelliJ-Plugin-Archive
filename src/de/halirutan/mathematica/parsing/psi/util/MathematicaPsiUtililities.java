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
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.assignment.Set;
import de.halirutan.mathematica.parsing.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.parsing.psi.api.assignment.TagSet;
import de.halirutan.mathematica.parsing.psi.api.assignment.TagSetDelayed;
import de.halirutan.mathematica.parsing.psi.api.pattern.*;
import de.halirutan.mathematica.parsing.psi.api.rules.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author patrick (5/21/13)
 */
public class MathematicaPsiUtililities {


  /**
   * Extracts the assignment symbol from assignment operations, <code>g[x_]:=x^2</code> should return the g and  x
   * <code>a = 2</code> returns a. Note that vector assignments like <code>{a,{b,c}} = {1,{2,3}}</code> return a list of
   * variables.
   *
   * @param element PsiElement of the assignment
   * @return List of symbols which are assigned.
   */
  @Nullable
  public static List<Symbol> getAssignmentSymbols(PsiElement element) {
    PsiElement firstChild = element.getFirstChild();
    final List<Symbol> assignees = Lists.newArrayList();

    if (element instanceof SetDelayed || element instanceof Set) {
      if (firstChild instanceof Symbol) {
        assignees.add((Symbol) firstChild);
      }

      // extract f from f[arg,..] := blub
      if (firstChild instanceof FunctionCall) {
        // test for SubValues definition f[][] := ...
        if (firstChild.getFirstChild() instanceof FunctionCall)
          firstChild = firstChild.getFirstChild();

        if (firstChild.getFirstChild() instanceof Symbol) {
          assignees.add((Symbol) firstChild.getFirstChild());
        }
      } else
        // extract a,b,c,d from things like {{a,b},{c,d}} = {{1,2},{3,4}}
        if (firstChild instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
          assignees.addAll(getSymbolsFromNestedList(firstChild));
        }
    } else if (element instanceof TagSetDelayed || element instanceof TagSet) {
      if (firstChild instanceof Symbol) {
        assignees.add((Symbol) firstChild);
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
      private final List<String> myDoNotDiveIn = Lists.newArrayList("Verbatim");

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

          if (element instanceof Pattern) {
            element.acceptChildren(this);
          }

        } else if (element instanceof Optional || element instanceof Condition || element instanceof PatternTest) {
          PsiElement firstChild = element.getFirstChild();
          if (firstChild != null) {
            firstChild.accept(this);
          }
        } else if (element instanceof FunctionCall) {
          PsiElement head = element.getFirstChild();
          final String name = head.getNode().getText();
          if (myDiveInFirstChild.contains(name)) {
            List<PsiElement> args = getArguments(element);
            if (args.size() > 0) {
              args.get(0).accept(this);
            }
          } else if (!myDoNotDiveIn.contains(name)) {
            element.acceptChildren(this);
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
   * This extracts the local defined arguments of a <code>Function</code> call. Examples are <ul>
   * <li><code>Function[arg, arg^2]</code> extracts <code>arg</code></li> <li><code>Function[{arg1,arg2},
   * arg1+arg2]</code> extracts <code>arg1,arg2</code></li> <li><code>Function[#+#]</code> extracts nothing</li>
   * <li><code>Function[Null, #+#, {Listable}]</code> extracts nothing</li> </ul>
   *
   * @param element The {@link PsiElement} of the function call
   * @return The set of localized function arguments
   */
  public static List<Symbol> getLocalFunctionVariables(@NotNull FunctionCall element) {
    List<Symbol> localVariables = Lists.newArrayList();

    if (element.isScopingConstruct() && element.getScopingConstruct().equals(LocalizationConstruct.ConstructType.FUNCTION)) {
      final List<PsiElement> arguments = getArguments(element);
      if (arguments.size() < 1) {
        return localVariables;
      }

      final PsiElement firstArgument = arguments.get(0);
      switch (arguments.size()) {
        case 1:
          break;
        case 2:
          if (firstArgument instanceof Symbol) {
            localVariables.add((Symbol) firstArgument);
          } else if (firstArgument instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
            localVariables = getSymbolsFromNestedList(firstArgument);
          }
          break;
        case 3:
          if (firstArgument instanceof Symbol && !((Symbol) firstArgument).getSymbolName().equals("Null")) {
            localVariables.add((Symbol) firstArgument);
          } else if (firstArgument instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
            localVariables = getSymbolsFromNestedList(firstArgument);
          }
          break;
      }
    }
    return localVariables;

  }

  /**
   * This extracts the local defined arguments of a <code>Module</code>, <code>Block</code>, ... call. Examples are <ul>
   * <li><code>Module[{arg}, arg^2]</code> extracts <code>arg</code></li> <li><code>With[{a=1,b=2}, a*b]</code> extracts
   * <code>a,b</code></li> </ul>
   *
   * @param element The {@link PsiElement} of the function call
   * @return The set of localized function arguments
   */
  public static List<Symbol> getLocalModuleLikeVariables(@NotNull FunctionCall element) {
    List<Symbol> localVariables = Lists.newArrayList();

    if (element.isScopingConstruct() && LocalizationConstruct.isModuleLike(element.getScopingConstruct())) {
      final List<PsiElement> arguments = getArguments(element);
      if (arguments.size() < 1) {
        return localVariables;
      }

      final PsiElement firstArgument = arguments.get(0);
      if (firstArgument instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
        for (PsiElement e : firstArgument.getChildren()) {
          if (e instanceof Symbol) {
            localVariables.add((Symbol) e);
          }
          if (e instanceof Set || e instanceof SetDelayed) {
            if (e.getFirstChild() instanceof Symbol) localVariables.add((Symbol) e.getFirstChild());
          }
        }
      }
    }
    return localVariables;
  }

  /**
   * This extracts the local defined arguments of a <code>Table</code>, <code>Sum</code>, ... call. Examples are <ul>
   * <li><code>Table[i,{i,10}]</code> extracts <code>i</code></li> <li><code>NSum[a+b,{a,0,10},{b,0,10}]</code> extracts
   * <code>a,b</code></li> </ul>
   *
   * @param element The {@link PsiElement} of the function call
   * @return The set of localized function arguments
   */
  public static List<Symbol> getLocalTableLikeVariables(FunctionCall element) {
    List<Symbol> localVariables = Lists.newArrayList();

    if (element.isScopingConstruct() && LocalizationConstruct.isTableLike(element.getScopingConstruct())) {
      final List<PsiElement> arguments = getArguments(element);
      if (arguments.size() < 2) {
        return localVariables;
      }

      for (int i = 1; i < arguments.size(); i++) {
        final PsiElement currentArgument = arguments.get(i);
        if (currentArgument instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
          final PsiElement firstListElement = getFirstListElement(currentArgument);
          if (firstListElement instanceof Symbol) {
            localVariables.add((Symbol) firstListElement);
          }
        }
      }
    }
    return localVariables;
  }

  /**
   * This extracts the local defined arguments of a <code>Manipulate</code>. There are many variations for the
   * definition of a <code>Manipulate</code> variable and I'm not sure whether this works in all circumstance. What I
   * haven't implemented is the usage of <code>Control[...]</code> objects.
   *
   * @param element The {@link PsiElement} of the function call
   * @return The set of localized function arguments for this <code>Manipulate</code>
   */
  public static List<Symbol> getLocalManipulateLikeVariables(FunctionCall element) {
    List<Symbol> localVariables = Lists.newArrayList();

    if (element.isScopingConstruct() && LocalizationConstruct.isManipulateLike(element.getScopingConstruct())) {
      final List<PsiElement> arguments = getArguments(element);
      if (arguments.size() < 2) {
        return localVariables;
      }

      for (int i = 1; i < arguments.size(); i++) {
        final PsiElement currentArgument = arguments.get(i);
        if (currentArgument instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
          final PsiElement firstListElement = getFirstListElement(currentArgument);
          if (firstListElement instanceof Symbol) {
            localVariables.add((Symbol) firstListElement);
          } else if (firstListElement instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
            final PsiElement subListArg = getFirstListElement(firstListElement);
            if (subListArg instanceof Symbol) {
              localVariables.add((Symbol) subListArg);
            }
          }
        }
      }
    }
    return localVariables;
  }

  /**
   * This extracts the local defined arguments of a <code>Compile</code>.
   *
   * @param element The {@link PsiElement} of the function call
   * @return The set of localized function arguments for this <code>Compile</code>
   */
  public static List<Symbol> getLocalCompileLikeVariables(FunctionCall element) {
    List<Symbol> localVariables = Lists.newArrayList();

    if (element.isScopingConstruct() && LocalizationConstruct.isCompileLike(element.getScopingConstruct())) {
      final List<PsiElement> arguments = getArguments(element);
      if (arguments.size() < 1) {
        return localVariables;
      }

      final PsiElement firstArgument = arguments.get(0);

      if (firstArgument instanceof Symbol) {
        localVariables.add((Symbol) firstArgument);
        return localVariables;
      }

      if (firstArgument instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
        PsiElement listElement = getFirstListElement(firstArgument);
        while (listElement != null) {
          if (listElement instanceof de.halirutan.mathematica.parsing.psi.api.lists.List) {
            final PsiElement possibleSymbol = getFirstListElement(listElement);
            if (possibleSymbol instanceof Symbol) {
              localVariables.add((Symbol) possibleSymbol);
            }
          }
          listElement = getNextArgument(listElement);
        }
      }
    }
    return localVariables;
  }

  /**
   * This extracts the local defined argument for a <code>Limit[Sin[x]/x, x-> 0]</code> call. Note that the returned
   * list has always only one element since <code>Limit</code> always uses only one variable.
   *
   * @param element The {@link PsiElement} of the function call
   * @return The localized argument
   */
  public static List<Symbol> getLocalLimitVariables(FunctionCall element) {
    List<Symbol> localVariables = Lists.newArrayList();

    if (element.isScopingConstruct() && LocalizationConstruct.isLimitLike(element.getScopingConstruct())) {
      final List<PsiElement> arguments = getArguments(element);
      if (arguments.size() < 2) {
        return localVariables;
      }

      final PsiElement rule = arguments.get(1);
      if (rule instanceof Rule && rule.getFirstChild() instanceof Symbol) {
        localVariables.add((Symbol) rule.getFirstChild());
      }
    }
    return localVariables;
  }

  public static PsiElement getNextSiblingSkippingWhitespace(@Nullable PsiElement elm) {
    if (elm == null) return null;
    PsiElement sibling = elm.getNextSibling();
    while (sibling instanceof PsiWhiteSpace || sibling instanceof PsiComment) {
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

  /**
   * Takes the complete {@link PsiElement} of a function-call like <code>f[x,y,z]</code> or <code>Plot[x,{x,0,1}]</code>
   * and skips over function-head, whitespaces and commas to give you only the arguments.
   *
   * @param func {@link FunctionCall} element from which you want the arguments
   * @return List of arguments
   */
  @NotNull
  public static List<PsiElement> getArguments(@Nullable PsiElement func) {
    List<PsiElement> allArguments = Lists.newLinkedList();

    if (!(func instanceof FunctionCall)) {
      return allArguments;
    }

    PsiElement head = func.getFirstChild();
    if (head == null) {
      return allArguments;
    }

    PsiElement bracket = head.getNextSibling();
    if (bracket == null || !bracket.getNode().getElementType().equals(MathematicaElementTypes.LEFT_BRACKET)) {
      return allArguments;
    }

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

  @Nullable
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
