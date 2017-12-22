/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.module.ui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;
import de.halirutan.mathematica.module.MathematicaLanguageLevelModuleExtensionImpl;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author patrick (22.12.17).
 */
abstract public class MathematicaModuleLanguageLevelConfigurable implements UnnamedConfigurable {

  private final Module myModule;
  private final JPanel myPanel;
  private final MathematicaLanguageLevelModuleExtensionImpl myModuleExtension;
  private MathematicaLanguageLevelComboBox myLanguageLevelCombo;

  public MathematicaModuleLanguageLevelConfigurable(Module module, Runnable onChange) {
    myModule = module;
    myPanel = new JPanel();
    myModuleExtension = getModuleExtension();
    myLanguageLevelCombo = new MathematicaLanguageLevelComboBox();
    myLanguageLevelCombo.setSelectedItem(getModuleExtension().getMathematicaLanguageLevel());
    myLanguageLevelCombo.addActionListener(e -> {
      final Object lang = myLanguageLevelCombo.getSelectedItem();
//      myModuleExtension.setMathematicaLanguageLevel(lang instanceof MathematicaLanguageLevel ?
//          (MathematicaLanguageLevel) lang : MathematicaLanguageLevel.HIGHEST);
    });
    myPanel.add(myLanguageLevelCombo);
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return myPanel;
  }

  @Override
  public boolean isModified() {
    return myLanguageLevelCombo.getSelectedItem() != myModuleExtension.getMathematicaLanguageLevel();
  }

  @Override
  public void apply() throws ConfigurationException {
    myModuleExtension.setMathematicaLanguageLevel((MathematicaLanguageLevel) myLanguageLevelCombo.getSelectedItem());
    myModuleExtension.commit();
  }

  @Override
  public void reset() {
    myLanguageLevelCombo.setSelectedItem(getModuleExtension().getMathematicaLanguageLevel());
  }

  abstract MathematicaLanguageLevelModuleExtensionImpl getModuleExtension();
}
