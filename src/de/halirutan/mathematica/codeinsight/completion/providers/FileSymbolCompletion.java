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

import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.hash.HashSet;
import de.halirutan.mathematica.codeinsight.completion.util.LocalDefinitionCompletionProvider;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static de.halirutan.mathematica.codeinsight.completion.MathematicaCompletionContributor.GLOBAL_VARIABLE_PRIORITY;
import static de.halirutan.mathematica.codeinsight.completion.MathematicaCompletionContributor.LOCAL_VARIABLE_PRIORITY;


/**
 * @author patrick (4/2/13)
 */
public class FileSymbolCompletion extends MathematicaCompletionProvider {


  @Override
  public void addTo(CompletionContributor contributor) {
    final Capture<PsiElement> symbolPattern = PlatformPatterns.psiElement().withParent(Symbol.class);
    contributor.extend(CompletionType.BASIC, symbolPattern, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final Symbol callingSymbol = (Symbol) parameters.getPosition().getParent();

    if (!parameters.isExtendedCompletion()) {
      String prefix = findCurrentText(parameters, parameters.getPosition());
      if (prefix.isEmpty() || Character.isDigit(prefix.charAt(0))) {
        return;
      }
      final PsiFile containingFile = parameters.getOriginalFile();
      List<Symbol> variants = Lists.newArrayList();

      final LocalDefinitionCompletionProvider processor = new LocalDefinitionCompletionProvider(callingSymbol);
      PsiTreeUtil.treeWalkUp(processor, callingSymbol, containingFile, ResolveState.initial());

      variants.addAll(processor.getSymbols());


      for (Symbol currentSymbol : variants) {
        result.addElement(PrioritizedLookupElement.withPriority(
            LookupElementBuilder.create(currentSymbol).bold().withItemTextForeground(JBColor.GREEN),
            LOCAL_VARIABLE_PRIORITY));
      }
    } else {
      final Set<String> allSymbols = new HashSet<>();
      PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
        @Override
        public void visitElement(PsiElement element) {
          if (element instanceof Symbol) {
            allSymbols.add(((Symbol) element).getFullSymbolName());
          }
          element.acceptChildren(this);
        }
      };
      visitor.visitFile(parameters.getOriginalFile());
      for (String currentSymbol : allSymbols) {
        result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(currentSymbol),
            GLOBAL_VARIABLE_PRIORITY));

      }

    }
  }


}
