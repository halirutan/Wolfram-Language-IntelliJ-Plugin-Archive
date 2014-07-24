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

package de.halirutan.mathematica.codeinsight.structureview.representations;

import com.intellij.navigation.ItemPresentation;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionCollector;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Basic representation of an "assignment node" in a structure view. All different kinds of
 * assignment extend from this and they only change their Icon and probably the way the represent
 * themselves as text in the structure view.
 * @author patrick (6/20/14)
 */
public class BaseAssignmentRepresentation implements ItemPresentation {

  protected GlobalDefinitionCollector.AssignmentProperty myAssignmentProperty;

  public BaseAssignmentRepresentation(GlobalDefinitionCollector.AssignmentProperty assignmentProperty) {
    myAssignmentProperty = assignmentProperty;
  }

  @Nullable
  @Override
  public String getPresentableText() {
    return myAssignmentProperty.myLhsOfAssignment.getText();
  }


  @Nullable
  @Override
  public String getLocationString() {
    return myAssignmentProperty.myAssignmentSymbol.getText();
  }

  @Nullable
  @Override
  public Icon getIcon(final boolean unused) {
    return null;
  }
}
