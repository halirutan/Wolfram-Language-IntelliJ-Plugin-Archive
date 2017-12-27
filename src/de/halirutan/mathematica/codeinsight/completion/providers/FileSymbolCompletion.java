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
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.lang.psi.LocalizationConstruct;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

import static de.halirutan.mathematica.codeinsight.completion.MathematicaCompletionContributor.GLOBAL_VARIABLE_PRIORITY;


/**
 * Provides completion for symbols that are defined at File Scope (opposed to locally bound variables).
 */
public class FileSymbolCompletion extends MathematicaCompletionProvider {

  private static final Logger LOG =
      Logger.getInstance("#de.halirutan.mathematica.codeinsight.completion.providers.FileSymbolCompletion");

  @Override
  public void addTo(CompletionContributor contributor) {
    final Capture<PsiElement> symbolPattern = PlatformPatterns.psiElement().withParent(Symbol.class);
    contributor.extend(CompletionType.BASIC, symbolPattern, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final PsiFile containingFile = parameters.getOriginalFile();

    if (!parameters.isExtendedCompletion()) {
      LOG.debug("Running file symbol completion");
      final Set<String> symbols = PsiTreeUtil.findChildrenOfType(containingFile, Symbol.class).stream().filter(
          s -> {
            final LocalizationConstruct.MScope localizationConstruct = s.getLocalizationConstruct();
            return s.isValid() && localizationConstruct == LocalizationConstruct.MScope.FILE_SCOPE;
          }
      ).map(
          Symbol::getSymbolName
      ).collect(Collectors.toSet());
      symbols.forEach(s -> result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(s),
          GLOBAL_VARIABLE_PRIORITY)));
    }
  }


}
