/*
 * Copyright (c) 2013 Patrick Scheibe
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
import de.halirutan.mathematica.MathematicaIcons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author patrick (4/27/13)
 */
public class MathematicaSdkType extends SdkType {
  private static final Pattern PACKAGE_INIT_PATTERN = Pattern.compile(".*Kernel/init\\.m");

  public MathematicaSdkType() {
    super("MATHEMATICA SDK");
  }

  /**
   * Extracts the version from the .VersionID file for Mathematica version > 5
   *
   * @param path Path to the install directory
   * @return Version number in the format e.g. 9.0.1
   */
  public static String getMathematicaVersionString(String path) {
    File versionID = new File(path + File.separatorChar + ".VersionID");
    String versionString = "Unknown";

    try {
      if (versionID.exists()) {
        Scanner scanner = new Scanner(versionID).useDelimiter("\\A");
        if (scanner.hasNext()) versionString = scanner.next().trim();
      }
    } catch (FileNotFoundException ignored) {
    }
    return versionString;
  }

  @Nullable
  @Override
  public String suggestHomePath() {
    final String property = System.getProperty("os.name");
    String path = "";
    if (property.matches("Linux.*")) {
      path = "/usr/local/Wolfram";
    }
    if (new File(path).exists()) {
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
    return (new File(path + File.separatorChar + ".VersionID")).exists();
  }

  @Override
  public String suggestSdkName(String currentSdkName, String sdkHome) {
    return "Mathematica " + getMathematicaVersionString(sdkHome);
  }

  @Override
  public boolean isRootTypeApplicable(OrderRootType type) {
    return type.equals(OrderRootType.SOURCES) || type.equals(OrderRootType.DOCUMENTATION) || type.equals(OrderRootType.CLASSES);
  }

  @Nullable
  @Override
  public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
    return null;
  }

  @Override
  public String getPresentableName() {
    return "Mathematica Sdk";
  }

  @Override
  public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
  }

  @Override
  public Icon getIcon() {
    return MathematicaIcons.FILE_ICON;
  }

  @Override
  public Icon getIconForAddAction() {
    return MathematicaIcons.FILE_ICON;
  }

  @Override
  public boolean setupSdkPaths(Sdk sdk, SdkModel sdkModel) {
    final SdkModificator sdkModificator = sdk.getSdkModificator();
    final String homePath = sdk.getHomePath();
    sdkModificator.setVersionString(getMathematicaVersionString(homePath));
    addAddOnPackageSources(sdkModificator, homePath);
    addJLinkJars(sdkModificator, homePath);
    sdkModificator.commitChanges();
    return true;


  }

  private static void addJLinkJars(SdkModificator sdkModificator, String homePath) {

    final JarFileSystem jarFileSystem = JarFileSystem.getInstance();
    String path = homePath.replace(File.separatorChar, '/') + "/SystemFiles/Links/JLink/JLink.jar" + JarFileSystem.JAR_SEPARATOR;
    jarFileSystem.setNoCopyJarForPath(path);
    VirtualFile vFile = jarFileSystem.findFileByPath(path);
    sdkModificator.addRoot(vFile, OrderRootType.CLASSES);
  }

  private static void addAddOnPackageSources(SdkModificator sdkModificator, String homePath) {
    File addOns = new File(homePath, "AddOns");
    Pattern initMPattern = Pattern.compile(".*init\\.m");
    if (addOns.isDirectory()) {
      final List<File> initFiles = FileUtil.findFilesByMask(initMPattern, addOns);
      for (File file : initFiles) {
        if (PACKAGE_INIT_PATTERN.matcher(file.getPath()).matches()) {
          final VirtualFile packageDirectory = LocalFileSystem.getInstance().findFileByPath(file.getPath().replace("Kernel/init.m", ""));
          sdkModificator.addRoot(packageDirectory, OrderRootType.SOURCES);
        }
      }
    }
  }


}
