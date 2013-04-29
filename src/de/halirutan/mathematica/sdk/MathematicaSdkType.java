package de.halirutan.mathematica.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import de.halirutan.mathematica.MathematicaIcons;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author patrick (4/27/13)
 */
public class MathematicaSdkType extends SdkType {
    private static final String DOC_URL = "http://reference.wolfram.com/mathematica/guide/Mathematica.html";
    private final String version = null;

    public MathematicaSdkType() {
        super("MATHEMATICA SDK");
    }

    /**
     * Extracts the version from the .VersionID file for Mathematica version > 5
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
        String path = "/usr/local/Wolfram";
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
    public String getVersionString(Sdk sdk) {
        return getMathematicaVersionString(sdk.getHomePath());
    }

    @Override
    public boolean isValidSdkHome(String path) {
        boolean valid = (new File(path + File.separatorChar + ".VersionID")).exists();
        return valid;
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return "Mathematica " + getMathematicaVersionString(sdkHome);
    }

    @Override
    public boolean isRootTypeApplicable(OrderRootType type) {
        return true;
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
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
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
        addInternalDocumentationNotebooks(sdkModificator, homePath);

        return true;


    }

    private void addJLinkJars(SdkModificator sdkModificator, String homePath) {
        VirtualFile jLinkJar = JarFileSystem.getInstance().findLocalVirtualFileByPath(homePath + "/SystemFiles/Links/JLink/JLink.jar");
        sdkModificator.addRoot(jLinkJar, OrderRootType.CLASSES);
    }

    private void addAddOnPackageSources(SdkModificator sdkModificator, String homePath) {
    }

    private void addInternalDocumentationNotebooks(SdkModificator sdkModificator, String homePath) {

    }

    private static String convertFileName(String input) {
        return input.replace("/", File.pathSeparator);
    }

}
