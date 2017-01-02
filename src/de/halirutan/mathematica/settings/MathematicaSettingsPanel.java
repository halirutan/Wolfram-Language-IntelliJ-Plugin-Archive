/*
 * Copyright (c) 2016 Patrick Scheibe
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
import de.halirutan.mathematica.settings.MathematicaSettings.SmartEnterResult;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author patrick (01.12.16).
 */
@SuppressWarnings("InstanceVariableNamingConvention")
public class MathematicaSettingsPanel extends BaseConfigurable{
  private MathematicaSettings mySettings;
  private JPanel mySettingsPanel;
  private JRadioButton insertCallPatternAsLiveTemplateRadioButton;
  private JRadioButton insertCallPatternAsCodeRadioButton;
  private JRadioButton insertBracesOnlyRadioButton;
  private JRadioButton sortEntriesByImportanceRadioButton;
  private JRadioButton sortEntriesByNameRadioButton;

  private MathematicaSettingsPanel(MathematicaSettings settings) {
    mySettings = settings;
    loadSettings(mySettings);
  }

  public JComponent createComponent() {
    return mySettingsPanel;
  }

  @Override
  public void apply() throws ConfigurationException {
    mySettings.setSortCompletionEntriesLexicographically(sortEntriesByNameRadioButton.isSelected());
    mySettings.setSmartEnterResult(
        insertBracesOnlyRadioButton.isSelected() ? SmartEnterResult.INSERT_BRACES :
            insertCallPatternAsCodeRadioButton.isSelected() ? SmartEnterResult.INSERT_CODE :
                SmartEnterResult.INSERT_TEMPLATE
    );
  }

  private void updateUI() {
    insertBracesOnlyRadioButton.setSelected(
        mySettings.getSmartEnterResult().equals(SmartEnterResult.INSERT_BRACES));
    insertCallPatternAsCodeRadioButton.setSelected(
        mySettings.getSmartEnterResult().equals(SmartEnterResult.INSERT_CODE)
    );
    insertCallPatternAsLiveTemplateRadioButton.setSelected(
        mySettings.getSmartEnterResult().equals(SmartEnterResult.INSERT_TEMPLATE)
    );
    sortEntriesByNameRadioButton.setSelected(
        mySettings.isSortCompletionEntriesLexicographically()
    );
    sortEntriesByImportanceRadioButton.setSelected(
        !mySettings.isSortCompletionEntriesLexicographically()
    );
  }

  @Override
  public void reset() {
    mySettings = MathematicaSettings.getInstance();
    updateUI();
  }

  @Override
  public void disposeUIResources() {
  }

  private void loadSettings(MathematicaSettings settings) {
    mySettings = settings;
    updateUI();
  }


  @Override
  public boolean isModified() {
    switch (mySettings.getSmartEnterResult()) {
      case INSERT_BRACES:
        if (!insertBracesOnlyRadioButton.isSelected()) return true;
        break;
      case INSERT_CODE:
        if (!insertCallPatternAsCodeRadioButton.isSelected()) return true;
        break;
      case INSERT_TEMPLATE:
        if (!insertCallPatternAsLiveTemplateRadioButton.isSelected()) return true;
        break;
    }
    if (mySettings.isSortCompletionEntriesLexicographically()) {
      return !sortEntriesByNameRadioButton.isSelected();
    }
    return false;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Mathematica";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }


  public static void main(String[] args) {
    JFrame  frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    MathematicaSettingsPanel panel = new MathematicaSettingsPanel(new MathematicaSettings());
    frame.getContentPane().add(panel.createComponent());
    frame.setSize(450,450);
    frame.setVisible(true);
  }


}
