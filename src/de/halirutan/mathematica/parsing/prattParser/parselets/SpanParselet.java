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

package de.halirutan.mathematica.parsing.prattParser.parselets;

import com.intellij.lang.PsiBuilder;
import de.halirutan.mathematica.parsing.prattParser.CriticalParserError;
import de.halirutan.mathematica.parsing.prattParser.MathematicaParser;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.SPAN;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.SPAN_EXPRESSION;
import static de.halirutan.mathematica.parsing.prattParser.ParseletProvider.getPrefixParselet;

/**
 * Parses <code>Span</code> constructs. This parselet handles not situations like <code>list[[;;]]</code> or
 * <code>list[[;;i]]</code> because this is a prefix operator and not infix.
 *
 * @author patrick (3/27/13)
 */
public class SpanParselet implements InfixParselet {
    private final int precedence;

    public SpanParselet(int precedence) {
        this.precedence = precedence;
    }


    // Parses things like <code>expr1;;expr2</code>, <code>expr0;; ;;expr1</code> or <code>expr0;;expr1;;expr2</code>.
    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        final PsiBuilder.Marker spanMark = left.getMark().precede();
        boolean skipped = false;
        parser.advanceLexer();
        // if we meet a second ;; right after the first ;; we just skip it
        if (parser.testToken(SPAN)) {
            skipped = true;
            parser.advanceLexer();
        }

        // since here we met either expr0;; or expr0;;;;. By testing whether the next token is start of a new expression
        // (which requires to start with a prefix operator) we can tell whether we met the form list[[expr0;;]] or
        // which is a syntax error list[expr0;;;;]].
        PrefixParselet nextPrefix = getPrefixParselet(parser.getTokenType());
        if (nextPrefix == null) {
            if (!skipped) {
                spanMark.done(SPAN_EXPRESSION);
            } else {
                spanMark.error("Expression expected after  \"expr0;; ;;\"");
            }
            return parser.result(spanMark, SPAN_EXPRESSION, left.parsed() && !skipped);
        }

        final MathematicaParser.Result expr1 = parser.parseExpression(precedence);

        // if we had expr0;;;;expr1
        if (skipped) {
            spanMark.done(SPAN_EXPRESSION);
            return parser.result(spanMark, SPAN_EXPRESSION, left.parsed() && expr1.parsed());
        }

        if (parser.testToken(SPAN)) {
            parser.advanceLexer();
            final MathematicaParser.Result expr2 = parser.parseExpression(precedence);
            if (expr2.parsed()) {
                spanMark.done(SPAN_EXPRESSION);
            } else
                spanMark.error("Expression expected after \"expr0;;expr1;;\"");
            return parser.result(spanMark, SPAN_EXPRESSION, left.parsed() && expr1.parsed() && expr2.parsed());
        } else {
            // we have the form expr0;;expr1
            spanMark.done(SPAN_EXPRESSION);
            return parser.result(spanMark, SPAN_EXPRESSION, left.parsed() && expr1.parsed());
        }
    }

    @Override
    public int getPrecedence() {
        return precedence;
    }
}
