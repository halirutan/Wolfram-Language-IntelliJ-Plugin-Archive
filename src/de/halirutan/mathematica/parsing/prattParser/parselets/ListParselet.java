package de.halirutan.mathematica.parsing.prattParser.parselets;

import com.intellij.lang.PsiBuilder;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (3/29/13)
 */
public class ListParselet implements PrefixParselet {

    final int precedence;

    public ListParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public de.halirutan.mathematica.parsing.prattParser.MathematicaParser.Result parse(MathematicaParser parser) throws CriticalParserError {
        final PsiBuilder.Marker listMarker = parser.mark();
        boolean result = true;

        if(parser.testToken(LEFT_BRACE)) {
            parser.advanceLexer();
        } else {
            listMarker.drop();
            throw new CriticalParserError("List parselet does not start with {");
        }

        final MathematicaParser.Result seqResult = ParserUtil.parseSequence(parser, RIGHT_BRACE);

        if (parser.testToken(RIGHT_BRACE)) {
            parser.advanceLexer();
        } else {
            parser.error("Closing '}' expected");
            result = false;
        }
        listMarker.done(LIST_EXPRESSION);
        return parser.result(listMarker, LIST_EXPRESSION, result && seqResult.parsed());
    }
}
