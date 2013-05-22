/*
 * Copyright (c) 2013 Wolfram Research, Inc.  All rights reserved.
 * Redistribution or use of this work in any form, with or without modification,
 * requires written permission from the copyright holder.
 */

package de.halirutan.mathematica.codeInsight.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.intellij.util.SharedProcessingContext;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;


/**
 * @author patrick (4/2/13)
 */
public class MathematicaLocalizedSymbolCompletionContributor extends CompletionContributor {


  public MathematicaLocalizedSymbolCompletionContributor() {
    extend(CompletionType.BASIC, PlatformPatterns.psiElement().withParent(Symbol.class), new LocalVariableProvider());
  }

  private class LocalVariableProvider extends CompletionProvider {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
      final SharedProcessingContext sharedContext = context.getSharedContext();

    }
  }

}
