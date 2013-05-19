package de.halirutan.mathematica.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.LinkedList;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;

%%

%class _MathematicaLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

%{
    // This adds support for nested states. I'm no JFlex pro, so maybe this is overkill, but it works quite well.
    private final LinkedList<Integer> states = new LinkedList();

    private void yypushstate(int state) {
        states.addFirst(yystate());
        yybegin(state);
    }
    private void yypopstate() {
        final int state = states.removeFirst();
        yybegin(state);
    }

%}


LineTerminator = \n | \r | \r\n
WhiteSpace = [\ \t\f]

Comment   = "(*" [^*] ~"*)" | "(*" "*"+ ")"

CommentStart = "(*"
CommentEnd = "*)"

Identifier = [a-zA-Z\$] [a-zA-Z0-9\$]*
IdInContext = (`?){Identifier}(`{Identifier})*(`?)

NamedCharacter = "\\["{Identifier}"]"

Digits = [0-9]+
Digits2 = [0-9a-zA-Z]+
Base = 2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36
Number = {Digits}(\.{Digits}?)? | \.{Digits}
PrecisionNumber = {Number}`((`?){Number})?
BaseNumber = {Base} "^^" {Digits2}(\.{Digits2}?)?
BasePrecisionNumber = {BaseNumber}((`{Number}?)|(``{Number}))
ScientificInteger = {Number} "\*^"(-?){Digits}
ScientificNumber = {PrecisionNumber} "\*^"(-?){Digits}
BaseScientificNumber = {BasePrecisionNumber} "\*^"(-?){Digits}

Slot = "#" [0-9]*
SlotSequence = "##" [0-9]*

Out = "%"+


%xstate IN_COMMENT
%xstate IN_STRING

%%

<YYINITIAL> {
	"(*"				{ yypushstate(IN_COMMENT); return MathematicaElementTypes.COMMENT;}
	{WhiteSpace}+ 		{ return MathematicaElementTypes.WHITE_SPACE; }
    "\\"{LineTerminator}  { return MathematicaElementTypes.WHITE_SPACE; }

	{LineTerminator}+   { return MathematicaElementTypes.LINE_BREAK; }
	\"				 	{ yypushstate(IN_STRING); return MathematicaElementTypes.STRING_LITERAL_BEGIN; }

	{IdInContext} 		{ return MathematicaElementTypes.IDENTIFIER; }
	{NamedCharacter}    { return MathematicaElementTypes.IDENTIFIER; }

	{BaseScientificNumber}|
	{BasePrecisionNumber}|
	{ScientificInteger}|
	{BaseNumber}|
	{ScientificNumber}|
	{PrecisionNumber}|
	{Number}  			{ return MathematicaElementTypes.NUMBER; }

	"``"				{ return MathematicaElementTypes.ACCURACY; }
	"["					{ return MathematicaElementTypes.LEFT_BRACKET; }
	"]"					{ return MathematicaElementTypes.RIGHT_BRACKET; }
	"("					{ return MathematicaElementTypes.LEFT_PAR; }
	")"					{ return MathematicaElementTypes.RIGHT_PAR; }
	"{"					{ return MathematicaElementTypes.LEFT_BRACE; }
	"}"					{ return MathematicaElementTypes.RIGHT_BRACE; }
	"@@@"				{ return MathematicaElementTypes.APPLY1; }
	"@@"				{ return MathematicaElementTypes.APPLY; }
	"@"					{ return MathematicaElementTypes.PREFIX_CALL; }
	"//@"				{ return MathematicaElementTypes.MAP_ALL; }
	"/@"				{ return MathematicaElementTypes.MAP; }

	{Out}				{ return MathematicaElementTypes.OUT; }

	"^:="				{ return MathematicaElementTypes.UP_SET_DELAYED; }
	"^="				{ return MathematicaElementTypes.UP_SET; }
	":="				{ return MathematicaElementTypes.SET_DELAYED; }
	"->"				{ return MathematicaElementTypes.RULE; }
	":>"				{ return MathematicaElementTypes.RULE_DELAYED; }
	"//."				{ return MathematicaElementTypes.REPLACE_REPEATED; }
	"/."				{ return MathematicaElementTypes.REPLACE_ALL; }
	"/;"				{ return MathematicaElementTypes.CONDITION; }
	"/:"				{ return MathematicaElementTypes.TAG_SET; }

	">>>"				{ return MathematicaElementTypes.PUT_APPEND; }
	">>"				{ return MathematicaElementTypes.PUT; }
	"<<"				{ return MathematicaElementTypes.GET; }

	"___"				{ return MathematicaElementTypes.BLANK_NULL_SEQUENCE; }
	"__"				{ return MathematicaElementTypes.BLANK_SEQUENCE; }
	"_."				{ return MathematicaElementTypes.DEFAULT; }
	"_"					{ return MathematicaElementTypes.BLANK; }

	"//"				{ return MathematicaElementTypes.POSTFIX; }

	"==="				{ return MathematicaElementTypes.SAME_Q; }
	"=!="				{ return MathematicaElementTypes.UNSAME_Q; }
	"=="				{ return MathematicaElementTypes.EQUAL; }
	"!="				{ return MathematicaElementTypes.UNEQUAL; }
	"<="				{ return MathematicaElementTypes.LESS_EQUAL; }
	">="				{ return MathematicaElementTypes.GREATER_EQUAL; }
	"<"					{ return MathematicaElementTypes.LESS; }
	">"					{ return MathematicaElementTypes.GREATER; }
	"+="				{ return MathematicaElementTypes.ADD_TO; }
	"-="				{ return MathematicaElementTypes.SUBTRACT_FROM; }
	"*="				{ return MathematicaElementTypes.TIMES_BY; }
	"/="				{ return MathematicaElementTypes.DIVIDE_BY; }

	"++"				{ return MathematicaElementTypes.INCREMENT; }
	"+"					{ return MathematicaElementTypes.PLUS; }
	"--"				{ return MathematicaElementTypes.DECREMENT; }
	"-"					{ return MathematicaElementTypes.MINUS; }
	"**"				{ return MathematicaElementTypes.NON_COMMUTATIVE_MULTIPLY; }
	"*"					{ return MathematicaElementTypes.TIMES; }
	"/"					{ return MathematicaElementTypes.DIVIDE; }
	"^"					{ return MathematicaElementTypes.POWER; }



    "<>"				{ return MathematicaElementTypes.STRING_JOIN; }
    "~~"				{ return MathematicaElementTypes.STRING_EXPRESSION; }
    "~"					{ return MathematicaElementTypes.INFIX_CALL; }

    "`"					{ return MathematicaElementTypes.BACK_TICK; }

    ","					{ return MathematicaElementTypes.COMMA; }
	"..."				{ return MathematicaElementTypes.REPEATED_NULL; }
	"=."				{ return MathematicaElementTypes.UNSET; }
	".."				{ return MathematicaElementTypes.REPEATED; }
	"."					{ return MathematicaElementTypes.POINT; }
	";;"				{ return MathematicaElementTypes.SPAN; }
	";"					{ return MathematicaElementTypes.SEMICOLON; }
	"::"				{ return MathematicaElementTypes.DOUBLE_COLON; }
	":"					{ return MathematicaElementTypes.COLON; }

	"="					{ return MathematicaElementTypes.SET; }


    {SlotSequence}		{ return MathematicaElementTypes.SLOT_SEQUENCE; }
    {Slot}				{ return MathematicaElementTypes.SLOT; }

    "?"					{ return MathematicaElementTypes.QUESTION_MARK; }
    "!"					{ return MathematicaElementTypes.EXCLAMATION_MARK; }

    "||"				{ return MathematicaElementTypes.OR; }
    "|"					{ return MathematicaElementTypes.ALTERNATIVE; }
    "&&"				{ return MathematicaElementTypes.AND; }
    "&"					{ return MathematicaElementTypes.FUNCTION; }

    "'"					{ return MathematicaElementTypes.DERIVATIVE; }

    "'"					{ return MathematicaElementTypes.DERIVATIVE; }

	.       			{ return MathematicaElementTypes.BAD_CHARACTER; }
}

//<IN_STRING> {
//	\\                  { return MathematicaElementTypes.STRING_LITERAL; }
//	(\\\" | [^\"])*		{ return MathematicaElementTypes.STRING_LITERAL; }
//	\"					{ yypushstate(YYINITIAL); return MathematicaElementTypes.STRING_LITERAL_END; }
//
//}

<IN_STRING> {
  \"                             { yypopstate(); return MathematicaElementTypes.STRING_LITERAL_END; }
  [^\"\\]+                       { return MathematicaElementTypes.STRING_LITERAL; }
  "\\"{LineTerminator}           { return MathematicaElementTypes.STRING_LITERAL; }
  "\\\\"                         {  return MathematicaElementTypes.STRING_LITERAL; }
  "\\\""                         { return MathematicaElementTypes.STRING_LITERAL; }
  "\\"                           { return MathematicaElementTypes.STRING_LITERAL; }
}


<IN_COMMENT> {
	"(*"				{ yypushstate(IN_COMMENT); return MathematicaElementTypes.COMMENT; }
	[^\*\)\(]*			{ return MathematicaElementTypes.COMMENT; }
	"*)"				{ yypopstate(); return MathematicaElementTypes.COMMENT; }
	[\*\)\(]			{ return MathematicaElementTypes.COMMENT; }
	.					{ return MathematicaElementTypes.BAD_CHARACTER; }

}

.|{LineTerminator}+ 	{ return MathematicaElementTypes.BAD_CHARACTER; }