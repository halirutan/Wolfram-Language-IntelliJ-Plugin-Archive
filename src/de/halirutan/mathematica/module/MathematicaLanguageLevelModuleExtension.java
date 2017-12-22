package de.halirutan.mathematica.module;

import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;

import javax.annotation.Nullable;

/**
 * @author patrick (21.12.17).
 */
public interface MathematicaLanguageLevelModuleExtension {
  @Nullable
  MathematicaLanguageLevel getMathematicaLanguageLevel();

  void setMathematicaLanguageLevel(MathematicaLanguageLevel languageLevel);

}
