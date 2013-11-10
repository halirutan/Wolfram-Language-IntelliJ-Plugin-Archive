package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author patrick (11/4/13)
 */
public class MathematicaFunctionBlock extends AbstractMathematicaBlock {

  private enum myState {
    BEFORE_BRACE, IN_BODY
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


    return null;
  }
}
