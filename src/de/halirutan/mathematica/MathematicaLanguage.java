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

package de.halirutan.mathematica;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (4/4/13)
 */
public class MathematicaLanguage extends Language {

    public static final Language INSTANCE = new MathematicaLanguage();

    public MathematicaLanguage() {
        super(Mathematica.NAME);


//        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, new SingleLazyInstanceSyntaxHighlighterFactory() {
//            @NotNull
//            protected SyntaxHighlighter createHighlighter() {
//                return new MathematicaSyntaxHighlighterColors();
//            }
//        });

    }

}
