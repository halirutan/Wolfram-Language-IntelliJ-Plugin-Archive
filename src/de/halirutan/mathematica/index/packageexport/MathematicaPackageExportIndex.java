/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.index.packageexport;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.HashMap;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex.InputFilter;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.KeyDescriptor;
import de.halirutan.mathematica.lang.MathematicaLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * Simple file index for functions that are exported from a package by giving them a usage message.
 * @author patrick (01.11.16).
 */
public class MathematicaPackageExportIndex extends ScalarIndexExtension<PackageExportSymbol> {

  public static final ID<PackageExportSymbol, Void> INDEX_ID = ID.create("Mathematica.fileExports");
  private static final int BASE_VERSION = 9;

  @NotNull
  @Override
  public InputFilter getInputFilter() {
    return file -> {
      final LanguageFileType fileType = MathematicaLanguage.INSTANCE.getAssociatedFileType();
      return file.getFileType().equals(fileType);
    };
  }

  @Override
  public boolean indexDirectories() {
    return false;
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }

  @NotNull
  @Override
  public ID<PackageExportSymbol, Void> getName() {
    return INDEX_ID;
  }

  @NotNull
  @Override
  public DataIndexer<PackageExportSymbol, Void, FileContent> getIndexer() {
    return inputData -> {
      final Map<PackageExportSymbol , Void> map = new HashMap<>();
      final PsiFile psiFile = inputData.getPsiFile();
      PackageClassifier visitor = new PackageClassifier();
      psiFile.accept(visitor);
      final Collection<PackageExportSymbol> listOfExportSymbols = visitor.getMyExportInfo();

      for (PackageExportSymbol symbol : listOfExportSymbols) {
        map.putIfAbsent(symbol, null);
      }
      return map;
    };
  }

  @NotNull
  @Override
  public KeyDescriptor<PackageExportSymbol> getKeyDescriptor() {
    return PackageExportSymbol.INSTANCE;
  }

  @Override
  public int getVersion() {
    return BASE_VERSION;
  }

}
