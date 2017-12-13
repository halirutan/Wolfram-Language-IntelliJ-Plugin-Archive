package de.halirutan.mathematica.module;

import com.intellij.openapi.ui.ComboBoxWithWidePopup;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;

/**
 * @author patrick (13.12.17).
 */
public class MathematicaLanguageLevelComboBox extends ComboBoxWithWidePopup<MathematicaLanguageLevel> {
  public MathematicaLanguageLevelComboBox() {
    super(MathematicaLanguageLevel.values());
  }
}
