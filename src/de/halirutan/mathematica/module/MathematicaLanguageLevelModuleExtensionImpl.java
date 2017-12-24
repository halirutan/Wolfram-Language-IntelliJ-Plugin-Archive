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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LanguageLevelModuleExtensionImpl;
import com.intellij.openapi.roots.ModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import de.halirutan.mathematica.sdk.MathematicaLanguageLevel;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a simple per Module setting for the Mathematica version used. This is mainly intended to provide a
 * "Mathematica Version" inspection that can be adjusted on module level. The inspection checks if functions that are
 * used inside the code are indeed available and it marks them as error if they are part of a later version.
 *
 * @author patrick (13.12.17).
 * @implNote This module extension is serialized into the module settings file and persists throughout restarts. I
 * tried for far too long to get it working with {@link PersistentStateComponent} but for some reason, I couldn't get it
 * store its state when I changed the settings in the project structure menu. If you try this yourself, take the following notes:
 *
 * <ul>
 *   <li>You have to implement {@link PersistentStateComponentWithModificationTracker} and not as claimed {@link PersistentStateComponent}
 *   or otherwise, the framework will try to call {@link ModuleExtension#writeExternal(Element)}
 *   </li>
 *   <li>You should heavily stare at {@link LanguageLevelModuleExtensionImpl} which is the only implementation so far that doesn't use the deprecated read and write methods</li>
 * </ul>
 */
public class MathematicaLanguageLevelModuleExtensionImpl extends MathematicaLanguageLevelModuleExtension {

  private static final Logger LOG = Logger.getInstance(MathematicaLanguageLevelModuleExtensionImpl.class);
  private static final String LANGUAGE_TAG = "MathematicaLanguageLevel";
  private static final String VERSION_ATTR = "version";
  private final boolean myWritable;
  private Module myModule;
  private MathematicaLanguageLevelModuleExtensionImpl mySource;
  private MathematicaLanguageLevel myState = MathematicaLanguageLevel.HIGHEST;

  /**
   * Called by the framework to create a first (almost) un-initialized instance.
   * This constructor should not be used directly! Instead, use {@link #getInstance}.
   */
  @SuppressWarnings("unused")
  public MathematicaLanguageLevelModuleExtensionImpl(Module module) {
    myModule = module;
    mySource = null;
    myWritable = false;
  }

  /**
   * Called by the framework to create an instance.
   * This constructor should not be used directly! Instead, use {@link #getInstance}.
   */
  @SuppressWarnings("WeakerAccess")
  public MathematicaLanguageLevelModuleExtensionImpl(MathematicaLanguageLevelModuleExtensionImpl source, boolean writable) {
    this.myWritable = writable;
    myModule = source.myModule;
    mySource = source;
    myState = source.myState;
  }

  /**
   * Provides a way to access this module extension for a module.
   *
   * @param module The module you want the Language Level extension for
   *
   * @return Instance of this class
   */
  @Nullable
  public static MathematicaLanguageLevelModuleExtensionImpl getInstance(final Module module) {
    return ModuleRootManager.getInstance(module).getModuleExtension(MathematicaLanguageLevelModuleExtensionImpl.class);
  }

  @NotNull
  @Override
  public MathematicaLanguageLevel getMathematicaLanguageLevel() {
    return myState;
  }

  /**
   * Sets a new language level for the underlying module. Note, that you need a writable instance of
   * {@link MathematicaLanguageLevelModuleExtension}.
   * @param languageLevel New language level
   */
  @Override
  public void setMathematicaLanguageLevel(MathematicaLanguageLevel languageLevel) {
    LOG.assertTrue(myWritable, "Writable model can be retrieved from writable ModifiableRootModel");
    myState = languageLevel;
  }

  @Override
  public ModuleExtension getModifiableModel(boolean writable) {
    return new MathematicaLanguageLevelModuleExtensionImpl(this, writable);
  }

  @Override
  public void commit() {
    mySource.myState = myState;
  }

  @Override
  public boolean isChanged() {
    return mySource != null && mySource.myState != myState;
  }


  @Override
  public void dispose() {
    myModule = null;
    myState = MathematicaLanguageLevel.HIGHEST;
  }


  @SuppressWarnings("deprecation")
  @Override
  public void readExternal(@NotNull Element element) {
    final Element version = element.getChild(LANGUAGE_TAG);
    if (version != null) {
      myState = MathematicaLanguageLevel.fromString(version.getAttributeValue(VERSION_ATTR));
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public void writeExternal(@NotNull Element element) {
    assert myState != null;
    Element version = new Element(LANGUAGE_TAG);
    version.setAttribute(VERSION_ATTR, myState.getName());
    element.addContent(version);
  }
}
