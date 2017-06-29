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
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.codeinsight.formatter.settings.MathematicaCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.LINE_BREAK;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.WHITE_SPACE;

/**
 * @author patrick (10/20/13)
 */
public class MathematicaBlock extends AbstractMathematicaBlock {


  public MathematicaBlock(@NotNull ASTNode node,
                          @Nullable Alignment alignment,
                          SpacingBuilder spacingBuilder,
                          @Nullable Wrap wrap,
                          CommonCodeStyleSettings settings,
                          MathematicaCodeStyleSettings mmaSettings) {
    super(node,
        alignment,
        spacingBuilder,
        wrap,
        settings,
        mmaSettings
    );
  }

  @Override
  protected List<Block> buildChildren() {
    List<Block> blocks = new ArrayList<>();
    Alignment baseAlignment = Alignment.createAlignment();
    IElementType parentType = getNode().getElementType();
    PsiElement psi = getNode().getPsi();


    for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
      IElementType childType = child.getElementType();
      if (child.getTextRange().getLength() == 0 ||
          childType == WHITE_SPACE ||
          childType == LINE_BREAK) continue;

      blocks.add(createMathematicaBlock(child,
          null,
          mySpacingBuilder,
          myWrap,
          mySettings,
          myMathematicaSettings));
    }
    return Collections.unmodifiableList(blocks);
  }


  @NotNull
  @Override
  public ChildAttributes getChildAttributes(int newChildIndex) {
    if (isIncomplete()) {
      return new ChildAttributes(getIndent(), null);
    }
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }


}
