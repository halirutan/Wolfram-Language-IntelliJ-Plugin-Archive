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

package de.halirutan.mathematica.codeinsight.completion;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Loads and extracts all information of Mathematica built-in symbols that are used for completion and intelligent
 * code insight.
 * @author hal (4/3/13)
 */
public class SymbolVersionProvider {

  private final static String ourSymbolVersionFile = "/de/halirutan/mathematica/codeinsight/completion/symbolVersions";
  private final static HashMap<String, Double> ourSymbols;

  private SymbolVersionProvider() {}

  static {

    ResourceBundle info = ResourceBundle.getBundle(ourSymbolVersionFile);
    ourSymbols = new HashMap<>(6000);

    Enumeration<String> names = info.getKeys();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      double version = Double.parseDouble(info.getString(name));
      ourSymbols.put(name, version);
    }

  }

  public static HashMap<String, Double> getSymbolNames() {
    return ourSymbols;
  }

  public static String getName(String nameWithContext) {
    String context = nameWithContext.split("`")[0] + "`";
    return nameWithContext.substring(context.length());
  }

}
