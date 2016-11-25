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

package de.halirutan.mathematica.sdk;

import com.btr.proxy.util.PListParser;
import com.btr.proxy.util.PListParser.Dict;
import com.btr.proxy.util.PListParser.XmlParseException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author patrick (25.11.16).
 */
class Util {
  private static final String OS = System.getProperty("os.name").toLowerCase();

  static boolean isAccessibleDir(final String dir) {
    File rootDir = new File(dir);
    return (rootDir.exists() && rootDir.isDirectory() && rootDir.canRead());
  }

  /**
   * Cross-system version extraction
   *
   * @param rootDir the root directory of the Mathematica installation. On OSX the .app directory
   * @return version string or null
   */
  static String parseVersion(final File rootDir) {
    // On Windows and Linux, there is always a .VersionID file in the root directory of the Mathematica installation
    if (OS.contains("win") || OS.contains("nux")) {
      String versionString;
      final List<File> versionFile = FileUtil.findFilesOrDirsByMask(Pattern.compile(".*\\.VersionID"), rootDir);
      final List<File> creationFile = FileUtil.findFilesOrDirsByMask(Pattern.compile(".*\\.CreationID"), rootDir);
      if (versionFile.size() > 0 && creationFile.size() > 0) {
        try {
          final String vString = StreamUtil.readText(new FileInputStream(versionFile.get(0)), Charset.defaultCharset()).trim();
          final String bString = StreamUtil.readText(new FileInputStream(creationFile.get(0)), Charset.defaultCharset()).trim();
          versionString = vString +  "." + bString;
          return versionString;
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    } else {
      if (OS.contains("mac")) {
        final String contentsPath = rootDir.getAbsolutePath() + File.separatorChar + "Contents";
        if (!isAccessibleDir(contentsPath)) {
          return null;
        }
        File contentsDir = new File(contentsPath);
        final File pkinfo = findFileInDir(contentsDir, "Info.plist");
        if (pkinfo != null && pkinfo.canRead()) {
          try {
            final Dict dict = PListParser.load(pkinfo);
            return (String) dict.get("CFBundleShortVersionString");
          } catch (XmlParseException e) {
            e.printStackTrace();
            return null;
          } catch (IOException e) {
            e.printStackTrace();
            return null;
          }
        }
      }
    }
    return null;
  }

  private static File findFileInDir(File rootDir, final String fileName) {
    final File[] matchedFiles = rootDir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return fileName.matches(name);
      }
    });
    if (matchedFiles != null && matchedFiles.length > 0) {
      return matchedFiles[0];
    }
    return null;
  }

  static List<File> findMathematicaKernels(String homePath) {
    List<File> kernels = new ArrayList<File>();
    File rootDir = new File(homePath);
    Pattern kernelPattern;
    if (OS.contains("win")) {
      kernelPattern = Pattern.compile(".*MathKernel.exe");
    } else {
      kernelPattern = Pattern.compile(".*MathKernel");
    }
    if (rootDir.isDirectory()) {
      final List<File> foundKernels = FileUtil.findFilesByMask(kernelPattern, rootDir);
      for (File file : foundKernels) {
        if (file.canExecute())
          kernels.add(file);
      }
    }
    return kernels;
  }

}
