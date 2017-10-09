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

package de.halirutan.mathematica.lang.psi.util;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lang.psi.SymbolNames;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.assignment.Set;
import de.halirutan.mathematica.lang.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.lang.psi.api.assignment.TagSet;
import de.halirutan.mathematica.lang.psi.api.assignment.TagSetDelayed;
import de.halirutan.mathematica.lang.psi.api.lists.MList;
import de.halirutan.mathematica.lang.psi.api.pattern.*;
import de.halirutan.mathematica.lang.psi.api.rules.Rule;
import de.halirutan.mathematica.lang.psi.api.string.MString;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct.MScope;
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult;
import de.halirutan.mathematica.lang.resolve.processors.SymbolResolveHint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author patrick (5/21/13)
 */
public class MathematicaPsiUtilities {

  private static final java.util.Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();

  public static boolean isBuiltInSymbol(PsiElement element) {
    if (element instanceof Symbol) {
      Symbol symbol = (Symbol) element;
      final String name = symbol.getMathematicaContext().equals("") ?
          "System`" + symbol.getSymbolName() : symbol.getFullSymbolName();
      return NAMES.contains(name);
    }
    return false;
  }


  /**
   * Extracts the assignment symbol from assignment operations, <code>g[x_]:=x^2</code> should return the g and  x
   * <code>a = 2</code> returns a. Note that vector assignments like <code>{a,{b,c}} = {1,{2,3}}</code> return a list of
   * variables.
   *
   * @param element PsiElement of the assignment
   * @return MList of symbols which are assigned.
   */
  @NotNull
  static List<Symbol> getAssignmentSymbols(PsiElement element) {
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
        if (firstChild instanceof MList) {
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
    final LinkedList<Symbol> result = new LinkedList<>();
    if (element == null) {
      return result;
    }

    //noinspection OverlyComplexAnonymousInnerClass
    PsiElementVisitor patternVisitor = new PsiRecursiveElementVisitor() {

      private final List<String> myDiveInFirstChild = Lists.newArrayList("Longest", "Shortest", "Repeated", "Optional", "PatternTest", "Condition");
      private final List<String> myDoNotDiveIn = Lists.newArrayList("Verbatim");

      @Override
      public void visitElement(PsiElement elm) {
        if (elm instanceof Blank ||
            elm instanceof BlankSequence ||
            elm instanceof BlankNullSequence ||
            elm instanceof Pattern) {
          PsiElement possibleSymbol = elm.getFirstChild();
          if (possibleSymbol instanceof Symbol) {
            result.add((Symbol) possibleSymbol);
          }

          if (elm instanceof Pattern) {
            elm.acceptChildren(this);
          }

        } else if (elm instanceof Optional || elm instanceof Condition || elm instanceof PatternTest) {
          PsiElement firstChild = elm.getFirstChild();
          if (firstChild != null) {
            firstChild.accept(this);
          }
        } else if (elm instanceof FunctionCall) {
          PsiElement head = elm.getFirstChild();
          final String name = head.getNode().getText();
          if (myDiveInFirstChild.contains(name)) {
            List<PsiElement> args = getArguments(elm);
            if (args.size() > 0) {
              args.get(0).accept(this);
            }
          } else if (!myDoNotDiveIn.contains(name)) {
            elm.acceptChildren(this);
          }
        } else {
          elm.acceptChildren(this);
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
   * @param listHead MList to extract from
   * @return MList of extracted {@link Symbol} PsiElement's.
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
   * Extracts all lhs from a list of definitions like {a, b=1, c}
   *
   * @param listHead MList to extract from
   * @return MList of extracted {@link Symbol} PsiElement's.
   */
  @NotNull
  private static List<Symbol> getDefinitionSymbolsFromList(PsiElement listHead) {
    List<Symbol> assignees = Lists.newArrayList();
    PsiElement children[] = listHead.getChildren();

    for (PsiElement child : children) {
      if (child instanceof Symbol) {
        assignees.add((Symbol) child);
      }
      if (child instanceof Set || child instanceof SetDelayed) {
        if (child.getFirstChild() instanceof Symbol) {
          assignees.add((Symbol) child.getFirstChild());
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

    if (element.isScopingConstruct() && element.getScopingConstruct().equals(MScope.FUNCTION)) {
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
          } else if (firstArgument instanceof MList) {
            localVariables = getSymbolsFromNestedList(firstArgument);
          }
          break;
        case 3:
          if (firstArgument instanceof Symbol && !((Symbol) firstArgument).getSymbolName().equals("Null")) {
            localVariables.add((Symbol) firstArgument);
          } else if (firstArgument instanceof MList) {
            localVariables = getSymbolsFromNestedList(firstArgument);
          }
          break;
      }
    }
    return localVariables;

  }


  @Nullable
  public static SymbolResolveResult resolveLocalFunctionVariables(Symbol myStartElement, FunctionCall functionCall, ResolveState state) {
    final List<PsiElement> arguments = getArguments(functionCall);
    if (arguments.size() < 1) {
      return null;
    }

    final MScope scope = functionCall.getScopingConstruct();

    final PsiElement lastParent = state.get(SymbolResolveHint.LAST_PARENT);
    final PsiElement firstArgument = arguments.get(0);
    PsiElement definitions = arguments.size() > 1 ? firstArgument : null;
    if (definitions != null) {
      boolean inDefinition = definitions.equals(lastParent);
      if (firstArgument instanceof Symbol && !((Symbol) firstArgument).getSymbolName().equals("Null") && ((Symbol) firstArgument).getFullSymbolName().equals(myStartElement.getFullSymbolName())) {
        return new SymbolResolveResult(firstArgument, scope, functionCall, true);
      } else if (firstArgument instanceof MList) {
        final List<Symbol> defSymbols = getDefinitionSymbolsFromList(firstArgument);
        for (Symbol def : defSymbols) {
          if (def.getFullSymbolName().equals(myStartElement.getFullSymbolName())) {
            if (!inDefinition || def.equals(myStartElement)) {
              return new SymbolResolveResult(def, scope, functionCall, true);
            } else {
              return new SymbolResolveResult(myStartElement, scope, functionCall, true);
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * This extracts the local defined arguments of a <code>Module</code>, <code>Block</code>, ... call. Examples are <ul>
   * <li><code>Module[{arg}, arg^2]</code> extracts <code>arg</code></li> <li><code>With[{a=1,b=2}, a*b]</code> extracts
   * <code>a,b</code></li> </ul>
   *
   * @param  element Module to find the local variables for
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
      if (firstArgument instanceof MList) {
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
   * This extracts the local defined arguments of a <code>Module</code>, <code>Block</code>, ... call. Examples are <ul>
   * <li><code>Module[{arg}, arg^2]</code> extracts <code>arg</code></li> <li><code>With[{a=1,b=2}, a*b]</code> extracts
   * <code>a,b</code></li> </ul>
   *
   * @param symbol Symbol we try to resolve
   * @param functionCall    The {@link PsiElement} of the function call
   * @param state The resolve state
   * @return The set of localized function arguments
   */
  @Nullable
  public static SymbolResolveResult resolveLocalModuleLikeVariables(@NotNull Symbol symbol, @NotNull FunctionCall functionCall, ResolveState state) {

    final PsiElement lastParent = state.get(SymbolResolveHint.LAST_PARENT);
    final MScope scopingConstruct = functionCall.getScopingConstruct();
    if (functionCall.isScopingConstruct() && LocalizationConstruct.isModuleLike(scopingConstruct)) {
      final List<PsiElement> arguments = getArguments(functionCall);
      if (arguments.size() < 1) {
        return null;
      }

      final PsiElement moduleDefList = arguments.get(0);
      final boolean isInDefList = moduleDefList.equals(lastParent);
      if (moduleDefList instanceof MList) {
        for (PsiElement e : moduleDefList.getChildren()) {
          if (e instanceof Set || e instanceof SetDelayed) {
            if (e.getFirstChild() instanceof Symbol) {
              e = e.getFirstChild();
            } else {
              continue;
            }
          }
          if (e instanceof Symbol) {
            if (((Symbol) e).getSymbolName().equals(symbol.getSymbolName())) {
              if (!isInDefList || e.equals(symbol)) {
                return new SymbolResolveResult(e, scopingConstruct, functionCall, true);
              } else {
                final List<Symbol> definitionInList = getDefinitionSymbolsFromList(moduleDefList);
                if (definitionInList.contains(symbol)) {
                  return new SymbolResolveResult(symbol, scopingConstruct, functionCall, false);
                } else {
                  return null;
                }
              }
            }
          }
        }
      }
    }
    return null;
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
        if (currentArgument instanceof MList) {
          final PsiElement firstListElement = getFirstListElement(currentArgument);
          if (firstListElement instanceof Symbol) {
            localVariables.add((Symbol) firstListElement);
          }
        }
      }
    }
    return localVariables;
  }

  @Nullable
  public static SymbolResolveResult resolveLocalTableLikeVariables(Symbol myStartElement, FunctionCall functionCall, ResolveState state) {

    final PsiElement lastParent = state.get(SymbolResolveHint.LAST_PARENT);
    final PsiElement[] args = functionCall.getArguments();
    // in a table we need at least Head, body and one iterator to search for a reference
    if (args.length < 3) return null;

    // find out where we were coming from. From within the body we can reference to the symbols in all iterator lists.
    // But it is also possible to reference from to a earlier iterator inside an iterator itself.
    int pos = -1;
    for (int i = 1; i < args.length; i++) {
      if (lastParent.equals(args[i])) {
        pos = i;
        break;
      }
    }

    // This means we were coming from the body of the table and have to search for definitions in all of the iterator lists
    final MScope scopingConstruct = functionCall.getScopingConstruct();
    if (pos == 1) {
      for (int i = 2; i < args.length; i++) {
        final PsiElement currentIterator = args[i];
        if (currentIterator instanceof MList) {
          final PsiElement firstListElement = getFirstListElement(currentIterator);
          if (firstListElement instanceof Symbol) {
            if (((Symbol) firstListElement).getFullSymbolName().equals(myStartElement.getFullSymbolName())) {
              return new SymbolResolveResult(firstListElement, scopingConstruct, functionCall, true);
            }
          }
        }
      }
    } else if (pos > 1) {
      // self-reference if we are the iterator
      if (args[pos] instanceof MList) {
        if (Objects.equals(getFirstListElement(args[pos]), myStartElement)) {
          return new SymbolResolveResult(myStartElement, scopingConstruct, functionCall, true);
        }
      }

      // test if we refer to an earlier iterator
      for (int i = 2; i < pos; i++) {
        if (args[i] instanceof MList) {
          final PsiElement firstListElement = getFirstListElement(args[i]);
          if (firstListElement instanceof Symbol) {
            if (((Symbol) firstListElement).getFullSymbolName().equals(myStartElement.getFullSymbolName())) {
              return new SymbolResolveResult(firstListElement, scopingConstruct, functionCall, true);
            }
          }
        }
      }

    }
    return null;
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
        if (currentArgument instanceof MList) {
          final PsiElement firstListElement = getFirstListElement(currentArgument);
          if (firstListElement instanceof Symbol) {
            localVariables.add((Symbol) firstListElement);
          } else if (firstListElement instanceof MList) {
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

      if (firstArgument instanceof MList) {
        PsiElement listElement = getFirstListElement(firstArgument);
        while (listElement != null) {
          if (listElement instanceof MList) {
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

  @Nullable
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
   * @return MList of arguments
   */
  @NotNull
  static List<PsiElement> getArguments(@Nullable PsiElement func) {
    List<PsiElement> allArguments = Lists.newLinkedList();
    if (!(func instanceof FunctionCall)) {
      return allArguments;
    }
    boolean skipHead = true;
    for (PsiElement child : func.getChildren()) {
      if (skipHead) {
        skipHead = false;
        continue;
      }
      allArguments.add(child);
    }
    return allArguments;
  }

  @Nullable
  private static PsiElement getNextArgument(@Nullable PsiElement arg) {
    if (arg == null) {
      return null;
    }
    PsiElement comma = getNextSiblingSkippingWhitespace(arg);
    if (comma != null && comma.getNode().getElementType().equals(MathematicaElementTypes.COMMA)) {
      return getNextSiblingSkippingWhitespace(comma);
    }
    return null;
  }

  /**
   * Extracts the context of a BeginPackage["Context`"] or Begin["Context`"] call
   *
   * @param element {@link FunctionCall} element that is a BeginPackage
   * @return context string or null if it could not be extracted
   */
  @Nullable
  public static String getBeginPackageContext(@NotNull PsiElement element) {
    return getContext(element, true);
  }

  /**
   * Extracts the context of a Begin["Context`"] call
   *
   * @param element {@link FunctionCall} element that is a BeginPackage
   * @return context string or null if it could not be extracted
   */
  @Nullable
  public static String getBeginContext(@NotNull PsiElement element) {
    return getContext(element, false);
  }

  /**
   * Extracts the context of a BeginPackage["Context`"] or Begin["Context`"] call
   *
   * @param element          {@link FunctionCall} element that is a BeginPackage
   * @param beginPackageOnly true if it only should extract BeginPackage contexts
   * @return context string or null if it could not be extracted
   */
  @Nullable
  private static String getContext(@NotNull PsiElement element, final boolean beginPackageOnly) {
    if (element instanceof FunctionCall) {
      final FunctionCall functionCall = (FunctionCall) element;
      if (functionCall.matchesHead(SymbolNames.BeginPackage) || (!beginPackageOnly && functionCall.matchesHead(SymbolNames.Begin))) {
        final PsiElement context = functionCall.getArgument(1);
        if (context instanceof MString) {
          final String contextString = context.getText();
          // We need to strip the quotes from the beginning and the end
          return contextString.substring(1, contextString.length() - 1);
        }
      }
    }
    return null;
  }

}
