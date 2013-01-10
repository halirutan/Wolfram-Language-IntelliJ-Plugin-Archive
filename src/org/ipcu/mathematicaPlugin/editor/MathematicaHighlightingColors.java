package org.ipcu.mathematicaPlugin.editor;

import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.colors.impl.BundledColorSchemesProvider;
import com.intellij.openapi.editor.colors.impl.DefaultColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13 *
 * Purpose:
 * Time: 6:18 AM
 */
public interface MathematicaHighlightingColors {
    Color base03 = new Color(0x002b36);
    Color base02 = new Color(0x073642);
    Color base01 = new Color(0x586e75);
    Color base00 = new Color(0x657b83);
    Color base0 = new Color(0x839496);
    Color base1 = new Color(0x93a1a1);
    Color base2 = new Color(0xeee8d5);
    Color base3 = new Color(0xfdf6e3);
    Color yellow = new Color(0xb58900);
    Color orange = new Color(0xcb4b16);
    Color red = new Color(0xdc322f);
    Color magenta = new Color(0xd33682);
    Color violet = new Color(0x6c71c4);
    Color blue = new Color(0x268bd2);
    Color cyan = new Color(0x2aa198);
    Color green = new Color(859900);


    TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            "MMA.COMMENT", SyntaxHighlighterColors.DOC_COMMENT
    );

    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(
            "MMA.STRING_LITERAL", SyntaxHighlighterColors.NUMBER
    );

    TextAttributesKey OPERATORS = TextAttributesKey.createTextAttributesKey(
            "MMA.OPERATORS",  SyntaxHighlighterColors.KEYWORD
    );


    TextAttributesKey LITERALS = TextAttributesKey.createTextAttributesKey(
            "MMA.LITERALS", SyntaxHighlighterColors.KEYWORD
    );


    TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey(
            "MMA.IDENTIFIER", SyntaxHighlighterColors.BRACKETS
    );

    TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(
            "MMA.BRACES", new TextAttributes(SyntaxHighlighterColors.BRACKETS.getDefaultAttributes().getForegroundColor(),null,null,null,Font.BOLD)
    );



    TextAttributesKey BAD_CHARACTER = HighlighterColors.BAD_CHARACTER;

}
