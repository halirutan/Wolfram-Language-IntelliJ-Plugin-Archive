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

package de.halirutan.mathematica.codeinsight.structureview.groupers;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.smartTree.*;
import de.halirutan.mathematica.MathematicaIcons;
import de.halirutan.mathematica.codeinsight.structureview.elements.AssignmentLeafViewTreeElement;
import de.halirutan.mathematica.codeinsight.structureview.elements.MathematicaFileTreeElement;
import de.halirutan.mathematica.parsing.psi.SymbolAssignmentType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

/**
 * @author patrick (7/21/14)
 */
public class AssignmentTypeGrouper implements Grouper, Sorter {

  private static final String ID = "ASSIGNMENT_TYPE_GROUPER";

  @NotNull
  @Override
  public Collection<Group> group(@NotNull final AbstractTreeNode parent, @NotNull final Collection<TreeElement> children) {

    if (!(parent.getValue() instanceof MathematicaFileTreeElement)) return Collections.emptySet();
    final HashMap<SymbolAssignmentType, Collection<TreeElement>> groupedElements = new HashMap<SymbolAssignmentType, Collection<TreeElement>>(children.size());

    for (TreeElement definition : children) {
      if (definition instanceof AssignmentLeafViewTreeElement) {
        final SymbolAssignmentType type = ((AssignmentLeafViewTreeElement) definition).getAssignmentType();
        if (groupedElements.containsKey(type)) {
          groupedElements.get(type).add(definition);
        } else {
          groupedElements.put(type, new HashSet<TreeElement>());
          groupedElements.get(type).add(definition);
        }
      }
    }

    Collection<Group> result = new HashSet<Group>(groupedElements.size());
    for (final SymbolAssignmentType key : groupedElements.keySet()) {
      result.add(new SymbolNameGroup(key.toString(), groupedElements.get(key)));
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
        return "Group by Assignment Type";
      }

      @Override
      public String getDescription() {
        return "Groups all assignments by their type.";
      }

      @Override
      public Icon getIcon() {
        return MathematicaIcons.GROUP_BY_TYPE_ICON;
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
    return Sorter.ALPHA_SORTER.getComparator();
  }

  @Override
  public boolean isVisible() {
    return true;
  }

}
