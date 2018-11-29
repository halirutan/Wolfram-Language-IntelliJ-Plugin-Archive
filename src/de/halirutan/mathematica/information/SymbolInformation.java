/*
 * Copyright (c) 2018 Patrick Scheibe
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

package de.halirutan.mathematica.information;

import com.intellij.openapi.components.ServiceManager;
import de.halirutan.mathematica.information.impl.SymbolProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

/**
 * Provides unified access to all information gathered from Mathematica including symbol-names, contexts,
 * symbol-properties, named characters.
 */
public interface SymbolInformation {

  static SymbolInformation getInstance() {
    return ServiceManager.getService(SymbolInformation.class);
  }

  boolean isNamedCharacter(@NotNull String name);

  boolean isSystemSymbol(@NotNull String nameWithoutContext);

  boolean isBuiltinSymbol(@NotNull String nameWithContext);

  Set<String> getContextSymbols();

  Set<String> getSystemSymbols();

  Set<String> getAllContexts();

  Set<String> getSymbolsWithProperties();

  boolean hasProperties(@NotNull String nameWithContext);

  @NotNull
  Collection<SymbolProperties> getAllSymbolProperties();

  SymbolProperties getSymbolProperties(@NotNull String nameWithContext);

  String getNamedCharacter(@NotNull String name);

  Set<String> getNamedCharacters();

  double getSymbolVersion(@NotNull String nameWithContext);

  boolean isVersionedSymbol(@NotNull String nameWithContext);
}
