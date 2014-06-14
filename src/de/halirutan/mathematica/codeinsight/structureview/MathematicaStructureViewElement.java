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
import com.intellij.navigation.ItemPresentation;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.util.MathematicaTopLevelFunctionVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author patrick (6/14/14)
 */
public class MathematicaStructureViewElement implements StructureViewTreeElement {

  protected enum Visibility {
    GLOBAL,
    LOCAL,
    NONE
  }

  private Expression myElement;
  private Visibility myVisibility;

  public MathematicaStructureViewElement(Expression element) {
    this(element, Visibility.NONE);
  }

  public MathematicaStructureViewElement(Expression element, Visibility visibility) {
    myElement = element;
    myVisibility = visibility;
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
        if (myElement instanceof Symbol) {
          return ((Symbol) myElement).getSymbolName();
        }
        if (myElement instanceof MathematicaPsiFile) {
          return ((MathematicaPsiFile) myElement).getName();
        }
        return "ERROR";

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
    final Collection<StructureViewTreeElement> children = new ArrayList<StructureViewTreeElement>();
    final MathematicaTopLevelFunctionVisitor functionVisitor = new MathematicaTopLevelFunctionVisitor();
    if (myElement instanceof MathematicaPsiFile) {
      myElement.accept(functionVisitor);
      for (Symbol sym : functionVisitor.getAssignedSymbols()) {
        children.add(new MathematicaStructureViewElement((Expression) sym));
      }
    }
    return children.toArray(new StructureViewTreeElement[children.size()]);
  }

  @Override
  public Expression getValue() {
    return myElement;
  }
}
