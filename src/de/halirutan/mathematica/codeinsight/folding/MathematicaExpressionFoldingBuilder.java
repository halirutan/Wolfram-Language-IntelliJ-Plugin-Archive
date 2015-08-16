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
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct.ConstructType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author patrick (26.07.15)
 */
public class MathematicaExpressionFoldingBuilder implements FoldingBuilder {

  private static final ResourceBundle ourCharactersResourceBundle;
  private static final HashMap<String, String> ourNamedCharacters;
  private static final Pattern namedCharacterPattern = Pattern.compile("\\\\\\[[A-Z][a-zA-Z]+\\]");

  public static final FoldingGroup NAMED_CHARACTERS_FOLDING_GROUP = FoldingGroup.newGroup("Mathematica Named Characters");

  static {
    ourCharactersResourceBundle = ResourceBundle.getBundle("/de/halirutan/mathematica/codeinsight/folding/namedCharacters");
    ourNamedCharacters = new HashMap<String, String>(ourCharactersResourceBundle.keySet().size());
    for (String key : ourCharactersResourceBundle.keySet()) {
      try {
        ourNamedCharacters.put(key, new String(ourCharactersResourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8"));
      } catch (UnsupportedEncodingException ignored) {
      }
    }
  }

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
    if (elementType == MathematicaElementTypes.IDENTIFIER) {
      final String symbol = node.getText();
      final Matcher matcher = namedCharacterPattern.matcher(symbol);
      while (matcher.find()) {
        if (matcher.end() - matcher.start() > 3) {
          final String key = symbol.substring(matcher.start() + 2, matcher.end() - 1);
          final TextRange nodeRange = node.getTextRange();
          final TextRange range = TextRange.create(nodeRange.getStartOffset()+matcher.start(), nodeRange.getStartOffset() + matcher.end());
          if (ourNamedCharacters.containsKey(key)) {
            descriptors.add(new MathematicaNamedFoldingDescriptor(node, range, null, ourNamedCharacters.get(key), false));
          }
        }
      }
    } else if (elementType == MathematicaElementTypes.STRING_NAMED_CHARACTER) {
      final String name = node.getText();
      final String key = name.substring(2,name.length()-1);
      if (ourNamedCharacters.containsKey(key)) {
        descriptors.add(new MathematicaNamedFoldingDescriptor(node, node.getTextRange(), null, ourNamedCharacters.get(key), false));
      }
    } else if(elementType==MathematicaElementTypes.LIST_EXPRESSION) {
      // Well, we count the number of elements by counting the commas and adding one. Not bullet-proof, but will do.
    final int numberOfListElements = node.getChildren(TokenSet.create(MathematicaElementTypes.COMMA)).length + 1;
    descriptors.add(new NamedFoldingDescriptor(
        node,
        node.getTextRange(),
        null,
        "{ <<" + numberOfListElements + ">> }"));
  }

  else if(elementType==MathematicaElementTypes.FUNCTION_CALL_EXPRESSION)

  {
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
      }
    }
  }


  for(
  ASTNode child
  :node.getChildren(null))

  {
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
    return node.getElementType() == MathematicaElementTypes.IDENTIFIER || node.getElementType() == MathematicaElementTypes.STRING_NAMED_CHARACTER;
  }


}
