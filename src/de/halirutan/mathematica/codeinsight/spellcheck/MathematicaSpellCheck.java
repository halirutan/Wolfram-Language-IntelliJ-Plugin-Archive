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

package de.halirutan.mathematica.codeinsight.spellcheck;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.spellchecker.tokenizer.PsiIdentifierOwnerTokenizer;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.api.string.MString;
import org.jetbrains.annotations.NotNull;

/**
 * Spell check for Mathematica. This works out of the box since Mathematica uses Camel Case built in functions which
 * are separated correctly by the built-in tokenizer for spelling.
 *
 * @author patrick (08.06.17).
 */
public class MathematicaSpellCheck extends SpellcheckingStrategy {

  @NotNull
  public Tokenizer getTokenizer(PsiElement element) {
    InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(element.getProject());
    if (element instanceof PsiWhiteSpace) {
      return EMPTY_TOKENIZER;
    }
    if (element instanceof PsiLanguageInjectionHost && injectedLanguageManager.getInjectedPsiFiles(element) != null) {
      return EMPTY_TOKENIZER;
    }
    if (element instanceof Symbol) return new PsiIdentifierOwnerTokenizer();
    if (element instanceof MString) return TEXT_TOKENIZER;
    if (element instanceof PsiComment) {
      return myCommentTokenizer;
    }
    return EMPTY_TOKENIZER;
  }

}
