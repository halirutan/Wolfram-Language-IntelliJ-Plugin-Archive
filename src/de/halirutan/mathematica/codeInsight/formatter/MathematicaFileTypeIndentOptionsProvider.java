package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.FileTypeIndentOptionsProvider;
import de.halirutan.mathematica.fileTypes.MathematicaFileType;

/**
 * @author patrick (10/21/13)
 */
public class MathematicaFileTypeIndentOptionsProvider implements FileTypeIndentOptionsProvider {
  @Override
  public CommonCodeStyleSettings.IndentOptions createIndentOptions() {
    return new CommonCodeStyleSettings.IndentOptions();
  }

  @Override
  public FileType getFileType() {
    return MathematicaFileType.INSTANCE;
  }

  @Override
  public IndentOptionsEditor createOptionsEditor() {
    return null;
  }

  @Override
  public String getPreviewText() {
    return null;
  }

  @Override
  public void prepareForReformat(PsiFile psiFile) {
  }
}
