/*
 * Copyright (c) 2018 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.navigation;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.util.MathematicaIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides a {@link GotoRelatedItem} that is useful to give information about the usage of a Mathematica symbol
 *
 * @author patrick (28.12.16).
 */
class MathematicaGotoSymbolItem extends GotoRelatedItem {

  private final String myDescription;
  private final String myContext;
  private final int myLineNumber;

  /**
   * Create an entry with description text
   *
   * @param element     {@link PsiElement} to navigate to
   * @param description descriptive text that appears in the info box
   * @param contextInfo additional information like line number and context
   * @param lineNumber  used for sorting the entries
   */
  MathematicaGotoSymbolItem(@NotNull PsiElement element, @NotNull final String description, @NotNull final String contextInfo, final int lineNumber) {
    super(element);
    myDescription = description;
    myContext = contextInfo;
    myLineNumber = lineNumber;
  }

  @Nullable
  @Override
  public String getCustomContainerName() {
    return myContext;
  }

  @Nullable
  @Override
  public String getCustomName() {
    return myDescription;
  }

  int getLineNumber() {
    return myLineNumber;
  }

  @Nullable
  @Override
  public Icon getCustomIcon() {
    return MathematicaIcons.FILE_ICON;
  }
}
