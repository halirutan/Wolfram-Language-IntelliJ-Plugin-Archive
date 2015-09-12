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
 * @author patrick (4/3/13)
 */
public class SymbolInformationProvider {

  private static HashMap<String, SymbolInformation> ourSymbols;

  private SymbolInformationProvider() {
  }

  private static void initialize() {

    ResourceBundle info = ResourceBundle.getBundle("/de/halirutan/mathematica/codeinsight/completion/symbolInformationV10_0_2");

    ourSymbols = new HashMap<String, SymbolInformation>(6000);

    Enumeration<String> names = info.getKeys();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      String parts[] = info.getString(name).split(";");

      boolean isFunction = false;
      int importance = 0;
      String attributes[] = {};
      String shortName = "";
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

      if (parts.length > 2) {
        shortName = parts[2];

      }

      if (parts.length > 3) {
        pattern = parts[3].trim();
      }

      if (parts.length > 4) {
        options = parts[4].replace("{", "").replace("}", "").trim().split(", ");
      }

      if (!pattern.equals("")) {
        isFunction = true;
      }

      ourSymbols.put(name, new SymbolInformation(name, shortName, importance, pattern, isFunction, attributes, options));
    }

  }

  public static HashMap<String, SymbolInformation> getSymbolNames() {
    if (ourSymbols == null) {
      initialize();
    }
    return ourSymbols;
  }

  public static class SymbolInformation {
    public final String name;
    public final int importance;
    public final String shortName;
    public final String callPattern;
    public final boolean function;
    public final String attributes[];
    public final String options[];
    public String context = "System";
    public String nameWithoutContext;

    public SymbolInformation(String name, String shortName, int importance, String callPattern, boolean function, String[] attributes, String[] options) {
      this.name = name;
      this.nameWithoutContext = name;
      this.shortName = shortName.length() == 0 ? name : shortName;
      this.importance = importance;
      this.callPattern = callPattern;
      this.function = function;
      this.attributes = attributes;
      this.options = options;

      if (name.contains("`")) {
        context = name.split("`")[0];
        nameWithoutContext = name.substring(context.length() + 1);
      }


    }

    public SymbolInformation(String name) {
      this.name = name;
      shortName = name;
      importance = 0;
      callPattern = "";
      function = false;
      attributes = new String[0];
      options = new String[0];
    }


  }

}
