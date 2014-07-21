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

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.codeinsight.structureview.representations.*;
import de.halirutan.mathematica.parsing.psi.SymbolAssignmentType;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionCollector;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (7/20/14)
 */
public class AssignmentLeafViewTreeElement implements
    StructureViewTreeElement, SortableTreeElement {

  private final GlobalDefinitionCollector.AssignmentProperty myAssignment;

  public AssignmentLeafViewTreeElement(final GlobalDefinitionCollector.AssignmentProperty assignment) {
    this.myAssignment = assignment;
  }

  @Override
  public Object getValue() {
    final PsiElement myAssignmentSymbol = myAssignment.myAssignmentSymbol;
    return myAssignmentSymbol.isValid() ? myAssignmentSymbol : null;
  }

  @Override
  public void navigate(final boolean requestFocus) {
    ((Expression) myAssignment.myAssignmentSymbol).navigate(requestFocus);
  }

  @Override
  public boolean canNavigate() {
    return ((Expression) myAssignment.myAssignmentSymbol).canNavigate();
  }

  @Override
  public boolean canNavigateToSource() {
    return ((Expression) myAssignment.myAssignmentSymbol).canNavigateToSource();
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
  public TreeElement[] getChildren() {
    return StructureViewTreeElement.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getAlphaSortKey() {
    return myAssignment.myAssignmentSymbol.getText() + getTypeSortKey(myAssignment.myAssignmentType);
  }

  private String getTypeSortKey(final SymbolAssignmentType type) {
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

//  @Override
//  public boolean isAutoExpand(@NotNull final StructureViewTreeElement element) {
//    return true;
//  }
//
//  @Override
//  public boolean isSmartExpand() {
//    return true;
//  }
}