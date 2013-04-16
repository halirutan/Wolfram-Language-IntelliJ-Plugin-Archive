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

import java.util.ArrayList;
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
        ArrayList<String> urls = new ArrayList<String>();
        urls.add("http://reference.wolfram.com/mathematica/ref/character/Alpha.html");
        return urls;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        return "<h3><a href=\"http://reference.wolfram.com/mathematica/ref/LinearModelFit.html\">LinearModelFit</a></h3><ul><li>LinearModelFit[{<em>y</em><sub>1</sub>,<em>y</em><sub>2</sub>,<math><ms>&#8230;</ms></math>},{<em>f</em><sub>1</sub>,<em>f</em><sub>2</sub>,<math><ms>&#8230;</ms></math>},<em>x</em>] constructs a linear model of the form <math><ms>&#946;</ms></math><sub>0</sub>+<math><ms>&#946;</ms></math><sub>1</sub><em>f</em><sub>1</sub>+<math><ms>&#946;</ms></math><sub>2</sub><em>f</em><sub>2</sub>+<math><ms>&#8230;</ms></math> that fits the <em>y</em><sub><em>i</em></sub> for successive <em>x</em> values 1, 2, <math><ms>&#8230;</ms></math>.<li>LinearModelFit[{{<em>x</em><sub>11</sub>,<em>x</em><sub>12</sub>,<math><ms>&#8230;</ms></math>,<em>y</em><sub>1</sub>},{<em>x</em><sub>21</sub>,<em>x</em><sub>22</sub>,<math><ms>&#8230;</ms></math>,<em>y</em><sub>2</sub>},<math><ms>&#8230;</ms></math>},{<em>f</em><sub>1</sub>,<em>f</em><sub>2</sub>,<math><ms>&#8230;</ms></math>},{<em>x</em><sub>1</sub>,<em>x</em><sub>2</sub>,<math><ms>&#8230;</ms></math>}] constructs a linear model of the form <math><ms>&#946;</ms></math><sub>0</sub>+<math><ms>&#946;</ms></math><sub>1</sub><em>f</em><sub>1</sub>+<math><ms>&#946;</ms></math><sub>2</sub><em>f</em><sub>2</sub>+<math><ms>&#8230;</ms></math> where the <em>f</em><sub><em>i</em></sub> depend on the variables <em>x</em><sub><em>k</em></sub>. <li>LinearModelFit[{<em>m</em>,<em>v</em>}] constructs a linear model from the design matrix <em>m</em> and response vector <em>v</em>.</ul>";
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
        return contextElement;
    }
}
