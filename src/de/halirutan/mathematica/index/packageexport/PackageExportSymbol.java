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

import com.intellij.util.io.IOUtil;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 * @author patrick (01.11.16).
 */
public class PackageExportSymbol implements KeyDescriptor<PackageExportSymbol> {

  public static final PackageExportSymbol INSTANCE = new PackageExportSymbol();

  private final String myNameSpace;
  private final String mySymbol;
  private final boolean myExported;
  private final String myFileName;

  PackageExportSymbol(String filename, String nameSpace, String symbol, boolean isExport)  {
    this.myFileName = filename;
    this.myNameSpace = nameSpace;
    this.mySymbol = symbol;
    this.myExported = isExport;
  }

  private PackageExportSymbol() {
    this("", "", "", false);
  }

  public String getFileName() {
    return myFileName;
  }

  public String getNameSpace() {
    return myNameSpace;
  }

  public String getSymbol() {
    return mySymbol;
  }

  public boolean isExported() {
    return myExported;
  }

  @Override
  public void save(@NotNull DataOutput out, PackageExportSymbol value) throws IOException {
    IOUtil.writeUTF(out, value.myFileName);
    IOUtil.writeUTF(out, value.myNameSpace);
    IOUtil.writeUTF(out, value.mySymbol);
    out.writeBoolean(value.myExported);
  }

  @Override
  public PackageExportSymbol read(@NotNull DataInput in) throws IOException {
    final String filename = IOUtil.readUTF(in);
    final String namespace = IOUtil.readUTF(in);
    final String symbol = IOUtil.readUTF(in);
    final boolean exported = in.readBoolean();
    return new PackageExportSymbol(filename, namespace, symbol, exported);
  }

  @Override
  public int hashCode() {
    int hash = myExported ? 1 : 2;
    hash = hash*31 + myFileName.hashCode();
    hash = hash*31 + myNameSpace.hashCode();
    hash = hash*31 + mySymbol.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PackageExportSymbol)) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    return myExported == ((PackageExportSymbol) obj).myExported &&
        Objects.equals(mySymbol, ((PackageExportSymbol) obj).mySymbol) &&
        Objects.equals(myNameSpace, ((PackageExportSymbol) obj).myNameSpace) &&
        Objects.equals(myFileName, ((PackageExportSymbol) obj).myFileName);
  }

  @Override
  public int getHashCode(PackageExportSymbol value) {
    return hashCode();
  }

  @Override
  public boolean isEqual(PackageExportSymbol val1, PackageExportSymbol val2) {
    return val1.equals(val2);
  }
}
