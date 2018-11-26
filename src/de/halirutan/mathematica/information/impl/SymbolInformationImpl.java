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
import de.halirutan.mathematica.information.SymbolInformation;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author patrick (05.09.18).
 */
public class SymbolInformationImpl implements SymbolInformation {

  private static final Pattern namedCharacterPattern = Pattern.compile("\\\\\\[[A-Z][a-zA-Z]+]");
  private Map<String, String> myNamedCharacters = convertNamedCharacters();
  private Set<String> myContexts;
  private Set<String> myContextSymbols;
  private Set<String> mySystemSymbols;
  private Map<String, Double> mySymbolVersions;
  private Map<String, SymbolProperties> mySymbolProperties;

  public SymbolInformationImpl() {
    myContexts = loadJSONList("Contexts");
    myContextSymbols =
        loadJSONList("ContextSymbolNames");
    mySystemSymbols = loadJSONList("SystemSymbolNames");
    mySymbolProperties = loadSymbolProperties();
    mySymbolVersions = loadSymbolVersions();
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

  @NotNull
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

  private JsonReader getJsonReaderForFile(@NotNull String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();
    final InputStream stream =
        classLoader.getResourceAsStream(fileName);
    return new JsonReader(new BufferedReader(new InputStreamReader(stream)));
  }

  private HashSet<String> loadJSONList(String fileName) {
    Gson gson = new Gson();
    final JsonReader reader =
        getJsonReaderForFile("de/halirutan/mathematica/codeinsight/completion/" + fileName + ".json");
    return gson.fromJson(reader, new TypeToken<HashSet<String>>() {
    }.getType());
  }

  private HashMap<String, Double> loadSymbolVersions() {
    Gson gson = new Gson();
    final JsonReader reader =
        getJsonReaderForFile("de/halirutan/mathematica/codeinsight/completion/SymbolVersions.json");
    return gson.fromJson(reader, new TypeToken<HashMap<String, Double>>() {
    }.getType());
  }

  private HashMap<String, SymbolProperties> loadSymbolProperties() {
    Gson gson = new Gson();
    JsonReader reader = getJsonReaderForFile("de/halirutan/mathematica/codeinsight/completion/SymbolInformation.json");
    return gson.fromJson(reader, new TypeToken<HashMap<String, SymbolProperties>>() {
    }.getType());
  }

}
