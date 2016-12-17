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

package de.halirutan.mathematica.parsing.psi.impl.string;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.filetypes.MathematicaFileType;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.string.MString;
import de.halirutan.mathematica.parsing.psi.impl.MathematicaPsiFileImpl;
import de.halirutan.mathematica.parsing.psi.impl.OperatorNameProviderImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class StringImpl extends OperatorNameProviderImpl implements MString {

  private PsiReference[] myReferences = null;

  public StringImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    ASTNode literalNode = getNode().findChildByType(MathematicaElementTypes.STRING_LITERAL);
    final PsiFileFactory fileFactory = PsiFileFactory.getInstance(getProject());
    final MathematicaPsiFileImpl file = (MathematicaPsiFileImpl) fileFactory.createFileFromText("dummy.m", MathematicaFileType.INSTANCE, name);
    ASTNode newElm = file.getFirstChild().getNode().findChildByType(MathematicaElementTypes.STRING_LITERAL);
    if (literalNode != null && newElm != null) {
      getNode().replaceChild(literalNode, newElm);
    }
    return this;
  }

  @Override
  public PsiReference getReference() {
    final PsiReference[] references = getReferences();
    return references.length > 0 ? references[0] : null;
  }

  @Override
  public void subtreeChanged() {
    myReferences = null;
  }

  @NotNull
  @Override
  public PsiReference[] getReferences() {
    if (myReferences == null) {
      return myReferences = ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }
    return myReferences;
  }
}
