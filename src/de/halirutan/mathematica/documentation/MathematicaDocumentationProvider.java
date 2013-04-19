package de.halirutan.mathematica.documentation;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * @author patrick (4/4/13)
 */
public class MathematicaDocumentationProvider extends DocumentationProviderEx  {

    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return super.getQuickNavigateInfo(element, originalElement);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return super.getUrlFor(element, originalElement);
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof Symbol) {
            String context = ((Symbol) element).getMathematicaContext();
            String name = ((Symbol) element).getSymbolName();
            String path = "usages" + File.separatorChar + context.replace('`', File.separatorChar) + name + ".html";
            InputStream docFile = MathematicaDocumentationProvider.class.getResourceAsStream(path);
            String inputStreamString = new Scanner(docFile,"UTF-8").useDelimiter("\\A").next();
            return inputStreamString;
        }
        return "Could not fetch documentation.";
    }

    /**
     * This is called on elements which are LookupElement like functions and identifier when the autocompletion is open.
     * @param psiManager
     * @param object
     * @param element
     * @return
     */
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return element;    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * This is called when autocompletion is not open.
     * @param psiManager
     * @param link
     * @param context
     * @return
     */
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return super.getDocumentationElementForLink(psiManager, link, context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        return contextElement.getParent();
    }
}
