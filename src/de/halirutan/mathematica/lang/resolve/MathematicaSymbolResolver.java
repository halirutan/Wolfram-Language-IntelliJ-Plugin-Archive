/*
 * Copyright (c) 2017 Patrick Scheibe
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

package de.halirutan.mathematica.lang.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.impl.LightBuiltInSymbol;
import de.halirutan.mathematica.lang.psi.impl.LightSymbol;
import de.halirutan.mathematica.lang.psi.impl.LightUndefinedSymbol;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct.MScope;
import org.jetbrains.annotations.NotNull;

import static de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities.isBuiltInSymbol;

/**
 * @author patrick (08.07.17).
 */
public class MathematicaSymbolResolver implements AbstractResolver<Symbol, SymbolResolveResult> {


  @Override
  public SymbolResolveResult resolve(@NotNull Symbol ref, boolean incompleteCode) {

    if (isBuiltInSymbol(ref)){
      return new SymbolResolveResult(new LightBuiltInSymbol(ref), MScope.BUILT_IN, true);
    }

    LocalDefinitionResolveProcessor processor = new LocalDefinitionResolveProcessor(ref);
    PsiTreeUtil.treeWalkUp(processor, ref, ref.getContainingFile(), ResolveState.initial());
    final Symbol referringSymbol = processor.getMyReferringSymbol();
    if (referringSymbol != null) {
      return new SymbolResolveResult(referringSymbol, processor.getMyLocalization(), true);
    }

//    return null;
    GlobalDefinitionResolveProcessor globalProcessor = new GlobalDefinitionResolveProcessor(ref);
    PsiTreeUtil.processElements(ref.getContainingFile(), globalProcessor);


    final PsiElement globalDefinition = globalProcessor.getMyReferringSymbol();
    if (globalDefinition != null) {
      return new SymbolResolveResult(globalDefinition, MScope.FILE, true);
    } else {
      return new SymbolResolveResult(new LightUndefinedSymbol(ref), MScope.NULL, true);
    }
  }

}
