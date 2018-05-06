/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.codeinsight.completion.providers;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import de.halirutan.mathematica.lang.resolve.MathematicaGlobalResolveCache;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import static com.intellij.patterns.PlatformPatterns.psiComment;

/**
 * This smart completion provider helps the developer to expand commonly used words and tags into a comment.
 *
 * @author patrick (4/2/13)
 */
public class CommentCompletion extends MathematicaCompletionProvider {

  private static final String[] COMMENT_SECTIONS = {
      "Section", "Subsection", "Subsubsection", "Text", "Package", "Title", "Subtitle", "Subsubtitle", "Chapter", "Subchapter", "Subsubsubsection", "Subsubsubsubsubsection",
  };

  private static final String[] COMMENT_TAGS = {
      "Name", "Title", "Author", "Date", "Summary", "Context",
      "Package Version", "Copyright", "Keywords", "Source",
      "Mathematica Version", "Limitation", "Discussion"};
  static private final Pattern EMPTY_COMMENT = Pattern.compile("\\(\\*\\s\\*\\)");

  @Override
  public void addTo(CompletionContributor contributor) {
    final Capture<PsiComment> psiCommentCapture = psiComment().withLanguage(MathematicaLanguage.INSTANCE);
    contributor.extend(CompletionType.BASIC, psiCommentCapture, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    if (parameters.getInvocationCount() > 0) {
      if (isEmptyComment(parameters)) {
        int priority = 100;
        for (String section : COMMENT_SECTIONS) {
          priority--;

          final LookupElementBuilder elm = LookupElementBuilder.create(" ::" + section + ":: ")
                                                               .withPresentableText("::" + section + "::");
          result.withPrefixMatcher(new PlainPrefixMatcher(""))
                .addElement(PrioritizedLookupElement.withPriority(elm, priority));
        }

        for (String tag : COMMENT_TAGS) {
          result.withPrefixMatcher(new PlainPrefixMatcher(""))
                .addElement(LookupElementBuilder.create(" :" + tag + ": "));
        }
      } else {
        final PsiFile file = parameters.getOriginalFile();
        final MathematicaGlobalResolveCache symbolCache = MathematicaGlobalResolveCache.getInstance(file.getProject());
        final List<String> cachedDefinitions = symbolCache.getCachedFileSymbolNames(file);
        for (String definition : cachedDefinitions) {
          result.addElement(LookupElementBuilder.create(definition));
        }
      }
    }
  }

  private boolean isEmptyComment(CompletionParameters parameters) {
    final PsiElement commentElement = parameters.getPosition();
    if (commentElement instanceof PsiComment) {
      final String commentText = commentElement.getText();
      return commentText.matches("\\(\\*ZZZ\\*\\)") || EMPTY_COMMENT.matcher(commentText).matches();
    }
    return false;
  }

}
