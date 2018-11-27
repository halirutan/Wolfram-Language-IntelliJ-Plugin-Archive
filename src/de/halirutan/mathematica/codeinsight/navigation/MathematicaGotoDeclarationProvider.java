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

package de.halirutan.mathematica.codeinsight.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import de.halirutan.mathematica.lang.psi.LocalizationConstruct;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.impl.LightSymbol;
import de.halirutan.mathematica.lang.resolve.GlobalDefinitionCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

/**
 * @author patrick (30.11.17).
 */
public class MathematicaGotoDeclarationProvider implements GotoDeclarationHandler {
  @Nullable
  @Override
  public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
    if (sourceElement == null) return PsiElement.EMPTY_ARRAY;
    if (sourceElement.getParent() instanceof Symbol) {
      final Symbol symbol = (Symbol) sourceElement.getParent();
      final LocalizationConstruct.MScope scope = symbol.getLocalizationConstruct();
      if (scope == LocalizationConstruct.MScope.NULL_SCOPE || scope == LocalizationConstruct.MScope.KERNEL_SCOPE) {
        return PsiElement.EMPTY_ARRAY;
      }
      if (LocalizationConstruct.isLocalScoping(scope)) {
        final PsiElement resolve = symbol.resolve();
        if (resolve != null) {
          return new PsiElement[]{resolve};
        }
      }

      if (scope == LocalizationConstruct.MScope.IMPORT_SCOPE) {
        final ResolveResult[] resolve = symbol.multiResolve(false);
        if (resolve.length > 0) {
          final PsiElement elm = resolve[0].getElement();
          if (elm instanceof LightSymbol) {
            return findLightSymbolDeclarations((LightSymbol) elm);
          }
        }
      }

      return findDeclarations(symbol);
    }
    return PsiElement.EMPTY_ARRAY;
  }

  @Nullable
  @Override
  public String getActionText(DataContext context) {
    return null;
  }

  private PsiElement[] findLightSymbolDeclarations(@NotNull LightSymbol lightSymbol) {
    java.util.Set<PsiElement> result = new HashSet<>();
    GlobalDefinitionCollector coll = new GlobalDefinitionCollector(lightSymbol.getContainingFile());
    if (coll.getAssignments().containsKey(lightSymbol.getName())) {
      coll.getAssignments().get(lightSymbol.getName()).forEach(
          assignmentProperty -> result.add(assignmentProperty.myLhsOfAssignment));
    }
    return result.toArray(PsiElement.EMPTY_ARRAY);
  }

  private PsiElement[] findDeclarations(@NotNull Symbol symbol) {
    java.util.Set<PsiElement> result = new HashSet<>();
    GlobalDefinitionCollector c = new GlobalDefinitionCollector(symbol.getContainingFile());
    if (c.getAssignments().containsKey(symbol.getSymbolName())) {
      c.getAssignments().get(symbol.getSymbolName()).forEach(
          assignmentProperty -> result.add(assignmentProperty.myLhsOfAssignment));
    }
    return result.toArray(PsiElement.EMPTY_ARRAY);
  }

}
