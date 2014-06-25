/*
 * Copyright (c) 2014 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.structureview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.MathematicaIcons;
import de.halirutan.mathematica.parsing.psi.api.assignment.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author patrick (6/20/14)
 */
public class SymbolDefinitionRepresentation implements ItemPresentation {

  private SymbolDefinition myDefinition;

  public SymbolDefinitionRepresentation(final SymbolDefinition symbolDefinition) {
    myDefinition = symbolDefinition;
  }

  @Nullable
  @Override
  public String getPresentableText() {
    return myDefinition.getLhs();
  }

  @Nullable
  @Override
  public String getLocationString() {
    return null;
  }

  @Nullable
  @Override
  public Icon getIcon(final boolean unused) {
    final PsiElement setType = myDefinition.getSetType();
    if (setType instanceof Set) {
      return MathematicaIcons.SET_ICON;
    } else if (setType instanceof SetDelayed) {
      return MathematicaIcons.SETDELAYED_ICON;
    } else if (setType instanceof TagSet) {
      return MathematicaIcons.TAGSET_ICON;
    } else if (setType instanceof TagSetDelayed) {
      return MathematicaIcons.TAGSETDELAYED_ICON;
    } else if (setType instanceof UpSet) {
      return MathematicaIcons.UPSET_ICON;
    } else if (setType instanceof UpSetDelayed) {
      return MathematicaIcons.UPSETDELAYED_ICON;
    }
    return null;
  }
}
