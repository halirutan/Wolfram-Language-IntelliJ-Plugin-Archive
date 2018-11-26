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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.intellij.openapi.diagnostic.Logger;
import de.halirutan.mathematica.information.SymbolInformation;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
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
  private Set<String> myContexts;
  private Set<String> myContextSymbols;
  private Set<String> mySystemSymbols;
  private Map<String, Double> mySymbolVersions;
  private Map<String, SymbolProperties> mySymbolProperties;

  public SymbolInformationImpl() {
    try {
      myContexts = loadJSONList("Contexts");
      myContextSymbols =
          loadJSONList("ContextSymbolNames");
      mySystemSymbols = loadJSONList("SystemSymbolNames");
      mySymbolProperties = loadSymbolProperties();
      mySymbolVersions = loadSymbolVersions();
    } catch (FileNotFoundException e) {
      LOG.error("Cannot open Mathematica information resources", e);
    }
  }


  private static Map<String, String> convertNamedCharacters() {
    ResourceBundle bundle = ResourceBundle.getBundle("de.halirutan.mathematica.codeinsight.completion.NamedCharacters");
    Map<String, String> map = new HashMap<>();

    Enumeration<String> keys = bundle.getKeys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      map.put(key, new String(bundle.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
    }
    return map;
  }


  @Override
  public boolean isNamedCharacter(@NotNull String name) {
    if (namedCharacterPattern.matcher(name).matches()) {
      return myNamedCharacters.containsKey(name.substring(2, name.length() - 1));
    }
    return false;
  }

  @Override
  public boolean isSystemSymbol(@NotNull String nameWithoutContext) {
    return mySystemSymbols.contains(nameWithoutContext);
  }

  @Override
  public boolean isBuiltinSymbol(@NotNull String nameWithContext) {
    return myContextSymbols.contains(nameWithContext);
  }

  @Override
  public Set<String> getContextSymbols() {
    return myContextSymbols;
  }

  @Override
  public Set<String> getAllContexts() {
    return myContexts;
  }

  @Override
  public Set<String> getSymbolsWithProperties() {
    return mySymbolProperties.keySet();
  }

  @Override
  public boolean hasProperties(@NotNull String nameWithContext) {
    return mySymbolProperties.containsKey(nameWithContext);
  }

  @Override
  public Collection<SymbolProperties> getAllSymbolProperties() {
    return mySymbolProperties.values();
  }

  @Override
  public SymbolProperties getSymbolProperties(@NotNull String nameWithContext) {
    return mySymbolProperties.get(nameWithContext);
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

  @Override
  public double getSymbolVersion(@NotNull String nameWithContext) {
    return mySymbolVersions.getOrDefault(nameWithContext, -1.0);
  }

  @Override
  public boolean isVersionedSymbol(@NotNull String nameWithContext) {
    return mySymbolVersions.containsKey(nameWithContext);
  }

  private HashSet<String> loadJSONList(String fileName) throws FileNotFoundException {
    Gson gson = new Gson();
    final URL resource = getClass().getClassLoader().getResource(
        "de/halirutan/mathematica/codeinsight/completion/" + fileName + ".json");
    if (resource == null) {
      throw new FileNotFoundException(fileName);
    }
    JsonReader reader = new JsonReader(new FileReader(resource.getFile()));
    return gson.fromJson(reader, new TypeToken<HashSet<String>>() {
    }.getType());
  }

  private HashMap<String, Double> loadSymbolVersions() throws FileNotFoundException {
    Gson gson = new Gson();
    final URL resource = getClass().getClassLoader().getResource(
        "de/halirutan/mathematica/codeinsight/completion/SymbolVersions.json");
    if (resource == null) {
      throw new FileNotFoundException("SymbolVersions.json");
    }
    JsonReader reader = new JsonReader(new FileReader(resource.getFile()));
    return gson.fromJson(reader, new TypeToken<HashMap<String, Double>>() {
    }.getType());
  }

  private HashMap<String, SymbolProperties> loadSymbolProperties() throws FileNotFoundException {
    Gson gson = new Gson();
    final URL resource = getClass().getClassLoader().getResource(
        "de/halirutan/mathematica/codeinsight/completion/SymbolInformation.json");
    if (resource == null) {
      throw new FileNotFoundException("SymbolInformation.json");
    }
    JsonReader reader = new JsonReader(new FileReader(resource.getFile()));
    return gson.fromJson(reader, new TypeToken<HashMap<String, SymbolProperties>>() {
    }.getType());
  }

}
