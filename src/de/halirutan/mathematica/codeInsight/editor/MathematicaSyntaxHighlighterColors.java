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

package de.halirutan.mathematica.codeInsight.editor;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13 *
 * Purpose:
 * Time: 6:18 AM
 */
public interface MathematicaSyntaxHighlighterColors {


    TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("MMA.COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("MMA.STRING_LITERAL", DefaultLanguageHighlighterColors.STATIC_FIELD);
    TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey("MMA.OPERATORS",  DefaultLanguageHighlighterColors.FUNCTION_CALL);
    TextAttributesKey LITERALS = TextAttributesKey.createTextAttributesKey("MMA.LITERALS", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("MMA.IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    TextAttributesKey KEYWORDS = TextAttributesKey.createTextAttributesKey("MMA.KEYWORDS", new TextAttributes(
            DefaultLanguageHighlighterColors.IDENTIFIER.getDefaultAttributes().getForegroundColor(),
            DefaultLanguageHighlighterColors.IDENTIFIER.getDefaultAttributes().getBackgroundColor(),
            null, null, Font.BOLD
    ));
    TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("MMA.BRACES", new TextAttributes(
            DefaultLanguageHighlighterColors.NUMBER.getDefaultAttributes().getForegroundColor(),
            DefaultLanguageHighlighterColors.NUMBER.getDefaultAttributes().getBackgroundColor(),
            null, null, Font.BOLD));

    TextAttributesKey PATTERNS = TextAttributesKey.createTextAttributesKey("MMA.PATTERNS", DefaultLanguageHighlighterColors.DOC_COMMENT);

    TextAttributesKey BAD_CHARACTER = HighlighterColors.BAD_CHARACTER;

//    TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("MMA.COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
//    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("MMA.STRING_LITERAL", DefaultLanguageHighlighterColors.STRING);
//    TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey("MMA.OPERATORS",  DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
//    TextAttributesKey LITERALS = TextAttributesKey.createTextAttributesKey("MMA.LITERALS", DefaultLanguageHighlighterColors.NUMBER);
//    TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("MMA.IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
//    TextAttributesKey KEYWORDS = TextAttributesKey.createTextAttributesKey("MMA.KEYWORDS", DefaultLanguageHighlighterColors.FUNCTION_CALL);
//    TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("MMA.BRACES", new TextAttributes(
//            DefaultLanguageHighlighterColors.BRACKETS.getDefaultAttributes().getForegroundColor(),
//            DefaultLanguageHighlighterColors.BRACKETS.getDefaultAttributes().getBackgroundColor(),
//            null, null, Font.BOLD));
//
//    TextAttributesKey BAD_CHARACTER = HighlighterColors.BAD_CHARACTER;
//
//

}
