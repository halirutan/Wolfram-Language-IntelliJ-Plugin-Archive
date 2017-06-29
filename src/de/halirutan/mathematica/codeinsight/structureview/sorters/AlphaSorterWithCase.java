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

import com.intellij.icons.AllIcons.ObjectBrowser;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.ide.util.treeView.smartTree.SorterUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Szabolcs pointed out that in Mathematica, functions starting with upper case are usually the ones
 * that get exported from a package. Therefore, and until I have a reliable way to extract the contexts
 * from a package file, I will sort the entries in the structure view alphabetically, but taking the
 * capitalization into account.
 * @author patrick (7/24/14)
 */
public class AlphaSorterWithCase implements Sorter {

  public static final AlphaSorterWithCase INSTANCE = new AlphaSorterWithCase();

  @Override
  public Comparator getComparator() {
    return (o1, o2) -> {
      String s1 = SorterUtil.getStringPresentation(o1);
      String s2 = SorterUtil.getStringPresentation(o2);
      return s1.compareTo(s2);
    };
  }

  @Override
  public boolean isVisible() {
    return true;
  }

  public String toString() {
    return getName();
  }

  @Override
  @NotNull
  public ActionPresentation getPresentation() {
    return new ActionPresentationData(IdeBundle.message("action.sort.alphabetically"),
        IdeBundle.message("action.sort.alphabetically"),
        ObjectBrowser.Sorted);
  }

  @Override
  @NotNull
  public String getName() {
    return ALPHA_SORTER_ID;
  }
}


