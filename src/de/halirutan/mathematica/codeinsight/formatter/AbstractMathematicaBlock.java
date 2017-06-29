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

package de.halirutan.mathematica.codeinsight.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.MathematicaLanguage;
import de.halirutan.mathematica.codeinsight.formatter.settings.MathematicaCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (10/20/13)
 */
public abstract class AbstractMathematicaBlock extends AbstractBlock implements BlockEx {

  final MathematicaCodeStyleSettings myMathematicaSettings;
  private final Indent myIndent;
  final SpacingBuilder mySpacingBuilder;
  final CommonCodeStyleSettings mySettings;


  AbstractMathematicaBlock(@NotNull ASTNode node,
                           @Nullable Alignment alignment,
                           SpacingBuilder spacingBuilder,
                           @Nullable Wrap wrap,
                           CommonCodeStyleSettings settings,
                           MathematicaCodeStyleSettings mmaSettings) {
    super(node, wrap, alignment);
    mySpacingBuilder = spacingBuilder;
    mySettings = settings;
    myMathematicaSettings = mmaSettings;
    myIndent = MathematicaIndentProcessor.getChildIndent(node);
  }

  public static Block createMathematicaBlock(PsiElement element,
                                             CommonCodeStyleSettings settings,
                                             MathematicaCodeStyleSettings mathematicaSettings) {
    return createMathematicaBlock(
        element.getNode(),
        null,
        MathematicaSpacingBuilderProvider.getSpacingBuilder(settings, mathematicaSettings),
        null,
        settings,
        mathematicaSettings
    );
  }

  static Block createMathematicaBlock(@NotNull ASTNode node,
                                      @Nullable Alignment alignment,
                                      SpacingBuilder spacingBuilder,
                                      @Nullable Wrap wrap,
                                      CommonCodeStyleSettings codeStyleSettings,
                                      MathematicaCodeStyleSettings mathematicaSettings) {
    final IElementType elementType = node.getElementType();
    if (elementType == FUNCTION_CALL_EXPRESSION) {
      return new MathematicaFunctionBlock(node, alignment, spacingBuilder, wrap, codeStyleSettings, mathematicaSettings);
    } else if (elementType == LIST_EXPRESSION) {
      return new MathematicaListBlock(node, alignment, spacingBuilder, wrap, codeStyleSettings, mathematicaSettings);
    }else if (elementType == ASSOCIATION_EXPRESSION) {
      return new MathematicaAssociationBlock(node, alignment, spacingBuilder, wrap, codeStyleSettings, mathematicaSettings);
    } else {
      return new MathematicaBlock(node, alignment, spacingBuilder, wrap, codeStyleSettings, mathematicaSettings);
    }
  }

  @Nullable
  @Override
  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    return mySpacingBuilder.getSpacing(this, child1, child2);
  }

  @Override
  public Indent getIndent() {
    return myIndent;
  }

  @NotNull
  @Override
  public ChildAttributes getChildAttributes(int newChildIndex) {
    return super.getChildAttributes(newChildIndex);
  }

  @Override
  public boolean isIncomplete() {
    ASTNode lastChild = myNode.getLastChildNode();
    while (lastChild != null && lastChild.getElementType() == TokenType.WHITE_SPACE) {
      lastChild = lastChild.getTreePrev();
    }
    if (lastChild == null) return false;
    if (lastChild.getElementType() == TokenType.ERROR_ELEMENT) return true;
    return FormatterUtil.isIncomplete(lastChild);
  }

  @Override
  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return null;
  }

  @Nullable
  @Override
  public Language getLanguage() {
    return MathematicaLanguage.INSTANCE;
  }

  public IElementType getElementType() {
    return myNode.getElementType();
  }
}
