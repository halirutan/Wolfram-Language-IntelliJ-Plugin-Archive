/*
 * Copyright (c) 2017 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.livetemplates;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import de.halirutan.mathematica.Mathematica;
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighter;
import de.halirutan.mathematica.file.MathematicaFileType;
import de.halirutan.mathematica.lang.psi.api.string.MString;
import org.jetbrains.annotations.NotNull;

/**
 * This only checks whether we are in a Mathematica file and there, not in a comment. Additional checks seem to hinder
 * more because in Mathematica everything is an expression and you probably want to expand a template in all kinds of
 * different positions.
 *
 * @author rsmenon 7/7/14.
 */
public class MathematicaTemplateContextType extends TemplateContextType {
  protected MathematicaTemplateContextType() {
    super(Mathematica.NAME, "Mathematica");
  }

  public boolean isInContext(@NotNull PsiFile file, int offset) {
    if (PsiUtilBase.getLanguageAtOffset(file, offset).isKindOf(MathematicaFileType.INSTANCE.getLanguage())) {
      PsiElement element = PsiUtilBase.getElementAtOffset(file, offset);
      return PsiTreeUtil.getParentOfType(element, PsiComment.class, MString.class) == null;
    }
    return false;
  }

  @Override
  public SyntaxHighlighter createHighlighter() {
    return new MathematicaSyntaxHighlighter();
  }

}
