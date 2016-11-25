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

package de.halirutan.mathematica.codeinsight.inspections.bugs;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import de.halirutan.mathematica.codeinsight.completion.SymbolVersionProvider;
import de.halirutan.mathematica.codeinsight.inspections.AbstractInspection;
import de.halirutan.mathematica.codeinsight.inspections.MathematicaInspectionBundle;
import de.halirutan.mathematica.filetypes.MathematicaFileType;
import de.halirutan.mathematica.module.MathematicaLanguageLevel;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.sdk.MathematicaSdkType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Provides warnings when you are using Mathematica symbols that are introduces later than the version you are using.
 *
 * @author halirutan
 */
public class UnsupportedVersion extends AbstractInspection {

  @SuppressWarnings("InstanceVariableNamingConvention")
  public MathematicaLanguageLevel languageLevel = MathematicaLanguageLevel.HIGHEST;

  @SuppressWarnings("InstanceVariableNamingConvention")
  public boolean useSDKLanguageLevelOrHighest = true;

  /**
   * Sets the correct text for the info label in the inspection settings page
   *
   * @param label label to set the text
   */
  private void setLabelTextToVersion(JLabel label) {
    if (useSDKLanguageLevelOrHighest) {
      label.setText("Use language version from Project SDK");
    } else {
      label.setText(languageLevel.getPresentableText());
    }
  }

  @Nullable
  @Override
  public JComponent createOptionsPanel() {
    final JPanel mainPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP));
    final JCheckBox useSDKCheckbox = new JCheckBox("Use Project SDK Language Level");
    final JLabel infoLabel = new JLabel();
    //noinspection Since15
    final ComboBox<MathematicaLanguageLevel> versionComboBox = new ComboBox<MathematicaLanguageLevel>();

    for (MathematicaLanguageLevel level : MathematicaLanguageLevel.values()) {
      //noinspection unchecked
      versionComboBox.addItem(level);
    }
    versionComboBox.setSelectedItem(languageLevel);
    versionComboBox.setEditable(false);
    //noinspection unchecked
    versionComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final MathematicaLanguageLevel selectedItem = (MathematicaLanguageLevel) versionComboBox.getSelectedItem();
        if (selectedItem != null) {
          languageLevel = selectedItem;
          setLabelTextToVersion(infoLabel);
        }
      }
    });

    useSDKCheckbox.setSelected(useSDKLanguageLevelOrHighest);
    useSDKCheckbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        useSDKLanguageLevelOrHighest = useSDKCheckbox.isSelected();
        versionComboBox.setVisible(!useSDKLanguageLevelOrHighest);
        if (!useSDKLanguageLevelOrHighest) {
          languageLevel = (MathematicaLanguageLevel) versionComboBox.getSelectedItem();
        }
        setLabelTextToVersion(infoLabel);
      }
    });

    setLabelTextToVersion(infoLabel);
    versionComboBox.setVisible(!useSDKLanguageLevelOrHighest);

    mainPanel.add(infoLabel);
    mainPanel.add(useSDKCheckbox);
    mainPanel.add(versionComboBox);

    return mainPanel;
  }

  @Nls
  @NotNull
  @Override
  public String getDisplayName() {
    return MathematicaInspectionBundle.message("bugs.unsupported.version.name");
  }

  @Nullable
  @Override
  public String getStaticDescription() {
    return MathematicaInspectionBundle.message("bugs.unsupported.version.description");
  }

  @Nls
  @NotNull
  @Override
  public String getGroupDisplayName() {
    return MathematicaInspectionBundle.message("group.bugs");
  }

  @NotNull
  @Override
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.ERROR;
  }

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
    if (session.getFile().getFileType() instanceof MathematicaFileType) {
      if (useSDKLanguageLevelOrHighest) {
        final ProjectRootManager manager = ProjectRootManager.getInstance(holder.getProject());
        final Sdk projectSdk = manager.getProjectSdk();
        if (projectSdk != null && projectSdk.getSdkType() instanceof MathematicaSdkType) {
          languageLevel = MathematicaLanguageLevel.createFromSdk(projectSdk);
        }
      }
      return new WrongVersionVisitor(holder, languageLevel);
    } else return PsiElementVisitor.EMPTY_VISITOR;
  }

  /**
   * This visitor just inspects all symbols in the file. For each symbol it checks whether it is in the list of built-in
   * symbols and if yes, if it is already defined in the Mathematica version the user specified
   */
  private static class WrongVersionVisitor extends MathematicaVisitor {

    private HashMap<String, Double> mySymbolVersions = SymbolVersionProvider.getSymbolNames();
    private MathematicaLanguageLevel myLanguageLevel = MathematicaLanguageLevel.HIGHEST;
    private final ProblemsHolder myHolder;

    WrongVersionVisitor(final ProblemsHolder holder, final MathematicaLanguageLevel usedLanguageVersion) {
      this.myHolder = holder;
      final Sdk projectSdk = ProjectRootManager.getInstance(myHolder.getProject()).getProjectSdk();
      if (projectSdk != null && projectSdk.getSdkType() instanceof MathematicaSdkType) {
        myLanguageLevel = usedLanguageVersion;
      }

    }

    private void registerProblem(final PsiElement element, final String message) {
      myHolder.registerProblem(
          element,
          TextRange.from(0, element.getTextLength()),
          message);
    }

    @Override
    public void visitSymbol(Symbol symbol) {
      final String symbolName = symbol.getSymbolName();
      if (Character.isLowerCase(symbolName.charAt(0))) {
        return;
      }

      String nameWithContext = symbol.getMathematicaContext() + symbolName;
      if (mySymbolVersions.containsKey(nameWithContext)) {
        double version = mySymbolVersions.get(nameWithContext);
        if (version > myLanguageLevel.getVersionNumber()) {
          registerProblem(symbol, "Mathematica " + version + " required. You are using " + myLanguageLevel.getPresentableText());
        }
      }
    }
  }
}


