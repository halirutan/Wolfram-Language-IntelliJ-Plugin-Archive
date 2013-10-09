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

package de.halirutan.mathematica.parsing.psi.api;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct;

/**
 * Created with IntelliJ IDEA. User: patrick Date: 3/28/13 Time: 12:33 AM Purpose:
 */
public interface Symbol extends PsiNameIdentifierOwner {

  /**
   * Returns the context of a symbol which is not the correct context. It is either <code >System</code> or the explicit
   * context in a symbols name like in <code >Global`variable</code>.
   *
   * @return Context part of a symbol name
   */
  String getMathematicaContext();

  /**
   * Removes a possible context part from a symbol name and returns the symbol name only. <code >Global`variable</code>
   * gives therefore <code >variable</code>.
   *
   * @return Symbol name without context part
   */
  String getSymbolName();

  /**
   * Returns true if the <em>definition element</em> of this symbol was already resolved and is up to date. If this
   * returns true then you can call {@link #getResolveElement()} to get the place of definition or {@link
   * #getLocalizationConstruct()} to get the type of the localization construct which is used.
   *
   * @return true if the cache is up to date
   */
  boolean cachedResolve();

  /**
   * Returns the element where the symbol is defined. So for the last <code>x</code> in <code>Module[{x},y+x^2]</code>
   * it will return the PsiElement of the first <code>x</code> in the definition list.
   *
   * @return the place of definition
   */
  Symbol getResolveElement();

  /**
   * Returns e.g. <code>Module</code> for symbols that are localized with a Module-construct. This is mainly for the
   * highlighting.
   *
   * @return type of localization
   */
  LocalizationConstruct.ConstructType getLocalizationConstruct();

  /**
   * Caches the resolved referring symbol for later use.
   *
   * @param referringSymbol Element which is the defining element for the variable. E.g. in a situation like
   *                        <code>Module[{x=3}, x+x]</code> the <code>x</code> in the braces is the definition for both
   *                        <code>x</code> in the <code>Module</code> body.
   */
  void setReferringElement(Symbol referringSymbol, LocalizationConstruct.ConstructType type, PsiElement localizationElement);

  public void subtreeChanged();
}
