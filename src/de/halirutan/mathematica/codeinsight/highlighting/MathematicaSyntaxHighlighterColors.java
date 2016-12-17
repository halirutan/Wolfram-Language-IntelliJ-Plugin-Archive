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

package de.halirutan.mathematica.codeinsight.highlighting;

import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * User: rsmenon (5/17/13)
 */

public class MathematicaSyntaxHighlighterColors {

  public final static TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("MMA.COMMENT");
  public final static TextAttributesKey COMMENT_SPECIAL = TextAttributesKey.createTextAttributesKey("MMA.COMMENT_SPECIAL");
  public final static TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("MMA.STRING");
  public final static TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey("MMA.OPERATORS");
  public final static TextAttributesKey LITERAL = TextAttributesKey.createTextAttributesKey("MMA.LITERAL");
  public final static TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("MMA.IDENTIFIER");
  public final static TextAttributesKey BUILTIN_FUNCTION = TextAttributesKey.createTextAttributesKey("MMA.BUILTIN_FUNCTION");
  public final static TextAttributesKey BRACE = TextAttributesKey.createTextAttributesKey("MMA.BRACE");
  public final static TextAttributesKey PATTERN = TextAttributesKey.createTextAttributesKey("MMA.PATTERN");
  public final static TextAttributesKey SLOT = TextAttributesKey.createTextAttributesKey("MMA.SLOT");
  public final static TextAttributesKey ANONYMOUS_FUNCTION = TextAttributesKey.createTextAttributesKey("MMA.ANONYMOUS_FUNCTION");
  public final static TextAttributesKey MESSAGE = TextAttributesKey.createTextAttributesKey("MMA.MESSAGE");
  public final static TextAttributesKey USAGE_MESSAGE = TextAttributesKey.createTextAttributesKey("MMA.USAGE_MESSAGE");
  public final static TextAttributesKey MODULE_LOCALIZED = TextAttributesKey.createTextAttributesKey("MMA.MODULE_LOCALIZED");
  public final static TextAttributesKey BLOCK_LOCALIZED = TextAttributesKey.createTextAttributesKey("MMA.BLOCK_LOCALIZED");
  public final static TextAttributesKey BAD_CHARACTER = HighlighterColors.BAD_CHARACTER;

}
