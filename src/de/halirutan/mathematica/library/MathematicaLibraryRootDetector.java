/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.library;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.ui.RootDetector;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.CommonProcessors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provides functionality to detect the root directories of valid Mathematica source libraries
 *
 * @author patrick (25.11.17).
 */
public class MathematicaLibraryRootDetector extends RootDetector {
  MathematicaLibraryRootDetector(OrderRootType rootType, boolean jarDirectory, String presentableRootTypeName) {
    super(rootType, jarDirectory, presentableRootTypeName);
  }

  /**
   * Provides a way to select directories, starting from some root, that are likely to be the parent-directory of
   * package sources. We test for each directory if either there is a PacletInfo.m in it or if it contains a package
   * file that has the same name as the directory itself.
   * <p>
   * If the user selects a directory that contains several packages, we collect all valid package dirs.
   *
   * @param dir Starting directory for the package search. In the best case, it is the root of the package itself
   *
   * @return A list of found packages.
   */
  private static Collection<VirtualFile> collectPackageDirs(final VirtualFile dir) {
    if (!dir.isDirectory()) {
      return Collections.emptyList();
    }

    CommonProcessors.CollectProcessor<VirtualFile> paclets = new CommonProcessors.CollectProcessor<VirtualFile>() {
      @Override
      public boolean accept(VirtualFile virtualFile) {
        if (virtualFile.isDirectory()) {
          final String dirName = virtualFile.getName();
          final VirtualFile[] children = virtualFile.getChildren();
          for (VirtualFile child : children) {
            if (!child.isDirectory() && ("PacletInfo.m".equals(child.getName()) ||
                (dirName.equals(child.getNameWithoutExtension()) && "m".equals(child.getExtension())))) {
              return true;
            }
          }
        }
        return false;
      }
    };
    VfsUtil.processFilesRecursively(dir, paclets);
    return paclets.getResults();
  }

  @NotNull
  @Override
  public Collection<VirtualFile> detectRoots(@NotNull VirtualFile rootCandidate, @NotNull ProgressIndicator progressIndicator) {
    final List<VirtualFile> result = new ArrayList<>();
    final Collection<VirtualFile> packageDirectories = collectPackageDirs(rootCandidate);
    if (rootCandidate.getFileSystem() instanceof JarFileSystem || packageDirectories.isEmpty()) {
      return result;
    }
    result.addAll(packageDirectories);
    return result;
  }

}
