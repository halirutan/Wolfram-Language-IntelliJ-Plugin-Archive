package de.halirutan.mathematica.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Interface declaring the methods that should be used by the user. Most other stuff in {@link MathematicaLanguageLevelModuleExtensionImpl}
 * is internal (although declared public to make it accessible for the IntelliJ framework).
 * @author patrick (21.12.17).
 */
public abstract class MathematicaLanguageLevelModuleExtension extends ModuleExtension {

  /**
   * Returns an instance of the extension for a module. Always use this and not the constructor of the implementing
   * class.
   *
   * @param module The module you want the extension for
   *
   * @return Instance that can be accessed and changed
   */
  public static MathematicaLanguageLevelModuleExtension getInstance(Module module) {
    return ModuleRootManager.getInstance(module).getModuleExtension(MathematicaLanguageLevelModuleExtension.class);
  }

  /**
   * Gets the current the language version for the module.
   *
   * @return Mathematica version that is used in the module
   */
  @NotNull
  public abstract MathematicaLanguageLevel getMathematicaLanguageLevel();

  /**
   * Sets the language version for a module.
   *
   * @param languageLevel New language version.
   */
  public abstract void setMathematicaLanguageLevel(MathematicaLanguageLevel languageLevel);

}
