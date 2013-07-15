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

package de.halirutan.mathematica.codeInsight.editor;

import com.google.common.collect.Lists;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.impl.source.tree.TreeUtil;
import de.halirutan.mathematica.codeInsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.function.Function;
import de.halirutan.mathematica.parsing.psi.api.pattern.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author patrick (5/14/13)
 */
public class MathematicaHighlightingAnnotator implements Annotator {

    private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull final AnnotationHolder holder) {
        if (element instanceof Symbol) {
            PsiElement id = element.getFirstChild();

            if (NAMES.contains(id.getText())) {
                setHighlighting(element, holder, MathematicaSyntaxHighlighterColors.KEYWORDS);
            }
        } else if (element instanceof Pattern) {
            PsiElement fst = element.getFirstChild();
            if (fst != null && !(fst instanceof Pattern))
                setHighlighting(fst, holder, MathematicaSyntaxHighlighterColors.PATTERNS);
        } else if (element instanceof Blank || element instanceof BlankSequence || element instanceof BlankNullSequence) {
            setHighlighting(element, holder, MathematicaSyntaxHighlighterColors.PATTERNS);
        } else if (element instanceof Function) {
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(MathematicaSyntaxHighlighterColors.ANONYMOUS_FUNCTION));

            PsiElementVisitor patternVisitor = new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    if (element instanceof Symbol && MathematicaElementTypes.SLOTS.contains(element.getNode().getFirstChildNode().getElementType())) {
                        setHighlighting(element, holder, MathematicaSyntaxHighlighterColors.PATTERNS);
                    } else {
                        element.acceptChildren(this);
                    }
                }
            };

            patternVisitor.visitElement(element);
        }
    }

    private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
        holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
    }

}
