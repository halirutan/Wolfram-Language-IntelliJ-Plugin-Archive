/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.file;

import java.util.Properties;

/**
 * Provides an easy way to create properties specific to Mathematica that can be inserted into templates.
 */
@SuppressWarnings("unused")
public class MathematicaTemplateProperties {
  public static final String CONTEXT = "MATHEMATICA_CONTEXT";
  public static final String MATHEMATICA_VERSION = "MATHEMATICA_VERSION";
  public static final String PACKAGE_NAME = "MATHEMATICA_PACKAGE_NAME";
  public static final String PACKAGE_VERSION = "MATHEMATICA_PACKAGE_VERSION";

  private Properties myProps = new Properties();

  private MathematicaTemplateProperties() {
  }

  public static MathematicaTemplateProperties create() {
    return new MathematicaTemplateProperties();
  }

  public MathematicaTemplateProperties setProperty(final String property, final String value) {
    myProps.setProperty(property, value);
    return this;
  }

  public Properties getProperties() {
    return myProps;
  }
}
