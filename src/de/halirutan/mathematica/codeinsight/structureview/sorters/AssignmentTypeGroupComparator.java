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

package de.halirutan.mathematica.codeinsight.structureview.sorters;

import com.intellij.ide.util.treeView.smartTree.Group;
import com.intellij.ide.util.treeView.smartTree.SorterUtil;
import de.halirutan.mathematica.codeinsight.structureview.groupers.AssignmentTypeGroup;

import java.util.Comparator;

/**
 * Provides functionality to sort the assignment-type nodes in the structure-view correctly
 *
 * @author patrick (7/24/14)
 */
public class AssignmentTypeGroupComparator implements Comparator<Group> {

  @Override
  public int compare(final Group o1, final Group o2) {
    if (o1 instanceof AssignmentTypeGroup && o2 instanceof AssignmentTypeGroup) {
      return ((AssignmentTypeGroup) o1).getPosition() - ((AssignmentTypeGroup) o2).getPosition();
    }
    String s1 = SorterUtil.getStringPresentation(o1);
    String s2 = SorterUtil.getStringPresentation(o2);
    return s1.compareToIgnoreCase(s2);
  }
}


