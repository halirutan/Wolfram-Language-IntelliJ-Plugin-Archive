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

package de.halirutan.mathematica.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import de.halirutan.mathematica.MathematicaBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Provides a way to represent the different versions of a Mathematica SDK. We will not use all possible different builds
 * of Mathematica, but only represent Major and Minor version jumps.
 * @author patrick (11/22/2016)
 */
@SuppressWarnings({"EnumeratedConstantNamingConvention", "WeakerAccess"})
public enum MathematicaLanguageLevel {
//  M_11_3("11", MathematicaBundle.message("language.level.11.3")),
  M_11_2("11.2", MathematicaBundle.message("language.level.11.2")),
  M_11_1("11.1", MathematicaBundle.message("language.level.11.1")),
  M_11("11", MathematicaBundle.message("language.level.11")),
  M_10_4("10.4", MathematicaBundle.message("language.level.10.4")),
  M_10_3("10.3", MathematicaBundle.message("language.level.10.3")),
  M_10_2("10.2", MathematicaBundle.message("language.level.10.2")),
  M_10_1("10.1", MathematicaBundle.message("language.level.10.1")),
  M_10("10",MathematicaBundle.message("language.level.10")),
  M_9("9", MathematicaBundle.message("language.level.9")),
  M_8("8", MathematicaBundle.message("language.level.8"));


  public static final MathematicaLanguageLevel HIGHEST = M_11;

  private final String myName;
  private final String myPresentableText;
  private final double myVersionNumber;
  MathematicaLanguageLevel(@NotNull String name, @NotNull @Nls String presentableText) {
    myName = name;
    myPresentableText = presentableText;
    myVersionNumber = Double.parseDouble(name);
  }

  /**
   * The parsed version string from an Sdk of type {@link MathematicaSdkType} will look like this: </br>
   * <ul>
   *   <li>10.0.2.5206630</li>
   *   <li>11.0.1.5597743</li>
   *   <li>9.0.1.4055646</li>
   * </ul>
   *
   * The last long number seems to be some built-number, while the part in the front is the version number the user
   * usually sees.
   *
   * @param sdk an Sdk of type {@link MathematicaSdkType}
   * @return the extracted language version
   */
  public static MathematicaLanguageLevel createFromSdk(@NotNull Sdk sdk) {
    if (sdk.getSdkType() instanceof MathematicaSdkType) {
      final String version = sdk.getVersionString();
      if(version != null) {
//        if (version.matches("11\\.3.*")) return M_11_3;
        if (version.matches("11\\.2.*")) return M_11_2;
        if (version.matches("11\\.1.*")) return M_11_1;
        if (version.matches("11\\.0.*")) return M_11;
        if (version.matches("10\\.4.*")) return M_10_4;
        if (version.matches("10\\.3.*")) return M_10_3;
        if (version.matches("10\\.2.*")) return M_10_2;
        if (version.matches("10\\.1.*")) return M_10_1;
        if (version.matches("10\\.0.*")) return M_10;
        if (version.matches("9.*")) return M_9;
        if (version.matches("8.*")) return M_8;
      }
    }
    return M_11;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  public double getVersionNumber() {
    return myVersionNumber;
  }

  @NotNull
  @Nls
  public String getPresentableText() {
    return myPresentableText;
  }

  public boolean isAtLeast(@NotNull MathematicaLanguageLevel level) {
    return compareTo(level) <= 0;
  }

  public boolean isLessThan(@NotNull MathematicaLanguageLevel level) {
    return compareTo(level) > 0;
  }


  @Override
  public String toString() {
    return myPresentableText;
  }
}
