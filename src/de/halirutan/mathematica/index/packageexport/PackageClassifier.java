/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.index.packageexport;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import de.halirutan.mathematica.index.PackageUtil;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.*;
import de.halirutan.mathematica.lang.psi.api.assignment.Set;
import de.halirutan.mathematica.lang.psi.api.assignment.SetDelayed;
import de.halirutan.mathematica.lang.psi.impl.assignment.SetDefinitionSymbolVisitor;
import de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/**
 * @author patrick (17.12.16).
 */
public class PackageClassifier extends MathematicaVisitor {

  private HashSet<PackageExportSymbol> myExportInfo;
  private Stack<String> myContextStack;
  private String myFileName;

  PackageClassifier() {
    myContextStack = new Stack<>();
    myExportInfo = new HashSet<>();
    myContextStack.push("Global`");
    myFileName = "";
  }

  Collection<PackageExportSymbol> getListOfExportSymbols() {
    return myExportInfo;
  }


  @Override
  public void visitFile(PsiFile file) {
    myFileName = file.getName();
    file.acceptChildren(this);
  }

  @Override
  public void visitCompoundExpression(CompoundExpression compoundExpression) {
    compoundExpression.acceptChildren(this);
  }

  @Override
  public void visitFunctionCall(FunctionCall functionCall) {
    if (functionCall.matchesHead("BeginPackage")) {
      final String beginPackageContext = MathematicaPsiUtilities.getBeginPackageContext(functionCall);
      myContextStack.push(beginPackageContext != null ? beginPackageContext : "");
    } else if (functionCall.matchesHead("Begin")) {
      final String beginContext = MathematicaPsiUtilities.getBeginContext(functionCall);
      myContextStack.push(beginContext != null ? beginContext : "");
    } else if (functionCall.matchesHead("End") || functionCall.matchesHead("EndPackage")) {
      if (!myContextStack.empty()) {
        myContextStack.pop();
      }
    }
  }

  @Override
  public void visitSet(Set set) {
    final PsiElement lhs = set.getFirstChild();
    if (lhs instanceof MessageName) {
      final StringifiedSymbol tag = ((MessageName) lhs).getTag();
      if ("usage".equals(tag != null ? tag.getText() : "")) {
        final Expression symbol = ((MessageName) lhs).getSymbol();
        if (symbol instanceof Symbol) {
          final String context = PackageUtil.buildContext(myContextStack);
          myExportInfo.add(new PackageExportSymbol(myFileName, context, ((Symbol) symbol).getSymbolName(), true));
        }
      }
    }
  }



//  @Override
//  public void visitSetDelayed(SetDelayed setDelayed) {
//    final PsiElement lhs = setDelayed.getFirstChild();
//    collectSetDefinitions(lhs);
//  }
//
//
//  private void collectSetDefinitions(PsiElement lhs) {
//    if (lhs != null) {
//      SetDefinitionSymbolVisitor visitor = new SetDefinitionSymbolVisitor(lhs);
//      lhs.accept(visitor);
//      final java.util.Set<Symbol> unboundSymbols = visitor.getUnboundSymbols();
//      for (Symbol symbol : unboundSymbols) {
//        final String mathematicaContext = symbol.getMathematicaContext();
//        if ("".equals(mathematicaContext)) {
//          final String context = PackageUtil.buildContext(myContextStack);
//          myExportInfo.put(context+symbol.getSymbolName(), new PackageExportSymbol(context, symbol.getSymbolName(), true));
//        } else {
//          myContextStack.push(mathematicaContext);
//          final String context = PackageUtil.buildContext(myContextStack);
//          myExportInfo.put(context+symbol.getSymbolName(), new PackageExportSymbol(context, symbol.getSymbolName(), true));
//          myContextStack.pop();
//        }
//      }
//    }
//  }

}
