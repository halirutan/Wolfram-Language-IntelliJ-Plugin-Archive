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

package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.string.MString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author patrick (29.11.16).
 */
public class StringUsageReference extends PsiReferenceBase<MString> {

  private MString myElement1;
  private final String mySymbolNameInside;
  private final Symbol myTarget;

  public StringUsageReference(MString element, TextRange rangeInElement, final String symbolName, Symbol target) {
    super(element, rangeInElement, true);
    mySymbolNameInside = symbolName;
    myElement1 = element;
    myTarget = target;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return myTarget;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    final String text = myElement1.getText();
    Matcher matcher = getSymbolPattern(mySymbolNameInside).matcher(text);
    String newContent = matcher.replaceAll("$1" + newElementName + "$3");
    myElement1 = (MString) myElement1.setName(newContent);
    return myElement1;
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return element.equals(myTarget);
  }

  /**
   * Returns the pattern that matches the usage of a function-name or symbol inside a Mathematica usage string.
   * Example: "ref[x] works like a charm. The head of ref[x] is ref."
   * We match ref if it is at the beginning of a string or preceded by a whitespace. ref needs to be followed
   * by either [, a whitespace, a literal dot, or the end of the string.
   * @param symbolName the symbol name you want to match
   * @return a compiled pattern to match symbolName inside a usage string
   */
  public static Pattern getSymbolPattern(String symbolName) {
    return Pattern.compile("(\"|\\s)(" + symbolName + ")(\\[|\\.|\\s|\")");
  }


}
