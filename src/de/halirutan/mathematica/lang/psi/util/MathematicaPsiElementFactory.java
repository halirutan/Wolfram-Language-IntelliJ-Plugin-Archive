/*
 * Copyright (c) 2017 Patrick Scheibe
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

package de.halirutan.mathematica.lang.psi.util;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.file.MathematicaFileType;
import de.halirutan.mathematica.lang.psi.api.Expression;
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (7/10/14)
 */
public class MathematicaPsiElementFactory {

  private static final String DUMMY_FILE_NAME = "dummy.m";
  private final Project myProject;

  public MathematicaPsiElementFactory(final Project project) {
    this.myProject = project;
  }

  /**
   * Taken from here {@link com.intellij.psi.impl.PsiJavaParserFacadeImpl}
   *
   * @param code
   *     Content of the Mathematica file
   * @return The newly created PsiFile with content code
   */
  @NotNull
  public MathematicaPsiFile createDummyFile(@NotNull CharSequence code) {
    final FileType type = MathematicaFileType.INSTANCE;
    return (MathematicaPsiFile) PsiFileFactory.getInstance(myProject).createFileFromText(DUMMY_FILE_NAME, type, code);
  }

  public Expression createExpressionFromText(@NotNull String expr) {
    final PsiElement exprFile = createDummyFile(expr).getFirstChild();
    if (exprFile != null && exprFile instanceof Expression) {
      return (Expression) exprFile;
    }
    throw new IncorrectOperationException("The supplied string is not a valid Mathematica expression.");
  }

  @NotNull
  public Symbol createSymbol(@NotNull String symbolName) {
    final PsiElement symbol = createDummyFile(symbolName).getFirstChild();
    if (symbol != null && symbol instanceof Symbol) {
      return (Symbol) symbol;
    }
    throw new IncorrectOperationException("The supplied string is not a valid Mathematica Symbol.");
  }

  /*
  This is another comment
   */
//  @NotNull
//  public PsiComment createComment(@NotNull String text) {
//    final MathematicaPsiFile aFile = createDummyFile(text);
//    for (PsiElement aChildren : aFile.getChildren()) {
//      if (aChildren instanceof PsiComment) {
//        if (!aChildren.getText().equals(text)) {
//          break;
//        }
//        final PsiComment comment = (PsiComment)aChildren;
//        DummyHolderFactory.createHolder(myManager, (TreeElement) SourceTreeToPsiMap.psiElementToTree(comment), context);
//        return comment;
//      }
//    }
//
//    throw new IncorrectOperationException("Incorrect comment \"" + text + "\".");
//  }


}
