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

import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * User: rsmenon (5/17/13)
 */

public class MathematicaSyntaxHighlighterColors {

//    //Define colors from built-ins based on color-scheme
//    static final boolean isDark = UIManager.getLookAndFeel().getName().contains("Darcula");
//
//    private static Color getFG(TextAttributesKey id) {
//        return id.getDefaultAttributes().getForegroundColor();
//    }
//
//    private static Color getBG(TextAttributesKey id) {
//        return id.getDefaultAttributes().getBackgroundColor();
//    }

//    static final Color BLUE = getFG(DefaultLanguageHighlighterColors.NUMBER);
//    static final Color GRAY = getFG(DefaultLanguageHighlighterColors.LINE_COMMENT);
//    static final Color GREEN = isDark ?
//            getFG(DefaultLanguageHighlighterColors.DOC_COMMENT) :
//            getFG(DefaultLanguageHighlighterColors.STRING);
//    static final Color PINK = getFG(DefaultLanguageHighlighterColors.STATIC_FIELD);
//    static final Color FOREGROUND = getFG(DefaultLanguageHighlighterColors.IDENTIFIER);
//    static final Color BACKGROUND = getBG(DefaultLanguageHighlighterColors.IDENTIFIER);



    public final static TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("MMA.COMMENT");
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
    public final static TextAttributesKey BAD_CHARACTER = HighlighterColors.BAD_CHARACTER;

}
