/*
 * Copyright (c) 2013 Patrick Scheibe
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
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import de.halirutan.mathematica.sdk.MathematicaSdkType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * @author rsmenon (5/13/13)
 */
public class MathematicaModifiedSettingsStep extends SdkSettingsStep {
  protected final MathematicaModuleBuilder myModuleBuilder;

  MathematicaModifiedSettingsStep(@NotNull final MathematicaModuleBuilder builder, @NotNull SettingsStep settingsStep) {
    super(settingsStep, builder, new Condition<SdkTypeId>() {
      @Override
      public boolean value(SdkTypeId sdkType) {
        return builder.isSuitableSdkType(sdkType);
      }
    });
    myModuleBuilder = builder;
  }

  @Override
  protected void onSdkSelected(Sdk sdk) {
    if (sdk instanceof MathematicaSdkType) {

    }
  }

  @Override
  public void updateDataModel() {
    super.updateDataModel();
    final String path = myModuleBuilder.getContentEntryPath();

    if (path != null) {
      //don't create an src file
      myModuleBuilder.setSourcePaths(Collections.singletonList(Pair.create(path, "")));
    }
  }

}
