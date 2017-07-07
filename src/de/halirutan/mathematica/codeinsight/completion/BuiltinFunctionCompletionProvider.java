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

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider.SymbolInformation;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.settings.MathematicaSettings;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Provides completion for Mathematica built-in symbols. The underlying important file with all information can be
 * found in the resource directory de/halirutan/mathematica/codeinsight/completion.
 * @author hal (4/2/13)
 */
class BuiltinFunctionCompletionProvider extends MathematicaCompletionProvider {

  @Override
  void addTo(CompletionContributor contributor) {
    final Capture<PsiElement> psiElementCapture = psiElement().withElementType(MathematicaElementTypes.IDENTIFIER);
    contributor.extend(CompletionType.BASIC, psiElementCapture, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    HashMap<String, SymbolInformation> symbols = SymbolInformationProvider.getSymbolNames();

    if (Character.isLowerCase(parameters.getPosition().getText().charAt(0))) {
      return;
    }

    String prefix = findCurrentText(parameters, parameters.getPosition());
    final CamelHumpMatcher matcher = new CamelHumpMatcher(prefix, true);
    CompletionResultSet result2 = result.withPrefixMatcher(matcher);

    final boolean sortByImportance = !MathematicaSettings.getInstance().isSortCompletionEntriesLexicographically();

    for (SymbolInformation info : symbols.values()) {
      BuiltinSymbolLookupElement lookup = new BuiltinSymbolLookupElement(info);
      if (sortByImportance) {
        result2.addElement(PrioritizedLookupElement.withPriority(lookup, info.importance));
      } else {
        result2.addElement(lookup);
      }
    }
  }
}
