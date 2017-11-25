/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.lang.psi.util;

import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import de.halirutan.mathematica.lang.psi.MathematicaRecursiveVisitor;
import de.halirutan.mathematica.lang.psi.api.*;
import de.halirutan.mathematica.lang.psi.api.Number;
import de.halirutan.mathematica.lang.psi.api.pattern.Blank;
import de.halirutan.mathematica.lang.psi.api.pattern.BlankNullSequence;
import de.halirutan.mathematica.lang.psi.api.pattern.BlankSequence;
import de.halirutan.mathematica.lang.psi.api.string.MString;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author patrick (03.09.17).
 */
public class MathematicaFullFormCreator extends MathematicaRecursiveVisitor {

  private final StringBuilder myBuilder = new StringBuilder();
  private int myCharCount = 0;

  @Override
  public void visitElement(PsiElement element) {
    ProgressIndicatorProvider.checkCanceled();
    if (element instanceof PsiWhiteSpace || element instanceof PsiComment) {
      return;
    }
    addContent(element);
    createEnclosedExpression(element.getChildren(), element.getTextLength() > 40 || myCharCount > 120);
  }

  @Override
  public void visitFile(PsiFile file) {
    final PsiElement[] children = file.getChildren();
    for (PsiElement child : children) {
      child.accept(this);
      addLineFeed();
      addLineFeed();
    }
  }

  @Override
  public void visitFunctionCall(FunctionCall functionCall) {
    functionCall.getHead().accept(this);
    final List<Expression> parameters = functionCall.getParameters();
    PsiElement[] parameterArray = parameters.toArray(new PsiElement[parameters.size()]);
    createEnclosedExpression(parameterArray, functionCall.getTextLength() > 30 || myCharCount > 120);
  }

  @Override
  public void visitSymbol(Symbol symbol) {
    addContent(symbol.getText());
  }

  @Override
  public void visitNumber(Number number) {
    addContent(number.getText());
  }

  @Override
  public void visitString(MString mString) {
    addContent(mString.getText());
  }

  @Override
  public void visitBlank(Blank blank) {
    visitBlankLikeElement(blank);
  }

  @Override
  public void visitBlankSequence(BlankSequence blankSequence) {
    visitBlankLikeElement(blankSequence);
  }

  @Override
  public void visitBlankNullSequence(BlankNullSequence blankNullSequence) {
    visitBlankLikeElement(blankNullSequence);
  }

  private void visitBlankLikeElement(PsiElement blank) {
    if (blank instanceof Blank || blank instanceof BlankSequence || blank instanceof BlankNullSequence) {
      String head = blank.toString();
      final PsiElement[] children = blank.getChildren();
      if (children.length < 1 || children.length > 2) {
        return;
      }
      addContent("Pattern[");
      children[0].accept(this);
      if (children.length == 2) {
        addContent("," + head + "[");
        children[1].accept(this);
        addContent("]]");
      } else {
        addContent("," + head + "[]]");
      }
    }

  }

  @Override
  public void visitStringifiedSymbol(StringifiedSymbol stringifiedSymbol) {
    addContent("\"" + stringifiedSymbol.getText() + "\"");
  }

  private void createEnclosedExpression(PsiElement[] elements, boolean makeLineBreak) {
    if (elements == null) {
      addContent("[]");
    } else {
      addContent("[");
      if (makeLineBreak) {
        addLineFeed();
      }
      myCharCount++;
      for (int i = 0; i < elements.length; i++) {
        if (elements[i] instanceof PsiWhiteSpace || elements[i] instanceof PsiComment) {
          continue;
        }
        elements[i].accept(this);
        if (i != elements.length - 1) {
          addContent(",");
          if (makeLineBreak) {
            addLineFeed();
          }
        }
      }
      if (makeLineBreak) {
        addLineFeed();
      }
      addContent("]");
    }
  }

  private void addContent(@NotNull PsiElement element) {
    addContent(element.toString());
  }

  private void addContent(@NotNull String content) {
    myBuilder.append(content);
    myCharCount += content.length();
  }

  private void addLineFeed() {
    myBuilder.append("\n");
    myCharCount = 0;
  }

  public String getFullForm(PsiElement element) {
    element.accept(this);
    return myBuilder.toString();
  }


}
