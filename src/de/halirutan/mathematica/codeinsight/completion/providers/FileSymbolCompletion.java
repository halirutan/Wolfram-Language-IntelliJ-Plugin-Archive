/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.codeinsight.completion.providers;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static de.halirutan.mathematica.codeinsight.completion.MathematicaCompletionContributor.GLOBAL_VARIABLE_PRIORITY;


/**
 * Provides completion for symbols that are defined at File Scope (opposed to locally bound variables).
 */
public class FileSymbolCompletion extends MathematicaCompletionProvider {

  @Override
  public void addTo(CompletionContributor contributor) {
    final Capture<PsiElement> symbolPattern = PlatformPatterns.psiElement().withParent(Symbol.class);
    contributor.extend(CompletionType.BASIC, symbolPattern, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final PsiFile containingFile = parameters.getOriginalFile();

    String prefix = findCurrentText(parameters, parameters.getPosition());
    if (!parameters.isExtendedCompletion() && !prefix.isEmpty() && containingFile instanceof MathematicaPsiFile) {
      final CamelHumpMatcher matcher = new CamelHumpMatcher(prefix, true);
      CompletionResultSet result2 = result.withPrefixMatcher(matcher);
      final HashSet<SymbolResolveResult> cachedDefinitions =
          ((MathematicaPsiFile) containingFile).getCachedDefinitions();
      for (SymbolResolveResult cachedDefinition : cachedDefinitions) {
        if (cachedDefinition.isValidResult() && cachedDefinition.getElement() != null) {
          result2.addElement(
              PrioritizedLookupElement
                  .withPriority(
                      LookupElementBuilder
                          .create(cachedDefinition.getElement())
                          .bold()
                          .withTypeText("(" + containingFile.getName() + ")", true),
                      GLOBAL_VARIABLE_PRIORITY));
        }
      }
    }
  }


}
