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

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author patrick (6/20/14)
 */
public class MathematicaStructureViewDefinitionElement implements StructureViewTreeElement, StructureViewModel.ExpandInfoProvider {

  private final String mySymbolName;
  private final List<SymbolDefinition> mySymbolDefinitions;
  private final SymbolDefinition myFirstSymbolDefinition;
  private final boolean myIsLeaf;

  public MathematicaStructureViewDefinitionElement(final String symbolName, final List<SymbolDefinition> symbolDefinitions) {
    assert symbolDefinitions.size() > 0;
    mySymbolName = symbolName;
    mySymbolDefinitions = symbolDefinitions;
    myFirstSymbolDefinition = symbolDefinitions.get(0);
    myIsLeaf = mySymbolDefinitions.size() == 1;
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    if (myIsLeaf) {
      return mySymbolDefinitions.iterator().next().getPresentation();
    } else {
      return new NodeItemPresentation();
    }
  }

  @NotNull
  @Override
  public TreeElement[] getChildren() {
    if (myIsLeaf) {
      return new TreeElement[0];
    } else {
      final Collection<StructureViewTreeElement> children = new ArrayList<StructureViewTreeElement>();
      for (SymbolDefinition symbolDefinition : mySymbolDefinitions) {
        children.add(symbolDefinition);
      }
      return children.toArray(new StructureViewTreeElement[children.size()]);
    }
  }

  @Override
  public Object getValue() {
    return myFirstSymbolDefinition.getValue();
  }

  @Override
  public void navigate(final boolean requestFocus) {
    myFirstSymbolDefinition.navigate(requestFocus);
  }

  @Override
  public boolean canNavigate() {
    return myFirstSymbolDefinition.canNavigate();
  }

  @Override
  public boolean canNavigateToSource() {
    return myFirstSymbolDefinition.canNavigateToSource();
  }

  @Override
  public boolean isAutoExpand(@NotNull final StructureViewTreeElement element) {
    return true;
  }

  @Override
  public boolean isSmartExpand() {
    return true;
  }

  private class NodeItemPresentation implements ItemPresentation {
    @Nullable
    @Override
    public String getPresentableText() {
      return mySymbolName;
    }

    @Nullable
    @Override
    public String getLocationString() {
      return null;
    }

    @Nullable
    @Override
    public Icon getIcon(final boolean unused) {
      return null;
    }
  }
}
