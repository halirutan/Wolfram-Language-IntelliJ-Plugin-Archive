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
package de.halirutan.mathematica.errorreporting;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

/**
 * This class allows for i18n of the messages displayed by the error report submitter.
 *
 * @author <a href="mailto:intellij@studer.nu">Etienne Studer</a>, Jun 13, 2006
 */
class PluginErrorReportSubmitterBundle {
  private static final ResourceBundle OUR_BUNDLE = ResourceBundle.getBundle("com.sylvanaar.idea.errorreporting.PluginErrorReportSubmitterBundle");

  private PluginErrorReportSubmitterBundle() {
  }

  public static String message(@PropertyKey(resourceBundle = "com.sylvanaar.idea.errorreporting.PluginErrorReportSubmitterBundle") String key,
                               Object... params) {
    return CommonBundle.message(OUR_BUNDLE, key, params);
  }
}
