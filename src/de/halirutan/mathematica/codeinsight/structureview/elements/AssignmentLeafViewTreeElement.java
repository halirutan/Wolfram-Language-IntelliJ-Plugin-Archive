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

package de.halirutan.mathematica.codeinsight.structureview.elements;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.codeinsight.structureview.representations.*;
import de.halirutan.mathematica.codeinsight.structureview.sorters.CodePlaceProvider;
import de.halirutan.mathematica.lang.psi.SymbolAssignmentType;
import de.halirutan.mathematica.lang.resolve.GlobalDefinitionCollector.AssignmentProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * The leafs of all structure view trees. A single assignment is always at the end of a structure view.
 * @author patrick (7/20/14)
 */
public class AssignmentLeafViewTreeElement extends PsiTreeElementBase<PsiElement> implements SortableTreeElement, CodePlaceProvider {

  private final AssignmentProperty myAssignment;

  AssignmentLeafViewTreeElement(final AssignmentProperty assignment) {
    super(assignment.myAssignmentSymbol);
    myAssignment = assignment;
  }

  public SymbolAssignmentType getAssignmentType() {
    return myAssignment.myAssignmentType;
  }


  @Override
  public int getPosition() {
    return myAssignment.myAssignmentSymbol != null ? myAssignment.myAssignmentSymbol.getTextOffset() : 0;
  }

  @Override
  public PsiElement getValue() {
    final PsiElement myAssignmentSymbol = myAssignment.myAssignmentSymbol;
    return myAssignmentSymbol.isValid() ? myAssignmentSymbol : null;
  }

  @Override
  public void navigate(final boolean requestFocus) {
    final PsiElement symbol = myAssignment.myAssignmentSymbol;
    if (symbol != null) {
      ((Navigatable) symbol).navigate(requestFocus);
    }
  }

  @Override
  public boolean canNavigate() {
    final PsiElement symbol = myAssignment.myAssignmentSymbol;
    if (symbol != null) {
      return ((Navigatable) symbol).canNavigate();
    }
    return false;
  }

  @Override
  public boolean canNavigateToSource() {
    final PsiElement symbol = myAssignment.myAssignmentSymbol;
    if (symbol != null) {
      return ((Navigatable) symbol).canNavigateToSource();
    }
    return false;
  }

  @NotNull
  @Override
  public Collection<StructureViewTreeElement> getChildrenBase() {
    return Collections.emptyList();
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    switch (myAssignment.myAssignmentType) {
      case SET_DELAYED_ASSIGNMENT:
        return new SetDelayedAssignmentRepresentation(myAssignment);
      case SET_ASSIGNMENT:
        return new SetAssignmentRepresentation(myAssignment);
      case OPTIONS_ASSIGNMENT:
        return new OptionsRepresentation(myAssignment);
      case ATTRIBUTES_ASSIGNMENT:
        return new AttributesRepresentation(myAssignment);
      case DEFAULT_ASSIGNMENT:
        return new DefaultValuesRepresentation(myAssignment);
      case FORMAT_ASSIGNMENT:
        return new FormatValuesRepresentation(myAssignment);
      case MESSAGE_ASSIGNMENT:
        return new MessagesRepresentation(myAssignment);
      case N_ASSIGNMENT:
        return new NValuesRepresentation(myAssignment);
      case SYNTAX_INFORMATION_ASSIGNMENT:
        return new SyntaxInformationRepresentation(myAssignment);
      case TAG_SET_ASSIGNMENT:
        return new TagSetAssignmentRepresentation(myAssignment);
      case TAG_SET_DELAYED_ASSIGNMENT:
        return new TagSetDelayedAssignmentRepresentation(myAssignment);
      case UP_SET_ASSIGNMENT:
        return new UpSetAssignmentRepresentation(myAssignment);
      case UP_SET_DELAYED_ASSIGNMENT:
        return new UpSetDelayedAssignmentRepresentation(myAssignment);
      default:
        return new BaseAssignmentRepresentation(myAssignment);
    }
  }

  @NotNull
  @Override
  public String getAlphaSortKey() {
    return myAssignment.myAssignmentSymbol.getText() + getTypeSortKey();
  }

  private String getTypeSortKey() {
    switch (myAssignment.myAssignmentType) {
      case SET_DELAYED_ASSIGNMENT:
        return "130";
      case SET_ASSIGNMENT:
        return "120";
      case OPTIONS_ASSIGNMENT:
        return "110";
      case ATTRIBUTES_ASSIGNMENT:
        return "115";
      case DEFAULT_ASSIGNMENT:
        return "200";
      case FORMAT_ASSIGNMENT:
        return "500";
      case MESSAGE_ASSIGNMENT:
        return "100";
      case N_ASSIGNMENT:
        return "510";
      case SYNTAX_INFORMATION_ASSIGNMENT:
        return "520";
      case TAG_SET_ASSIGNMENT:
        return "140";
      case TAG_SET_DELAYED_ASSIGNMENT:
        return "150";
      case UP_SET_ASSIGNMENT:
        return "160";
      case UP_SET_DELAYED_ASSIGNMENT:
        return "170";
      default:
        return "";
    }

  }

  @Nullable
  @Override
  public String getPresentableText() {
    return "Assignment";
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof AssignmentLeafViewTreeElement) {
      final AssignmentLeafViewTreeElement leaf = (AssignmentLeafViewTreeElement) o;
      return leaf.getValue().equals(getValue());
    }
    return false;
  }


}
