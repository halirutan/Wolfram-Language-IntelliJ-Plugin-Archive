/*
 * Copyright (c) 2015 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct.ConstructType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author patrick (26.07.15)
 */
public class MathematicaFolding implements FoldingBuilder {

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull final ASTNode node, @NotNull final Document document) {

    List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
    collectRegionsRecursively(node, document, descriptors);
    return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
  }

  private void collectRegionsRecursively(@NotNull final ASTNode node,
                                         @NotNull final Document document,
                                         @NotNull List<FoldingDescriptor> descriptors) {

    final IElementType elementType = node.getElementType();

    if (elementType == MathematicaElementTypes.LIST_EXPRESSION) {
      // Well, we count the number of elements by counting the commas and adding one. Not bullet-proof, but will do.
      final int numberOfListElements = node.getChildren(TokenSet.create(MathematicaElementTypes.COMMA)).length + 1;
      descriptors.add(new NamedFoldingDescriptor(
          node,
          node.getTextRange(),
          null,
          "{ <<" + numberOfListElements + ">> }"));
      return;
    } else if (elementType == MathematicaElementTypes.FUNCTION_CALL_EXPRESSION) {
      final PsiElement psi = node.getPsi();
      if (psi instanceof FunctionCall) {
        final FunctionCall functionCall = (FunctionCall) psi;
        if (functionCall.getScopingConstruct() != ConstructType.NULL) {
          descriptors.add(new NamedFoldingDescriptor(
                  node,
                  node.getTextRange(),
                  null,
                  functionCall.getHead().getText() + "[...]"
              ));
          return;
        }
      }
    }


    for (ASTNode child : node.getChildren(null)) {
      collectRegionsRecursively(child, document, descriptors);
    }
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull final ASTNode node) {
    return "<<...>>";
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull final ASTNode node) {
    return false;
  }
}
