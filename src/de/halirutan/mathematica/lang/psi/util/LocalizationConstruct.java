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

package de.halirutan.mathematica.lang.psi.util;

import com.google.common.collect.Sets;

import java.util.Set;

/**
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
        return scope.myType != ScopeParameter.NONE;
    }

    public static boolean isModuleLike(MScope scopingConstruct) {
        return scopingConstruct.myType == ScopeParameter.MODULE_LIKE;
    }

    public static boolean isTableLike(MScope scopingConstruct) {
        return scopingConstruct.myType == ScopeParameter.TABLE_LIKE;
    }

    public static boolean isCompileLike(MScope scopingConstruct) {
        return scopingConstruct.myType == ScopeParameter.COMPILE_LIKE;
    }

    public static boolean isManipulateLike(MScope scopingConstruct) {
        return scopingConstruct.myType == ScopeParameter.MANIPULATE_LIKE;
    }

    public static boolean isRuleLike(MScope scopingConstruct) {
        return scopingConstruct.myType == ScopeParameter.RULE_LIKE;
    }

    public static boolean isLimitLike(MScope scopingConstruct) {
        return scopingConstruct.myType == ScopeParameter.LIMIT_LIKE;
    }

    public static boolean isFunctionLike(MScope scopingConstruct) {
        return scopingConstruct.myType == ScopeParameter.FUNCTION_LIKE;
    }

    public static MScope getScope(String name) {
        if (isScopingFunction(name)) {
            return MScope.valueOf(name.toUpperCase());
        }
        return MScope.NULL_SCOPE;
    }

    public enum ScopeParameter {
        MODULE_LIKE,
        TABLE_LIKE,
        COMPILE_LIKE,
        MANIPULATE_LIKE,
        RULE_LIKE,
        LIMIT_LIKE,
        FUNCTION_LIKE,
        ANONYMOUS_FUNCTION_LIKE,
        NONE
    }

    public enum MScope {
        MODULE("Module", ScopeParameter.MODULE_LIKE, 1, 1),
        BLOCK("Block", ScopeParameter.MODULE_LIKE, 1, 1),
        DYNAMICMODULE("DynamicModule", ScopeParameter.MODULE_LIKE, 1, 1),
        WITH("With", ScopeParameter.MODULE_LIKE, 1, 1),
        FUNCTION("Function", ScopeParameter.FUNCTION_LIKE, 1, 1),
        TABLE("Table", ScopeParameter.TABLE_LIKE, 2, -1),
        DO("Do", ScopeParameter.TABLE_LIKE, 2, -1),
        SUM("Sum", ScopeParameter.TABLE_LIKE, 2, -1),
        NSUM("NSum", ScopeParameter.TABLE_LIKE, 2, -1),
        INTEGRATE("Integrate", ScopeParameter.TABLE_LIKE, 2, -1),
        NINTEGRATE("NIntegrate", ScopeParameter.TABLE_LIKE, 2, -1),

        PLOT("Plot", ScopeParameter.TABLE_LIKE, 2, 2),
        PLOT3D("Plot3D", ScopeParameter.TABLE_LIKE, 2, 3),
        CONTOURPLOT("ContourPlot", ScopeParameter.TABLE_LIKE, 2, 3),
        CONTOURPLOT3D("ContourPlot3D", ScopeParameter.TABLE_LIKE, 2, 4),
        PARAMETRICPLOT("ParametricPlot", ScopeParameter.TABLE_LIKE, 2, 3),
        PARAMETRICPLOT3D("ParametricPlot3D", ScopeParameter.TABLE_LIKE, 2, 3),
        STREAMPLOT("StreamPlot", ScopeParameter.TABLE_LIKE, 2, 3),
        STREAMDENSITYPLOT("StreamDensityPlot", ScopeParameter.TABLE_LIKE, 2, 3),
        VECTORPLOT("VectorPlot", ScopeParameter.TABLE_LIKE, 2, 3),
        VECTORPLOT3D("VectorPlot3D", ScopeParameter.TABLE_LIKE, 2, 4),

        LIMIT("Limit", ScopeParameter.LIMIT_LIKE, 2, 2),
        MANIPULATE("Manipulate", ScopeParameter.TABLE_LIKE, 2, -1),
        COMPILE("Compile", ScopeParameter.COMPILE_LIKE, 1, 1),
        // The last entries because they are not directly connected to a function call with a special head,
        // because we create the scope from the PsiElement automatically.
        ANONYMOUS_FUNCTION_SCOPE("AnonymousFunction Scope", ScopeParameter.ANONYMOUS_FUNCTION_LIKE, 1, 1),
        RULEDELAYED_SCOPE("RuleDelayed Scope", ScopeParameter.RULE_LIKE, 1, 1),
        SETDELAYED_SCOPE("SetDelayed Scope", ScopeParameter.RULE_LIKE, 1, 1),
        KERNEL_SCOPE("Kernel Scope"),
        FILE_SCOPE("File Scope"),
        NULL_SCOPE("Null Scope");

        final String myName;
        final ScopeParameter myType;
        final int myScopePositionStart;
        final int myScopePositionEnd;

        MScope(final String name, final ScopeParameter type, int start, int end) {
            myName = name;
            myType = type;
            myScopePositionStart = start;
            myScopePositionEnd = end;
        }

        MScope(final String name) {
            myName = name;
            myType = ScopeParameter.NONE;
            myScopePositionStart = -1;
            myScopePositionEnd = -1;
        }


        @Override
        public String toString() {
            return myName;
        }

    }


}
