package de.halirutan.mathematica.codeinsight.spellcheck;

import com.intellij.spellchecker.BundledDictionaryProvider;

/**
 * @author patrick (19.09.17).
 */
public class MathematicaDictionaryProvider implements BundledDictionaryProvider {
  @Override
  public String[] getBundledDictionaries() {
    return new String[]{"MathematicaDictionary.dic"};
  }
}
