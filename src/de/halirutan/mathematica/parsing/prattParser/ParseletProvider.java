/*
 * Mathematica Plugin for Jetbrains IDEA
 * Copyright (C) 2013 Patrick Scheibe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.halirutan.mathematica.parsing.prattParser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.parselets.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This works like a singleton but instead of providing an instance of the class, you can access
 * the Mathematica operator properties (precedences, parselets, Psi-tree elements) which is initialized only once.
 * This class is basically the center of the parser because it provides the {@link MathematicaParser} with all the small
 * parselets which finally do the work of parsing specific expressions.
 * <p>Therefore, I first need to select the appropriate parselet for a lexer token. This parselet parses then the
 * specific expression and marks the node in the AST. </p>
 *
 * @author patrick (3/27/13)
 */
public class ParseletProvider {

    private static ParseletProvider instance = null;

    private static final Map<IElementType, PrefixParselet> prefixParselets = new HashMap<IElementType, PrefixParselet>();
    private static final Map<IElementType, InfixParselet> infixParselets = new HashMap<IElementType, InfixParselet>();

    private static final Map<PrefixParselet, IElementType> prefixToPsiElement = new HashMap<PrefixParselet, IElementType>();
    private static final Map<InfixParselet, IElementType> infixParsletToPsiElement = new HashMap<InfixParselet, IElementType>();


    /**
     * Provides the prefix-parselet which is connected to the specified token.
     * @param token The token for which the parselet is wanted
     * @return The {@link PrefixParselet} if available for this token and {@code null} otherwise.
     */
    public static PrefixParselet getPrefixParselet(IElementType token) {
        if (instance == null) {
            instance = new ParseletProvider();
        }
        return prefixParselets.get(token);
    }

    /**
     * Provides the infix-parselet which is connected to the specified token.
     * @param token The token for which the parselet is wanted
     * @return The {@link InfixParselet} if available for this token and {@code null} otherwise.
     */
    public static InfixParselet getInfixParselet(IElementType token) {
        if (instance == null) {
            instance = new ParseletProvider();
        }
        return infixParselets.get(token);
    }

    /**
     * Extracts the precedence of an infix operator connected to the specified token
     * @param token Token for which the precedence is required
     * @return The precedence of the specified token or 0 whether the precedence is not available.
     */
    public static int getPrecedence(IElementType token) {
        if (instance == null) {
            instance = new ParseletProvider();
        }
        InfixParselet parselet = infixParselets.get(token);
        if (parselet != null) {
            return parselet.getPrecedence();
        }
        return 0;
    }

    /**
     * Extracts the precedence of an infix operator connected to the first token in the token stream of builder.
     * @param builder Builder from which the first token is extracted to find the required precedence
     * @return The precedence of the specified token or 0 whether the precedence is not available.
     */
    public static int getPrecedence(PsiBuilder builder) {
        if (instance == null) {
            instance = new ParseletProvider();
        }
        IElementType token = builder.getTokenType();
        if (token == null) {
            return 0;
        }
        InfixParselet parselet = infixParselets.get(token);
        if (parselet != null) {
            return parselet.getPrecedence();
        }
        return 0;
    }

    /**
     * Provides the parselet with the element type of the node for the AST. When an expression was parsed with a
     * specific {@link InfixParselet} the node in the AST is then the {@link IElementType} which is returned by this
     * method.
     * E.g. When a PLUS token arises in the lexer token stream first the infix-parselet of the PLUS token is extracted
     * with {@link #getInfixParselet(com.intellij.psi.tree.IElementType)} which parses the left and right operand.
     * Afterwards it marks the whole expression a+b in the AST tree as being a node of type returned by this
     * method.
     * @param parselet The parselet for which the node type is wanted
     * @return The node type.
     */
    public static IElementType getInfixPsiElement(InfixParselet parselet) {
        IElementType elm = infixParsletToPsiElement.get(parselet);
        if (elm == null) {
            return MathematicaElementTypes.FAILBACK;
        }
        return elm;
    }

    /**
     * Please see {@link #getInfixPsiElement(de.halirutan.mathematica.parsing.prattParser.parselets.InfixParselet)}
     * @param parselet The parselet for which the type is wanted
     * @return The element of the node in the AST tree
     */
    public static IElementType getPrefixPsiElement(PrefixParselet parselet) {
        IElementType elm = prefixToPsiElement.get(parselet);
        if (elm == null) {
            return MathematicaElementTypes.FAILBACK;
        }
        return elm;
    }

    private ParseletProvider() {
        registerAll();
    }

    private static void register(IElementType token, IElementType expressionToken, PrefixParselet parselet) {
        prefixParselets.put(token, parselet);
        prefixToPsiElement.put(parselet, expressionToken);
    }

    private static void register(IElementType token, IElementType expressionToken, InfixParselet parselet) {
        infixParselets.put(token, parselet);
        infixParsletToPsiElement.put(parselet, expressionToken);
    }

    private static void postfix(IElementType token, IElementType expressionToken, int precedence) {
        register(token, expressionToken, new PostfixOperatorParselet(precedence));
    }

    private static void prefix(IElementType token, IElementType expressionToken,int precedence) {
        register(token, expressionToken, new PrefixOperatorParselet(precedence));
    }

    private static void infixLeft(IElementType token, IElementType expressionToken,int precedence) {
        register(token, expressionToken, new InfixOperatorParselet(precedence, false));
    }

    private static void infixRight(IElementType token, IElementType expressionToken, int precedence) {
        register(token, expressionToken, new InfixOperatorParselet(precedence, true));
    }



// THIS SECTION IS AUTOMATICALLY CREATED WITH MATHEMATICA

    private void registerAll(){
        register(MathematicaElementTypes.LEFT_PAR,	MathematicaElementTypes.GROUP_EXPRESSION,	 new GroupParselet(82)); // Group(()
        register(MathematicaElementTypes.LEFT_BRACE,	MathematicaElementTypes.LIST_EXPRESSION,	 new ListParselet(82)); // Group(()

//        register(MathematicaElementTypes.RIGHT_PAR,	MathematicaElementTypes.UNBALANCED_PARANTHESIS,	 new UnbalancedParselet(82)); // Group(()
//        register(MathematicaElementTypes.RIGHT_BRACKET,	MathematicaElementTypes.UNBALANCED_PARANTHESIS,	 new UnbalancedParselet(82)); // Group(()
//        register(MathematicaElementTypes.RIGHT_BRACE,	MathematicaElementTypes.UNBALANCED_PARANTHESIS,	 new UnbalancedParselet(82)); // Group(()

        register(MathematicaElementTypes.NUMBER,	MathematicaElementTypes.NUMBER_EXPRESSION,	 new NumberParselet(80)); // Number(123)
        register(MathematicaElementTypes.IDENTIFIER,	MathematicaElementTypes.SYMBOL_EXPRESSION,	 new SymbolParselet(80)); // Symbol($var)
        register(MathematicaElementTypes.STRING_LITERAL_BEGIN,	MathematicaElementTypes.STRING_EXPRESSION,	 new StringParselet(80)); // String(abc)

        register(MathematicaElementTypes.DOUBLE_COLON,	MathematicaElementTypes.MESSAGE_NAME_EXPRESSION,	 new MessageNameParselet(78)); // MessageName(::)

        register(MathematicaElementTypes.SLOT_SEQUENCE, MathematicaElementTypes.SLOT_SEQUENCE, new SymbolParselet(77)); // ##n expressions
        register(MathematicaElementTypes.SLOT, MathematicaElementTypes.SLOT, new SymbolParselet(77)); // ##n expressions

        register(MathematicaElementTypes.BLANK, MathematicaElementTypes.BLANK_EXPRESSION, new BlankParselet(76)); // Blank(_)
        register(MathematicaElementTypes.BLANK, MathematicaElementTypes.BLANK_EXPRESSION, new PrefixBlankParselet(76)); // Blank(_)
        register(MathematicaElementTypes.BLANK_SEQUENCE, MathematicaElementTypes.BLANK_SEQUENCE_EXPRESSION, new BlankSequenceParselet(76)); // BlankSequence(__)
        register(MathematicaElementTypes.BLANK_SEQUENCE, MathematicaElementTypes.BLANK_SEQUENCE_EXPRESSION, new PrefixBlankSequenceParselet(76)); // BlankSequence(__)
        register(MathematicaElementTypes.BLANK_NULL_SEQUENCE, MathematicaElementTypes.BLANK_NULL_SEQUENCE_EXPRESSION, new BlankNullSequenceParselet(76)); // BlankNullSequence(___)
        register(MathematicaElementTypes.BLANK_NULL_SEQUENCE, MathematicaElementTypes.BLANK_NULL_SEQUENCE_EXPRESSION, new PrefixBlankNullSequenceParselet(76)); // BlankNullSequence(___)
        postfix(MathematicaElementTypes.OPTIONAL, MathematicaElementTypes.OPTIONAL_EXPRESSION, 76); // Optional(_.)

        prefix(MathematicaElementTypes.GET,	MathematicaElementTypes.GET_PREFIX,	74); // Get(<<)

        infixLeft(MathematicaElementTypes.QUESTION_MARK,	MathematicaElementTypes.PATTERN_TEST_EXPRESSION,	72); // PatternTest(?)

        register(MathematicaElementTypes.LEFT_BRACKET,	MathematicaElementTypes.FUNCTION_CALL_EXPRESSION,	 new FunctionCallParselet(70)); // FunctionCall([)
        register(MathematicaElementTypes.PART_BEGIN,	MathematicaElementTypes.PART_EXPRESSION,	 new PartParselet(70)); // Part([[)

        postfix(MathematicaElementTypes.INCREMENT,	MathematicaElementTypes.INCREMENT_POSTFIX,	68);
        postfix(MathematicaElementTypes.DECREMENT,	MathematicaElementTypes.DECREMENT_POSTFIX,	68);

        prefix(MathematicaElementTypes.INCREMENT,	MathematicaElementTypes.PRE_INCREMENT_PREFIX,	66); // PreIncrement(++)
        prefix(MathematicaElementTypes.DECREMENT,	MathematicaElementTypes.PRE_DECREMENT_PREFIX,	66); // PreDecrement(--)

        infixRight(MathematicaElementTypes.PREFIX_CALL,	MathematicaElementTypes.PREFIX_CALL_EXPRESSION,	64); // PrefixCall(@)

        register(MathematicaElementTypes.INFIX_CALL,	MathematicaElementTypes.INFIX_CALL_EXPRESSION,	 new InfixCallParselet(62)); // InfixCall(~)

        infixRight(MathematicaElementTypes.MAP,	MathematicaElementTypes.MAP_EXPRESSION,	60); // Map(/@)
        infixRight(MathematicaElementTypes.MAP_ALL,	MathematicaElementTypes.MAP_ALL_EXPRESSION,	60); // MapAll(//@)
        infixRight(MathematicaElementTypes.APPLY,	MathematicaElementTypes.APPLY_EXPRESSION,	60); // Apply(@@)
        infixRight(MathematicaElementTypes.APPLY1,	MathematicaElementTypes.APPLY1_EXPRESSION,	60); // Apply1(@@@)

        postfix(MathematicaElementTypes.EXCLAMATION_MARK,	MathematicaElementTypes.FACTORIAL_POSTFIX,	58);

        register(MathematicaElementTypes.DERIVATIVE,	MathematicaElementTypes.DERIVATIVE_EXPRESSION,	 new DerivativeParselet(56)); // Derivative(')

        infixLeft(MathematicaElementTypes.STRING_JOIN,	MathematicaElementTypes.STRING_JOIN_EXPRESSION,	54); // StringJoin(<>)

        infixRight(MathematicaElementTypes.POWER,	MathematicaElementTypes.POWER_EXPRESSION,	52); // Power(^)

        infixLeft(MathematicaElementTypes.NON_COMMUTATIVE_MULTIPLY,	MathematicaElementTypes.NON_COMMUTATIVE_MULTIPLY_EXPRESSION,	50); // NonCommutativeMultiply(**)

        infixLeft(MathematicaElementTypes.POINT,	MathematicaElementTypes.DOT_EXPRESSION,	48); // Dot(.)

        prefix(MathematicaElementTypes.MINUS,	MathematicaElementTypes.UNARY_MINUS_PREFIX,	46); // UnaryMinus(-)
        prefix(MathematicaElementTypes.PLUS,	MathematicaElementTypes.UNARY_PLUS_PREFIX,	46); // UnaryPlus(+)

        infixLeft(MathematicaElementTypes.DIVIDE,	MathematicaElementTypes.DIVIDE_EXPRESSION,	44); // Divide(/)

        infixLeft(MathematicaElementTypes.TIMES,	MathematicaElementTypes.TIMES_EXPRESSION,	42); // Times(*)

        infixLeft(MathematicaElementTypes.PLUS,	MathematicaElementTypes.PLUS_EXPRESSION,	40); // Plus(+)
        infixLeft(MathematicaElementTypes.MINUS,	MathematicaElementTypes.MINUS_EXPRESSION,	40); // Minus(-)

        register(MathematicaElementTypes.SPAN,	MathematicaElementTypes.SPAN_EXPRESSION,	 new SpanParselet(38)); // Span(;;)

        infixLeft(MathematicaElementTypes.EQUAL,	MathematicaElementTypes.EQUAL_EXPRESSION,	36); // Equal(==)
        infixLeft(MathematicaElementTypes.UNEQUAL,	MathematicaElementTypes.UNEQUAL_EXPRESSION,	36); // Unequal(!=)
        infixLeft(MathematicaElementTypes.GREATER,	MathematicaElementTypes.GREATER_EXPRESSION,	36); // Greater(>)
        infixLeft(MathematicaElementTypes.GREATER_EQUAL,	MathematicaElementTypes.GREATER_EQUAL_EXPRESSION,	36); // GreaterEqual(>=)
        infixLeft(MathematicaElementTypes.LESS,	MathematicaElementTypes.LESS_EXPRESSION,	36); // Less(<)
        infixLeft(MathematicaElementTypes.LESS_EQUAL,	MathematicaElementTypes.LESS_EQUAL_EXPRESSION,	36); // LessEqual(<=)

        infixLeft(MathematicaElementTypes.SAME_Q,	MathematicaElementTypes.SAME_Q_EXPRESSION,	34); // SameQ(===)
        infixLeft(MathematicaElementTypes.UNSAME_Q,	MathematicaElementTypes.UNSAME_Q_EXPRESSION,	34); // UnsameQ(=!=)

        prefix(MathematicaElementTypes.EXCLAMATION_MARK,	MathematicaElementTypes.NOT_PREFIX,	32); // Not(!)

        infixLeft(MathematicaElementTypes.AND,	MathematicaElementTypes.AND_EXPRESSION,	30); // And(&&)

        infixLeft(MathematicaElementTypes.OR,	MathematicaElementTypes.OR_EXPRESSION,	28); // Or(||)

        postfix(MathematicaElementTypes.REPEATED,	MathematicaElementTypes.REPEATED_POSTFIX,	26);
        postfix(MathematicaElementTypes.REPEATED_NULL,	MathematicaElementTypes.REPEATED_NULL_POSTFIX,	26);

        infixLeft(MathematicaElementTypes.ALTERNATIVE,	MathematicaElementTypes.ALTERNATIVE_EXPRESSION,	24); // Alternative(|)

        infixLeft(MathematicaElementTypes.COLON, MathematicaElementTypes.PATTERN_EXPRESSION, 22); // Optional(:) and Patter (:)

        infixLeft(MathematicaElementTypes.STRING_EXPRESSION,	MathematicaElementTypes.STRING_EXPRESSION_EXPRESSION,	20); // StringExpression(~~)

        infixLeft(MathematicaElementTypes.CONDITION,	MathematicaElementTypes.CONDITION_EXPRESSION,	18); // Condition(/;)

        infixRight(MathematicaElementTypes.RULE,	MathematicaElementTypes.RULE_EXPRESSION,	16); // Rule(->)
        infixRight(MathematicaElementTypes.RULE_DELAYED,	MathematicaElementTypes.RULE_DELAYED_EXPRESSION,	16); // RuleDelayed(:>)

        infixLeft(MathematicaElementTypes.REPLACE_ALL,	MathematicaElementTypes.REPLACE_ALL_EXPRESSION,	14); // ReplaceAll(/.)
        infixLeft(MathematicaElementTypes.REPLACE_REPEATED,	MathematicaElementTypes.REPLACE_REPEATED_EXPRESSION,	14); // ReplaceRepeated(//.)

        infixRight(MathematicaElementTypes.ADD_TO,	MathematicaElementTypes.ADD_TO_EXPRESSION,	12); // AddTo(+=)
        infixRight(MathematicaElementTypes.SUBTRACT_FROM,	MathematicaElementTypes.SUBTRACT_FROM_EXPRESSION,	12); // SubtractFrom(-=)
        infixRight(MathematicaElementTypes.TIMES_BY,	MathematicaElementTypes.TIMES_BY_EXPRESSION,	12); // TimesBy(*=)
        infixRight(MathematicaElementTypes.DIVIDE_BY,	MathematicaElementTypes.DIVIDE_BY_EXPRESSION,	12); // DivideBy(/=)

        postfix(MathematicaElementTypes.FUNCTION,	MathematicaElementTypes.FUNCTION_POSTFIX,	10);

        infixLeft(MathematicaElementTypes.POSTFIX,	MathematicaElementTypes.POSTFIX_EXPRESSION,	8); // Postfix(//)

        infixRight(MathematicaElementTypes.SET,	MathematicaElementTypes.SET_EXPRESSION,	6); // Set(=)
        infixRight(MathematicaElementTypes.SET_DELAYED,	MathematicaElementTypes.SET_DELAYED_EXPRESSION,	6); // SetDelayed(:=)
        infixRight(MathematicaElementTypes.UP_SET,	MathematicaElementTypes.UP_SET_EXPRESSION,	6); // UpSet(^=)
        infixRight(MathematicaElementTypes.UP_SET_DELAYED,	MathematicaElementTypes.UP_SET_DELAYED_EXPRESSION,	6); // UpSetDelayed(^:=)
        register(MathematicaElementTypes.TAG_SET,	MathematicaElementTypes.TAG_SET_EXPRESSION,	 new TagSetParselet(6)); // TagSet(/:)
        infixLeft(MathematicaElementTypes.UNSET,	MathematicaElementTypes.UNSET_EXPRESSION,	6); // Unset(=.)

        infixLeft(MathematicaElementTypes.PUT,	MathematicaElementTypes.PUT_EXPRESSION,	4); // Put(>>)
        infixLeft(MathematicaElementTypes.PUT_APPEND,	MathematicaElementTypes.PUT_APPEND_EXPRESSION,	4); // PutAppend(>>>)

        register(MathematicaElementTypes.SEMICOLON, MathematicaElementTypes.COMPOUND_EXPRESSION_EXPRESSION, new CompoundExpressionParselet(2)); // CompoundExpression(;)
//        infixLeft(MathematicaElementTypes.SEMICOLON,	MathematicaElementTypes.COMPOUND_EXPRESSION_EXPRESSION,	 2); // CompoundExpression(;)
//        postfix(MathematicaElementTypes.SEMICOLON, MathematicaElementTypes.COMPOUND_EXPRESSION_EXPRESSION, 2); // CompoundExpression(;)

//        infixLeft(MathematicaElementTypes.COMMA,	MathematicaElementTypes.COMPOUND_EXPRESSION_EXPRESSION,	 1); // Sequence(,)
//        postfix(MathematicaElementTypes.COMMA, MathematicaElementTypes.COMPOUND_EXPRESSION_EXPRESSION, 1); // Sequence(,)

    }
}