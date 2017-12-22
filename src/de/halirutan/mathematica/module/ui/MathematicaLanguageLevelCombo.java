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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ColoredListCellRenderer;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import de.halirutan.mathematica.sdk.MathematicaSdkType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author ven
 */
@SuppressWarnings("unchecked")
public class MathematicaLanguageLevelCombo extends ComboBox {
  private MathematicaLanguageLevel myDefaultItem;
  private final ModifiableRootModel myModel;

  public MathematicaLanguageLevelCombo(ModifiableRootModel model) {
    myDefaultItem = MathematicaLanguageLevel.HIGHEST;
    myModel = model;
    final Sdk sdk = myModel.getSdk();
    if ((sdk != null ? sdk.getSdkType() : null) instanceof MathematicaSdkType) {
      myDefaultItem = MathematicaLanguageLevel.createFromSdk(sdk);
    }
    for (MathematicaLanguageLevel level : MathematicaLanguageLevel.values()) {
      addItem(level);
    }
    setSelectedItem(myDefaultItem);
    setRenderer(new ColoredListCellRenderer() {
      @Override
      protected void customizeCellRenderer(@NotNull JList list, Object value, int index, boolean selected, boolean hasFocus) {
          append(((MathematicaLanguageLevel)value).getPresentableText());
      }
    });
  }

  public void reset(@NotNull Project project) {
    removeAllItems();
    for (MathematicaLanguageLevel level : MathematicaLanguageLevel.values()) {
      addItem(level);
    }
  }

  @Override
  public void setSelectedItem(Object anObject) {
    super.setSelectedItem(anObject == null ? myDefaultItem : anObject);
  }
}
