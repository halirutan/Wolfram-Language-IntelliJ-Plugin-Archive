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

package de.halirutan.mathematica.codeInsight.formatter.settings;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import de.halirutan.mathematica.MathematicaLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author patrick (11/2/13)
 */
public class MathematicaLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
  public static final String AROUND_OPERATORS = "Around Operators";

  private static final String GENERAL_EXAMPLE = readFromFile("spacingExample.m");

  @NotNull
  @Override
  public Language getLanguage() {
    return MathematicaLanguage.INSTANCE;
  }

  @Override
  public String getCodeSample(@NotNull SettingsType settingsType) {
    if (settingsType == SettingsType.SPACING_SETTINGS) return GENERAL_EXAMPLE;
    if (settingsType == SettingsType.BLANK_LINES_SETTINGS) return readFromFile("blankLinesExample.m");
    if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) return readFromFile("spacingExample.m");

    return GENERAL_EXAMPLE;
  }


  @Override
  public IndentOptionsEditor getIndentOptionsEditor() {
    return new SmartIndentOptionsEditor();
  }

  @Override
  public CommonCodeStyleSettings getDefaultCommonSettings() {
    CommonCodeStyleSettings defaultSettings = new CommonCodeStyleSettings(getLanguage());
    CommonCodeStyleSettings.IndentOptions indentOptions = defaultSettings.initIndentOptions();
    indentOptions.INDENT_SIZE = 2;
    indentOptions.CONTINUATION_INDENT_SIZE = 4;
    indentOptions.TAB_SIZE = 2;
    indentOptions.USE_TAB_CHARACTER = false;

    return defaultSettings;
  }

  @Nullable
  @Override
  public PsiFile createFileFromText(Project project, String text) {
    return super.createFileFromText(project, text);
  }


  //    final PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(
//        "sample.m", MathematicaFileType.INSTANCE, text, LocalTimeCounter.currentTime(), false, false
//    );
//    return file;
//  }

  @Override
  public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
    if (settingsType == SettingsType.SPACING_SETTINGS) {
      consumer.showStandardOptions(
          "SPACE_AFTER_COMMA"
      );
      consumer.showCustomOption(MathematicaCodeStyleSettings.class, "SPACE_AROUND_ASSIGNMENT_OPERATIONS", "Assignment (=, :=)", AROUND_OPERATORS);
      consumer.showCustomOption(MathematicaCodeStyleSettings.class, "SPACE_AROUND_ARITHMETIC_OPERATIONS", "Arithmetic (+, -)", AROUND_OPERATORS);
      consumer.showCustomOption(MathematicaCodeStyleSettings.class, "SPACE_AROUND_RELATION_OPERATIONS", "Relation (==, =!=)", AROUND_OPERATORS);
      consumer.showCustomOption(MathematicaCodeStyleSettings.class, "SPACE_AROUND_RULE_OPERATIONS", "Rules (/., ->)", AROUND_OPERATORS);
      consumer.showCustomOption(MathematicaCodeStyleSettings.class, "SPACE_AROUND_FUNCTIONAL_OPERATIONS", "Functional (/@, @@)", AROUND_OPERATORS);
      consumer.showCustomOption(MathematicaCodeStyleSettings.class, "SPACE_AROUND_OTHER_OPERATIONS", "Other (~~, /;)", AROUND_OPERATORS);
    } else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
      consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
    }
  }

  public static String readFromFile(@NonNls final String fileName) {
    try {
      final InputStream stream = MathematicaLanguageCodeStyleSettingsProvider.class.getResourceAsStream(fileName);
      final InputStreamReader reader = new InputStreamReader(stream);
      final StringBuffer result;
      final LineNumberReader lineNumberReader = new LineNumberReader(reader);
      try {
        result = new StringBuffer();
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
          result.append(line);
          result.append("\n");
        }
      } finally {
        lineNumberReader.close();
      }

      return result.toString();
    } catch (IOException e) {
      return "";
    }
  }

}
