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

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * @author patrick (7/20/14)
 */
public class AssignmentSymbolNodeViewTreeElement implements StructureViewTreeElement {

  private final String mySymbolName;
  private final PsiElement myNavigationElement;
  private final HashSet<GlobalDefinitionCollector.AssignmentProperty> myAssignments;


  public AssignmentSymbolNodeViewTreeElement(final String symbolName, final HashSet<GlobalDefinitionCollector.AssignmentProperty> assignments) {
    mySymbolName = symbolName;
    myAssignments = assignments;
    myNavigationElement = myAssignments.iterator().next().myAssignmentSymbol;
  }

  @Override
  public Object getValue() {
    return myNavigationElement;
  }

  @Override
  public void navigate(final boolean requestFocus) {
    ((Expression) myNavigationElement).navigate(requestFocus);

  }

  @Override
  public boolean canNavigate() {
    return ((Expression) myNavigationElement).canNavigate();
  }

  @Override
  public boolean canNavigateToSource() {
    return ((Expression) myNavigationElement).canNavigateToSource();
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    return new ItemPresentation() {
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
    };
  }

  @NotNull
  @Override
  public TreeElement[] getChildren() {
    TreeSet<AssignmentLeafViewTreeElement> result = new TreeSet<AssignmentLeafViewTreeElement>(new TextPositionComparator());
    for (GlobalDefinitionCollector.AssignmentProperty assignment : myAssignments) {
      result.add(new AssignmentLeafViewTreeElement(assignment));
    }
    return result.toArray(new TreeElement[result.size()]);
  }


}
