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
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author patrick (4/2/13)
 */
public class BuiltinOptionCompletionProvider extends MathematicaCompletionProvider {


  static final HashMap<String, SymbolInformationProvider.SymbolInformation> ourSymbolInformation = SymbolInformationProvider.getSymbolNames();
  static HashSet<String> ourOptionsWithSetDelayed = null;
  static final String[] SET_DELAYED_OPTIONS = {
    "EvaluationMonitor", "StepMonitor", "DisplayFunction", "Deinitialization", "DisplayFunction",
    "DistributedContexts", "Initialization", "UnsavedVariables", "UntrackedVariables"};

  @Override
  void addTo(CompletionContributor contributor) {
    final PsiElementPattern.Capture<PsiElement> funcPattern = psiElement().withSuperParent(2, FunctionCall.class);
    contributor.extend(CompletionType.SMART, funcPattern, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {

    if (ourOptionsWithSetDelayed == null) {
      ourOptionsWithSetDelayed = new HashSet<String>(Arrays.asList(SET_DELAYED_OPTIONS));
    }


    final PsiElement position = parameters.getPosition();
    final PsiElement function = position.getParent().getParent();
    if (function instanceof FunctionCall) {
      String functionName = ((Symbol) function.getFirstChild()).getSymbolName();
      if (ourSymbolInformation.containsKey(functionName) && ourSymbolInformation.get(functionName).function) {
        final SymbolInformationProvider.SymbolInformation functionInformation = ourSymbolInformation.get(functionName);
        final String[] options = functionInformation.options;
        if (options != null) {
          for (String opt : options) {
            String ruleSymbol = ourOptionsWithSetDelayed.contains(opt) ? " :> " : " -> ";
            result.addElement(LookupElementBuilder.create(opt + ruleSymbol).withTypeText("Opt"));
          }
        }
      }
    }


  }
}

