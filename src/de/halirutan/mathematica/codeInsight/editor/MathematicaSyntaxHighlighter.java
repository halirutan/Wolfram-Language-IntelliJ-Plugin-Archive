package de.halirutan.mathematica.codeInsight.editor;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.options.colors.pages.ANSIColoredConsoleColorsPage;
import groovy.util.GroovyCollections;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13 *
 * Purpose:
 * Time: 6:18 AM
 */
public interface MathematicaSyntaxHighlighter {

    TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("MMA.COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("MMA.STRING_LITERAL", DefaultLanguageHighlighterColors.STATIC_FIELD);
    TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey("MMA.OPERATORS",  DefaultLanguageHighlighterColors.FUNCTION_CALL);
    TextAttributesKey LITERALS = TextAttributesKey.createTextAttributesKey("MMA.LITERALS", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("MMA.IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
//    TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("MMA.BRACES", new TextAttributes(DefaultLanguageHighlighterColors.BRACKETS.getDefaultAttributes().getForegroundColor(),null,null,null,Font.BOLD));
    TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("MMA.BRACES", new TextAttributes(
        DefaultLanguageHighlighterColors.NUMBER.getDefaultAttributes().getForegroundColor(),
        DefaultLanguageHighlighterColors.NUMBER.getDefaultAttributes().getBackgroundColor(),
        null, null, Font.BOLD));

    TextAttributesKey BAD_CHARACTER = HighlighterColors.BAD_CHARACTER;

}
