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

package de.halirutan.mathematica.module;

import com.intellij.ide.util.projectWizard.SdkSettingsStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.projectRoots.Sdk;
import de.halirutan.mathematica.module.ui.MathematicaLanguageLevelComboBox;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import de.halirutan.mathematica.sdk.MathematicaSdkType;
import org.jetbrains.annotations.NotNull;

/**
 * Provides the setting step where the user can choose the Mathematica SDK and the Language Version that should be
 * used.
 */
class MathematicaModifiedSettingsStep extends SdkSettingsStep {
  private final MathematicaModuleBuilder myModuleBuilder;
  private final MathematicaLanguageLevelComboBox myLanguageLevelCombo;
  private MathematicaLanguageLevel mySDKLanguageLevel;

  MathematicaModifiedSettingsStep(@NotNull final MathematicaModuleBuilder builder, @NotNull SettingsStep settingsStep) {
    super(settingsStep, builder, builder::isSuitableSdkType);
    myModuleBuilder = builder;

    if (mySDKLanguageLevel == null) {
      mySDKLanguageLevel = MathematicaLanguageLevel.HIGHEST;
    }
    myLanguageLevelCombo = new MathematicaLanguageLevelComboBox();
    myLanguageLevelCombo.setSelectedItem(mySDKLanguageLevel);
    settingsStep.addSettingsField("Language Level:", myLanguageLevelCombo);

  }

  @Override
  protected void onSdkSelected(Sdk sdk) {
    if (sdk != null && sdk.getSdkType() == MathematicaSdkType.getInstance()) {
      final MathematicaLanguageLevel fromSdk = MathematicaLanguageLevel.createFromSdk(sdk);
      mySDKLanguageLevel = fromSdk;
      if (myLanguageLevelCombo != null) {
        myLanguageLevelCombo.setSelectedItem(fromSdk);
      }
    }
  }

  @Override
  public void updateDataModel() {
    super.updateDataModel();
    if (myLanguageLevelCombo.getSelectedItem() instanceof MathematicaLanguageLevel) {
      myModuleBuilder.setLanguageLevel((MathematicaLanguageLevel) myLanguageLevelCombo.getSelectedItem());
    }
  }

}
