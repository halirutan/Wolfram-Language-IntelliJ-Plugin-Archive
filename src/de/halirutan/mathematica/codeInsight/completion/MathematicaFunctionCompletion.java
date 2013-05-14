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
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.StandardPatterns;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.parsing.psi.impl.SymbolImpl;
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
