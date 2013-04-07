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
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.MathematicaIcons;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author patrick (4/2/13)
 */
public class MathematicaCompletionContributor extends CompletionContributor {


    public MathematicaCompletionContributor() {
        extend(CompletionType.BASIC, psiElement().withElementType(MathematicaElementTypes.IDENTIFIER), new MathematicaFunctionCompletion());
    }



    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public String handleEmptyLookup(@NotNull CompletionParameters parameters, Editor editor) {
        return super.handleEmptyLookup(parameters, editor);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public AutoCompletionDecision handleAutoCompletionPossibility(AutoCompletionContext context) {
        return super.handleAutoCompletionPossibility(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void duringCompletion(@NotNull CompletionInitializationContext context) {
        super.duringCompletion(context);    //To change body of overridden methods use File | Settings | File Templates.
    }


}
