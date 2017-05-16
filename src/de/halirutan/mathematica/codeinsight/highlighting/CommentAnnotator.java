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

package de.halirutan.mathematica.codeinsight.highlighting;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.codeinsight.completion.CommentCompletionProvider;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author patrick (03.03.15)
 */
public class CommentAnnotator implements Annotator {

  private static Pattern ourTagPattern;

  static {
    List<String> commentTags = new ArrayList<>(CommentCompletionProvider.COMMENT_TAGS.length);
    for (String tag : CommentCompletionProvider.COMMENT_TAGS) {
      commentTags.add(":" + tag + ":");
    }
    ourTagPattern = Pattern.compile(StringUtils.join(commentTags, "|"));
  }


  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
    if (element instanceof PsiComment) {
      final Annotation commentAnnotation = holder.createInfoAnnotation(element, null);
      if (isCorrectSectionComment(element)) {
        commentAnnotation.setTextAttributes(MathematicaSyntaxHighlighterColors.COMMENT_SPECIAL);
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
      tagAnnotation.setTextAttributes(MathematicaSyntaxHighlighterColors.COMMENT_SPECIAL);
    }
  }

  /**
   * Tests if a comment is a valid Section, Subsection, ... comment. AFAIK these comments can only contain the section
   * specifier and nothing else (whitespace should be OK). Therefore, I match comments that look like this
   * <p>
   * <code>(* ::Section:: *)</code>
   * <p>
   * Please see {@link CommentCompletionProvider#COMMENT_SECTIONS}.
   *
   * @param comment the comment PsiElement
   * @return true if the comment is a valid title, section, ... comment
   */
  private boolean isCorrectSectionComment(@NotNull final PsiElement comment) {
    final String text = comment.getText();
    final String name = text.replace("(*", "").replace("*)", "").trim();
    if (name.length() > 0 && name.matches("::.*::")) {
      final String sectionName = name.replace(":", "");
      for (String commentSection : CommentCompletionProvider.COMMENT_SECTIONS) {
        if (commentSection.equals(sectionName)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * This is not as strict as {@link CommentAnnotator#isCorrectSectionComment(PsiElement)} and it does only check
   * if there is a section tag (::Section::, ::Subsection::, ...) inside the comment.
   *
   * @param comment Comment to check
   * @return true if a section tag could be found inside the comment
   */
  private boolean containsSectionTag(@NotNull final PsiElement comment) {
    final String text = comment.getText();
    for (String commentSection : CommentCompletionProvider.COMMENT_SECTIONS) {
      if (text.contains("::" + commentSection + "::")) {
        return true;
      }
    }
    return false;
  }

}
