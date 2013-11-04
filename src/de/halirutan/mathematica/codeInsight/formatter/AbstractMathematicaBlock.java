/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (10/20/13)
 */
public abstract class AbstractMathematicaBlock extends AbstractBlock {

  private final Indent myIndent;
  protected SpacingBuilder mySpacingBuilder;
  protected CodeStyleSettings mySettings;


  protected AbstractMathematicaBlock(@NotNull ASTNode node,
                                     @Nullable Alignment alignment,
                                     SpacingBuilder spacingBuilder,
                                     @Nullable Wrap wrap,
                                     CodeStyleSettings settings) {
    super(node, wrap, alignment);
    mySpacingBuilder = spacingBuilder;
    mySettings = settings;
    myIndent = MathematicaIndentProcessor.getChildIndent(node);
  }

  public static Block createMathematicaBlock(PsiElement element, CodeStyleSettings settings) {
    return createMathematicaBlock(
        element.getNode(),
        null,
        MathematicaSpacingBuilderProvider.getSpacingBuilder(settings),
        null,
        settings
    );
  }

  public static Block createMathematicaBlock(@NotNull ASTNode node,
                                             @Nullable Alignment alignment,
                                             SpacingBuilder spacingBuilder,
                                             @Nullable Wrap wrap,
                                             CodeStyleSettings codeStyleSettings) {
    final IElementType elementType = node.getElementType();
    if (elementType == MathematicaElementTypes.FUNCTION_CALL_EXPRESSION) {
      return new MathematicaFunctionBlock(node, alignment, spacingBuilder, wrap, codeStyleSettings);
    }
    return new MathematicaBlock(node, alignment, spacingBuilder, wrap, codeStyleSettings);
  }

  @Override
  public boolean isLeaf() {
    return getNode().getElementType() == MathematicaElementTypes.SYMBOL_EXPRESSION;
  }

  @Nullable
  @Override
  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    return mySpacingBuilder.getSpacing(this, child1, child2);
  }

  public Indent getIndent() {
    return myIndent;
  }


  //
//  @Nullable
//  private static Indent getChildIndent(@NotNull ASTNode parent) {
//    final IElementType parentType = parent.getElementType();
//    if (parentType == MathematicaElementTypes.FUNCTION_CALL_EXPRESSION) {
//      return Indent.getNormalIndent();
//    }
//    return null;
//  }
//
//  private static int calcNewState(final ASTNode child, int state) {
//    switch (state) {
//      case BEFORE_FIRST: {
//        if (StdTokenSets.COMMENT_BIT_SET.contains(child.getElementType())) {
//          return BEFORE_FIRST;
//        } else if (isLBrace(child)) {
//          return INSIDE_BODY;
//        } else {
//          return BEFORE_LBRACE;
//        }
//      }
//      case BEFORE_LBRACE: {
//        if (isLBrace(child)) {
//          return INSIDE_BODY;
//        } else {
//          return BEFORE_LBRACE;
//        }
//      }
//    }
//    return INSIDE_BODY;
//  }
//
//  private static boolean isLBrace(ASTNode child) {
//    return child.getElementType() == MathematicaElementTypes.LEFT_BRACKET;
//  }
//
//  protected static boolean isRBrace(@NotNull final ASTNode child) {
//    return child.getElementType() == MathematicaElementTypes.RIGHT_BRACKET;
//  }
//
//
//  private Wrap createChildWrap() {
//    return myWrap;
//  }
//
//  @Nullable
//  private Alignment createChildAlignment() {
//    return Alignment.createChildAlignment(myAlignment);
//  }
//
//  @Nullable
//  @Override
//  protected Indent getChildIndent() {
//    return getChildIndent(myNode);
//  }
//
//  @Nullable
//  @Override
//  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
//    return mySpacingBuilder.getSpacing(this, child1, child2);
//  }
//
//  @Override
//  public boolean isLeaf() {
//    return myNode.getFirstChildNode() == null;
//  }
//
//  private Indent calcCurrentIndent(final ASTNode child, final int state) {
//    if (isRBrace(child) || child.getElementType() == JavaTokenType.AT) {
//      return Indent.getNoneIndent();
//    }
//
//    if (state == BEFORE_FIRST) return Indent.getNoneIndent();
//
//    if (state == BEFORE_LBRACE) {
//      if (isLBrace(child)) {
//        return Indent.getNoneIndent();
//      } else {
//        return Indent.getContinuationIndent();
//      }
//    } else {
//      if (isRBrace(child)) {
//        return Indent.getNoneIndent();
//      } else {
//        return Indent.getNormalIndent();
//      }
//    }
//  }

}
