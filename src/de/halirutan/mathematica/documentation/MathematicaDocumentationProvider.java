package de.halirutan.mathematica.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.impl.SymbolImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author patrick (4/4/13)
 */
public class MathematicaDocumentationProvider extends AbstractDocumentationProvider {

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if(element instanceof Symbol) {
            return "Hello you..";
        }
        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return super.getUrlFor(element, originalElement);
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        String path = null;

        if (element instanceof Symbol) {
            String context = ((Symbol) element).getMathematicaContext();
            String name = ((Symbol) element).getSymbolName();
            path = "usages" + File.separatorChar + context.replace('`', File.separatorChar) + name + ".html";
        }

        String operatorName = toHtmlName(element.getNode().getElementType().toString());
        PsiElement parent = element.getParent();
        if ((parent != null) && operatorName.equals(parent.toString())) {
            path = "usages" + File.separatorChar + "System" + File.separatorChar + operatorName + ".html";
        }

        InputStream docFile = (path != null) ? MathematicaDocumentationProvider.class.getResourceAsStream(path) : null;
        if (docFile != null) {
            return new Scanner(docFile,"UTF-8").useDelimiter("\\A").next();
        }
        return null;
    }


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
        return result.substring(0,1).toUpperCase() + result.substring(1);
    }

    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return element;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return super.getDocumentationElementForLink(psiManager, link, context);    //To change body of overridden methods use File | Settings | File Templates.
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
