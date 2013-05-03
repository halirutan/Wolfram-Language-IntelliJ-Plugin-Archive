/*
 * Copyright (c) 2013 Patrick Scheibe
 *
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

package de.halirutan.mathematica.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.impl.SymbolImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author patrick (4/4/13)
 */
public class MathematicaDocumentationProvider extends AbstractDocumentationProvider {

    private static String toHtmlName(String tokenName) {
        String name = tokenName.toLowerCase();
        Pattern pattern = Pattern.compile("_[a-z]");
        Matcher matcher = pattern.matcher(name);
        StringBuffer sb = new StringBuffer(name.length());

        while (matcher.find()) {
            String replacement = matcher.group().substring(1).toUpperCase();
            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        String result = sb.toString();
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof Symbol) {
            return "Hello you..";
        }
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        String path = null;

        if (element instanceof Symbol) {
            String context = ((Symbol) element).getMathematicaContext();
            String name = ((Symbol) element).getSymbolName();
            path = "usages" + File.separatorChar + context.replace('`', File.separatorChar) + name + ".html";
        } else {
            String operatorName = toHtmlName(element.getNode().getElementType().toString());
            PsiElement parent = element.getParent();
            if ((parent != null) && operatorName.equals(parent.toString())) {
                path = "usages" + File.separatorChar + "System" + File.separatorChar + operatorName + ".html";
            }
        }
        InputStream docFile = (path != null) ? MathematicaDocumentationProvider.class.getResourceAsStream(path) : null;
        if (docFile != null) {
            return new Scanner(docFile, "UTF-8").useDelimiter("\\A").next();
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement == null) {
            return null;
        }

        PsiElement parent = contextElement.getParent();
        if (parent instanceof Symbol) {
            return new SymbolImpl(parent.getNode());
        }
        return contextElement;
    }
}
