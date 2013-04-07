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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.MathematicaIcons;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.intellij.patterns.StandardPatterns.character;

/**
 * @author patrick (4/7/13)
 */
public class MathematicaFunctionCompletion extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet _result) {
        final HashMap<String,SymbolInformationProvider.SymbolInformation> symbols = SymbolInformationProvider.getSymbolNames();

        if (Character.isLowerCase(parameters.getPosition().getText().charAt(0))) {
            _result.stopHere();
            return;
        }

        // We want to find a prefix which can contain $ since this is allowed in Mathematica
        final String prefix = CompletionUtil.findIdentifierPrefix(parameters.getPosition().getContainingFile(),
                parameters.getOffset(),
                character().andOr(character().letterOrDigit(),character().equalTo('$')),
                character().andOr(character().letterOrDigit(), character().equalTo('$')));
        CamelHumpMatcher matcher = new CamelHumpMatcher(prefix, false);
        CompletionResultSet result = _result.withPrefixMatcher(matcher);
        for (String name : symbols.keySet()) {
            if (name.length() < 3) continue;
            LookupElementBuilder elm = LookupElementBuilder.create(name).withInsertHandler(MathematicaBracketInsertHandler.getInstance());
            final SymbolInformationProvider.SymbolInformation symbol = symbols.get(name);
            result.addElement(PrioritizedLookupElement.withPriority(elm, symbol.importance));
        }
    }

//    public static String findMathematicaIdentifierPrefix(CompletionParameters parameters) {
//        final PsiFile file = parameters.getPosition().getContainingFile();
//        if (file == null) return "";
//        final String text = file.getText();
//        ElementPattern<Character>
//
//
//        //, parameters.getOffset(), character().letterOrDigit(), character().letterOrDigit());
//    }
//
//    public static String findIdentifierPrefix(PsiElement insertedElement, int offset, ElementPattern<Character> idPart,
//                                              ElementPattern<Character> idStart) {
//        if(insertedElement == null) return "";
//        final String text = insertedElement.getText();
//
//        final int offsetInElement = offset - insertedElement.getTextRange().getStartOffset();
//        int start = offsetInElement - 1;
//        while (start >=0 ) {
//            if (!idPart.accepts(text.charAt(start))) break;
//            --start;
//        }
//        while (start + 1 < offsetInElement && !idStart.accepts(text.charAt(start + 1))) {
//            start++;
//        }
//
//        return text.substring(start + 1, offsetInElement).trim();
//    }

}
