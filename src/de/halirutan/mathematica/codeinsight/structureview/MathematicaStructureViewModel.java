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

package de.halirutan.mathematica.codeinsight.structureview;

import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.codeinsight.structureview.elements.AssignmentLeafViewTreeElement;
import de.halirutan.mathematica.codeinsight.structureview.elements.MathematicaFileTreeElement;
import de.halirutan.mathematica.codeinsight.structureview.groupers.AssignmentTypeGrouper;
import de.halirutan.mathematica.codeinsight.structureview.groupers.SymbolNameGrouper;
import de.halirutan.mathematica.codeinsight.structureview.sorters.AlphaSorterWithCase;
import de.halirutan.mathematica.codeinsight.structureview.sorters.CodePlaceSorter;
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (6/13/14)
 */
class MathematicaStructureViewModel extends TextEditorBasedStructureViewModel
    implements ElementInfoProvider {

  private static final Grouper[] ourGroupers;

  private final MathematicaPsiFile myRootElement;

  static {
    ourGroupers = new Grouper[]{
        new AssignmentTypeGrouper(),
        new SymbolNameGrouper()
    };
  }

  private Editor myEditor;


  public MathematicaStructureViewModel(@NotNull MathematicaPsiFile psiFile, @Nullable Editor editor) {
    super(editor, psiFile);
    myRootElement = psiFile;
    myEditor = editor;
  }

  @NotNull
  @Override
  public Sorter[] getSorters() {
    return new Sorter[]{
        AlphaSorterWithCase.INSTANCE,
        CodePlaceSorter.INSTANCE
    };
  }

  @Override
  public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
    return element instanceof MathematicaFileTreeElement;
//    return false;
  }

  @Override
  public boolean isAlwaysLeaf(StructureViewTreeElement element) {
    return element instanceof AssignmentLeafViewTreeElement;
  }

  @NotNull
  @Override
  public Filter[] getFilters() {
    return new Filter[0];
  }

  @Override
  public Object getCurrentEditorElement() {
    if (myEditor == null) return null;
    final int offset = myEditor.getCaretModel().getOffset();
    final PsiFile file = getPsiFile();
    final PsiElement element = file.getViewProvider().findElementAt(offset, file.getLanguage());
    final PsiElement parent = element != null ? element.getParent() : null;
    if (parent instanceof Symbol) {
      return parent;
    }
    return null;
  }

  @NotNull
  @Override
  public StructureViewTreeElement getRoot() {
    return new MathematicaFileTreeElement(myRootElement);
  }

  @NotNull
  @Override
  public Grouper[] getGroupers() {
    return ourGroupers;
  }


}
