/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.module;

import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.xmlb.annotations.Property;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (13.12.17).
 */
public class MathematicaLanguageLevelModuleExtensionImpl extends ModuleExtension implements MathematicaLanguageLevelModuleExtension,
    PersistentStateComponentWithModificationTracker<MathematicaLanguageLevelModuleExtensionImpl.State> {

  private static final Logger LOG = Logger.getInstance(MathematicaLanguageLevelModuleExtensionImpl.class);

  private boolean myWritable;
  private Module myModule;
  private MathematicaLanguageLevelModuleExtensionImpl mySource;
  private State myState;

  public MathematicaLanguageLevelModuleExtensionImpl(Module module) {
    myWritable = false;
    myModule = module;
    mySource = null;
    myState = new State();
  }

  public MathematicaLanguageLevelModuleExtensionImpl(MathematicaLanguageLevelModuleExtensionImpl source, boolean writable) {
    this.myWritable = writable;
    myModule = source.myModule;
    mySource = source;
    myState = new State();
    myState.versionNumber = source.myState.versionNumber;
  }

  @Nullable
  public static MathematicaLanguageLevelModuleExtensionImpl getInstance(final Module module) {
    return ModuleRootManager.getInstance(module).getModuleExtension(MathematicaLanguageLevelModuleExtensionImpl.class);
  }

  @Override
  public long getStateModificationCount() {
    return myState.getModificationCount();
  }

  @Override
  public ModuleExtension getModifiableModel(boolean writable) {
    return new MathematicaLanguageLevelModuleExtensionImpl(this, writable);
  }

  @Override
  public void commit() {
    if (isChanged()) {
      mySource.myState = myState;
    }
  }

  @Override
  public boolean isChanged() {
    return mySource != null && mySource.myState.versionNumber != myState.versionNumber;
  }

  @Nullable
  @Override
  public State getState() {
    return myState;
  }

  @Override
  public void loadState(State state) {
    myState = state;
  }

  @Override
  public void dispose() {
    myModule = null;
    myState = null;
  }

  @Nullable
  @Override
  public MathematicaLanguageLevel getMathematicaLanguageLevel() {
    return MathematicaLanguageLevel.fromDouble(myState.versionNumber);
  }

  @Override
  public void setMathematicaLanguageLevel(MathematicaLanguageLevel languageLevel) {
    LOG.assertTrue(myWritable, "Writable model can be retrieved from writable ModifiableRootModel");
    myState.versionNumber = languageLevel.getVersionNumber();
  }

  static class State extends BaseState {
    @Property
    double versionNumber;
  }
}
