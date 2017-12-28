/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.lang.psi;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This is the main source of information for finding local symbol bindings. In the same spirit as
 * <code>SyntaxInformation</code> in Mathematica, it tells which function follows which localization pattern. We need to
 * know where the positions of the parameters are that are localized and where the body of the function is. In the body
 * one can refer to all localized variables. Furthermore, one can refer to local parameters in later definition
 * positions like here
 * <p>
 * <code>Table[i+j, {i, 0, 10}, {j, 0, i}]</code>
 * <p>
 * This class divides localization constructs into different classes that follow different strategies. For more
 * information about how it is
 *
 * @author patrick (7/24/13)
 */
public class LocalizationConstruct {

  private static final Set<String> ourScopesAsString = Sets.newHashSet();

  static {
    for (MScope scope : MScope.values()) {
      final String s = scope.toString();
      if (!s.contains(" ")) {
        ourScopesAsString.add(scope.toString());
      }
    }
  }

  public static boolean isScopingFunction(String elementName) {
    return ourScopesAsString.contains(elementName);
  }

  public static boolean isLocalScoping(MScope scope) {
    return scope.myType != ScopeType.NONE;
  }

  public static boolean isModuleLike(MScope scopingConstruct) {
    return scopingConstruct.myType == ScopeType.MODULE_LIKE;
  }

  public static boolean isTableLike(MScope scopingConstruct) {
    return scopingConstruct.myType == ScopeType.TABLE_LIKE;
  }

  public static boolean isCompileLike(MScope scopingConstruct) {
    return scopingConstruct.myType == ScopeType.COMPILE_LIKE;
  }

  public static boolean isManipulateLike(MScope scopingConstruct) {
    return scopingConstruct.myType == ScopeType.MANIPULATE_LIKE;
  }

  @SuppressWarnings("unused")
  public static boolean isRuleLike(MScope scopingConstruct) {
    return scopingConstruct.myType == ScopeType.RULE_LIKE;
  }

  public static boolean isLimitLike(MScope scopingConstruct) {
    return scopingConstruct.myType == ScopeType.LIMIT_LIKE;
  }

  public static boolean isFunctionLike(MScope scopingConstruct) {
    return scopingConstruct.myType == ScopeType.FUNCTION_LIKE;
  }

  public static MScope getScope(String name) {
    if (isScopingFunction(name)) {
      return MScope.valueOf(name.toUpperCase());
    }
    return MScope.NULL_SCOPE;
  }

  public enum ScopeType {
    MODULE_LIKE, TABLE_LIKE, COMPILE_LIKE, MANIPULATE_LIKE, RULE_LIKE, LIMIT_LIKE, FUNCTION_LIKE, ANONYMOUS_FUNCTION_LIKE, NONE
  }

  public enum MScope {
    MODULE("Module", ScopeType.MODULE_LIKE, 1, 0, 0),
    BLOCK("Block", ScopeType.MODULE_LIKE, 1, 0, 0),
    DYNAMICMODULE("DynamicModule", ScopeType.MODULE_LIKE, 1, 0, 0),
    WITH("With", ScopeType.MODULE_LIKE, -1, 0, -2),
    FUNCTION("Function", ScopeType.FUNCTION_LIKE, 1, 0, 0),
    TABLE("Table", ScopeType.TABLE_LIKE, 0, 1, -1),
    DO("Do", ScopeType.TABLE_LIKE, 0, 1, -1),
    SUM("Sum", ScopeType.TABLE_LIKE, 0, 1, -1),
    NSUM("NSum", ScopeType.TABLE_LIKE, 0, 1, -1),
    INTEGRATE("Integrate", ScopeType.TABLE_LIKE, 0, 1, -1),
    NINTEGRATE("NIntegrate", ScopeType.TABLE_LIKE, 0, 1, -1),
    PLOT("Plot", ScopeType.TABLE_LIKE, 0, 1, 1),
    PLOT3D("Plot3D", ScopeType.TABLE_LIKE, 0, 1, 2),
    CONTOURPLOT("ContourPlot", ScopeType.TABLE_LIKE, 0, 1, 2),
    CONTOURPLOT3D("ContourPlot3D", ScopeType.TABLE_LIKE, 0, 1, 3),
    PARAMETRICPLOT("ParametricPlot", ScopeType.TABLE_LIKE, 0, 1, 2),
    PARAMETRICPLOT3D("ParametricPlot3D", ScopeType.TABLE_LIKE, 0, 1, 2),
    STREAMPLOT("StreamPlot", ScopeType.TABLE_LIKE, 0, 1, 2),
    STREAMDENSITYPLOT("StreamDensityPlot", ScopeType.TABLE_LIKE, 0, 1, 2),
    VECTORPLOT("VectorPlot", ScopeType.TABLE_LIKE, 0, 1, 2),
    VECTORPLOT3D("VectorPlot3D", ScopeType.TABLE_LIKE, 0, 1, 3),
    LIMIT("Limit", ScopeType.LIMIT_LIKE, 0, 1, 1),
    MANIPULATE("Manipulate", ScopeType.MANIPULATE_LIKE, 0, 1, -1),
    COMPILE("Compile", ScopeType.COMPILE_LIKE, 1, 0, 0),
    // The last entries because they are not directly connected to a function call with a special head,
    // because we create the scope from the PsiElement automatically.
    ANONYMOUS_FUNCTION_SCOPE("AnonymousFunction Scope", ScopeType.ANONYMOUS_FUNCTION_LIKE, 0, 1, 1),
    RULEDELAYED_SCOPE("RuleDelayed Scope", ScopeType.RULE_LIKE, 1, 0, 0),
    SETDELAYED_SCOPE("SetDelayed Scope", ScopeType.RULE_LIKE, 1, 0, 0),
    KERNEL_SCOPE("Kernel Scope"),
    FILE_SCOPE("File Scope"),
    IMPORT_SCOPE("Import Scope"),
    NULL_SCOPE("Null Scope");

    final String myName;
    final ScopeType myType;
    final int myBodyPosition;
    final int myScopePositionStart;
    final int myScopePositionEnd;

    /**
     * Creates a certain scope type for special constructs.
     *
     * @param name  Usually without space and matches the name of the Mathematica symbol.
     * @param type  How the localization acts
     * @param body  Position of the body, where all localized variables can be referenced
     * @param start First position of the element that defines a local variable
     * @param end   Last position of element defining local scope. -1 indicates that all elements beginning from start
     *              are collected
     */
    MScope(final String name, final ScopeType type, int body, int start, int end) {
      myName = name;
      myType = type;
      myBodyPosition = body;
      myScopePositionStart = start;
      myScopePositionEnd = end;
    }

    /**
     * Fake constructor to provide special scope indicators since {@link MScope} is used for all references
     *
     * @param name Name of the scope. Usually containing a whitespace to make them distinguishable.
     */
    MScope(final String name) {
      myName = name;
      myType = ScopeType.NONE;
      myBodyPosition = -1;
      myScopePositionStart = -1;
      myScopePositionEnd = -1;
    }

    @Override
    public String toString() {
      return myName;
    }

    @NotNull
    public ScopeType getType() {
      return myType;
    }

    public int getBodyPosition() {
      return myBodyPosition;
    }

    public int getScopePositionStart() {
      return myScopePositionStart;
    }

    public int getScopePositionEnd() {
      return myScopePositionEnd;
    }
  }
}
