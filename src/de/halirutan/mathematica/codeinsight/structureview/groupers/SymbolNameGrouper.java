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

package de.halirutan.mathematica.codeinsight.structureview.groupers;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.smartTree.*;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.util.MathematicaIcons;
import de.halirutan.mathematica.codeinsight.structureview.elements.AssignmentLeafViewTreeElement;
import de.halirutan.mathematica.codeinsight.structureview.sorters.AlphaSorterWithCase;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

/**
 * Groups assignment by the symbols they are assigning.
 * @author patrick (7/21/14)
 */
public class SymbolNameGrouper implements Grouper, Sorter {

  private static final String ID = "SYMBOL_NAME_GROUPER";

  @NotNull
  @Override
  public Collection<Group> group(@NotNull final AbstractTreeNode parent, @NotNull final Collection<TreeElement> children) {

    if (parent.getValue() instanceof SymbolNameGroup) {
      return Collections.emptySet();
    }

    final HashMap<String, Collection<TreeElement>> groupedElements = new HashMap<>(children.size());

    for (TreeElement definition : children) {
      if (definition instanceof AssignmentLeafViewTreeElement) {
        final Object symbol = ((AssignmentLeafViewTreeElement) definition).getValue();
        if (symbol instanceof Symbol) {
          final String symbolName = ((Symbol) symbol).getSymbolName();
          if (groupedElements.containsKey(symbolName)) {
            groupedElements.get(symbolName).add(definition);
          } else {
            groupedElements.put(symbolName, new HashSet<>());
            groupedElements.get(symbolName).add(definition);
          }
        }
      }
    }

    Collection<Group> result = new HashSet<>(groupedElements.size());
    for (final String key : groupedElements.keySet()) {
      result.add(new SymbolNameGroup(key, groupedElements.get(key)));
    }

    return result;

  }

  @NotNull
  @Override
  public ActionPresentation getPresentation() {
    return new ActionPresentation() {
      @NotNull
      @Override
      public String getText() {
        return MathematicaBundle.message("structureview.grouper.by.symbol.name.text");
      }

      @Override
      public String getDescription() {
        return MathematicaBundle.message("structureview.grouper.by.symbol.name.description");
      }

      @Override
      public Icon getIcon() {
        return MathematicaIcons.GROUP_BY_NAME_ICON;
      }
    };
  }

  @NotNull
  @Override
  public String getName() {
    return ID;
  }

  @Override
  public Comparator getComparator() {
    return AlphaSorterWithCase.INSTANCE.getComparator();
  }

  @Override
  public boolean isVisible() {
    return true;
  }


}
