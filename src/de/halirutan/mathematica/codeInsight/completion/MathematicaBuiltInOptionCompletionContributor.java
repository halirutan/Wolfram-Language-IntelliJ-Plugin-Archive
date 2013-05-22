/*
 * Copyright (c) 2013 Wolfram Research, Inc.  All rights reserved.
 * Redistribution or use of this work in any form, with or without modification,
 * requires written permission from the copyright holder.
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
