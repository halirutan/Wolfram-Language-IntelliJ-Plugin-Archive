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
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (10/20/13)
 */
public class MathematicaBlock extends AbstractBlock {

  private final Indent myIndent;
  private final CommonCodeStyleSettings myCommonCodeStyleSettings;
  private final SpacingBuilder mySpacingBuilder;

  public MathematicaBlock(@NotNull ASTNode node,
                          @Nullable Wrap wrap,
                          @Nullable Alignment alignment,
                          CommonCodeStyleSettings myCommonCodeStyleSettings,
                          SpacingBuilder mySpacingBuilder) {
    super(node, wrap, alignment);
    this.myCommonCodeStyleSettings = myCommonCodeStyleSettings;
    this.mySpacingBuilder = mySpacingBuilder;
    myIndent = MathematicaIndentProcessor.getChildIndent(node);
  }

  @Override
  protected List<Block> buildChildren() {
    List<Block> blocks = new ArrayList<Block>();
    Alignment baseAlignment = Alignment.createAlignment();
    IElementType parentType = getNode().getElementType();
    PsiElement psi = getNode().getPsi();


    for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
      IElementType childType = child.getElementType();
      if (child.getTextRange().getLength() == 0 ||
          childType == WHITE_SPACE ||
          childType == LINE_BREAK) continue;

      Alignment alignment = getAlignment(psi, childType, parentType, baseAlignment);
      blocks.add(new MathematicaBlock(child, Wrap.createWrap(WrapType.NONE, false), alignment, myCommonCodeStyleSettings, mySpacingBuilder));
    }
    return Collections.unmodifiableList(blocks);


  }

  @Nullable
  private Alignment getAlignment(PsiElement psi, IElementType childType, IElementType parentType, Alignment baseAlignment) {
    if (parentType.equals(FUNCTION_CALL_EXPRESSION)) {
      return baseAlignment;
    }

    if (childType.equals(COMPOUND_EXPRESSION_EXPRESSION)) {
      return baseAlignment;
    }
    return baseAlignment;
  }

  @NotNull
  @Override
  public ChildAttributes getChildAttributes(int newChildIndex) {
    return super.getChildAttributes(newChildIndex);
  }

  @Nullable
  @Override
  protected Indent getChildIndent() {
    return Indent.getNormalIndent();
  }

  @Nullable
  @Override
  public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
    return null;
  }

  @Override
  public boolean isLeaf() {
    if (getNode().equals(SYMBOL_EXPRESSION)) {
      return true;
    }
    return false;
  }

  @Override
  public Indent getIndent() {
    return myIndent;
  }


}
