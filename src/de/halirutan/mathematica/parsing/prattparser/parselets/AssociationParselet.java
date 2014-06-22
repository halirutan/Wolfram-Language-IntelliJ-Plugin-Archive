/*
 * Copyright (c) 2014 Patrick Scheibe
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

package de.halirutan.mathematica.parsing.prattparser.parselets;

import com.intellij.lang.PsiBuilder;
import de.halirutan.mathematica.parsing.prattparser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattparser.MathematicaParser;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * Created by rsmenon on 3/28/14.
 */
public class AssociationParselet implements PrefixParselet {

    private final int myPrecedence;

    public AssociationParselet(int precedence) {
        myPrecedence = precedence;
    }

    @Override
    public MathematicaParser.Result parse(MathematicaParser parser) throws CriticalParserError {
        PsiBuilder.Marker listMarker = parser.mark();
        boolean result = true;

        if (parser.matchesToken(LEFT_ASSOCIATION)) {
            parser.advanceLexer();
        } else {
            listMarker.drop();
            throw new CriticalParserError("Association parselet does not start with <|");
        }

        MathematicaParser.Result seqResult = ParserUtil.parseSequence(parser, RIGHT_ASSOCIATION);

        if (parser.matchesToken(RIGHT_ASSOCIATION)) {
            parser.advanceLexer();
        } else {
            parser.error("Closing '}' expected");
            result = false;
        }
        listMarker.done(ASSOCIATION_EXPRESSION);
        return MathematicaParser.result(listMarker, ASSOCIATION_EXPRESSION, result && seqResult.isMyParsed());
    }

    public int getPrecedence() {
        return myPrecedence;
    }
}
