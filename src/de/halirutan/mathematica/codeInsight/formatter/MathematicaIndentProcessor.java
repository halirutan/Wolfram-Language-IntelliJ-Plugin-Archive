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

package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (11/1/13)
 */
public class MathematicaIndentProcessor {

  public static Indent getChildIndent(ASTNode node) {
    IElementType elementType = node.getElementType();
    ASTNode parent = node.getTreeParent();
    IElementType parentType = parent != null ? parent.getElementType() : null;
    ASTNode grandfather = parent != null ? parent.getTreeParent() : null;
    IElementType grandfatherType = grandfather != null ? grandfather.getElementType() : null;
    ASTNode prevSibling = FormatterUtil.getPreviousNonWhitespaceSibling(node);
    IElementType prevSiblingElementType = prevSibling != null ? prevSibling.getElementType() : null;


    if (parent == null) {
      return Indent.getNoneIndent();
    }

    if (parentType.equals(FUNCTION_CALL_EXPRESSION)) {

      if (MathematicaBlock.isFunctionHead(node) || elementType.equals(LEFT_BRACKET) || elementType.equals(RIGHT_BRACKET)) {
        return Indent.getNoneIndent();
      }
      return Indent.getNormalIndent();
    }

    if (parentType.equals(GROUP_EXPRESSION)) {
      if (elementType.equals(LEFT_PAR) || elementType.equals(RIGHT_PAR)) {
        return Indent.getNoneIndent();
      }
      return Indent.getNormalIndent();
    }

    if (parentType.equals(LIST_EXPRESSION)) {
      if (elementType.equals(LEFT_BRACE) || elementType.equals(RIGHT_BRACE)) {
        return Indent.getNoneIndent();
      }
      return Indent.getNormalIndent();
    }

    return Indent.getNoneIndent();


  }
}
