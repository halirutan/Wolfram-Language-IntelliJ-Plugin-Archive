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

package de.halirutan.mathematica.index.export;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.HashMap;
import com.intellij.util.indexing.*;
import com.intellij.util.indexing.FileBasedIndex.InputFilter;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.IOUtil;
import com.intellij.util.io.KeyDescriptor;
import de.halirutan.mathematica.index.export.MathematicaPackageExportIndex.Key;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author patrick (01.11.16).
 */
public class MathematicaPackageExportIndex extends FileBasedIndexExtension<Key, List<PackageExportSymbol>> {

  public static final ID<Key,List<PackageExportSymbol>> INDEX_ID = ID.create("Mathematica.fileExports");
  private static final int BASE_VERSION = 2;

  @NotNull
  @Override
  public InputFilter getInputFilter() {
    return new InputFilter() {
      @Override
      public boolean acceptInput(@NotNull VirtualFile file) {
        return "m".equals(file.getExtension());
      }
    };
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }

  @NotNull
  @Override
  public ID<Key, List<PackageExportSymbol>> getName() {
    return INDEX_ID;
  }

  @NotNull
  @Override
  public DataIndexer<Key, List<PackageExportSymbol>, FileContent> getIndexer() {
    return new DataIndexer<Key, List<PackageExportSymbol>, FileContent>() {
      @NotNull
      @Override
      public Map<Key, List<PackageExportSymbol>> map(@NotNull FileContent inputData) {
        final Map<Key, List<PackageExportSymbol>> map = new HashMap<Key, List<PackageExportSymbol>>();
        if (!"m".equals(inputData.getFile().getExtension())) return map;
        final PsiFile psiFile = inputData.getPsiFile();
        PackageClassifier visitor = new PackageClassifier();
        psiFile.accept(visitor);
        map.put(new FileKey(inputData.getFile()), new ArrayList<PackageExportSymbol>(visitor.getListOfExportSymbols()));
        return map;
      }
    };
  }

  @NotNull
  @Override
  public KeyDescriptor<Key> getKeyDescriptor() {
    //noinspection AnonymousInnerClassWithTooManyMethods,OverlyComplexAnonymousInnerClass
    return new KeyDescriptor<Key>() {
      @Override
      public int getHashCode(Key value) {
        return value.hashCode();
      }

      @Override
      public boolean isEqual(Key val1, Key val2) {
        return val1.equals(val2);
      }

      @Override
      public void save(@NotNull DataOutput out, Key value) throws IOException {
        value.writeValue(out);
      }

      @Override
      public Key read(@NotNull DataInput in) throws IOException {
        return new FileKey(in.readInt());
      }
    };
  }

  @NotNull
  @Override
  public DataExternalizer<List<PackageExportSymbol>> getValueExternalizer() {
    return new ListDataExternalizer();
  }

  @Override
  public int getVersion() {
    return BASE_VERSION;
  }

  public interface Key {
    void writeValue(DataOutput out) throws IOException;
  }

  public static class FileKey implements Key {
    private final int myFileId;
    private String myFileName = "";

    private FileKey(int fileId) {
      myFileId = fileId;
    }

    private FileKey(VirtualFile file) {
      myFileId = FileBasedIndex.getFileId(file);
      myFileName = file.getName();
    }

    public String getFileName() {
      return myFileName;
    }

    public int getFileId() {
      return myFileId;
    }

    @Override
    public void writeValue(DataOutput out) throws IOException {
      out.writeInt(myFileId);
    }

    @Override
    public int hashCode() {
      return myFileId;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof FileKey && ((FileKey)obj).myFileId == myFileId;
    }
  }

  private static class ListDataExternalizer implements DataExternalizer<List<PackageExportSymbol>> {
    @Override
    public void save(@NotNull DataOutput out, List<PackageExportSymbol> value) throws IOException {
      out.writeInt(value.size());
      for (PackageExportSymbol info : value) {
        IOUtil.writeUTF(out, info.nameSpace);
        IOUtil.writeUTF(out, info.symbol);
      }
    }

    @Override
    public List<PackageExportSymbol> read(@NotNull DataInput in) throws IOException {
      int size = in.readInt();
      ArrayList<PackageExportSymbol> info = new ArrayList<PackageExportSymbol>(size);
      for (int i = 0; i < size; i++) {
        info.add(new PackageExportSymbol(IOUtil.readUTF(in), IOUtil.readUTF(in)));
      }
      return info;
    }
  }
}
