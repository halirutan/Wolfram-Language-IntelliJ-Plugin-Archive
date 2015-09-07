/*
 * Copyright (c) 2015 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.parameterinfo;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.MathematicaFileTypeFactory;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider.SymbolInformation;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.util.MathematicaPsiElementFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * @author patrick (05.09.15)
 */
public class BuiltInParameterInfo implements ParameterInfoHandler<FunctionCall, Expression> {

  static final HashMap<String, SymbolInformation> mySymbolInformation = SymbolInformationProvider.getSymbolNames();

  public boolean couldShowInLookup() {
    return true;
  }

  @Nullable
  public Object[] getParametersForLookup(final LookupElement item, final ParameterInfoContext context) {
    MathematicaPsiElementFactory factory = new MathematicaPsiElementFactory(context.getProject());
    final Expression expr = factory.createExpressionFromText(item.getLookupString() + "[]");
    return new Object[]{expr};
  }

  @Nullable
  public Object[] getParametersForDocumentation(final Expression p, final ParameterInfoContext context) {
    return new Object[0];
  }

  @Nullable
  public FunctionCall findElementForParameterInfo(@NotNull final CreateParameterInfoContext context) {
    final int offset = context.getOffset();
    final PsiFile file = context.getFile();

    if (file == null) {
      return null;
    }

    PsiElement element = file.findElementAt(offset);
    FunctionCall functionCall = findEnclosingFunctionCall(element);

    if (functionCall == null) {
      return null;
    }

    context.setItemsToShow(new Object[]{functionCall});
    return functionCall;

  }

  public void showParameterInfo(@NotNull final FunctionCall element, @NotNull final CreateParameterInfoContext context) {
    context.showHint(element, element.getTextRange().getStartOffset() + 1, this);
  }

  @Nullable
  public FunctionCall findElementForUpdatingParameterInfo(@NotNull final UpdateParameterInfoContext context) {
    final FunctionCall enclFuncCall = findEnclosingFunctionCall(context.getFile().findElementAt(context.getOffset()));
    if (enclFuncCall != null && enclFuncCall == context.getParameterOwner()) {
      return enclFuncCall;
    }
    return null;
  }

  public void updateParameterInfo(@NotNull final FunctionCall parameterOwner, @NotNull final UpdateParameterInfoContext context) {
    if (context.getParameterOwner() == null || parameterOwner.equals(context.getParameterOwner())) {
      context.setParameterOwner(parameterOwner);
    } else {
      context.removeHint();
    }
  }

  @Nullable
  public String getParameterCloseChars() {
    return ",]";
  }

  public boolean tracksParameterIndex() {
    return true;
  }

  public void updateUI(final Expression expr, @NotNull final ParameterInfoUIContext context) {
    updateUIText(expr, context);
  }

  private static void updateUIText(final Expression expression, @NotNull final ParameterInfoUIContext context) {
    if (!(expression instanceof FunctionCall)) {
      return;
    }

    FunctionCall functionCall = (FunctionCall) expression;

    final PsiElement head = functionCall.getHead();
    if (head != null && mySymbolInformation.containsKey(head.getText())) {
      final SymbolInformation symbolInformation = mySymbolInformation.get(head.getText());
      StringBuilder buffer = new StringBuilder(symbolInformation.name);
      if (symbolInformation.function) {
        buffer.append("[");
        String callPattern = symbolInformation.callPattern;
        callPattern = callPattern.length() > 2 ? callPattern.substring(1, callPattern.length() - 1) : "";
        buffer.append(callPattern);
        buffer.append("]");
      } else {
        buffer.append(" is no function");
      }
      context.setupUIComponentPresentation(buffer.toString(),
          0,0, false, false, false, context.getDefaultParameterColor());
    } else {
      context.setupUIComponentPresentation("No parameter info available", 0, 0, false, false, false, context.getDefaultParameterColor());
    }
  }

  private FunctionCall findEnclosingFunctionCall(final PsiElement element) {
    PsiElement current = element;

    while (current != null) {
      if (current instanceof FunctionCall) {
        return (FunctionCall) current;
      }
      if (current instanceof PsiFile) {
        break;
      }
      current = current.getParent();
    }
    return null;
  }


}
