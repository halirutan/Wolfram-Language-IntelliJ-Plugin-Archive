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

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Group;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import de.halirutan.mathematica.codeinsight.structureview.representations.SimpleFunctionNameRepresentation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author patrick (7/21/14)
 */
public class SymbolNameGroup implements Group, StructureViewModel.ExpandInfoProvider {

  private final Collection<TreeElement> myTreeElements;
  private final String myName;

  public SymbolNameGroup(final String symbolName, final Collection<TreeElement> treeElements) {
    this.myTreeElements = treeElements;
    this.myName = symbolName;
  }

  @Override
  public boolean isAutoExpand(@NotNull final StructureViewTreeElement element) {
    return false;
  }

  @Override
  public boolean isSmartExpand() {
    return false;
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    return new SimpleFunctionNameRepresentation(myName);
  }

  @NotNull
  @Override
  public Collection<TreeElement> getChildren() {
    return myTreeElements;
  }
}
