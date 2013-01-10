package org.ipcu.mathematicaPlugin.lexer;

import com.intellij.lexer.FlexAdapter;
import java.io.Reader;


public class MathematicaLexer extends FlexAdapter {

    public MathematicaLexer() {
        super(new _MathematicaLexer((Reader) null));
    }

}
