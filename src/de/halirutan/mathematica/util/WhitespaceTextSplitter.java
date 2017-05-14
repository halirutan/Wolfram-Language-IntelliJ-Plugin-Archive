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
package de.halirutan.mathematica.util;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.spellchecker.inspections.BaseSplitter;
import com.intellij.spellchecker.inspections.TextSplitter;
import com.intellij.util.Consumer;
import org.jdom.Verifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhitespaceTextSplitter extends BaseSplitter {
  private static final WhitespaceTextSplitter INSTANCE = new WhitespaceTextSplitter();

  public static WhitespaceTextSplitter getInstance() {
    return INSTANCE;
  }

  @NonNls
  private static final
  Pattern SPLIT_PATTERN = Pattern.compile("\\s");


  @Override
  public void split(@Nullable String text, @NotNull TextRange range, Consumer<TextRange> consumer) {
    if (text == null || StringUtil.isEmpty(text)) {
      return;
    }

    final String substring = StringUtil.replaceChar(range.substring(text), '\f', '\n');
    if (Verifier.checkCharacterData(SPLIT_PATTERN.matcher(substring).replaceAll("")) != null) {
      return;
    }

    final TextSplitter ws = TextSplitter.getInstance();
    int from = range.getStartOffset();
    int till;
    Matcher matcher = SPLIT_PATTERN.matcher(range.substring(text));
    while (true) {
      checkCancelled();
      List<TextRange> toCheck;
      TextRange wRange;
      if (matcher.find()) {
        TextRange found = matcherRange(range, matcher);
        till = found.getStartOffset();
        if (badSize(from, till)) {
          continue;
        }
        wRange = new TextRange(from, till);
        from = found.getEndOffset();
      } else { // end hit or zero matches
        wRange = new TextRange(from, range.getEndOffset());
      }
      toCheck = Collections.singletonList(wRange);
      for (TextRange r : toCheck) {
        ws.split(text, r, consumer);
      }
      if (matcher.hitEnd()) break;
    }
  }
}
