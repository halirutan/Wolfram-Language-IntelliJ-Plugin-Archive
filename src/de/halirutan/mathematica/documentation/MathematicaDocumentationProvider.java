/*
 * Copyright (c) 2013 Patrick Scheibe
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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import de.halirutan.mathematica.parsing.psi.api.OperatorNameProvider;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.impl.SymbolImpl;
import de.halirutan.mathematica.parsing.psi.util.MathematicaPsiElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author patrick (4/4/13)
 */
public class MathematicaDocumentationProvider extends AbstractDocumentationProvider {

  private static final Pattern SLOT_PATTERN = Pattern.compile("#[0-9]*");
  private static final Pattern ALL_SLOT_PATTERN = Pattern.compile("#+[0-9]*");
  private static final Pattern SLOT_SEQUENCE_PATTERN = Pattern.compile("##[0-9]*");

  /**
   * Generates the documentation (if available) for element. This does two things, first it looks whether the element is
   * a {@link Symbol}. If this is true it tries to load the usage message. If element is not a Symbol, it is possibly an
   * operator. Then it tries to guess the usage message of the operator by converting the class name to a hopefully
   * valid operator name.
   *
   * @param element
   *     Element which was possibly altered by {@link #getCustomDocumentationElement(Editor, PsiFile, PsiElement)} or by
   *     {@link #getDocumentationElementForLookupItem(PsiManager, Object, PsiElement)} if the lookup was active
   * @param originalElement
   *     The original element for which the doc was called (possibly whitespace)
   * @return The html string of the usage message or null if it could not be loaded
   */
  @Nullable
  @Override
  public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
    String path = null;

    if (element instanceof Symbol) {
      String context = ((Symbol) element).getMathematicaContext();
      context = "".equals(context) ? "System`" : context;
      String name = ((Symbol) element).getSymbolName();
      if (ALL_SLOT_PATTERN.matcher(name).matches()) {
        if (SLOT_PATTERN.matcher(name).matches()) name = "Slot";
        else if (SLOT_SEQUENCE_PATTERN.matcher(name).matches()) name = "SlotSequence";
      }
//      path = "usages" + File.separatorChar + context.replace('`', File.separatorChar) + name + ".html";
      path = "usages/" + context.replace('`', '/') + name + ".html";
    }

    if (element instanceof OperatorNameProvider) {
//      path = "usages" + File.separatorChar + "System" + File.separatorChar + ((OperatorNameProviderImpl) element).getOperatorName() + ".html";
      path = "usages/System/" + ((OperatorNameProvider) element).getOperatorName() + ".html";
    }

    InputStream docFile = (path != null) ? MathematicaDocumentationProvider.class.getResourceAsStream(path) : null;
    if (docFile != null) {
      return new Scanner(docFile, "UTF-8").useDelimiter("\\A").next();
    }
    return null;
  }

  /**
   * Calculates the correct element for which the user wants documentation.
   *
   * @param editor
   *     The editor of the file
   * @param file
   *     The file which is edited and where the doc call was made
   * @param contextElement
   *     The element where the caret was when the doc was called
   * @return The element for which the user wants documentation. If an item of the completion list is currently
   * highlighted, then this element. If the cursor is over/beside an identifier, then the symbol element. As last thing
   * it is determined whether the PsiElement is the operator-sign of an operation, then we get the corresponding
   * operation psi implementation element back.
   */
  @Nullable
  @Override
  public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {

    // Check whether there is a completion item which is currently active and give a Symbol element
    // containing the lookup name back.
    final LookupEx activeLookup = LookupManager.getActiveLookup(editor);
    if ((activeLookup != null) && activeLookup.isFocused()) {
      final PsiElement elementAt = file.findElementAt(editor.getCaretModel().getOffset() - 1);
      if (elementAt != null) {
        Symbol lookup = new SymbolImpl(elementAt.getNode());
        final LookupElement currentItem = activeLookup.getCurrentItem();
        final String lookupString = currentItem != null ? currentItem.getLookupString() : "";
        lookup.setName(lookupString);
        return lookup;
      }
    }

    if (contextElement != null) {
      PsiElement parent = contextElement.getParent();

      if ((contextElement instanceof PsiWhiteSpace) || !((parent instanceof Symbol) || (parent instanceof OperatorNameProvider))) {
        PsiElement elm = file.findElementAt(editor.getCaretModel().getOffset() - 1);
        if (elm != null) {
          contextElement = elm;
          parent = elm.getParent();
        }
      }

      if (parent instanceof Symbol) {
        return new SymbolImpl(parent.getNode());
      }

      // Determine if the contextElement is the operator sign of an operation.
      // See the doc to OperatorNameProviderImpl.
      if (parent instanceof OperatorNameProvider) {
        if (((OperatorNameProvider) parent).isOperatorSign(contextElement)) {
          return parent;
        }
      }
    }
    return null;
  }

  /**
   * This makes it possible to have the documentation for each function while scrolling through the completion
   * suggestion list.
   *
   * @param psiManager
   *     access to Psi related things
   * @param object
   *     the current lookup object
   * @param element
   *     the element, the documentation was initially called for. Note that this is typically not a valid built-in
   *     function, because you start typing Plo then the completion box pops up and when you call documentation on one
   *     of the selected lookup entries, the elements name is still Plo, while you want to check the documentation for
   *     the lookup element.
   * @return The Symbol which was created from the string of the lookup element or null if it wasn't possible.
   */
  @Nullable
  @Override
  public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
    if (element != null) {
      final LookupEx activeLookup = LookupManager.getActiveLookup(FileEditorManager.getInstance(element.getProject()).getSelectedTextEditor());
      if (activeLookup != null) {
        if (activeLookup.isFocused()) {
          MathematicaPsiElementFactory elementFactory = new MathematicaPsiElementFactory(psiManager.getProject());
          try {
            return elementFactory.createSymbol(object.toString());
          } catch (Exception e) {
            return null;
          }
        }
      }
    }
    return null;
  }
}
