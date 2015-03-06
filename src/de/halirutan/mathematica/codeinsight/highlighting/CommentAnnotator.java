/*
 * Copyright (c) 2015 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.highlighting;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;


/**
 * @author patrick (03.03.15)
 */
public class CommentAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull final PsiElement element, final AnnotationHolder holder) {
    if (element instanceof PsiComment &&
        (((PsiComment) element).getTokenType() == COMMENT_ANNOTATION || ((PsiComment) element).getTokenType() == COMMENT_SECTION)) {
      final Annotation commentSpecial = holder.createInfoAnnotation(element, null);
      commentSpecial.setTextAttributes(MathematicaSyntaxHighlighterColors.COMMENT_SPECIAL);
    }

  }

  private List<PsiComment> findComment(final PsiComment start, final AnnotationHolder holder) {
    PsiElement opening = start;
    while (opening instanceof PsiComment) {
      if (((PsiComment) opening).getTokenType() == COMMENT_START) {
        final PsiElement prevSibling = opening.getPrevSibling();
        // Check whether we have a nested comment
        if (prevSibling instanceof PsiComment && ((PsiComment) prevSibling).getTokenType() != COMMENT_END) {
          opening = prevSibling;
          continue;
        }
        break;
      }
      opening = opening.getPrevSibling();
    }

    if (!(opening instanceof PsiComment) || ((PsiComment) opening).getTokenType() != COMMENT_START) {
      holder.createErrorAnnotation(opening, "Expecting (*");
      return Collections.emptyList();
    }

    List<PsiComment> result = new ArrayList<PsiComment>();
    result.add((PsiComment) opening);
    int nested = 0;
    PsiElement end = opening.getNextSibling();
    while (end instanceof PsiComment) {
      result.add((PsiComment) end);
      if (((PsiComment) end).getTokenType() == COMMENT_START) {
        nested++;
      }
      if (((PsiComment) end).getTokenType() == COMMENT_END) {
        if (nested == 0) {
          break;
        }
        nested--;
      }
      end = end.getNextSibling();
    }
    if (!(end instanceof PsiComment)) {
      TextRange eof = TextRange.from(start.getContainingFile().getTextLength()-2, 1);
      holder.createErrorAnnotation(eof, "Expecting *)");
      return Collections.emptyList();
    }
    return result;
  }

  private void annotateSection(final List<PsiComment> comments, final AnnotationHolder holder) {
    final PsiComment last = comments.get(comments.size() - 1);
    TextRange all = new TextRange(
        comments.get(0).getTextOffset(),
        last.getTextOffset() + last.getTextLength()
    );

    holder.createInfoAnnotation(all,null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);

    // A well-formed section annotation like (* ::Section:: *)
    if (comments.size() == 5 &&
        comments.get(0).getTokenType() == COMMENT_START &&
        comments.get(1).getText().contentEquals(" ") &&
        comments.get(2).getTokenType() == COMMENT_SECTION &&
        comments.get(3).getText().contentEquals(" ") &&
        comments.get(4).getTokenType() == COMMENT_END) {
      final PsiComment end = comments.get(4);
      final Annotation annotation = holder.createInfoAnnotation(
          new TextRange(comments.get(0).getTextOffset(), end.getTextOffset() + end.getTextLength()),
          null
      );
      annotation.setTextAttributes(MathematicaSyntaxHighlighterColors.COMMENT_SPECIAL);
      return;
    }

    for (PsiComment commentPart : comments) {
      if (commentPart.getTokenType() == COMMENT_SECTION) {
        final Annotation warningAnnotation = holder.createWarningAnnotation(commentPart, "Comment sections must have the form (* ::Section:: *) with exactly one space around the keyword.");
        warningAnnotation.setTooltip("Test");

        return;
      }
    }

  }

}
