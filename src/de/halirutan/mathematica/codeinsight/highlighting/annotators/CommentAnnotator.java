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

package de.halirutan.mathematica.codeinsight.highlighting.annotators;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors;
import de.halirutan.mathematica.lang.psi.util.Comments;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides bold and bright highlighting for tags and sectioning comments.
 * @author patrick (03.03.15)
 */
public class CommentAnnotator implements Annotator {

  private static final Pattern ourTagPattern = Pattern.compile("[^:]:\\w+:[^:]");

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
    if (element instanceof PsiComment) {
      final Annotation commentAnnotation = holder.createInfoAnnotation(element, null);
      if (Comments.isCorrectSectionComment((PsiComment) element)) {
        commentAnnotation.setTextAttributes(MathematicaSyntaxHighlighterColors.INSTANCE.getCOMMENT_SPECIAL());
        return;
      }
      annotateCommentTags(element, holder);
    }
  }

  private void annotateCommentTags(@NotNull final PsiElement comment, @NotNull final AnnotationHolder holder) {
    final String text = comment.getText();
    final Matcher matcher = ourTagPattern.matcher(text);
    final int commentStart = comment.getTextOffset();
    while (matcher.find()) {
      int start = matcher.start();
      int end = matcher.end();
      final Annotation tagAnnotation = holder.createInfoAnnotation(TextRange.create(commentStart + start, commentStart + end), "");
      tagAnnotation.setTextAttributes(MathematicaSyntaxHighlighterColors.INSTANCE.getCOMMENT_SPECIAL());
    }
  }
}
