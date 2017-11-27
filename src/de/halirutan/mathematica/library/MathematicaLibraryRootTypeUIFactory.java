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

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.ui.SdkPathEditor;
import com.intellij.openapi.roots.ui.OrderRootTypeUIFactory;
import de.halirutan.mathematica.util.MathematicaIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * When I understood it correctly, then this is the class that provides the UI inside the settings dialog that is
 * opened after the user has selected a library dir. Then, the user can (de)select some of the found library roots.
 * As far as I can tell, this is the UI for it.
 *
 * @author patrick (25.11.17).
 */
public class MathematicaLibraryRootTypeUIFactory implements OrderRootTypeUIFactory {

  @Nullable
  @Override
  public SdkPathEditor createPathEditor(Sdk sdk) {
    return new SdkPathEditor(getNodeText(), MathematicaLibraryRootType.getInstance(),
        FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
  }

  @Override
  public Icon getIcon() {
    return MathematicaIcons.FILE_ICON;
  }

  @Override
  public String getNodeText() {
    return "Package Files";
  }
}
