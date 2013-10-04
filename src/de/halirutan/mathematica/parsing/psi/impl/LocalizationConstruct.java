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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author patrick (7/24/13)
 */
public class LocalizationConstruct {

  private static final Set<String> myModuleLike = new HashSet<String>(Arrays.asList(new String[]{
      "Module", "Block", "With", "Function", "DynamicModule"}));
  private static final Set<String> myTableLike = new HashSet<String>(Arrays.asList(new String[]{
      "Table", "Sum", "Integrate", "NSum", "Plot", "Plot3D", "ContourPlot", "ContourPlot3D",}));
  private static final Set<String> myLimitLike = new HashSet<String>(Arrays.asList(new String[]{"Limit"}));
  private static final Set<String> myRuleLike = new HashSet<String>(Arrays.asList(new String[]{
      "RuleDelayed"}));

  public static boolean isLocalizationConstruct(String elementName) {
    return myModuleLike.contains(elementName) ||
        myTableLike.contains(elementName) ||
        myLimitLike.contains(elementName) ||
        myRuleLike.contains(elementName);
  }

  public static boolean isModuleLike(String name) {
    return myModuleLike.contains(name);
  }

  public static boolean isTableLike(String name) {
    return myTableLike.contains(name);
  }

  public static boolean isRuleLike(String name) {
    return myRuleLike.contains(name);
  }

  public static boolean isLimitLike(String name) {
    return myLimitLike.contains(name);
  }

  public static ConstructType getType(String name) {
    if (isLocalizationConstruct(name)) {
      return ConstructType.valueOf(name.toUpperCase());
    }
    return ConstructType.NULL;
  }

  public enum ConstructType {
    MODULE, BLOCK, WITH, FUNCTION, DYNAMICMODULE, TABLE, SUM, NULL, INTEGRATE, NSUM, PLOT, PLOT3D, CONTOURPLOT, CONTOURPLOT3D,
    LIMIT, RULEDELAYED, SETDELAYEDPATTERN
  }


}
