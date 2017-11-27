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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.roots.libraries.ui.LibraryRootsComponentDescriptor;
import com.intellij.openapi.roots.ui.configuration.FacetsProvider;
import com.intellij.openapi.vfs.VirtualFile;
import de.halirutan.mathematica.MathematicaBundle;
import de.halirutan.mathematica.module.MathematicaModuleType;
import de.halirutan.mathematica.util.MathematicaIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Basic implementation of our custom Mathematica source libraries.
 *
 * @author patrick (25.11.17).
 */
public class MathematicaLibraryType extends LibraryType<DummyLibraryProperties> {

  private static final PersistentLibraryKind<DummyLibraryProperties> MMA_LIBRARY =
      new PersistentLibraryKind<DummyLibraryProperties>("Mathematica") {
        @NotNull
        @Override
        public DummyLibraryProperties createDefaultProperties() {
          return DummyLibraryProperties.INSTANCE;
        }
      };

  protected MathematicaLibraryType() {
    super(MMA_LIBRARY);
  }

  /**
   * Provides the text that is shown when the user clicks on "add library" and gets the selection box for different
   * library types.
   *
   * @return Name of the create Mathematica library option
   */
  @Nullable
  @Override
  public String getCreateActionName() {
    return MathematicaBundle.message("library.new");
  }

  @Nullable
  @Override
  public NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent, @Nullable VirtualFile contextDirectory, @NotNull Project project) {
    final LibraryRootsComponentDescriptor descriptor = createLibraryRootsComponentDescriptor();
    if (descriptor != null) {
      return LibraryTypeService.getInstance().createLibraryFromFiles(descriptor, parentComponent, contextDirectory,
          this, project);
    }
    return null;
  }

  @Override
  public LibraryRootsComponentDescriptor createLibraryRootsComponentDescriptor() {
    return new MathematicaLibraryRootsComponentDescriptor();
  }

  @Nullable
  @Override
  public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<DummyLibraryProperties> editorComponent) {
    return null;
  }

  /**
   * This one is important: If you don't have a Mathematica module, then you cannot add this library to it and the
   * "Add Mathematica Library" option will not be available.
   *
   * @param module         Should be a Mathematica module
   * @param facetsProvider Don't know
   *
   * @return
   */
  @Override
  public boolean isSuitableModule(@NotNull Module module, @NotNull FacetsProvider facetsProvider) {
    return ModuleType.get(module).equals(MathematicaModuleType.getInstance());
  }

  @Nullable
  @Override
  public Icon getIcon(@Nullable DummyLibraryProperties properties) {
    return MathematicaIcons.FILE_ICON;
  }

  @Nullable
  @Override
  public String getDescription(@NotNull DummyLibraryProperties properties) {
    return MathematicaBundle.message("library.description");
  }

  /**
   * Mathematica libraries are basically only sources. Therefore, we need to make this return the correct root type.
   *
   * @return List that contains only {@link OrderRootType#SOURCES}
   */
  @Override
  public OrderRootType[] getExternalRootTypes() {
    return new OrderRootType[]{OrderRootType.SOURCES};
  }
}
