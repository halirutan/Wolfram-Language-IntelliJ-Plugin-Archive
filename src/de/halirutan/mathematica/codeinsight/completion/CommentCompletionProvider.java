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

package de.halirutan.mathematica.codeinsight.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.intellij.util.text.StringTokenizer;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static com.intellij.patterns.PlatformPatterns.psiComment;

/**
 * This smart completion provider helps the developer to expand commonly used words and tags into a comment.
 *
 * @author patrick (4/2/13)
 */
public class CommentCompletionProvider extends MathematicaCompletionProvider {

  static final public String[] COMMENT_SECTIONS = {
      "Section", "Subsection", "Subsubsection", "Text", "Package", "Title", "Subtitle", "Subsubtitle", "Chapter", "Subchapter", "Subsubsubsection", "Subsubsubsubsubsection",
  };

  static final public String[] COMMENT_TAGS = {
      "Name", "Title", "Author", "Date", "Summary", "Context",
      "Package Version", "Copyright", "Keywords", "Source",
      "Mathematica Version", "Limitation", "Discussion"};
  static private final Pattern EMPTY_COMMENT = Pattern.compile("\\(\\*\\s\\*\\)");

  @Override
  void addTo(CompletionContributor contributor) {
    final Capture<PsiComment> psiCommentCapture = psiComment().withLanguage(MathematicaLanguage.INSTANCE);
    contributor.extend(CompletionType.BASIC, psiCommentCapture, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    if (parameters.getInvocationCount() > 0) {
      final String prefix = findCommentPrefix(parameters);

      if (isEmptyCommennt(parameters)) {
        int priority = 100;
        for (String section : COMMENT_SECTIONS) {
          priority--;

          final LookupElementBuilder elm = LookupElementBuilder.create(" ::" + section + ":: ")
              .withPresentableText("::" + section + "::");
          result.addElement(PrioritizedLookupElement.withPriority(elm, priority));
        }
      }

      final CompletionResultSet resultWithPrefix = result.withPrefixMatcher(prefix);

      for (String tag : COMMENT_TAGS) {
        resultWithPrefix.addElement(LookupElementBuilder.create(":" + tag + ":"));
      }

      final PsiFile file = parameters.getOriginalFile();
      GlobalDefinitionCompletionProvider provider = new GlobalDefinitionCompletionProvider();
      file.accept(provider);
      for (String functionName : provider.getFunctionsNames()) {
        resultWithPrefix.addElement(LookupElementBuilder.create(functionName));
      }
    }
  }

  private boolean isEmptyCommennt(CompletionParameters parameters) {
    final int posOffset = parameters.getOffset();
    final PsiElement commentElement = parameters.getPosition();
    if (commentElement instanceof PsiComment) {
      final int elementStart = commentElement.getTextOffset();
      final String commentText = commentElement.getText();
      return commentText.matches("\\(\\*\\s*ZZZ\\s*\\*\\)") || EMPTY_COMMENT.matcher(commentText).matches();
    }
    return false;
  }

  private String findCommentPrefix(CompletionParameters parameters) {
    final int posOffset = parameters.getOffset();
    final PsiElement commentElement = parameters.getPosition();

    if (commentElement instanceof PsiComment) {
      final int elementStart = commentElement.getTextOffset();
      final String commentText = commentElement.getText().substring(0, posOffset - elementStart);
      if (commentText.length() == 0 || commentText.matches(".*[ \t\n\f]")) {
        return "";
      }

      StringTokenizer tokenizer = new StringTokenizer(commentText);
      String prefix = "";
      while (tokenizer.hasMoreElements()) {
        prefix = tokenizer.nextToken();
      }
      return prefix;
    }
    return "";
  }

}
