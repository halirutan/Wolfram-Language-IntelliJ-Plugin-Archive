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

package de.halirutan.mathematica.refactoring;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For Mathematica syntax, we need special identifier rules because a variable in Mathematica can contain context
 * back-ticks. Therefore, this is a valid variable C`B`a.
 * <p/>
 * In-place renaming will check the validity of a variables name and the rename context-variables, this needs to be a
 * valid identifier.
 *
 * @author patrick (28.10.15)
 */
class MathematicaNamesValidator implements NamesValidator {

  private final Pattern mySymbolPattern;

  @SuppressWarnings("WeakerAccess")
  public MathematicaNamesValidator() {
    final String nc = "\\\\\\[[a-zA-Z]+]";
    final String symbol = "([$a-zA-Z]|" + nc + ")([$a-zA-Z0-9]|" + nc + ")*";
    final String withContext = "`?(" + symbol + "`)*" + symbol;
    mySymbolPattern = Pattern.compile(withContext);

  }

  @Override
  public boolean isKeyword(@NotNull final String name, final Project project) {
    return false;
  }

  /**
   * Makes it possible that a variable can contain back-ticks.
   *
   * @param name    name of the variable you like to check
   * @param project project where the call comes from
   * @return true if name is a valid Mathematica identifier
   */
  @Override
  public boolean isIdentifier(@NotNull final String name, final Project project) {
    Matcher matcher = mySymbolPattern.matcher(name);
    return matcher.matches();
  }

}
