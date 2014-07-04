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

package de.halirutan.mathematica.codeinsight.formatter;

import com.google.common.collect.Lists;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.codeinsight.formatter.settings.MathematicaCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (11/16/13)
 */
public class MathematicaListBlock extends AbstractMathematicaBlock {
  public MathematicaListBlock(@NotNull ASTNode node,
                              @Nullable Alignment alignment,
                              SpacingBuilder spacingBuilder,
                              @Nullable Wrap wrap,
                              CommonCodeStyleSettings settings,
                              MathematicaCodeStyleSettings mmaSettings) {
    super(
        node,
        alignment,
        spacingBuilder,
        wrap,
        settings,
        mmaSettings);
  }

  @Override
  protected List<Block> buildChildren() {
    List<Block> result = Lists.newArrayList();
    ChildState state = ChildState.BEFORE_BRACE;
    Alignment childAlignment = null;

    for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
      IElementType childType = child.getElementType();

      if (child.getTextRange().getLength() == 0 ||
          childType == WHITE_SPACE ||
          childType == LINE_BREAK) continue;

      switch (state) {
        case BEFORE_BRACE:
          if (childType == LEFT_BRACE) {
            state = ChildState.IN_BODY;
          }
          result.add(createMathematicaBlock(child, null, mySpacingBuilder, myWrap, mySettings, myMathematicaSettings));
          break;
        case IN_BODY:
          if (childType == RIGHT_BRACE) {
            state = ChildState.AFTER_BODY;
            result.add(createMathematicaBlock(child, null, mySpacingBuilder, myWrap, mySettings, myMathematicaSettings));
            break;
          }
          result.add(createMathematicaBlock(child, childAlignment, mySpacingBuilder, myWrap, mySettings, myMathematicaSettings));
          break;
        case AFTER_BODY:
          // theoretically not possible!
          result.add(createMathematicaBlock(child, null, mySpacingBuilder, myWrap, mySettings, myMathematicaSettings));
          break;
      }
    }
    return result;
  }

  @NotNull
  @Override
  public ChildAttributes getChildAttributes(int newChildIndex) {
    return new ChildAttributes(Indent.getNormalIndent(false), null);
  }

  private enum ChildState {
    BEFORE_BRACE, IN_BODY, AFTER_BODY
  }

}
