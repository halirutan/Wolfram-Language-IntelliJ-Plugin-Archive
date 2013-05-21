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

package de.halirutan.mathematica.parsing;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import de.halirutan.mathematica.MathematicaLanguage;
import de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter;
import de.halirutan.mathematica.parsing.psi.impl.*;
import de.halirutan.mathematica.parsing.psi.impl.arithmetic.*;
import de.halirutan.mathematica.parsing.psi.impl.assignment.*;
import de.halirutan.mathematica.parsing.psi.impl.comparison.*;
import de.halirutan.mathematica.parsing.psi.impl.files.GetImpl;
import de.halirutan.mathematica.parsing.psi.impl.files.PutAppendImpl;
import de.halirutan.mathematica.parsing.psi.impl.files.PutImpl;
import de.halirutan.mathematica.parsing.psi.impl.function.*;
import de.halirutan.mathematica.parsing.psi.impl.lists.PartImpl;
import de.halirutan.mathematica.parsing.psi.impl.lists.SpanImpl;
import de.halirutan.mathematica.parsing.psi.impl.logical.AndImpl;
import de.halirutan.mathematica.parsing.psi.impl.logical.NotImpl;
import de.halirutan.mathematica.parsing.psi.impl.logical.OrImpl;
import de.halirutan.mathematica.parsing.psi.impl.pattern.*;
import de.halirutan.mathematica.parsing.psi.impl.rules.ReplaceAllImpl;
import de.halirutan.mathematica.parsing.psi.impl.rules.ReplaceRepeatedImpl;
import de.halirutan.mathematica.parsing.psi.impl.rules.RuleDelayedImpl;
import de.halirutan.mathematica.parsing.psi.impl.rules.RuleImpl;
import de.halirutan.mathematica.parsing.psi.impl.string.StringExpressionImpl;
import de.halirutan.mathematica.parsing.psi.impl.string.StringImpl;
import de.halirutan.mathematica.parsing.psi.impl.string.StringJoinImpl;

/**
 * <p> This interface provides token types which are used by the Lexer and later by the parser. Some {@link TokenSet}'s
 * are defined which are used for the basic highlighter. Every {@link TokenSet} is then used to define a group of tokens
 * which are highlighted in the same color. Check {@link MathematicaSyntaxHighlighter}. As a last part this interface
 * </p> <p> is used to define the {@link IElementType}'s which are used to mark the AST during parsing. Since I use a
 * Pratt parser where every token gets its own small parser (called parselet) most lexer token types have one or more
 * corresponding parser element types which are then used as nodes in the AST tree. </p>
 *
 * @author patrick (12/27/12)
 */
public interface MathematicaElementTypes {

  IFileElementType FILE = new IFileElementType(MathematicaLanguage.INSTANCE);

  IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
  IElementType LINE_BREAK = new MathematicaElementType("LINE_BREAK");
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
  IElementType COMMENT = new MathematicaElementType("COMMENT");

  IElementType STRING_LITERAL = new MathematicaElementType("STRING_LITERAL");
  IElementType STRING_LITERAL_BEGIN = new MathematicaElementType("STRING_LITERAL_BEGIN");
  IElementType STRING_LITERAL_END = new MathematicaElementType("STRING_LITERAL_END");
  IElementType IDENTIFIER = new MathematicaElementType("IDENTIFIER");

  IElementType NUMBER = new MathematicaElementType("NUMBER");

  IElementType RIGHT_PAR = new MathematicaElementType("RIGHT_PAR");
  IElementType LEFT_PAR = new MathematicaElementType("LEFT_PAR");
  IElementType LEFT_BRACE = new MathematicaElementType("LEFT_BRACE");
  IElementType RIGHT_BRACE = new MathematicaElementType("RIGHT_BRACE");
  IElementType LEFT_BRACKET = new MathematicaElementType("LEFT_BRACKET");
  IElementType PART_BEGIN = new MathematicaElementType("PART_BEGIN");
  IElementType RIGHT_BRACKET = new MathematicaElementType("RIGHT_BRACKET");

  IElementType ACCURACY = new MathematicaElementType("ACCURACY");
  IElementType COMMA = new MathematicaElementType("COMMA");

  IElementType PREFIX = new MathematicaElementType("PREFIX");
  IElementType POSTFIX = new MathematicaElementType("POSTFIX");
  IElementType MAP = new MathematicaElementType("MAP");
  IElementType MAP_ALL = new MathematicaElementType("MAP_ALL");
  IElementType APPLY = new MathematicaElementType("APPLY");
  IElementType APPLY1 = new MathematicaElementType("APPLY1");
  IElementType REPLACE_ALL = new MathematicaElementType("REPLACE_ALL");
  IElementType REPLACE_REPEATED = new MathematicaElementType("REPLACE_REPEATED");

  IElementType POWER = new MathematicaElementType("POWER");
  IElementType TIMES = new MathematicaElementType("TIMES");
  IElementType NON_COMMUTATIVE_MULTIPLY = new MathematicaElementType("NON_COMMUTATIVE_MULTIPLY");
  IElementType PLUS = new MathematicaElementType("PLUS");
  IElementType MINUS = new MathematicaElementType("MINUS");
  IElementType DIVIDE = new MathematicaElementType("DIVIDE");
  IElementType DIVIDE_BY = new MathematicaElementType("DIVIDE_BY");
  IElementType TIMES_BY = new MathematicaElementType("TIMES_BY");
  IElementType SUBTRACT_FROM = new MathematicaElementType("SUBTRACT_FROM");
  IElementType ADD_TO = new MathematicaElementType("ADD_TO");
  IElementType INCREMENT = new MathematicaElementType("INCREMENT");
  IElementType DECREMENT = new MathematicaElementType("DECREMENT");


  IElementType SAME_Q = new MathematicaElementType("SAME_Q");
  IElementType UNSAME_Q = new MathematicaElementType("UNSAME_Q");
  IElementType EQUAL = new MathematicaElementType("EQUAL");
  IElementType UNEQUAL = new MathematicaElementType("UNEQUAL");
  IElementType LESS_EQUAL = new MathematicaElementType("LESS_EQUAL");
  IElementType GREATER_EQUAL = new MathematicaElementType("GREATER_EQUAL");
  IElementType LESS = new MathematicaElementType("LESS");
  IElementType GREATER = new MathematicaElementType("GREATER");

  IElementType SET = new MathematicaElementType("SET");
  IElementType SET_DELAYED = new MathematicaElementType("SET_DELAYED");
  IElementType UNSET = new MathematicaElementType("UNSET");
  IElementType TAG_SET = new MathematicaElementType("TAG_SET");
  IElementType UP_SET = new MathematicaElementType("UP_SET");
  IElementType UP_SET_DELAYED = new MathematicaElementType("UP_SET_DELAYED");
  IElementType RULE = new MathematicaElementType("RULE");
  IElementType RULE_DELAYED = new MathematicaElementType("RULE_DELAYED");

  IElementType BLANK = new MathematicaElementType("BLANK");
  IElementType BLANK_SEQUENCE = new MathematicaElementType("BLANK_SEQUENCE");
  IElementType BLANK_NULL_SEQUENCE = new MathematicaElementType("BLANK_NULL_SEQUENCE");
  IElementType REPEATED = new MathematicaElementType("REPEATED");
  IElementType REPEATED_NULL = new MathematicaElementType("REPEATED_NULL");
  IElementType CONDITION = new MathematicaElementType("CONDITION");
  IElementType DEFAULT = new MathematicaElementType("DEFAULT");

  IElementType COLON = new MathematicaElementType("COLON");
  IElementType DOUBLE_COLON = new MathematicaElementType("DOUBLE_COLON");
  IElementType SEMICOLON = new MathematicaElementType("SEMICOLON");
  IElementType SPAN = new MathematicaElementType("SPAN");
  IElementType OUT = new MathematicaElementType("OUT");
  IElementType STRING_JOIN = new MathematicaElementType("STRING_JOIN");
  IElementType STRING_EXPRESSION = new MathematicaElementType("STRING_EXPRESSION");
  IElementType POINT = new MathematicaElementType("POINT");

  IElementType AND = new MathematicaElementType("AND");
  IElementType OR = new MathematicaElementType("OR");
  IElementType ALTERNATIVE = new MathematicaElementType("ALTERNATIVE");

  IElementType DERIVATIVE = new MathematicaElementType("DERIVATIVE");


  IElementType EXCLAMATION_MARK = new MathematicaElementType("EXCLAMATION_MARK");
  IElementType QUESTION_MARK = new MathematicaElementType("QUESTION_MARK");

  IElementType SLOT = new MathematicaElementType("SLOT");
  IElementType SLOT_SEQUENCE = new MathematicaElementType("SLOT_SEQUENCE");
  IElementType FUNCTION = new MathematicaElementType("FUNCTION");

  IElementType BACK_TICK = new MathematicaElementType("BACK_TICK");
  IElementType INFIX_CALL = new MathematicaElementType("INFIX_CALL");
  IElementType PREFIX_CALL = new MathematicaElementType("PREFIX_CALL");
  IElementType GET = new MathematicaElementType("GET");
  IElementType PUT = new MathematicaElementType("PUT");
  IElementType PUT_APPEND = new MathematicaElementType("PUT_APPEND");


  /**
   * The following {@link TokenSet}'s are used for the basic highlighter.
   */
  TokenSet WHITE_SPACES = TokenSet.create(
      WHITE_SPACE, LINE_BREAK
  );

  TokenSet COMMENTS = TokenSet.create(
      COMMENT
  );
  TokenSet STRING_LITERALS = TokenSet.create(
      STRING_LITERAL, STRING_LITERAL_END, STRING_LITERAL_BEGIN
  );

  TokenSet OPERATORS = TokenSet.create(
      ACCURACY, ADD_TO, ALTERNATIVE, AND, APPLY, APPLY1,
      BACK_TICK, BLANK, BLANK_NULL_SEQUENCE, BLANK_SEQUENCE,
      COLON, CONDITION,
      DECREMENT, DIVIDE, DIVIDE_BY, DOUBLE_COLON, EQUAL,
      EXCLAMATION_MARK,
      FUNCTION,
      GET, GREATER, GREATER_EQUAL,
      INCREMENT, INFIX_CALL,
      LESS, LESS_EQUAL,
      MAP, MINUS,
      NON_COMMUTATIVE_MULTIPLY,
      DEFAULT, OR, OUT,
      PLUS, POSTFIX, PREFIX, PUT, PUT_APPEND,
      QUESTION_MARK,
      REPEATED, REPEATED_NULL, REPLACE_ALL, REPLACE_REPEATED, RULE, RULE_DELAYED,
      SAME_Q, SEMICOLON, SET, SET_DELAYED,
      TAG_SET, TIMES, TIMES_BY,
      UNEQUAL, UNSAME_Q, UNSET, UP_SET, UP_SET_DELAYED, PREFIX_CALL, STRING_JOIN, STRING_EXPRESSION
  );

  TokenSet LITERALS = TokenSet.create(
      NUMBER
  );

  TokenSet BRACES = TokenSet.create(
      LEFT_BRACE, LEFT_BRACKET, LEFT_PAR,
      RIGHT_BRACE, RIGHT_BRACKET, RIGHT_PAR
  );

  TokenSet SLOTS = TokenSet.create(
      SLOT, SLOT_SEQUENCE
  );

  class Factory {

    public static PsiElement create(ASTNode node) {
      IElementType type = node.getElementType();

      if (type.equals(GROUP_EXPRESSION)) return new GroupImpl(node);
      if (type.equals(FUNCTION_CALL_EXPRESSION)) return new FunctionCallImpl(node);

      // Basic types
      if (type.equals(NUMBER_EXPRESSION)) return new NumberImpl(node);
      if (type.equals(STRING_EXPRESSION)) return new StringImpl(node);

      // Arithmetic operations
      if (type.equals(SYMBOL_EXPRESSION)) return new SymbolImpl(node);
      if (type.equals(DIVIDE_EXPRESSION)) return new DivideImpl(node);
      if (type.equals(DOT_EXPRESSION)) return new DotImpl(node);
      if (type.equals(FACTORIAL_POSTFIX)) return new FactorialImpl(node);
      if (type.equals(MINUS_EXPRESSION)) return new MinusImpl(node);
      if (type.equals(PLUS_EXPRESSION)) return new PlusImpl(node);
      if (type.equals(POWER_EXPRESSION)) return new PowerImpl(node);
      if (type.equals(TIMES_EXPRESSION)) return new TimesImpl(node);
      if (type.equals(UNARY_MINUS_PREFIX)) return new UnaryMinusImpl(node);
      if (type.equals(UNARY_PLUS_PREFIX)) return new UnaryPlusImpl(node);

      // Assignments
      if (type.equals(ADD_TO_EXPRESSION)) return new AddToImpl(node);
      if (type.equals(DECREMENT_POSTFIX)) return new DecrementImpl(node);
      if (type.equals(DIVIDE_BY_EXPRESSION)) return new DivideByImpl(node);
      if (type.equals(INCREMENT_POSTFIX)) return new IncrementImpl(node);
      if (type.equals(PRE_INCREMENT_PREFIX)) return new PreIncrementImpl(node);
      if (type.equals(PRE_DECREMENT_PREFIX)) return new PreDecrementImpl(node);
      if (type.equals(SET_DELAYED_EXPRESSION)) return new SetDelayedImpl(node);
      if (type.equals(SET_EXPRESSION)) return new SetImpl(node);
      if (type.equals(SUBTRACT_FROM_EXPRESSION)) return new SubtractFromImpl(node);
      if (type.equals(TAG_SET_EXPRESSION)) return new TagSetImpl(node);
      if (type.equals(TAG_SET_DELAYED_EXPRESSION)) return new TagSetDelayedImpl(node);
      if (type.equals(TAG_UNSET_EXPRESSION)) return new TagUnsetImpl(node);
      if (type.equals(TIMES_BY_EXPRESSION)) return new TimesByImpl(node);
      if (type.equals(UNSET_EXPRESSION)) return new UnsetImpl(node);
      if (type.equals(UP_SET_DELAYED_EXPRESSION)) return new UpSetDelayedImpl(node);
      if (type.equals(UP_SET_EXPRESSION)) return new UpSetImpl(node);

      // Comparisons
      if (type.equals(EQUAL_EXPRESSION)) return new EqualImpl(node);
      if (type.equals(GREATER_EQUAL_EXPRESSION)) return new GreaterEqualImpl(node);
      if (type.equals(GREATER_EXPRESSION)) return new GreaterImpl(node);
      if (type.equals(LESS_EQUAL_EXPRESSION)) return new LessEqualImpl(node);
      if (type.equals(LESS_EXPRESSION)) return new LessImpl(node);
      if (type.equals(SAME_Q_EXPRESSION)) return new SameQImpl(node);
      if (type.equals(UNEQUAL_EXPRESSION)) return new UnequalImpl(node);
      if (type.equals(UNSAME_Q_EXPRESSION)) return new UnsameQImpl(node);

      // File operations
      if (type.equals(GET_PREFIX)) return new GetImpl(node);
      if (type.equals(PUT_APPEND_EXPRESSION)) return new PutAppendImpl(node);
      if (type.equals(PUT_EXPRESSION)) return new PutImpl(node);

      // Function related
      if (type.equals(APPLY1_EXPRESSION)) return new Apply1Impl(node);
      if (type.equals(APPLY_EXPRESSION)) return new ApplyImpl(node);
      if (type.equals(INFIX_CALL_EXPRESSION)) return new InfixImpl(node);
      if (type.equals(MAP_ALL_EXPRESSION)) return new MapAllImpl(node);
      if (type.equals(MAP_EXPRESSION)) return new MapImpl(node);
      if (type.equals(POSTFIX_EXPRESSION)) return new PostfixImpl(node);
      if (type.equals(PREFIX_CALL_EXPRESSION)) return new PrefixImpl(node);
      if (type.equals(FUNCTION_POSTFIX)) return new FunctionImpl(node);

      // List related
      if (type.equals(LIST_EXPRESSION)) return new ListImpl(node);
      if (type.equals(PART_EXPRESSION)) return new PartImpl(node);
      if (type.equals(SPAN_EXPRESSION)) return new SpanImpl(node);

      // Logical operations
      if (type.equals(AND_EXPRESSION)) return new AndImpl(node);
      if (type.equals(OR_EXPRESSION)) return new OrImpl(node);
      if (type.equals(NOT_PREFIX)) return new NotImpl(node);

      // Patterns
      if (type.equals(ALTERNATIVE_EXPRESSION)) return new AlternativeImpl(node);
      if (type.equals(BLANK_EXPRESSION)) return new BlankImpl(node);
      if (type.equals(BLANK_NULL_SEQUENCE_EXPRESSION)) return new BlankNullSequenceImpl(node);
      if (type.equals(BLANK_SEQUENCE_EXPRESSION)) return new BlankSequenceImpl(node);
      if (type.equals(CONDITION_EXPRESSION)) return new ConditionImpl(node);
      if (type.equals(OPTIONAL_EXPRESSION)) return new OptionalImpl(node);
      if (type.equals(PATTERN_EXPRESSION)) return new PatternImpl(node);
      if (type.equals(PATTERN_TEST_EXPRESSION)) return new PatternTestImpl(node);
      if (type.equals(REPEATED_POSTFIX)) return new RepeatedImpl(node);
      if (type.equals(REPEATED_NULL_POSTFIX)) return new RepeatedNullImpl(node);

      // Rules
      if (type.equals(REPLACE_ALL_EXPRESSION)) return new ReplaceAllImpl(node);
      if (type.equals(REPLACE_REPEATED_EXPRESSION)) return new ReplaceRepeatedImpl(node);
      if (type.equals(RULE_DELAYED_EXPRESSION)) return new RuleDelayedImpl(node);
      if (type.equals(RULE_EXPRESSION)) return new RuleImpl(node);

      // MString stuff
      if (type.equals(STRING_EXPRESSION_EXPRESSION)) return new StringExpressionImpl(node);
      if (type.equals(STRING_JOIN_EXPRESSION)) return new StringJoinImpl(node);
      if (type.equals(MESSAGE_NAME_EXPRESSION)) return new MessageNameImpl(node);

      if (type.equals(COMPOUND_EXPRESSION_EXPRESSION)) return new CompoundExpressionImpl(node);

      // At least everything is an expression
      return new ExpressionImpl(node);
    }
  }


// THIS SECTION IS AUTOMATICALLY CREATED WITH MATHEMATICA


  IElementType GROUP_EXPRESSION = new MathematicaElementType("GROUP_EXPRESSION");
  IElementType LIST_EXPRESSION = new MathematicaElementType("LIST_EXPRESSION");

  IElementType NUMBER_EXPRESSION = new MathematicaElementType("NUMBER_EXPRESSION");
  IElementType SYMBOL_EXPRESSION = new MathematicaElementType("SYMBOL_EXPRESSION");
  IElementType STRING = new MathematicaElementType("STRING");

  IElementType MESSAGE_NAME_EXPRESSION = new MathematicaElementType("MESSAGE_NAME_EXPRESSION");

  IElementType BLANK_EXPRESSION = new MathematicaElementType("BLANK_EXPRESSION");
  IElementType BLANK_SEQUENCE_EXPRESSION = new MathematicaElementType("BLANK_SEQUENCE_EXPRESSION");
  IElementType BLANK_NULL_SEQUENCE_EXPRESSION = new MathematicaElementType("BLANK_NULL_SEQUENCE_EXPRESSION");

  IElementType GET_PREFIX = new MathematicaElementType("GET_PREFIX");

  IElementType PATTERN_TEST_EXPRESSION = new MathematicaElementType("PATTERN_TEST_EXPRESSION");

  IElementType FUNCTION_CALL_EXPRESSION = new MathematicaElementType("FUNCTION_CALL_EXPRESSION");
  IElementType PART_EXPRESSION = new MathematicaElementType("PART_EXPRESSION");

  IElementType INCREMENT_POSTFIX = new MathematicaElementType("INCREMENT_POSTFIX");
  IElementType DECREMENT_POSTFIX = new MathematicaElementType("DECREMENT_POSTFIX");

  IElementType PRE_INCREMENT_PREFIX = new MathematicaElementType("PRE_INCREMENT_PREFIX");
  IElementType PRE_DECREMENT_PREFIX = new MathematicaElementType("PRE_DECREMENT_PREFIX");

  IElementType PREFIX_CALL_EXPRESSION = new MathematicaElementType("PREFIX_CALL_EXPRESSION");

  IElementType INFIX_CALL_EXPRESSION = new MathematicaElementType("INFIX_CALL_EXPRESSION");

  IElementType MAP_EXPRESSION = new MathematicaElementType("MAP_EXPRESSION");
  IElementType MAP_ALL_EXPRESSION = new MathematicaElementType("MAP_ALL_EXPRESSION");
  IElementType APPLY_EXPRESSION = new MathematicaElementType("APPLY_EXPRESSION");
  IElementType APPLY1_EXPRESSION = new MathematicaElementType("APPLY1_EXPRESSION");

  IElementType FACTORIAL_POSTFIX = new MathematicaElementType("FACTORIAL_POSTFIX");

  IElementType DERIVATIVE_EXPRESSION = new MathematicaElementType("DERIVATIVE_EXPRESSION");

  IElementType STRING_JOIN_EXPRESSION = new MathematicaElementType("STRING_JOIN_EXPRESSION");

  IElementType POWER_EXPRESSION = new MathematicaElementType("POWER_EXPRESSION");

  IElementType NON_COMMUTATIVE_MULTIPLY_EXPRESSION = new MathematicaElementType("NON_COMMUTATIVE_MULTIPLY_EXPRESSION");

  IElementType DOT_EXPRESSION = new MathematicaElementType("DOT_EXPRESSION");

  IElementType UNARY_MINUS_PREFIX = new MathematicaElementType("UNARY_MINUS_PREFIX");
  IElementType UNARY_PLUS_PREFIX = new MathematicaElementType("UNARY_PLUS_PREFIX");

  IElementType DIVIDE_EXPRESSION = new MathematicaElementType("DIVIDE_EXPRESSION");

  IElementType TIMES_EXPRESSION = new MathematicaElementType("TIMES_EXPRESSION");

  IElementType PLUS_EXPRESSION = new MathematicaElementType("PLUS_EXPRESSION");
  IElementType MINUS_EXPRESSION = new MathematicaElementType("MINUS_EXPRESSION");

  IElementType SPAN_EXPRESSION = new MathematicaElementType("SPAN_EXPRESSION");

  IElementType EQUAL_EXPRESSION = new MathematicaElementType("EQUAL_EXPRESSION");
  IElementType UNEQUAL_EXPRESSION = new MathematicaElementType("UNEQUAL_EXPRESSION");
  IElementType GREATER_EXPRESSION = new MathematicaElementType("GREATER_EXPRESSION");
  IElementType GREATER_EQUAL_EXPRESSION = new MathematicaElementType("GREATER_EQUAL_EXPRESSION");
  IElementType LESS_EXPRESSION = new MathematicaElementType("LESS_EXPRESSION");
  IElementType LESS_EQUAL_EXPRESSION = new MathematicaElementType("LESS_EQUAL_EXPRESSION");

  IElementType SAME_Q_EXPRESSION = new MathematicaElementType("SAME_Q_EXPRESSION");
  IElementType UNSAME_Q_EXPRESSION = new MathematicaElementType("UNSAME_Q_EXPRESSION");

  IElementType NOT_PREFIX = new MathematicaElementType("NOT_PREFIX");

  IElementType AND_EXPRESSION = new MathematicaElementType("AND_EXPRESSION");

  IElementType OR_EXPRESSION = new MathematicaElementType("OR_EXPRESSION");

  IElementType REPEATED_POSTFIX = new MathematicaElementType("REPEATED_POSTFIX");
  IElementType REPEATED_NULL_POSTFIX = new MathematicaElementType("REPEATED_NULL_POSTFIX");

  IElementType ALTERNATIVE_EXPRESSION = new MathematicaElementType("ALTERNATIVE_EXPRESSION");

  IElementType PATTERN_EXPRESSION = new MathematicaElementType("PATTERN_EXPRESSION");
  IElementType OPTIONAL_EXPRESSION = new MathematicaElementType("OPTIONAL_EXPRESSION");
  IElementType DEFAULT_EXPRESSION = new MathematicaElementType("DEFAULT_EXPRESSION");

  IElementType STRING_EXPRESSION_EXPRESSION = new MathematicaElementType("STRING_EXPRESSION_EXPRESSION");

  IElementType CONDITION_EXPRESSION = new MathematicaElementType("CONDITION_EXPRESSION");

  IElementType RULE_EXPRESSION = new MathematicaElementType("RULE_EXPRESSION");
  IElementType RULE_DELAYED_EXPRESSION = new MathematicaElementType("RULE_DELAYED_EXPRESSION");

  IElementType REPLACE_ALL_EXPRESSION = new MathematicaElementType("REPLACE_ALL_EXPRESSION");
  IElementType REPLACE_REPEATED_EXPRESSION = new MathematicaElementType("REPLACE_REPEATED_EXPRESSION");

  IElementType ADD_TO_EXPRESSION = new MathematicaElementType("ADD_TO_EXPRESSION");
  IElementType SUBTRACT_FROM_EXPRESSION = new MathematicaElementType("SUBTRACT_FROM_EXPRESSION");
  IElementType TIMES_BY_EXPRESSION = new MathematicaElementType("TIMES_BY_EXPRESSION");
  IElementType DIVIDE_BY_EXPRESSION = new MathematicaElementType("DIVIDE_BY_EXPRESSION");

  IElementType FUNCTION_POSTFIX = new MathematicaElementType("FUNCTION_POSTFIX");

  IElementType POSTFIX_EXPRESSION = new MathematicaElementType("POSTFIX_EXPRESSION");

  IElementType SET_EXPRESSION = new MathematicaElementType("SET_EXPRESSION");
  IElementType SET_DELAYED_EXPRESSION = new MathematicaElementType("SET_DELAYED_EXPRESSION");
  IElementType UP_SET_EXPRESSION = new MathematicaElementType("UP_SET_EXPRESSION");
  IElementType UP_SET_DELAYED_EXPRESSION = new MathematicaElementType("UP_SET_DELAYED_EXPRESSION");
  IElementType TAG_SET_EXPRESSION = new MathematicaElementType("TAG_SET_EXPRESSION");
  IElementType TAG_SET_DELAYED_EXPRESSION = new MathematicaElementType("TAG_SET_DELAYED_EXPRESSION");
  IElementType TAG_UNSET_EXPRESSION = new MathematicaElementType("TAG_UNSET_EXPRESSION");
  IElementType UNSET_EXPRESSION = new MathematicaElementType("UNSET_EXPRESSION");

  IElementType PUT_EXPRESSION = new MathematicaElementType("PUT_EXPRESSION");
  IElementType PUT_APPEND_EXPRESSION = new MathematicaElementType("PUT_APPEND_EXPRESSION");

  IElementType COMPOUND_EXPRESSION_EXPRESSION = new MathematicaElementType("COMPOUND_EXPRESSION_EXPRESSION");

  IElementType FAILBACK = new MathematicaElementType("FAILBACK");

//    TokenSet ARITHMETIC_OPERATIONS = TokenSet.create(
//            UNARY_PLUS_PREFIX, UNARY_MINUS_PREFIX, PLUS_EXPRESSION, MINUS_EXPRESSION,
//            TIMES_BY_EXPRESSION, TIMES_EXPRESSION, DIVIDE_BY_EXPRESSION, DIVIDE_EXPRESSION,
//            POWER_EXPRESSION, FACTORIAL_POSTFIX, DOT_EXPRESSION, STRING_JOIN_EXPRESSION,
//            INCREMENT_POSTFIX, DECREMENT_POSTFIX, PRE_INCREMENT_PREFIX, PRE_DECREMENT_PREFIX
//    );
//
//    TokenSet LOGICAL_OPERATIONS = TokenSet.create(AND_EXPRESSION, OR_EXPRESSION, NOT_PREFIX);
//
//    TokenSet ASSIGNMENT_OPERATIONS = TokenSet.create(SET_DELAYED_EXPRESSION, SET_EXPRESSION, UP_SET_EXPRESSION,
//            UP_SET_DELAYED_EXPRESSION, TAG_SET_EXPRESSION, UNSET_EXPRESSION);
//
//    TokenSet COMPARISON_EXPRESSIONS = TokenSet.create(EQUAL_EXPRESSION, UNEQUAL_EXPRESSION,
//            SAME_Q_EXPRESSION, UNSAME_Q_EXPRESSION,
//            GREATER_EQUAL_EXPRESSION, GREATER_EXPRESSION, LESS_EQUAL_EXPRESSION, LESS_EXPRESSION);
//
//    TokenSet FUNCTION_APPLICATION = TokenSet.create(MAP_ALL_EXPRESSION, MAP_EXPRESSION, POSTFIX_EXPRESSION,
//            PREFIX_CALL_EXPRESSION, APPLY1_EXPRESSION, APPLY_EXPRESSION, INFIX_CALL_EXPRESSION);
//
//    TokenSet RULES_REPLACEMENT = TokenSet.create(RULE_DELAYED_EXPRESSION, RULE_EXPRESSION, REPLACE_ALL_EXPRESSION,
//            REPLACE_REPEATED_EXPRESSION);
//
//    TokenSet PATTERNS = TokenSet.create(BLANK_NULL_SEQUENCE_EXPRESSION, BLANK_EXPRESSION, REPEATED_NULL_POSTFIX,
//            REPEATED_POSTFIX, OPTIONAL_EXPRESSION, CONDITION_EXPRESSION, ALTERNATIVE_EXPRESSION,
//            PATTERN_TEST_EXPRESSION, STRING_EXPRESSION_EXPRESSION);
//
//    TokenSet SAVE_LOAD = TokenSet.create(GET_PREFIX, PUT_EXPRESSION, PUT_APPEND_EXPRESSION);

}
