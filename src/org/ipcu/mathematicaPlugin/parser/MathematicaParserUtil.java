package org.ipcu.mathematicaPlugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/19/13
 * Time: 1:11 PM
 * Purpose:
 */
public class MathematicaParserUtil {

    public static boolean consumeToken(PsiBuilder builder, IElementType... tokens) {
        boolean result = true;
        for (int i = 0; i < tokens.length; ++i) {
            result &= builder.lookAhead(i) == tokens[i];
        }
        if (result) {
            for (int i = 0; i < tokens.length; ++i) {
            } builder.advanceLexer();
        }
        return result;
    }



}
