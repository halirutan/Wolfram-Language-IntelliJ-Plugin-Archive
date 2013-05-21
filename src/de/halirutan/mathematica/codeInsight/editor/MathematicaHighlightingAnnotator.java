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

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.codeInsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.pattern.Blank;
import de.halirutan.mathematica.parsing.psi.api.pattern.BlankNullSequence;
import de.halirutan.mathematica.parsing.psi.api.pattern.BlankSequence;
import de.halirutan.mathematica.parsing.psi.api.pattern.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author patrick (5/14/13)
 */
public class MathematicaHighlightingAnnotator implements Annotator {

  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
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
    }
  }

  private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
  }

}
