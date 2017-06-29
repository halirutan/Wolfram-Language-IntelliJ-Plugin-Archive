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

package de.halirutan.mathematica.codeinsight.structureview.sorters;

import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.MathematicaIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Comparator;

/**
 * Provides a way to sort assignments by there appearance in the source file. Basically, this is
 * sorting by line-number of the assignment.
 * @see {@link CodePlaceProvider}
 * @author patrick (7/21/14)
 */
public class CodePlaceSorter implements Sorter {

  public static final CodePlaceSorter INSTANCE = new CodePlaceSorter();
  private static final String CODE_SORTER_ID = "CODE_PLACE_COMPARATOR";

  private CodePlaceSorter() {
  }

  @Override
  public Comparator getComparator() {
    return (o1, o2) -> {
      if (o1 instanceof CodePlaceProvider && o2 instanceof CodePlaceProvider) {
        return ((CodePlaceProvider) o1).getPosition() - ((CodePlaceProvider) o2).getPosition();
      }
      return 0;
    };
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  @NotNull
  @Override
  public ActionPresentation getPresentation() {
    return new ActionPresentation() {
      @NotNull
      @Override
      public String getText() {
        return MathematicaBundle.message("structureview.sorter.by.appearance.text");
      }

      @Override
      public String getDescription() {
        return MathematicaBundle.message("structureview.sorter.by.appearance.description");
      }

      @Override
      public Icon getIcon() {
        return MathematicaIcons.SORT_BY_TYPE_APPEARANCE;
      }
    };
  }

  @NotNull
  @Override
  public String getName() {
    return CODE_SORTER_ID;
  }
}
