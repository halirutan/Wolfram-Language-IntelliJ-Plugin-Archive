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

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.io.StreamUtil;
import de.halirutan.mathematica.MathematicaIcons;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors.*;

/**
 * @author patrick (4/7/13)
 */
public class MathematicaColorSettingsPage implements ColorSettingsPage {

  private static final AttributesDescriptor[] ATTR;

  static {
    ATTR = new AttributesDescriptor[]{
        new AttributesDescriptor("Identifier", IDENTIFIER),
        new AttributesDescriptor("Built-In Function", BUILTIN_FUNCTION),
        new AttributesDescriptor("Number", LITERAL),
        new AttributesDescriptor("Operator Sign", OPERATORS),
        new AttributesDescriptor("Parenthesis", BRACE),
        new AttributesDescriptor("String", STRING),
        new AttributesDescriptor("Comment", COMMENT),
        new AttributesDescriptor("Comment Special", COMMENT_SPECIAL),
        new AttributesDescriptor("Module Variables", MODULE_LOCALIZED),
        new AttributesDescriptor("Block Variables", BLOCK_LOCALIZED),
        new AttributesDescriptor("Patterns and Arguments", PATTERN),
        new AttributesDescriptor("Anonymous functions", ANONYMOUS_FUNCTION),
        new AttributesDescriptor("Slots", SLOT),
        new AttributesDescriptor("Usage message", USAGE_MESSAGE),
        new AttributesDescriptor("Other message", MESSAGE),
    };
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return MathematicaIcons.FILE_ICON;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return new MathematicaSyntaxHighlighter();
  }

  @NotNull
  @Override
  public String getDemoText() {
    InputStream resource = getClass().getClassLoader().getResourceAsStream("/colors/demoText.txt");
    String demoText;
    try {
      demoText = StreamUtil.readText(resource, "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException("Mathematica Plugin could not load the syntax highlighter demo text.", e);
    }

    return demoText;
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    final THashMap<String, TextAttributesKey> map = new THashMap<String, TextAttributesKey>();
    map.put("i", IDENTIFIER);
    map.put("k", BUILTIN_FUNCTION);
    map.put("pat", PATTERN);
    map.put("usg", USAGE_MESSAGE);
    map.put("msg", MESSAGE);
    map.put("mod", MODULE_LOCALIZED);
    map.put("blk", BLOCK_LOCALIZED);
    map.put("fn", ANONYMOUS_FUNCTION);
    map.put("slot", SLOT);
    map.put("s", STRING);
    map.put("c", COMMENT);
    map.put("cs", COMMENT_SPECIAL);
    map.put("o", OPERATORS);
    map.put("b", BRACE);
    map.put("l", LITERAL);
    return map;
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return ATTR;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Mathematica";
  }
}
