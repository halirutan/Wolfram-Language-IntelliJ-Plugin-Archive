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
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiComment;
import com.intellij.util.ProcessingContext;
import de.halirutan.mathematica.MathematicaLanguage;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiComment;

/**
 * @author patrick (4/2/13)
 */
public class CommentCompletionProvider extends MathematicaCompletionProvider {

  static final private String[] ourSections = {
      "Section","Subsection","Subsubsection", "Text", "Package", "Title", "Subtitle","Subsubtitle","Chapter","Subchapter","Subsubsubsection","Subsubsubsubsubsection",
  };

  static final private String[] ourTags = {
      "Name", "Title", "Author", "Summary", "Context",
          "Package Version", "Copyright", "Keywords", "Source",
          "Mathematica Version", "Limitation", "Discussion"};

  
  @Override
  void addTo(CompletionContributor contributor) {
    final Capture<PsiComment> psiCommentCapture = psiComment().withLanguage(MathematicaLanguage.INSTANCE);
    contributor.extend(CompletionType.SMART, psiCommentCapture, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final String prefix = findCurrentText(parameters, parameters.getPosition());
    if ( prefix.matches("") ) {
      CamelHumpMatcher matcher = new CamelHumpMatcher(prefix, false);
      final CompletionResultSet completionResultSet = result.withPrefixMatcher(matcher);
      int priority = 100;
      for (String section : ourSections) {
        priority--;

        final LookupElementBuilder elm = LookupElementBuilder.create(" ::" + section + ":: ")
            .withPresentableText("::" + section + "::");
        completionResultSet.addElement(PrioritizedLookupElement.withPriority(elm, priority));
      }
    } else {
      for (String tags : ourTags) {
        result.addElement(LookupElementBuilder.create(":" + tags + ": "));
      }
    }

  }
}
