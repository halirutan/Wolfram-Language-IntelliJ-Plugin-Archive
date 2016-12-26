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

package de.halirutan.mathematica.codeinsight.completion;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Loads and extracts all information of Mathematica built-in symbols that are used for completion and intelligent
 * code insight.
 * @author hal (4/3/13)
 */
public class SymbolInformationProvider {

  private final static String ourSymbolInformationFile = "de.halirutan.mathematica.codeinsight.completion.symbolInformationV11_0_1";
//  private final static String ourSymbolInformationFile = "symbolInformationV11_0_1";
  private final static HashMap<String, SymbolInformation> ourSymbols;

  private SymbolInformationProvider() {}

  static {

    ResourceBundle info = ResourceBundle.getBundle(ourSymbolInformationFile);
    ourSymbols = new HashMap<String, SymbolInformation>(6000);

    Enumeration<String> names = info.getKeys();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      String context = name.split("`")[0] + "`";
      String nameWithoutContext = name.substring(context.length());
      String parts[] = info.getString(name).split(";");

      boolean isFunction = false;
      int importance = 0;
      String attributes[] = {};
      String pattern = "";
      String options[] = {};

      //extract whether it is a function
      if (parts.length > 0) {

        try {
          importance = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
          importance = 0;
        }
      }

      if (parts.length > 1) {
        attributes = parts[1].trim().split(" ");
      }

      // part 2 is missing because it was used as "ShortName" until everything was refactored and the short name is
      // now extracted from the full context name of a symbol

      if (parts.length > 3) {
        pattern = parts[3].trim();
      }

      if (parts.length > 4) {
        options = parts[4].replace("{", "").replace("}", "").trim().split(", ");
      }

      if (!pattern.equals("")) {
        isFunction = true;
      }

      ourSymbols.put(name, new SymbolInformation(name, nameWithoutContext, context, importance, pattern, isFunction, attributes, options));
    }

  }

  public static HashMap<String, SymbolInformation> getSymbolNames() {
    return ourSymbols;
  }

  @SuppressWarnings("InstanceVariableNamingConvention")
  public static class SymbolInformation {
    public final String name;
    public final int importance;
    public final String callPattern;
    public final boolean function;
    public final String attributes[];
    public final String options[];
    public String context;
    public String nameWithoutContext;

    SymbolInformation(String nameIn, String nameWithoutContextIn, String contextIn, int importanceIn, String callPatternIn, boolean functionIn, String[] attributesIn, String[] optionsIn) {
      this.name = nameIn;
      this.nameWithoutContext = nameWithoutContextIn;
      this.context = contextIn;
      this.importance = importanceIn;
      this.callPattern = callPatternIn;
      this.function = functionIn;
      this.attributes = attributesIn;
      this.options = optionsIn;
    }

    /**
     * Removes the braces around the call patter
     * @return call pattern without braces
     */
    String getCallPattern() {
      if (callPattern.length() > 2 && callPattern.matches("\\{.*}")) {
        return callPattern.substring(1, callPattern.length() - 1);
      }
      return "";
    }

  }

}
