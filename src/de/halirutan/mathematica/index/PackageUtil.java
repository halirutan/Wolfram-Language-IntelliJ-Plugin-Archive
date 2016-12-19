/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.index;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author patrick (18.12.16).
 */
public class PackageUtil {
  public static final Pattern contextPattern = Pattern.compile("`?(([a-zA-Z$]+[0-9]*)+`)+");
  public static final Pattern relativeContextPattern = Pattern.compile("`(([a-zA-Z$]+[0-9]*)+`)+");
  public static final Pattern absoluteContextPattern = Pattern.compile("(([a-zA-Z$]+[0-9]*)+`)+");
  private PackageUtil(){}

  public static String buildContext(List<String> contextStack) {
    StringBuilder context = new StringBuilder("Global`");
    for (String current : contextStack) {
      final Matcher mRelative = relativeContextPattern.matcher(current);
      final Matcher mAbsolute = absoluteContextPattern.matcher(current);
      if (mRelative.matches()) {
        context.append(current.substring(1));
      } else if (mAbsolute.matches()) {
        context = new StringBuilder(current);
      }
    }
    return context.toString();
  }
}
