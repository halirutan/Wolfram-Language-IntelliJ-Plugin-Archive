package org.ipcu.mathematicaPlugin.parser.parselets;

import com.intellij.lang.PsiBuilder;
import org.ipcu.mathematicaPlugin.parser.MathematicaParser;

import static org.ipcu.mathematicaPlugin.MathematicaElementTypes.*;

/**
 * @author patrick (3/29/13)
 */
public class ListParselet implements PrefixParselet {

    final int precedence;

    public ListParselet(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser) {
        final PsiBuilder.Marker listMarker = parser.mark();
        return EnclosedExpressionSequence.parse(parser,
                listMarker, LIST_EXPRESSION, LEFT_BRACE, RIGHT_BRACE, "'}' expected");
    }
}
