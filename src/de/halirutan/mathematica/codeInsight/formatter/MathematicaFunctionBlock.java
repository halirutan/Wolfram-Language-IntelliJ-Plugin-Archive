package de.halirutan.mathematica.codeInsight.formatter;

import com.google.common.collect.Lists;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (11/4/13)
 */
public class MathematicaFunctionBlock extends AbstractMathematicaBlock {

  private enum ChildState {
    BEFORE_BRACE, IN_BODY, AFTER_BODY
  }

  public MathematicaFunctionBlock(@NotNull ASTNode node,
                                  @Nullable Alignment alignment,
                                  SpacingBuilder spacingBuilder,
                                  @Nullable Wrap wrap,
                                  CodeStyleSettings settings) {
    super(
        node,
        alignment,
        spacingBuilder,
        wrap,
        settings);
  }

  @Override
  protected List<Block> buildChildren() {
    List<Block> result = Lists.newArrayList();
    ChildState state = ChildState.BEFORE_BRACE;
//    Alignment functionAlignment = Alignment.createAlignment();
//    Alignment argumentAlignment = Alignment.createChildAlignment(myAlignment);

    for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
      IElementType childType = child.getElementType();

      Alignment alignment = Alignment.createAlignment();

      if (child.getTextRange().getLength() == 0 ||
          childType == WHITE_SPACE ||
          childType == LINE_BREAK) continue;

      switch (state) {
        case BEFORE_BRACE:
          if (childType == LEFT_BRACKET) {
            state = ChildState.IN_BODY;
          }
          result.add(createMathematicaBlock(child, alignment, mySpacingBuilder, myWrap, mySettings));
          break;
        case IN_BODY:
          if (childType == RIGHT_BRACKET) {
            state = ChildState.AFTER_BODY;
            result.add(createMathematicaBlock(child, alignment, mySpacingBuilder, myWrap, mySettings));
            break;
          }
          result.add(createMathematicaBlock(child, alignment, mySpacingBuilder, myWrap, mySettings));
          break;
        case AFTER_BODY:
          // theoretically not possible!
          result.add(createMathematicaBlock(child, alignment, mySpacingBuilder, myWrap, mySettings));
          break;
      }
    }
    return result;
  }

  @NotNull
  @Override
  public ChildAttributes getChildAttributes(int newChildIndex) {
    return super.getChildAttributes(newChildIndex);
  }
}
