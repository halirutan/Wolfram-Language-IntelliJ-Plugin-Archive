/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.codeinsight.structureview.groupers;

import com.intellij.ide.util.treeView.smartTree.Group;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import de.halirutan.mathematica.codeinsight.highlighting.MathematicaSyntaxHighlighterColors;
import de.halirutan.mathematica.codeinsight.structureview.sorters.CodePlaceProvider;
import de.halirutan.mathematica.lang.psi.SymbolAssignmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 * The way a node in the structure view is represented when the user groups the code by assignment type.
 * @author patrick (7/21/14)
 */
public class AssignmentTypeGroup implements Group, CodePlaceProvider, ColoredItemPresentation, SortableTreeElement {

  private final Collection<TreeElement> myTreeElements;
  private final SymbolAssignmentType myType;

  AssignmentTypeGroup(final SymbolAssignmentType type, final Collection<TreeElement> treeElements) {
    this.myTreeElements = treeElements;
    this.myType = type;
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    return this;
  }

  @NotNull
  @Override
  public Collection<TreeElement> getChildren() {
    return myTreeElements;
  }

  @Override
  public int getPosition() {
    if (myType == null) {
      return 0;
    }
    return myType.getTypeSortKey();
  }

  @Nullable
  @Override
  public TextAttributesKey getTextAttributesKey() {
    switch (myType) {
      case SET_ASSIGNMENT:
      case SET_DELAYED_ASSIGNMENT:
      case TAG_SET_ASSIGNMENT:
      case TAG_SET_DELAYED_ASSIGNMENT:
      case UP_SET_ASSIGNMENT:
      case UP_SET_DELAYED_ASSIGNMENT:
        return DefaultLanguageHighlighterColors.INSTANCE_METHOD;
      case MESSAGE_ASSIGNMENT:
        return MathematicaSyntaxHighlighterColors.INSTANCE.getMESSAGE();
      case OPTIONS_ASSIGNMENT:
        return MathematicaSyntaxHighlighterColors.INSTANCE.getMODULE_LOCALIZED();
      case ATTRIBUTES_ASSIGNMENT:
        return MathematicaSyntaxHighlighterColors.INSTANCE.getKERNEL_SYMBOL();
    }
    return null;
  }

  @Nullable
  @Override
  public String getPresentableText() {
    return myType.toString();
  }

  @Nullable
  @Override
  public String getLocationString() {
    return null;
  }

  @Nullable
  @Override
  public Icon getIcon(final boolean unused) {
    return myType.getIcon();
  }

  @NotNull
  @Override
  public String getAlphaSortKey() {
    return String.valueOf(myType.getTypeSortKey());
  }
}
