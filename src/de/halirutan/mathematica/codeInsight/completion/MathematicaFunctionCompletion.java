/*
 * Mathematica Plugin for Jetbrains IDEA
 * Copyright (C) 2013 Patrick Scheibe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.halirutan.mathematica.codeInsight.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.StandardPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author patrick (4/7/13)
 */
public class MathematicaFunctionCompletion extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        HashMap<String,SymbolInformationProvider.SymbolInformation> symbols = SymbolInformationProvider.getSymbolNames();

        if (Character.isLowerCase(parameters.getPosition().getText().charAt(0))) {
            result.stopHere();
            return;
        }

        // We want to find a prefix which can contain $ since this is allowed in Mathematica
        String prefix = CompletionUtil.findIdentifierPrefix(parameters.getPosition().getContainingFile(),
                parameters.getOffset(),
                StandardPatterns.character().andOr(StandardPatterns.character().letterOrDigit(), StandardPatterns.character().equalTo('$')),
                StandardPatterns.character().andOr(StandardPatterns.character().letterOrDigit(), StandardPatterns.character().equalTo('$')));

        CamelHumpMatcher matcher = new CamelHumpMatcher(prefix, false);

        CompletionResultSet result2 = result.withPrefixMatcher(matcher);
        for (String name : symbols.keySet()) {
            if (name.length() < 3) continue;
            LookupElementBuilder elm = LookupElementBuilder.create(name).withInsertHandler(MathematicaBracketInsertHandler.getInstance());
            SymbolInformationProvider.SymbolInformation symbol = symbols.get(name);
            result2.addElement(PrioritizedLookupElement.withPriority(elm, symbol.importance));
        }
    }

}
