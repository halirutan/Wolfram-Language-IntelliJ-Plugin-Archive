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

package de.halirutan.mathematica.codeinsight.formatter;

import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;

import static de.halirutan.mathematica.lang.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (11/1/13)
 */
class MathematicaIndentProcessor {

  public static Indent getChildIndent(ASTNode node) {
    IElementType elementType = node.getElementType();
    ASTNode parent = node.getTreeParent();
    IElementType parentType = parent != null ? parent.getElementType() : null;
    ASTNode grandfather = parent != null ? parent.getTreeParent() : null;
    IElementType grandfatherType = grandfather != null ? grandfather.getElementType() : null;
    ASTNode prevSibling = FormatterUtil.getPreviousNonWhitespaceSibling(node);
    IElementType prevSiblingElementType = prevSibling != null ? prevSibling.getElementType() : null;


    if (parent == null ||
        COMMENTS.contains(elementType) ||
        parentType == FILE) {
      return Indent.getNoneIndent();
    }

    if (parentType.equals(FUNCTION_CALL_EXPRESSION)) {

      if (isInFunctionBody(node)) {
        return Indent.getNormalIndent(false);
      }
      return Indent.getNoneIndent();
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

    if (parentType.equals(ASSOCIATION_EXPRESSION)) {
      if (elementType.equals(LEFT_ASSOCIATION) || elementType.equals(RIGHT_ASSOCIATION)) {
        return Indent.getNoneIndent();
      }
      return Indent.getNormalIndent();
    }

    if (parentType == COMPOUND_EXPRESSION_EXPRESSION) {
      return Indent.getNoneIndent();
    }

    return Indent.getContinuationWithoutFirstIndent();


  }

  /**
   * Checks whether an ASTNode is in the function head.
   *
   * @param node
   *     the node to check
   * @return true if node is in the function head or the opening bracket
   */
  private static boolean isInFunctionHead(ASTNode node) {
    final ASTNode treeParent = node.getTreeParent();
    if (treeParent == null) {
      return false;
    }
    for (ASTNode child = treeParent.getFirstChildNode(); child != null; child = FormatterUtil.getNextNonWhitespaceSibling(child)) {
      if (child == node) {
        return true;
      }
      if (child.getElementType() == LEFT_BRACKET) {
        return false;
      }
    }
    return false;
  }

  /**
   * Checks whether an ASTNode is part of the function body, meaning one of the arguments of a function call or a comma
   *
   * @param node
   *     the node to check
   * @return true if the node is between the opening and closing bracket of <code >func[....]</code>
   */
  private static boolean isInFunctionBody(ASTNode node) {
    final ASTNode treeParent = node.getTreeParent();
    if (treeParent == null) {
      return false;
    }
    boolean inBody = false;
    for (ASTNode child = treeParent.getFirstChildNode(); child != null; child = FormatterUtil.getNextNonWhitespaceSibling(child)) {
      if (!inBody) {
        if (child.getElementType() == LEFT_BRACKET) {
          inBody = true;
        }
        continue;
      }

      if (child.getElementType() == RIGHT_BRACKET) {
        return false;
      }

      if (child == node) {
        return true;
      }
    }
    return false;
  }

}
