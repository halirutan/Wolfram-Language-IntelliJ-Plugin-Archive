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
import com.intellij.navigation.ItemPresentation;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author patrick (6/14/14)
 */
public class StructureViewFileElement implements StructureViewTreeElement, StructureViewModel.ExpandInfoProvider {

  private MathematicaPsiFile myElement;

  public StructureViewFileElement(MathematicaPsiFile element) {
    myElement = element;
  }

  @Override
  public void navigate(boolean requestFocus) {
    myElement.navigate(requestFocus);

  }

  @Override
  public boolean canNavigate() {
    return myElement.canNavigate();
  }

  @Override
  public boolean canNavigateToSource() {
    return myElement.canNavigateToSource();
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    //noinspection OverlyComplexAnonymousInnerClass
    return new ItemPresentation() {
      @Nullable
      @Override
      public String getPresentableText() {
        return myElement.getName();
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
  public StructureViewTreeElement[] getChildren() {
    GlobalDefinitionCollector collector = new GlobalDefinitionCollector(myElement.getContainingFile());
    final Map<String, HashSet<GlobalDefinitionCollector.AssignmentProperty>> assignments = collector.getAssignments();
    final Collection<AssignmentSymbolNodeViewTreeElement> children = new ArrayList<AssignmentSymbolNodeViewTreeElement>(assignments.size());

    for (String key : assignments.keySet()) {
      children.add(new AssignmentSymbolNodeViewTreeElement(key, assignments.get(key)));
    }

    return children.toArray(new AssignmentSymbolNodeViewTreeElement[children.size()]);
  }

  @Override
  public Expression getValue() {
    return myElement;
  }

  @Override
  public boolean isAutoExpand(@NotNull final StructureViewTreeElement element) {
    return false;
  }

  @Override
  public boolean isSmartExpand() {
    return true;
  }

}
