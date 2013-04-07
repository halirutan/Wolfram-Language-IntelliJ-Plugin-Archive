package de.halirutan.mathematica.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.paths.WebReferenceDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author patrick (4/4/13)
 */
public class MathematicaDocumentationProvider extends DocumentationProviderEx {


    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return super.getQuickNavigateInfo(element, originalElement);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return super.getUrlFor(element, originalElement);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        return super.generateDoc(element, originalElement);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * This is called on elements which are LookupElement like functions and identifier when the autocompletion is open.
     * @param psiManager
     * @param object
     * @param element
     * @return
     */
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return super.getDocumentationElementForLookupItem(psiManager, object, element);    //To change body of overridden methods use File | Settings | File Templates.
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
        return super.getCustomDocumentationElement(editor, file, contextElement);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
