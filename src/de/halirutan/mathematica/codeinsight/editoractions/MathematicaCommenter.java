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

package de.halirutan.mathematica.codeinsight.editoractions;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.Nullable;

/**
 * Provides the functionality of automatic commenting of code-blocks, lines and selected regions. The only thing
 * required here is to define appropriate Strings which are used to mark code as comment. In Mathematica everything
 * between <code >(*</code> and <code >*)</code> is ignored and regarded as comment. Note, that this class is registered
 * in the file /META-INF/plugin.xml as <code >lang.commenter</code> extension.
 *
 * @author patrick (3/22/13)
 */
public class MathematicaCommenter implements CodeDocumentationAwareCommenter {
  @Nullable
  @Override
  public String getLineCommentPrefix() {
    return null;
  }

  /**
   * Returns the opening string for a comment. In Java and C this is <code >&#47;*</code>, in Mathematica it is <code
   * >(*</code>
   *
   * @return the comment opening string
   */
  @Nullable
  @Override
  public String getBlockCommentPrefix() {
    return "(*";
  }

  /**
   * Returns the closing string for a block comment. In Java and C this is <code >*&#47;</code>
   *
   * @return the block comment closing string
   */
  @Nullable
  @Override
  public String getBlockCommentSuffix() {
    return "*)";
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentPrefix() {
    return "-";
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentSuffix() {
    return "+";
  }

  @Nullable
  @Override
  public IElementType getLineCommentTokenType() {
    return null;
  }

  @Nullable
  @Override
  public IElementType getBlockCommentTokenType() {
    return MathematicaElementTypes.COMMENT;
  }

  @Nullable
  @Override
  public IElementType getDocumentationCommentTokenType() {
    return MathematicaElementTypes.COMMENT;
  }

  @Nullable
  @Override
  public String getDocumentationCommentPrefix() {
    return "(**";
  }

  @Nullable
  @Override
  public String getDocumentationCommentLinePrefix() {
    return "*";
  }

  @Nullable
  @Override
  public String getDocumentationCommentSuffix() {
    return "*)";
  }

  @Override
  public boolean isDocumentationComment(PsiComment element) {
    return true;
  }
}
