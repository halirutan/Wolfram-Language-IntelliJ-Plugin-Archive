package org.ipcu.mathematicaPlugin;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.ipcu.mathematicaPlugin.MathematicaElementType;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/27/12
 * Time: 2:34 AM
 * Purpose:
 */
public interface MathematicaElementTypes {

    IFileElementType FILE = new IFileElementType(MathematicaLanguage.INSTANCE);

    IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    IElementType COMMENT = new MathematicaElementType("COMMENT");

    IElementType STRING_LITERAL = new MathematicaElementType("STRING_LITERAL");
    IElementType IDENTIFIER = new MathematicaElementType("IDENTIFIER");

    IElementType LITERAL = new MathematicaElementType("INTEGER_LITERAL");

    IElementType RIGHT_PAR = new MathematicaElementType("RIGHT_PAR");
    IElementType LEFT_PAR = new MathematicaElementType("LEFT_PAR");
    IElementType LEFT_BRACE = new MathematicaElementType("LEFT_BRACE");
    IElementType RIGHT_BRACE = new MathematicaElementType("RIGHT_BRACE");
    IElementType LEFT_BRACKET = new MathematicaElementType("LEFT_BRACKET");
    IElementType RIGHT_BRACKET = new MathematicaElementType("RIGHT_BRACKET");

    IElementType ACCURACY = new MathematicaElementType("ACCURACY");
    IElementType COMMA = new MathematicaElementType("COMMA");

    IElementType PREFIX = new MathematicaElementType("PREFIX");
    IElementType POSTFIX = new MathematicaElementType("POSTFIX");
    IElementType MAP = new MathematicaElementType("MAP");
    IElementType APPLY = new MathematicaElementType("APPLY");
    IElementType APPLY1 = new MathematicaElementType("APPLY1");
    IElementType REPLACE_ALL = new MathematicaElementType("REPLACE_ALL");
    IElementType REPLACE_REPEATED = new MathematicaElementType("REPLACE_REPEATED");

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
    IElementType OPTIONAL = new MathematicaElementType("OPTIONAL");

    IElementType COLON = new MathematicaElementType("COLON");
    IElementType DOUBLE_COLON = new MathematicaElementType("DOUBLE_COLON");
    IElementType SEMICOLON = new MathematicaElementType("SEMICOLON");
    IElementType SPAN = new MathematicaElementType("SPAN");
    IElementType OUT = new MathematicaElementType("OUT");
    IElementType STRING_EXPRESSION = new MathematicaElementType("STRING_EXPRESSION");
    IElementType STRING_JOIN = new MathematicaElementType("STRING_JOIN");
    IElementType POINT = new MathematicaElementType("POINT");
    
    IElementType AND = new MathematicaElementType("AND");
    IElementType OR = new MathematicaElementType("OR");
    IElementType ALTERNATIVE = new MathematicaElementType("ALTERNATIVE");

    IElementType EXCLAMATION_MARK = new MathematicaElementType("EXCLAMATION_MARK");
    IElementType QUESTION_MARK = new MathematicaElementType("QUESTION_MARK");
    
    IElementType SLOT = new MathematicaElementType("SLOT");
    IElementType SLOT_SEQUENCE = new MathematicaElementType("SLOT_SEQUENCE");
    IElementType FUNCTION = new MathematicaElementType("FUNCTION");
    
    IElementType BACK_TICK = new MathematicaElementType("BACK_TICK");
    IElementType INFIX = new MathematicaElementType("INFIX");
    IElementType GET = new MathematicaElementType("GET");
    IElementType PUT = new MathematicaElementType("PUT");
    IElementType PUT_APPEND = new MathematicaElementType("PUT_APPEND");
    


    TokenSet WHITE_SPACES = TokenSet.create(
            WHITE_SPACE
    );

    TokenSet COMMENTS = TokenSet.create(
            COMMENT
    );
    TokenSet STRING_LITERALS = TokenSet.create(
            STRING_LITERAL
    );

    TokenSet OPERATORS = TokenSet.create(
            ACCURACY, ADD_TO, ALTERNATIVE, AND, APPLY, APPLY1,
            BACK_TICK, BLANK, BLANK_NULL_SEQUENCE, BLANK_SEQUENCE,
            COLON, CONDITION,
            DECREMENT, DIVIDE, DIVIDE_BY, DOUBLE_COLON, EQUAL,
            EXCLAMATION_MARK,
            FUNCTION,
            GET, GREATER, GREATER_EQUAL,
            INCREMENT, INFIX,
            LESS, LESS_EQUAL,
            MAP, MINUS,
            NON_COMMUTATIVE_MULTIPLY,
            OPTIONAL, OR, OUT,
            PLUS, POSTFIX, PREFIX, PUT, PUT_APPEND,
            QUESTION_MARK,
            REPEATED, REPEATED_NULL, REPLACE_ALL, REPLACE_REPEATED, RULE, RULE_DELAYED,
            SAME_Q, SEMICOLON, SET, SET_DELAYED,
            TAG_SET, TIMES, TIMES_BY,
            UNEQUAL, UNSAME_Q, UNSET, UP_SET, UP_SET_DELAYED
    );

    TokenSet LITERALS = TokenSet.create(
            LITERAL
    );

    TokenSet BRACES = TokenSet.create(
            LEFT_BRACE,LEFT_BRACKET,LEFT_PAR,
            RIGHT_BRACE,RIGHT_BRACKET,RIGHT_PAR
    );
}
