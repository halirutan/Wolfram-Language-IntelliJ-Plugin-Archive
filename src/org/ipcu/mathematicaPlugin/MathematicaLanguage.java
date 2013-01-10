package org.ipcu.mathematicaPlugin;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import org.ipcu.mathematicaPlugin.fileTypes.MathematicaSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 12/23/12
 * Time: 10:06 PM
 * Purpose:
 */
public class MathematicaLanguage extends Language {

    public static final Language INSTANCE = new MathematicaLanguage();

    public MathematicaLanguage() {
        super(Mathematica.NAME);

        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, new SingleLazyInstanceSyntaxHighlighterFactory() {
            @NotNull
            protected SyntaxHighlighter createHighlighter() {
                return new MathematicaSyntaxHighlighter();
            }
        });

    }

}
