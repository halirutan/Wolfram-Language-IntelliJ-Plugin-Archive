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

package de.halirutan.mathematica.information.impl;

import com.intellij.openapi.diagnostic.Logger;
import de.halirutan.mathematica.information.SymbolInformation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author patrick (05.09.18).
 */
public class SymbolInformationImpl implements SymbolInformation {

  private static final Pattern namedCharacterPattern = Pattern.compile("\\\\\\[[A-Z][a-zA-Z]+]");
  private static Logger LOG = Logger.getInstance(SymbolInformation.class);
  private Map<String, String> myNamedCharacters = convertNamedCharacters();
  private Set<String> myContexts =
      convertResourceBundleToList("de.halirutan.mathematica.information.impl.MathematicaContexts");
  private Set<String> myContextSymbols =
      convertResourceBundleToList("de.halirutan.mathematica.information.impl.MathematicaContextSymbols");


  private static Map<String, String> convertNamedCharacters() {
    ResourceBundle bundle = ResourceBundle.getBundle("de.halirutan.mathematica.information.impl.NamedCharacters");
    Map<String, String> map = new HashMap<>();

    Enumeration<String> keys = bundle.getKeys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      map.put(key, new String(bundle.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
    }
    return map;
  }

  private static Set<String> convertResourceBundleToList(@NotNull String resource) {
    ResourceBundle bundle = ResourceBundle.getBundle(resource);
    return bundle.keySet();
  }

  @Override
  public boolean isNamedCharacter(@NotNull String name) {
    if (namedCharacterPattern.matcher(name).matches()) {
      return myNamedCharacters.containsKey(name.substring(2, name.length() - 1));
    }
    return false;
  }

  @Override
  public String getNamedCharacter(@NotNull String name) {
    if (namedCharacterPattern.matcher(name).matches()) {
      return myNamedCharacters.get(name.substring(2, name.length() - 1));
    }
    return null;
  }

  @Override
  public Set<String> getNamedCharacters() {
    return myNamedCharacters.keySet();
  }

}
