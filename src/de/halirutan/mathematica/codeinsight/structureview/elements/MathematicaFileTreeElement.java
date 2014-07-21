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

package de.halirutan.mathematica.codeinsight.structureview.elements;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import de.halirutan.mathematica.parsing.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

/**
 * @author patrick (6/14/14)
 */
public class MathematicaFileTreeElement extends PsiTreeElementBase<MathematicaPsiFile> implements ItemPresentation {

  private MathematicaPsiFile myElement;

  public MathematicaFileTreeElement(final MathematicaPsiFile psiElement) {
    super(psiElement);
    myElement = psiElement;
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
  public Collection<StructureViewTreeElement> getChildrenBase() {
    if (!myElement.isValid()) {
      return Collections.emptyList();
    }
    GlobalDefinitionCollector collector = new GlobalDefinitionCollector(myElement.getContainingFile());
    final Map<String, HashSet<GlobalDefinitionCollector.AssignmentProperty>> assignments = collector.getAssignments();
    final Collection<StructureViewTreeElement> children = new HashSet<StructureViewTreeElement>(assignments.size());

    for (String key : assignments.keySet())
    {
      final HashSet<GlobalDefinitionCollector.AssignmentProperty> assignmentProperties = assignments.get(key);
      for (GlobalDefinitionCollector.AssignmentProperty assignmentProperty : assignmentProperties) {
        children.add(new AssignmentLeafViewTreeElement(assignmentProperty));
      }
    }
    return children;
  }

  @Nullable
  @Override
  public String getPresentableText() {
    return myElement.getText();
  }

}
