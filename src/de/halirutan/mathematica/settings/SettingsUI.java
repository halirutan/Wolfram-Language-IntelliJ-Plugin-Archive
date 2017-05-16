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

import com.intellij.ui.IdeBorderFactory;
import de.halirutan.mathematica.settings.MathematicaSettings.SmartEnterResult;

import javax.swing.*;
import java.awt.*;

/**
 * The UI that is shown under Settings -> Languages -> Mathematica
 * @author patrick (01.12.16).
 */
@SuppressWarnings("InstanceVariableNamingConvention")
class SettingsUI extends JPanel {
  private JCheckBox insertTemplate;
  private JCheckBox insertAsCode;
  private JCheckBox insertBraces;
  private JCheckBox sortByImportance;
  private JCheckBox sortByName;

  SettingsUI() {
    init();
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Settings Test");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    JPanel panel = new JPanel();
    SettingsUI ui = new SettingsUI();
    ui.setSettings(new MathematicaSettings());
    panel.add(ui, BorderLayout.CENTER);
    frame.getContentPane().add(panel);
    frame.setSize(450, 450);
    frame.setVisible(true);
  }

  private void init() {
    setLayout(new BorderLayout());
    JPanel panel = this;

    insertAsCode = new JCheckBox("Insert template arguments as code");
    insertAsCode.setMnemonic('C');
    insertBraces = new JCheckBox("Insert braces only");
    insertBraces.setMnemonic('B');
    insertTemplate = new JCheckBox("Insert template arguments as LiveTemplate");
    insertTemplate.setMnemonic('T');

    ButtonGroup g1 = new ButtonGroup();
    g1.add(insertBraces);
    g1.add(insertAsCode);
    g1.add(insertTemplate);

    JPanel insertPanel = new JPanel(new BorderLayout());
    insertPanel.setBorder(IdeBorderFactory.createTitledBorder("Insert completion on SmartEnter"));
    panel.add(panel = new JPanel(new BorderLayout()), BorderLayout.NORTH);
    panel.add(insertPanel, BorderLayout.NORTH);
    insertPanel.add(insertTemplate, BorderLayout.NORTH);
    insertPanel.add(insertPanel = new JPanel(new BorderLayout()), BorderLayout.SOUTH);
    insertPanel.add(insertAsCode, BorderLayout.NORTH);
    insertPanel.add(insertPanel = new JPanel(new BorderLayout()), BorderLayout.SOUTH);
    insertPanel.add(insertBraces);

    sortByImportance = new JCheckBox("Sort by importance");
    sortByImportance.setMnemonic('I');
    sortByName = new JCheckBox("Sort by name");
    sortByName.setMnemonic('N');

    ButtonGroup g2 = new ButtonGroup();
    g2.add(sortByImportance);
    g2.add(sortByName);

    JPanel sortPanel = new JPanel(new BorderLayout());
    sortPanel.setBorder(IdeBorderFactory.createTitledBorder("Sorting of completion entries"));
    panel.add(panel = new JPanel(new BorderLayout()), BorderLayout.SOUTH);
    panel.add(sortPanel, BorderLayout.SOUTH);

    sortPanel.add(sortByImportance, BorderLayout.NORTH);
    sortPanel.add(sortByName, BorderLayout.SOUTH);
  }

  public MathematicaSettings getSettings() {
    final MathematicaSettings settings = new MathematicaSettings();
    if (insertAsCode.isSelected()) {
      settings.setSmartEnterResult(SmartEnterResult.INSERT_CODE);
    } else if (insertBraces.isSelected()) {
      settings.setSmartEnterResult(SmartEnterResult.INSERT_BRACES);
    } else if (insertTemplate.isSelected()) {
      settings.setSmartEnterResult(SmartEnterResult.INSERT_TEMPLATE);
    }
    settings.setSortCompletionEntriesLexicographically(sortByName.isSelected());
    return settings;
  }

  public void setSettings(MathematicaSettings settings) {
    insertBraces.setSelected(
        settings.getSmartEnterResult().equals(SmartEnterResult.INSERT_BRACES));
    insertAsCode.setSelected(
        settings.getSmartEnterResult().equals(SmartEnterResult.INSERT_CODE)
    );
    insertTemplate.setSelected(
        settings.getSmartEnterResult().equals(SmartEnterResult.INSERT_TEMPLATE)
    );
    sortByName.setSelected(
        settings.isSortCompletionEntriesLexicographically()
    );
    sortByImportance.setSelected(
        !settings.isSortCompletionEntriesLexicographically()
    );
  }
}
