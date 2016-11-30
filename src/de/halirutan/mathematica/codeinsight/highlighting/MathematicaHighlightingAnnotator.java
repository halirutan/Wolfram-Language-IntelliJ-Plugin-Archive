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

package de.halirutan.mathematica.codeinsight.highlighting;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.MathematicaRecursiveVisitor;
import de.halirutan.mathematica.parsing.psi.MathematicaVisitor;
import de.halirutan.mathematica.parsing.psi.api.MessageName;
import de.halirutan.mathematica.parsing.psi.api.StringifiedSymbol;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.api.function.Function;
import de.halirutan.mathematica.parsing.psi.api.slots.Slot;
import de.halirutan.mathematica.parsing.psi.api.slots.SlotExpression;
import de.halirutan.mathematica.parsing.psi.util.LocalDefinitionResolveProcessor;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct.ConstructType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Provides all highlighting except for the most basic one, which is already done after the lexical scanning (includes
 * string, number, operator highlighting if it is set). In this stage, all the fancy highlighting happens which means
 * <ul > <li >coloring of built-in functions</li> <li >coloring of local variables like in Module</li> <li >coloring of
 * messages</li> <li >coloring of anonymous functions</li> </ul>
 *
 * @author patrick (5/14/13)
 */
public class MathematicaHighlightingAnnotator extends MathematicaVisitor implements Annotator {
  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();
  private AnnotationHolder myHolder = null;

  private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
    final Annotation annotation = holder.createInfoAnnotation(element, null);
    annotation.setTextAttributes(key);
    annotation.setNeedsUpdateOnTyping(false);
  }

  private static void setHighlightingStrict(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
    final Annotation annotation = holder.createInfoAnnotation(element, null);
    annotation.setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
    annotation.setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
    annotation.setNeedsUpdateOnTyping(false);
  }

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull final AnnotationHolder holder) {
    assert myHolder == null : "unsupported concurrent annotator invocation";
    try {
      myHolder = holder;
      element.accept(this);
    } finally {
      myHolder = null;
    }
  }

  @Override
  public void visitSymbol(final Symbol symbol) {
    if (NAMES.contains(symbol.getMathematicaContext()+symbol.getSymbolName())) {
      setHighlighting(symbol, myHolder, MathematicaSyntaxHighlighterColors.BUILTIN_FUNCTION);
      return;
    }

    LocalDefinitionResolveProcessor processor = new LocalDefinitionResolveProcessor(symbol);
    PsiTreeUtil.treeWalkUp(processor, symbol, symbol.getContainingFile(), ResolveState.initial());

    final ConstructType scope = processor.getMyLocalization();
    switch (scope) {
      case NULL:
        break;
      case MODULE:
        setHighlighting(symbol, myHolder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED);
        break;
      case BLOCK:
        setHighlighting(symbol, myHolder, MathematicaSyntaxHighlighterColors.BLOCK_LOCALIZED);
        break;
      case SETDELAYEDPATTERN:
        setHighlighting(symbol, myHolder, MathematicaSyntaxHighlighterColors.PATTERN);
        break;
      default:
        setHighlighting(symbol, myHolder, MathematicaSyntaxHighlighterColors.MODULE_LOCALIZED);
        break;
    }
  }

  @Override
  public void visitFunction(final Function function) {
    setHighlighting(function, myHolder, MathematicaSyntaxHighlighterColors.ANONYMOUS_FUNCTION);

    final MathematicaRecursiveVisitor slotVisitor = new MathematicaRecursiveVisitor() {

      @Override
      public void visitSlot(final Slot slot) {
        setHighlighting(slot, myHolder, MathematicaSyntaxHighlighterColors.PATTERN);
      }

      @Override
      public void visitSlotExpression(final SlotExpression slotExpr) {

        IElementType head = slotExpr.getFirstChild().getNode().getElementType();
        if (head.equals(MathematicaElementTypes.ASSOCIATION_SLOT)) {
          setHighlighting(slotExpr, myHolder, MathematicaSyntaxHighlighterColors.PATTERN);
        } else {
          setHighlighting(slotExpr.getFirstChild(), myHolder, MathematicaSyntaxHighlighterColors.PATTERN);
        }
        slotExpr.acceptChildren(this);

      }
    };
    function.accept(slotVisitor);

  }

  @Override
  public void visitStringifiedSymbol(final StringifiedSymbol stringifiedSymbol) {
    setHighlighting(stringifiedSymbol, myHolder, MathematicaSyntaxHighlighterColors.MESSAGE);
  }

  @Override
  public void visitMessageName(final MessageName messageName) {
    final StringifiedSymbol tag = messageName.getTag();
    TextAttributesKey color = MathematicaSyntaxHighlighterColors.MESSAGE;
    if ("usage".equals(tag.getText())) {
      color = MathematicaSyntaxHighlighterColors.USAGE_MESSAGE;
    }
    final ASTNode[] children = messageName.getNode().getChildren(null);
    for (int i = 1; i < children.length; i++) {
      setHighlightingStrict(children[i].getPsi(), myHolder, color);
    }
  }

}
