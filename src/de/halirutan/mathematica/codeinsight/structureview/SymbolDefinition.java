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
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

/**
 * Class for holding information about the details of a (function)-definition. This includes: <ul> <li>PsiElement of the
 * symbol which is defined</li> <li>Symbol name</li> <li>Type of the definition: Set, SetDelayed, TagSet,
 * TagSetDelayed</li> <li>The LHS (or parts of it) as string</li> <li>Line number</li> </ul>
 *
 * @author patrick (6/20/14)
 */
public class SymbolDefinition implements StructureViewTreeElement {

  Symbol myElement;
  PsiElement mySetType;
  String myLhs;
  int myLineNumber;

  public SymbolDefinition(Symbol symbol, PsiElement setType) {
    myElement = symbol;
    mySetType = setType;
    myLhs = "";
    if (mySetType != null) {
      myLhs = mySetType.getFirstChild().getText();
      myLhs = myLhs.substring(0, Math.min(80, myLhs.length()));
    }
    myLineNumber = 0;
  }

  public PsiElement getSetType() {
    return mySetType;
  }

  public String getLhs() {
    return myLhs;
  }

  @Override
  public Object getValue() {
    return myElement;
  }

  @Override
  public void navigate(final boolean requestFocus) {
    ((Expression) myElement).navigate(requestFocus);
  }

  @Override
  public boolean canNavigate() {
    return ((Expression) myElement).canNavigate();
  }

  @Override
  public boolean canNavigateToSource() {
    return ((Expression) myElement).canNavigateToSource();
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    return new SymbolDefinitionRepresentation(this);
  }

  @NotNull
  @Override
  public TreeElement[] getChildren() {
    return new TreeElement[0];
  }
}

