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
    private final int m_precedence;

    public SpanParselet(int precedence) {
        this.m_precedence = precedence;
    }


    // Parses things like expr1;;expr2, expr0;; ;;expr1 or expr0;;expr1;;expr2.
    @Override
    public MathematicaParser.Result parse(MathematicaParser parser, MathematicaParser.Result left) throws CriticalParserError {
        PsiBuilder.Marker spanMark = left.getMark().precede();
        boolean skipped = false;

        if (parser.matchesToken(SPAN)) {
            parser.advanceLexer();
        } else {
            spanMark.drop();
            throw new CriticalParserError("SPAN token ';;' expected");
        }

        // if we meet a second ;; right after the first ;; we just skip it
        if (parser.matchesToken(SPAN)) {
            skipped = true;
            parser.advanceLexer();
        }

        // since here we met either expr0;; or expr0;;;;. By testing whether the next token is start of a new expression
        // (which requires to start with a prefix operator) we can tell whether we met the form list[[expr0;;]] or
        // which is a syntax error list[expr0;;;;]].
        PrefixParselet nextPrefix = getPrefixParselet(parser.getTokenType());
        if (nextPrefix == null) {
            if (skipped) {
                spanMark.error("Expression expected after  \"expr0;; ;;\"");
            } else {
                spanMark.done(SPAN_EXPRESSION);
            }
            return parser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && !skipped);
        }

        MathematicaParser.Result expr1 = parser.parseExpression(m_precedence);

        // if we had expr0;;;;expr1
        if (skipped) {
            spanMark.done(SPAN_EXPRESSION);
            return parser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && expr1.isParsed());
        }

        if (parser.matchesToken(SPAN)) {
            parser.advanceLexer();
            MathematicaParser.Result expr2 = parser.parseExpression(m_precedence);
            if (expr2.isParsed()) {
                spanMark.done(SPAN_EXPRESSION);
            } else
                spanMark.error("Expression expected after \"expr0;;expr1;;\"");
            return parser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && expr1.isParsed() && expr2.isParsed());
        } else {
            // we have the form expr0;;expr1
            spanMark.done(SPAN_EXPRESSION);
            return parser.result(spanMark, SPAN_EXPRESSION, left.isParsed() && expr1.isParsed());
        }
    }

    @Override
    public int getPrecedence() {
        return m_precedence;
    }
}
