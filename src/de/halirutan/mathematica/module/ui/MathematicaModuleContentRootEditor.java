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
import com.intellij.openapi.roots.ui.configuration.JavaContentEntriesEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import de.halirutan.mathematica.module.MathematicaLanguageLevelModuleExtensionImpl;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class MathematicaModuleContentRootEditor extends JavaContentEntriesEditor {

  private MathematicaModuleLanguageLevelConfigurable myLanguageLevelConfigurable;

  MathematicaModuleContentRootEditor(Module module, ModuleConfigurationState state) {
    super(module.getName(), state);
  }

  @Override
  protected void addAdditionalSettingsToPanel(JPanel mainPanel) {
    myLanguageLevelConfigurable =
        new MathematicaModuleLanguageLevelConfigurable() {
          @Override
          MathematicaLanguageLevelModuleExtensionImpl getModuleExtension() {
            return getModel().getModuleExtension(MathematicaLanguageLevelModuleExtensionImpl.class);
          }
        };
    final JComponent languageConfigurable = myLanguageLevelConfigurable.createComponent();
    assert languageConfigurable != null;
    mainPanel.add(languageConfigurable, BorderLayout.NORTH);
    myLanguageLevelConfigurable.reset();
  }

  @Override
  public void apply() throws ConfigurationException {
    myLanguageLevelConfigurable.apply();
  }

  @Override
  public boolean isModified() {
    return myLanguageLevelConfigurable.isModified();
  }
}
