package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.formatting.SpacingBuilder;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;

/**
 * Provides a {@link SpacingBuilder} which specifies where space is required around/before/after Mathematica
 * expressions.
 *
 * @author patrick (11/4/13)
 */
public class MathematicaSpacingBuilderProvider {
  private static TokenSet assignments;

  static {
    assignments = TokenSet.create(
        MathematicaElementTypes.SET_DELAYED,
        MathematicaElementTypes.SET,
        MathematicaElementTypes.ADD_TO,
        MathematicaElementTypes.TIMES_BY,
        MathematicaElementTypes.SUBTRACT_FROM,
        MathematicaElementTypes.DIVIDE_BY);


  }

  public static SpacingBuilder getSpacingBuilder(CodeStyleSettings settings) {
    return new SpacingBuilder(settings)
        .around(assignments).spaceIf(settings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
        .after(MathematicaElementTypes.COMMA).spaceIf(true)
        .before(MathematicaElementTypes.LEFT_BRACKET).none()
        .after(MathematicaElementTypes.RIGHT_BRACKET).none();
  }
}
