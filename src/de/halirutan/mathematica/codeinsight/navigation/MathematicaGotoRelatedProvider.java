/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.navigation;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionCollector;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionCollector.AssignmentProperty;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct.ConstructType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author patrick (28.12.16).
 */
public class MathematicaGotoRelatedProvider extends GotoRelatedProvider {

  @NotNull
  @Override
  public List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
    ArrayList<GotoRelatedItem> declarations = new ArrayList<>();
    if (psiElement instanceof LeafPsiElement && ((LeafPsiElement) psiElement).getElementType().equals(MathematicaElementTypes.IDENTIFIER)) {
      psiElement = psiElement.getParent();
    }
    if (psiElement instanceof Symbol) {
      PsiReference ref = psiElement.getReference();
      if (ref != null) {
        PsiElement resolve = ref.resolve();
        if (resolve != null) {
          if (resolve instanceof Symbol && ((Symbol) resolve).getLocalizationConstruct().equals(ConstructType.NULL)) {
            GlobalDefinitionCollector collector = new GlobalDefinitionCollector(psiElement.getContainingFile());
            Map<String, HashSet<AssignmentProperty>> assignments = collector.getAssignments();
            for (AssignmentProperty property : assignments.get(((Symbol) resolve).getSymbolName())) {
              String text = property.myLhsOfAssignment.getText();
              GotoRelatedItem item = new GotoSymbolItem(property.myAssignmentSymbol, text.substring(0, Math.min(text.length(),50)));
              declarations.add(item);
            }
          }
        }
      }
    }
    return declarations;
  }

}
