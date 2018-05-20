/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.codeinsight.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider.SymbolInformation;
import de.halirutan.mathematica.codeinsight.completion.providers.MathematicaCompletionProvider;
import de.halirutan.mathematica.lang.psi.MathematicaRecursiveVisitor;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.api.MessageName;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.util.MathematicaIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Smart completion is invoked by pressing Ctrl+Shift+Space in certain specific situations. Here, we handle two cases:
 * First case is when you are inside a function call like Plot[...], then the smart completion suggests the
 * options for the specific symbol. Secondly, we handel Message[...] calls and display a list of all messages
 * from within the file.
 *
 * @author patrick (4/2/13)
 */
class SmartContextAwareCompletion extends MathematicaCompletionProvider {


  private static final HashMap<String, SymbolInformation> ourSymbolInformation =
      SymbolInformationProvider.getSystemSymbolInformation();
  private static final HashSet<String> ourOptionsWithSetDelayed = new HashSet<>(
      Arrays.asList("EvaluationMonitor", "StepMonitor", "DisplayFunction", "Deinitialization", "DisplayFunction",
          "DistributedContexts", "Initialization", "UnsavedVariables", "UntrackedVariables"));

  @Override
  public void addTo(CompletionContributor contributor) {
    final Capture<PsiElement> funcPattern = psiElement().withSuperParent(2, FunctionCall.class);
    contributor.extend(CompletionType.SMART, funcPattern, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull final CompletionResultSet result) {

    final PsiElement position = parameters.getPosition();
    final PsiElement function = position.getParent().getParent();
    if (function instanceof FunctionCall) {
      final Symbol head = (Symbol) ((FunctionCall) function).getHead();
      String functionName = head.getSymbolName();
      String functionContext = head.getMathematicaContext();
      if (functionContext.equals("") && ourSymbolInformation.containsKey("System`" + functionName)) {
        functionContext = "System`";
      }
      final String key = functionContext + functionName;
      if (ourSymbolInformation.containsKey(key) && ourSymbolInformation.get(key).function) {
        final SymbolInformation functionInformation = ourSymbolInformation.get(key);
        final String[] options = functionInformation.options;
        if (options != null) {
          for (String opt : options) {
            String ruleSymbol = ourOptionsWithSetDelayed.contains(opt) ? " :> " : " -> ";
            result.addElement(LookupElementBuilder.create(opt + ruleSymbol).withIcon(MathematicaIcons.OPTIONS_ICON));
          }
        }
      }

      if (functionName.equals("Message")) {
        final Set<LookupElement> usages = new com.intellij.util.containers.hash.HashSet<>();

        MathematicaRecursiveVisitor visitor = new MathematicaRecursiveVisitor() {

          @Override
          public void visitMessageName(final MessageName messageName) {
            usages.add(
                LookupElementBuilder.create(messageName.getText()).
                    withIcon(MathematicaIcons.MESSAGES_ICON).
                    withCaseSensitivity(false)); // make it case insensitive so you can type argx in Sym::argx to
            // find the correct completion
          }
        };
        visitor.visitFile(parameters.getOriginalFile());
        result.addAllElements(usages);
      }

    }
  }
}

