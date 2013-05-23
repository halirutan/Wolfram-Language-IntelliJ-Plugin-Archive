/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.codeInsight.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author patrick (4/2/13)
 */
public class MathematicaBuiltInOptionCompletionContributor extends CompletionContributor {


  public MathematicaBuiltInOptionCompletionContributor() {
    extend(CompletionType.SMART, psiElement().withSuperParent(2, FunctionCall.class), new CompletionProvider<CompletionParameters>() {

      HashMap<String, SymbolInformationProvider.SymbolInformation> symbolInformation = SymbolInformationProvider.getSymbolNames();


      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        final PsiElement position = parameters.getPosition();
        final PsiElement function = position.getParent().getParent();
        if (function instanceof FunctionCall) {
          String functionName = ((Symbol) function.getFirstChild()).getSymbolName();
          if (symbolInformation.containsKey(functionName) && symbolInformation.get(functionName).function) {
            final SymbolInformationProvider.SymbolInformation functionInformation = symbolInformation.get(functionName);
            final String callPattern = functionInformation.callPattern;
            final String[] options = functionInformation.options;
            if (options != null) {
              for (String opt : options) {
                result.addElement(LookupElementBuilder.create(opt + " -> "));
              }
            }
          }
        }


      }
    });
  }
}
