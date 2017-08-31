///*
// * Copyright (c) 2016 Patrick Scheibe
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//
//package de.halirutan.mathematica.actions;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.actionSystem.DataKeys;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.psi.search.GlobalSearchScope;
//import com.intellij.util.indexing.FileBasedIndex;
//import com.intellij.util.indexing.ID;
//import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex;
//import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex.FileKey;
//import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex.Key;
//import de.halirutan.mathematica.index.packageexport.PackageExportSymbol;
//
//import java.util.Collection;
//import java.util.MList;
//
///**
// * @author patrick (08.11.16).
// */
//public class PackageExportChecker extends AnAction {
//
//  @Override
//  public void actionPerformed(AnActionEvent e) {
//
//    final Project project = e.getProject();
//    if (project == null) {
//      return;
//    }
//
//    final VirtualFile currentFile = e.getDataContext().getData(DataKeys.VIRTUAL_FILE);
//    final FileBasedIndex fileBasedIndex = FileBasedIndex.getInstance();
//    final ID<Key, MList<PackageExportSymbol>> indexId = MathematicaPackageExportIndex.INDEX_ID;
//    final Collection<Key> allKeys = fileBasedIndex.getAllKeys(indexId,project);
//
//    for (Key next : allKeys) {
//      final VirtualFile file = fileBasedIndex.findFileById(project, ((FileKey) next).getFileId());
//      System.out.printf("\nFILE: " + file.getPresentableName() + "\n-----------------------------------------\n");
//      final MList<MList<PackageExportSymbol>> values = fileBasedIndex.getValues(indexId, next, GlobalSearchScope.allScope(project));
//      for (MList<PackageExportSymbol> list : values) {
//        for (PackageExportSymbol info : list) {
//          System.out.println(info.symbol + " (" + info.nameSpace +")");
//        }
//      }
//    }
//  }
//}
