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

package de.halirutan.mathematica.codeinsight.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.parsing.psi.api.CompoundExpression;
import de.halirutan.mathematica.parsing.psi.api.FunctionCall;
import de.halirutan.mathematica.parsing.psi.util.Comments;
import de.halirutan.mathematica.parsing.psi.util.Comments.CommentStyle;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct.ConstructType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates fold-regions from particular nodes of the AST tree. Currently, we support the folding of functions, lists,
 * and special "sectioning comments".
 *
 * @author patrick (26.07.15)
 */
public class MathematicaExpressionFoldingBuilder implements FoldingBuilder {

  private static final ResourceBundle ourCharactersResourceBundle;
  private static final HashMap<String, String> ourNamedCharacters;
  private static final Pattern namedCharacterPattern = Pattern.compile("\\\\\\[[A-Z][a-zA-Z]+]");

  static {
    ourCharactersResourceBundle = ResourceBundle.getBundle("/de/halirutan/mathematica/codeinsight/folding/namedCharacters");
    ourNamedCharacters = new HashMap<>(ourCharactersResourceBundle.keySet().size());
    for (String key : ourCharactersResourceBundle.keySet()) {
      try {
        ourNamedCharacters.put(key, new String(ourCharactersResourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8"));
      } catch (UnsupportedEncodingException ignored) {
      }
    }
  }

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull final ASTNode node, @NotNull final Document document) {

    List<FoldingDescriptor> descriptors = new ArrayList<>();
    collectRegionsRecursively(node, document, descriptors);
    return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
  }

  private void collectRegionsRecursively(@NotNull final ASTNode node,
                                         @NotNull final Document document,
                                         @NotNull List<FoldingDescriptor> descriptors) {

    final boolean foldCharacters = getMathematicaFoldingSettings().isCollapseNamedCharacters();

    final IElementType elementType = node.getElementType();
    if (foldCharacters && elementType == MathematicaElementTypes.IDENTIFIER) {
      final String symbol = node.getText();
      final Matcher matcher = namedCharacterPattern.matcher(symbol);
      while (matcher.find()) {
        if (matcher.end() - matcher.start() > 3) {
          final String key = symbol.substring(matcher.start() + 2, matcher.end() - 1);
          final TextRange nodeRange = node.getTextRange();
          final TextRange range = TextRange.create(nodeRange.getStartOffset() + matcher.start(), nodeRange.getStartOffset() + matcher.end());
          if (ourNamedCharacters.containsKey(key)) {
            descriptors.add(new MathematicaNamedFoldingDescriptor(node, range, null, ourNamedCharacters.get(key), false));
          }
        }
      }
    } else if (foldCharacters && elementType == MathematicaElementTypes.STRING_NAMED_CHARACTER) {
      final String name = node.getText();
      final String key = name.substring(2, name.length() - 1);
      if (ourNamedCharacters.containsKey(key)) {
        descriptors.add(new MathematicaNamedFoldingDescriptor(node, node.getTextRange(), null, ourNamedCharacters.get(key), false));
      }
    } else if (elementType == MathematicaElementTypes.LIST_EXPRESSION) {
      // Well, we count the number of elements by counting the commas and adding one. Not bullet-proof, but will do.
      final int numberOfListElements = node.getChildren(TokenSet.create(MathematicaElementTypes.COMMA)).length + 1;
      descriptors.add(new NamedFoldingDescriptor(
          node,
          node.getTextRange(),
          null,
          "{ <<" + numberOfListElements + ">> }"));
    } else if (elementType == MathematicaElementTypes.FUNCTION_CALL_EXPRESSION) {
      final PsiElement psi = node.getPsi();
      if (psi instanceof FunctionCall) {
        final FunctionCall functionCall = (FunctionCall) psi;
        if (functionCall.getScopingConstruct() != ConstructType.NULL) {
          descriptors.add(new NamedFoldingDescriptor(
              node,
              node.getTextRange(),
              null,
              functionCall.getHead().getText() + "[...]"
          ));
        }
      }
    } else if (node instanceof PsiComment) {
      collectCommentRegion(node, document, descriptors);
    }

    for (ASTNode child : node.getChildren(null)) {
      collectRegionsRecursively(child, document, descriptors);
    }

  }

  /**
   * Entry method that only check if we have a valid section-comment. The work is done in
   * {@link MathematicaExpressionFoldingBuilder#collectCommentRegion(PsiComment, Document, List)}
   *
   * @param node        node to a PsiComment
   * @param document    the document of the code
   * @param descriptors collects all folding descriptors
   */
  private void collectCommentRegion(@NotNull final ASTNode node,
                                    @NotNull final Document document,
                                    @NotNull List<FoldingDescriptor> descriptors) {
    final PsiComment psi = (PsiComment) node.getPsi();
    boolean isAtTopLevel = false;
    if (psi.getParent() instanceof PsiFile) {
      isAtTopLevel = true;
    } else {
      final PsiElement parent = psi.getParent();
      if (parent instanceof CompoundExpression && parent.getParent() instanceof PsiFile) {
        isAtTopLevel = true;
      }
    }

    if (isAtTopLevel && Comments.isCorrectSectionComment(psi)) {
      final CommentStyle style = Comments.getStyle(psi);
      if (style != null && style.compareTo(CommentStyle.SUBSUBSUBSECTION) < 0) {
        collectCommentRegion(node, psi, style, document, descriptors);
      }
    }

  }

  /**
   * Here the work is done for finding the correct region to fold for a sectioning-comment. The sectioning comments like
   * <code>
   * (* ::Chapter:: *)
   * (*this is the text of the chapter*)
   * </code>
   *
   * @param node        AST node of the comment
   * @param comment     the PSI element of node
   * @param style       Extracted style of the sectioning comment of node
   * @param document    The document that contains the comment
   * @param descriptors Collection for the region descriptors for folding
   */
  private void collectCommentRegion(@NotNull ASTNode node,
                                    @NotNull final PsiComment comment,
                                    @NotNull final CommentStyle style,
                                    @NotNull final Document document,
                                    @NotNull List<FoldingDescriptor> descriptors) {
    final PsiFile file = comment.getContainingFile();
    StringBuilder placeHolderText = new StringBuilder("<< ::");
    placeHolderText.append(style).append(":: ");

    final int currentLine = document.getLineNumber(comment.getTextOffset());
    int endOffset = document.getTextLength();

    // Check if we have a valid section description in the next line
    if (currentLine < document.getLineCount() - 1) {
      final PsiComment nextLineComment = PsiTreeUtil.findElementOfClassAtRange(
          file,
          document.getLineStartOffset(currentLine + 1),
          document.getLineEndOffset(currentLine + 1),
          PsiComment.class
      );
      if (nextLineComment != null && !Comments.isCorrectSectionComment(nextLineComment)) {
        placeHolderText.append(Comments.getStrippedText(nextLineComment));
      } else {
        placeHolderText.append("No description given");
      }
    }

    for (int i = currentLine + 1; i < document.getLineCount(); i++) {
      int start = document.getLineStartOffset(i);
      int end = document.getLineEndOffset(i);
      final PsiComment commentsInLine = PsiTreeUtil.findElementOfClassAtRange(file, start, end, PsiComment.class);
      if (commentsInLine != null && Comments.isCorrectSectionComment(commentsInLine)) {
        final CommentStyle commentStyle = Comments.getStyle(commentsInLine);
        if (commentStyle != null && commentStyle.compareTo(style) <= 0) {
          endOffset = start - 1;
          break;
        }
      }
    }
    placeHolderText.append(">>");
    descriptors.add(new NamedFoldingDescriptor(
        node,
        TextRange.create(node.getStartOffset(), endOffset),
        null,
        placeHolderText.toString()
    ));


  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull final ASTNode node) {
    return "<<...>>";
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull final ASTNode node) {
    return node.getElementType() == MathematicaElementTypes.IDENTIFIER || node.getElementType() == MathematicaElementTypes.STRING_NAMED_CHARACTER;
  }

  private MathematicaCodeFoldingSettings getMathematicaFoldingSettings() {
    return MathematicaCodeFoldingSettingsImpl.getInstance();
  }


}
