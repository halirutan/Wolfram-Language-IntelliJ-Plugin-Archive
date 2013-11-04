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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (11/2/13)
 */
public class MathematicaLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

  @NotNull
  @Override
  public Language getLanguage() {
    return MathematicaLanguage.INSTANCE;
  }

  @Override
  public String getCodeSample(@NotNull SettingsType settingsType) {
    if (settingsType == SettingsType.SPACING_SETTINGS) return SPACING_SAMPLE;
    if (settingsType == SettingsType.BLANK_LINES_SETTINGS) return BLANK_LINE_SAMPLE;
    if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) return WRAPPING_CODE_SAMPLE;

    return GENERAL_CODE_SAMPLE;
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
          "SPACE_AROUND_ASSIGNMENT_OPERATORS",
          "SPACE_AROUND_LOGICAL_OPERATORS",
          "SPACE_AROUND_EQUALITY_OPERATORS",
          "SPACE_AROUND_ADDITIVE_OPERATORS",
          "SPACE_AROUND_MULTIPLICATIVE_OPERATORS",
          "SPACE_AFTER_COMMA",
          "SPACE_BEFORE_COMMA"
      );
    } else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
      consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
    } else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
      consumer.showStandardOptions(
//        "KEEP_LINE_BREAKS",
          "KEEP_FIRST_COLUMN_COMMENT"//,
//        "CALL_PARAMETERS_WRAP",
//        "CALL_PARAMETERS_LPAREN_ON_NEXT_LINE",
//        "CALL_PARAMETERS_RPAREN_ON_NEXT_LINE",
//        "METHOD_PARAMETERS_WRAP",
//        "METHOD_PARAMETERS_LPAREN_ON_NEXT_LINE",
//        "METHOD_PARAMETERS_RPAREN_ON_NEXT_LINE",
//        "ALIGN_MULTILINE_PARAMETERS",
//        "ALIGN_MULTILINE_PARAMETERS_IN_CALLS",
//        "ALIGN_MULTILINE_BINARY_OPERATION",
//        "BINARY_OPERATION_WRAP",
//        "BINARY_OPERATION_SIGN_ON_NEXT_LINE",
//        "PARENTHESES_EXPRESSION_LPAREN_WRAP",
//        "PARENTHESES_EXPRESSION_RPAREN_WRAP"
      );
//      consumer.showCustomOption(ErlangCodeStyleSettings.class, "ALIGN_MULTILINE_BLOCK", "Blocks (fun...end, etc)", "Alignment");
//      consumer.showCustomOption(ErlangCodeStyleSettings.class, "ALIGN_FUNCTION_CLAUSES", "Function clauses", "Alignment");
    }
  }

  private static final String SPACING_SAMPLE =
      "Bresenham[p1 : {x1_, y1_}, p2 : {x2_, y2_}] :=\n" +
          "  Module[{dx, dy, dir, corr, test, side},\n" +
          "    {dx, dy} = p2 - p1;\n" +
          "    dir = If[Abs[dx] > Abs[dy], {Sign[dx], 0}, {0, Sign[dy]}];\n" +
          "    test[{x_, y_}] := dy*x - dx*y + dx*y1 - dy*x1;\n" +
          "    side = Sign[test[p1 + dir]];\n" +
          "    corr = side*{-1, 1}*Reverse[dir];\n" +
          "    NestWhileList[\n" +
          "      Block[{new = # + dir},\n" +
          "        If[Sign[test[new]] == side, new += corr];\n" +
          "        new] &,/n" +
          "      p1,\n" +
          "      #1 =!= p2 &, 1, 500]]";
  private static final String BLANK_LINE_SAMPLE =
      "Bresenham[p1 : {x1_, y1_}, p2 : {x2_, y2_}] :=\n" +
          "  Module[{dx, dy, dir, corr, test, side},\n" +
          "    {dx, dy} = p2 - p1;\n" +
          "    dir = If[Abs[dx] > Abs[dy], {Sign[dx], 0}, {0, Sign[dy]}];\n" +
          "    test[{x_, y_}] := dy*x - dx*y + dx*y1 - dy*x1;\n" +
          "    side = Sign[test[p1 + dir]];\n" +
          "    corr = side*{-1, 1}*Reverse[dir];\n" +
          "    NestWhileList[\n" +
          "      Block[{new = # + dir},\n" +
          "        If[Sign[test[new]] == side, new += corr];\n" +
          "        new] &,\n" +
          "      p1,\n" +
          "      #1 =!= p2 &, 1, 500]]";
  private static final String WRAPPING_CODE_SAMPLE =
      "Bresenham[p1 : {x1_, y1_}, p2 : {x2_, y2_}] :=\n" +
          "  Module[{dx, dy, dir, corr, test, side},\n" +
          "    {dx, dy} = p2 - p1;\n" +
          "    dir = If[Abs[dx] > Abs[dy], {Sign[dx], 0}, {0, Sign[dy]}];\n" +
          "    test[{x_, y_}] := dy*x - dx*y + dx*y1 - dy*x1;\n" +
          "    side = Sign[test[p1 + dir]];\n" +
          "    corr = side*{-1, 1}*Reverse[dir];\n" +
          "    NestWhileList[\n" +
          "      Block[{new = # + dir},\n" +
          "        If[Sign[test[new]] == side, new += corr];\n" +
          "        new] &,\n" +
          "      p1,\n" +
          "      #1 =!= p2 &, 1, 500]]";
  private static final String GENERAL_CODE_SAMPLE =
      "Bresenham[p1 : {x1_, y1_}, p2 : {x2_, y2_}] :=\n" +
          "  Module[{dx, dy, dir, corr, test, side},\n" +
          "    {dx, dy} = p2 - p1;\n" +
          "    dir = If[Abs[dx] > Abs[dy], {Sign[dx], 0}, {0, Sign[dy]}];\n" +
          "    test[{x_, y_}] := dy*x - dx*y + dx*y1 - dy*x1;\n" +
          "    side = Sign[test[p1 + dir]];\n" +
          "    corr = side*{-1, 1}*Reverse[dir];\n" +
          "    NestWhileList[\n" +
          "      Block[{new = # + dir},\n" +
          "        If[Sign[test[new]] == side, new += corr];\n" +
          "        new] &,\n" +
          "      p1,\n" +
          "      #1 =!= p2 &, 1, 500]]";


}
