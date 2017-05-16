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
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.Group;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.MathematicaIcons;
import de.halirutan.mathematica.codeinsight.structureview.elements.AssignmentLeafViewTreeElement;
import de.halirutan.mathematica.codeinsight.structureview.elements.SimpleStringTreeElement;
import de.halirutan.mathematica.codeinsight.structureview.sorters.AssignmentTypeGroupComparator;
import de.halirutan.mathematica.parsing.psi.SymbolAssignmentType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

/**
 * Provides a way to group a collection of {@link AssignmentLeafViewTreeElement} by the type of the assignment.
 *
 * @author patrick (7/21/14)
 */
public class AssignmentTypeGrouper implements Grouper {

  private static final String ID = "ASSIGNMENT_TYPE_GROUPER";

  @NotNull
  @Override
  public Collection<Group> group(@NotNull final AbstractTreeNode parent, @NotNull final Collection<TreeElement> children) {

    if (!(parent.getValue() instanceof SimpleStringTreeElement)) return Collections.emptySet();

    final HashMap<SymbolAssignmentType, Collection<TreeElement>> groupedElements = new HashMap<>(children.size());

    for (TreeElement definition : children) {
      if (definition instanceof AssignmentLeafViewTreeElement) {
        final SymbolAssignmentType type = ((AssignmentLeafViewTreeElement) definition).getAssignmentType();
        if (groupedElements.containsKey(type)) {
          groupedElements.get(type).add(definition);
        } else {
          groupedElements.put(type, new HashSet<>());
          groupedElements.get(type).add(definition);
        }
      }
    }

    Collection<Group> result = new TreeSet<>(new AssignmentTypeGroupComparator());
    for (final SymbolAssignmentType key : groupedElements.keySet()) {
      result.add(new AssignmentTypeGroup(key, groupedElements.get(key)));
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
        return MathematicaBundle.message("structureview.grouper.by.type.text");
      }

      @Override
      public String getDescription() {
        return MathematicaBundle.message("structureview.grouper.by.type.description");
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

}
