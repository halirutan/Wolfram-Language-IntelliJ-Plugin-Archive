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

package de.halirutan.mathematica.lang.psi.util;

import com.intellij.psi.PsiComment;
import de.halirutan.mathematica.codeinsight.completion.providers.CommentCompletion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class that provides methods for section-comments.
 * {@see de.halirutan.mathematica.codeinsight.highlighting.CommentAnnotator}
 * {@see de.halirutan.mathematica.codeinsight.folding.MathematicaExpressionFoldingBuilder}
 *
 * @author patrick (05.06.17).
 */
public class Comments {

  /**
   * Tests if a comment is a valid Section, Subsection, ... comment. AFAIK these comments can only contain the section
   * specifier and nothing else (even additional whitespace is NOT OK). Therefore, I match comments that look like this
   * <p>
   * <code>(* ::Section:: *)</code>
   * <p>
   * Please see {@link CommentCompletion#COMMENT_SECTIONS}.
   *
   * @param comment the comment PsiElement
   * @return true if the comment is a valid title, section, ... comment
   */
  public static boolean isCorrectSectionComment(@NotNull final PsiComment comment) {

    final String name = getStrippedText(comment);

    // There must be exactly one space around the section specifier
    if (name.length() != comment.getText().length() - 6) {
      return false;
    }

    if (name.length() > 0 && name.matches("::\\w+::(\\w+::)*")) {
      final String[] args = name.split("::");
      if (args.length > 1) {
        for (CommentStyle commentStyle : CommentStyle.values()) {
          if (commentStyle.myStyleText.equals(args[1])) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Extracts the section from comments of the for (* ::Subsection:: *)
   *
   * @param comment element that contains the section
   * @return the section or null if it could not be extracted
   */
  @Nullable
  public static CommentStyle getStyle(@NotNull final PsiComment comment) {
    if (!isCorrectSectionComment(comment)) {
      return null;
    }
    // note: we can do this without checking because we already know it is valid
    String styleString = getStrippedText(comment).split("::")[1];
    for (CommentStyle cs : CommentStyle.values()) {
      if (cs.myStyleText.equals(styleString)) {
        return cs;
      }
    }
    return null;
  }

  public static String getStrippedText(@NotNull final PsiComment comment) {
    String text = comment.getText();
    assert text.length() >= 4; // we need at least this (**), should never happen!
    return text.substring(2, text.length() - 2).trim();
  }

  public enum CommentStyle implements Comparable<CommentStyle> {
    TITLE("Title"),
    SUBTITLE("Subtitle"),
    SUBSUBTITLE("Subsubtitle"),
    CHAPTER("Chapter"),
    SUBCHAPTER("Subchapter"),
    SECTION("Section"),
    SUBSECTION("Subsection"),
    SUBSUBSECTION("Subsubsection"),
    SUBSUBSUBSECTION("Subsubsubsection"),
    TEXT("Text"),
    ITEM("Item"),
    SUBITEM("Subitem"),
    SUBSUBITEM("Subsubitem"),
    ITEMPARAGRAPH("ItemParagraph"),
    SUBITEMPARAGRAPH("SubitemParagraph"),
    SUBSUBITEMPARAGRAPH("SubsubitemParagraph"),
    ITEMNUMBERED("ItemNumbered"),
    SUBITEMNUMBERED("SubitemNumbered"),
    SUBSUBITEMNUMBERED("SubsubitemNumbered");

    private final String myStyleText;

    CommentStyle(final String text) {
      this.myStyleText = text;
    }


    @Override
    public String toString() {
      return myStyleText;
    }
  }

  @SuppressWarnings("unused")
  public enum StyleModifier {
    CLOSED("Closed"),
    BOLD("Bold"),
    ITALIC("Italic");

    private final String myModifier;

    StyleModifier(String modifier) {
      this.myModifier = modifier;
    }
  }

}
