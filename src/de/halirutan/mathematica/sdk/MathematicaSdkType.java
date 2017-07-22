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

package de.halirutan.mathematica.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.util.MathematicaIcons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides a way to systematically extract information like version, AddOns packages, and JLink.jar location from a
 * Mathematica installation. I don't support versions < 8.
 *
 * @author patrick (4/27/13)
 */
public class MathematicaSdkType extends SdkType {

  private static final Pattern PACKAGE_INIT_PATTERN = Pattern.compile(".*Kernel/init\\.m");
  private static final String OS = System.getProperty("os.name").toLowerCase();

  private MathematicaSdkType() {
    super("Mathematica SDK");
  }

  @NotNull
  public static MathematicaSdkType getInstance() {
    return SdkType.findInstance(MathematicaSdkType.class);
  }

  /**
   * Extracts the version from the .VersionID file for Mathematica version > 5
   *
   * @param path Path to the install directory
   * @return Version number in the format e.g. 9.0.1
   */
  private static String getMathematicaVersionString(String path) {
    if (Util.isAccessibleDir(path)) {
      File rootDir = new File(path);
      return Util.parseVersion(rootDir);
    }
    return null;
  }


  private static void addJLinkJars(SdkModificator sdkModificator, String homePath) {

    final JarFileSystem jarFileSystem = JarFileSystem.getInstance();

    Pattern jlinkPattern = Pattern.compile(".*JLink.jar");
    List<File> jlinkFiles = FileUtil.findFilesByMask(jlinkPattern, new File(homePath));
    for (File jlinkFile : jlinkFiles) {
      jarFileSystem.setNoCopyJarForPath(jlinkFile.getAbsolutePath() + JarFileSystem.JAR_SEPARATOR);
      VirtualFile vFile = jarFileSystem.findFileByPath(jlinkFile.getAbsolutePath() + JarFileSystem.JAR_SEPARATOR);
      sdkModificator.addRoot(vFile, OrderRootType.CLASSES);
    }
  }

  private static void addAddOnPackageSources(SdkModificator sdkModificator, String homePath) {
    String addOnsPath = homePath + File.separatorChar + "AddOns";
    File addOnsFile;
    if (OS.contains("mac") && !Util.isAccessibleDir(addOnsPath)) {
      addOnsFile = new File(homePath + File.separatorChar + "Contents/AddOns");
    } else {
      addOnsFile = new File(addOnsPath);
    }
    Pattern initMPattern = Pattern.compile(".*init\\.m");
    if (addOnsFile.isDirectory()) {
      final List<File> initFiles = FileUtil.findFilesByMask(initMPattern, addOnsFile);
      for (File file : initFiles) {
        if (PACKAGE_INIT_PATTERN.matcher(file.getPath()).matches()) {
          final VirtualFile packageDirectory = LocalFileSystem.getInstance().findFileByPath(file.getPath().replace("Kernel/init.m", ""));
          sdkModificator.addRoot(packageDirectory, OrderRootType.SOURCES);
        }
      }
    }
  }


  @Nullable
  @Override
  public String suggestHomePath() {
    String path = "";
    if (OS.contains("nux")) {
      path = "/usr/local/Wolfram";
    } else if (OS.contains("win")) {
      path = "C:";
    } else if (OS.contains("mac")) {
      path = "/Applications";
    }
    if (Util.isAccessibleDir(path)) {
      return path;
    }
    return null;
  }

  @Nullable
  @Override
  public String getVersionString(String sdkHome) {
    return getMathematicaVersionString(sdkHome);
  }

  @Nullable
  @Override
  public String getVersionString(@NotNull Sdk sdk) {
    return getMathematicaVersionString(sdk.getHomePath());
  }

  @Override
  public boolean isValidSdkHome(String path) {
    String kernelLocation = "";
    if (OS.contains("win")) {
      kernelLocation = path + File.separatorChar + "MathKernel.exe";
    } else if (OS.contains("nux")) {
      kernelLocation = path + File.separatorChar + "Executables" + File.separatorChar + "MathKernel";
    } else if (OS.contains("mac")) {
      kernelLocation = path + File.separatorChar + "Contents/MacOS/MathKernel";
    }
    File kernel = new File(kernelLocation);
    return kernel.exists() && kernel.canExecute();

  }

  @Override
  public String suggestSdkName(String currentSdkName, String sdkHome) {
    return "Mathematica " + getMathematicaVersionString(sdkHome);
  }

  @Override
  public boolean isRootTypeApplicable(@NotNull OrderRootType type) {
    return type.equals(OrderRootType.SOURCES) || type.equals(OrderRootType.DOCUMENTATION) || type.equals(OrderRootType.CLASSES);
  }

  @Nullable
  @Override
  public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
    return null;
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return "Mathematica SDK";
  }

  @Override
  public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {

  }

  @Override
  public Icon getIcon() {
    return MathematicaIcons.FILE_ICON;
  }

  @NotNull
  @Override
  public Icon getIconForAddAction() {
    return MathematicaIcons.FILE_ICON;
  }

  @Override
  public boolean setupSdkPaths(@NotNull Sdk sdk, @NotNull SdkModel sdkModel) {
    final SdkModificator sdkModificator = sdk.getSdkModificator();
    final String homePath = sdk.getHomePath();
    sdkModificator.setVersionString(getMathematicaVersionString(homePath));
    addAddOnPackageSources(sdkModificator, homePath);
    addJLinkJars(sdkModificator, homePath);
    sdkModificator.commitChanges();
    return true;


  }


}
