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

package de.halirutan.mathematica.lang.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.lang.psi.api.Expression;
import de.halirutan.mathematica.lang.psi.api.MessageName;
import de.halirutan.mathematica.lang.psi.api.StringifiedSymbol;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.assignment.Set;
import de.halirutan.mathematica.lang.psi.api.string.MString;
import de.halirutan.mathematica.lang.psi.api.string.StringJoin;
import de.halirutan.mathematica.lang.psi.impl.StringUsageReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author patrick (29.11.16).
 */
class MathematicaStringReferenceProvider extends PsiReferenceProvider {


  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (!(element instanceof MString)) {
      return new PsiReference[0];
    }
    ArrayList<PsiReference> result = new ArrayList<>();

    PsiElement setElement = element.getParent();

    // Specifically for Szabolcs who likes to <> usage messages
    while (setElement instanceof StringJoin) {
      setElement = setElement.getParent();
    }

    if (setElement instanceof Set) {
      final PsiElement messageElement = setElement.getFirstChild();
      if (messageElement instanceof MessageName) {
        final StringifiedSymbol tag = ((MessageName) messageElement).getTag();
        if ("usage".equals(tag != null ? tag.getText() : null)) {
          final Expression symbol = ((MessageName) messageElement).getSymbol();
          if (symbol instanceof Symbol) {
            String usageText = element.getText();
            final String symbolName = Matcher.quoteReplacement(((Symbol) symbol).getFullSymbolName());
            Pattern symbolNamePattern = StringUsageReference.getSymbolPattern(symbolName);
            final Matcher matcher = symbolNamePattern.matcher(usageText);
            while (matcher.find()) {
              final int start = matcher.start(2);
              final int end = matcher.end(2);
              result.add(
                  new StringUsageReference((MString) element, TextRange.create(start, end), symbolName, (Symbol) symbol.getReference().resolve())
              );
            }
          }


        }
      }
    }
    return result.toArray(PsiReference.EMPTY_ARRAY);
  }


}
