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

package de.halirutan.mathematica.codeInsight.completion;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.tree.IElementType;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * Provides the intelligent insertion of quotes. This, like the {@link de.halirutan.mathematica.codeInsight.highlighting.MathematicaBraceMatcher}
 * works on the lexer token too. I should note, a string like <code >"blub"</code> is returned by the lexer as the
 * sequence <code >STRING_LITERAL_BEGIN, STRING_LITERAL, STRING_LITERAL_END</code>.
 *
 * @author patrick (4/8/13)
 */
public class MathematicaQuoteHandler extends SimpleTokenSetQuoteHandler {
  public MathematicaQuoteHandler() {
    super(STRING_LITERAL, STRING_LITERAL_BEGIN, STRING_LITERAL_END);
  }

  /**
   * Should return true, when you are inside a string literal.
   *
   * @param iterator the iterator to move through the token stream. Here, only used to get the token type
   * @return true, if the iterator is currently inside a string
   */
  @Override
  public boolean isInsideLiteral(HighlighterIterator iterator) {
    return iterator.getTokenType().equals(STRING_LITERAL);
  }

  /**
   * Should return true if the current offset is the closing quote of the string. Unfortunately, I'm not quite sure
   * anymore why I had to make the calculations but I remember that something with the removal of an empty string did
   * not work.
   *
   * @param iterator the iterator to move through the token stream. Here, only used to get the current token type
   * @param offset   current character offset
   * @return true, if the current offset is a closing quote
   */
  @Override
  public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
    final IElementType tokenType = iterator.getTokenType();

    if (tokenType.equals(STRING_LITERAL_END)) {
      int start = iterator.getStart();
      int end = iterator.getEnd();
      return end - start >= 1 && offset == end - 1;
    }
    return false;
  }

  /**
   * Finds out whether the current offset is an opening quote element.
   *
   * @param iterator the iterator to move through the token stream. Here, only used to get the current token type
   * @param offset   current character offset
   * @return true, if the current offset is an opening quote
   */
  @Override
  public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
    if (iterator.getTokenType().equals(STRING_LITERAL_BEGIN)) {
      int start = iterator.getStart();
      return offset == start;
    }

    return false;
  }

}
