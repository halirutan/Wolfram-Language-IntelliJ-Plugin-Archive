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

import com.intellij.formatting.SpacingBuilder;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.MathematicaLanguage;
import de.halirutan.mathematica.codeinsight.formatter.settings.MathematicaCodeStyleSettings;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * Provides a {@link SpacingBuilder} which specifies where space is required around/before/after Mathematica
 * expressions.
 *
 * @author patrick (11/4/13)
 */
class MathematicaSpacingBuilderProvider {
  private static final TokenSet ourAssignments;
  private static final TokenSet ourArithmeticOperations;
  private static final TokenSet ourRelationalOperations;
  private static final TokenSet ourFunctionalOperations;
  private static final TokenSet ourOtherOperations;
  private static final TokenSet ourRuleOperations;

  static {
    ourAssignments = TokenSet.create(
        SET_DELAYED,
        SET,
        ADD_TO,
        TIMES_BY,
        SUBTRACT_FROM,
        DIVIDE_BY,
        TAG_SET,
        TAG_UNSET_EXPRESSION,
        UP_SET,
        UP_SET_DELAYED);

    ourArithmeticOperations = TokenSet.create(
        PLUS,
        TIMES,
        DIVIDE,
        AND,
        OR,
        ALTERNATIVE,
        NON_COMMUTATIVE_MULTIPLY
    );

    ourRelationalOperations = TokenSet.create(
        EQUAL,
        SAME_Q,
        UNEQUAL,
        UNSAME_Q,
        GREATER,
        GREATER_EQUAL,
        LESS,
        LESS_EQUAL
    );

    ourRuleOperations = TokenSet.create(
        REPLACE_ALL,
        REPLACE_REPEATED,
        RULE,
        RULE_DELAYED
    );

    ourFunctionalOperations = TokenSet.create(
        MAP,
        MAP_ALL,
        APPLY,
        APPLY1,
        POSTFIX,
        PREFIX
    );

    ourOtherOperations = TokenSet.create(
        CONDITION,
        COLON,
        STRING_JOIN,
        STRING_EXPRESSION,
        INFIX_CALL,
        SPAN,
        GET,
        PUT,
        PUT_APPEND
    );

  }

  static SpacingBuilder getSpacingBuilder(CommonCodeStyleSettings settings, MathematicaCodeStyleSettings mathematicaSettings) {
    return new SpacingBuilder(settings.getRootSettings(), MathematicaLanguage.INSTANCE)
        .around(ourAssignments).spaceIf(mathematicaSettings.SPACE_AROUND_ASSIGNMENT_OPERATIONS)
        .around(ourArithmeticOperations).spaceIf(mathematicaSettings.SPACE_AROUND_ARITHMETIC_OPERATIONS)
        .aroundInside(MINUS, MINUS_EXPRESSION).spaceIf(mathematicaSettings.SPACE_AROUND_ARITHMETIC_OPERATIONS)
        .around(ourRelationalOperations).spaceIf(mathematicaSettings.SPACE_AROUND_RELATION_OPERATIONS)
        .around(ourRuleOperations).spaceIf(mathematicaSettings.SPACE_AROUND_RULE_OPERATIONS)
        .around(ourFunctionalOperations).spaceIf(mathematicaSettings.SPACE_AROUND_FUNCTIONAL_OPERATIONS)
        .around(ourOtherOperations).spaceIf(mathematicaSettings.SPACE_AROUND_OTHER_OPERATIONS)
        .after(COMMA).spaceIf(settings.SPACE_AFTER_COMMA)
        .before(LEFT_BRACKET).none()
        .after(RIGHT_BRACKET).none();
  }
}
