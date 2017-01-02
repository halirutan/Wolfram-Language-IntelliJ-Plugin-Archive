/*
 * Copyright (c) 2017 Patrick Scheibe
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

package de.halirutan.mathematica.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author patrick (01.01.17).
 */
public class MathematicaSettingsConfigurable extends BaseConfigurable {

  private SettingsUI settingsUI;

  @Nls
  @Override
  public String getDisplayName() {
    return "Mathematica";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return "xpath.settings";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    settingsUI = new SettingsUI();
    settingsUI.setSettings(MathematicaSettings.getInstance());
    return settingsUI;
  }

  @Override
  public void apply() throws ConfigurationException {
    if (settingsUI != null) {
      final MathematicaSettings instance = MathematicaSettings.getInstance();
      instance.loadState(settingsUI.getSettings());
    }

  }

  @Override
  public void reset() {
    if (settingsUI != null) {
      settingsUI.setSettings(MathematicaSettings.getInstance());
    }
  }

  @Override
  public boolean isModified() {
    if (settingsUI != null) {
      return !settingsUI.getSettings().equals(MathematicaSettings.getInstance());
    }
    return false;
  }
}
