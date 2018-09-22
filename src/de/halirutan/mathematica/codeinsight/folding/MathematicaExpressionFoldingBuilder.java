/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.codeinsight.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.folding.NamedFoldingDescriptor;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.information.SymbolInformation;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lang.psi.LocalizationConstruct.MScope;
import de.halirutan.mathematica.lang.psi.api.CompoundExpression;
import de.halirutan.mathematica.lang.psi.api.FunctionCall;
import de.halirutan.mathematica.lang.psi.util.Comments;
import de.halirutan.mathematica.lang.psi.util.Comments.CommentStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.halirutan.mathematica.codeinsight.folding.MathematicaFoldingGroups.NAMED_CHARACTER_GROUP;

/**
 * Creates fold-regions from particular nodes of the AST tree. Currently, we support the folding of functions, lists,
 * and special "sectioning comments".
 *
 * @author patrick (26.07.15)
 */
public class MathematicaExpressionFoldingBuilder implements FoldingBuilder {

  private static final Pattern namedCharacterPattern = Pattern.compile("\\\\\\[[A-Z][a-zA-Z]+]");
  private SymbolInformation information = ServiceManager.getService(SymbolInformation.class);

  @NotNull
  @Override
  public FoldingDescriptor[] buildFoldRegions(@NotNull final ASTNode node, @NotNull final Document document) {

    List<FoldingDescriptor> descriptors = new ArrayList<>();
    collectRegionsRecursively(node, document, descriptors);
    return descriptors.toArray(FoldingDescriptor.EMPTY);
  }

  private void collectRegionsRecursively(@NotNull final ASTNode node,
                                         @NotNull final Document document,
                                         @NotNull List<FoldingDescriptor> descriptors) {

    final boolean foldCharacters = getMathematicaFoldingSettings().isCollapseNamedCharacters();

    final IElementType elementType = node.getElementType();
    if (foldCharacters && elementType == MathematicaElementTypes.IDENTIFIER) {
      final String symbol = node.getText();
      if (information.isNamedCharacter(symbol)) {
        descriptors.add(
            new NamedFoldingDescriptor(node, node.getTextRange(), NAMED_CHARACTER_GROUP,
                information.getNamedCharacter(symbol)));
      }
    } else if (foldCharacters && elementType == MathematicaElementTypes.STRING_NAMED_CHARACTER) {
      final String character = node.getText();
      if (information.isNamedCharacter(character)) {
        descriptors.add(new NamedFoldingDescriptor(node, node.getTextRange(), NAMED_CHARACTER_GROUP,
            information.getNamedCharacter(character)));
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
        if (functionCall.getScopingConstruct() != MScope.NULL_SCOPE) {
          descriptors.add(new NamedFoldingDescriptor(
              node,
              node.getTextRange(),
              null,
              functionCall.getHead().getText() + "[...]"
          ));
        }
      }
    } else if (node instanceof PsiComment) {
      if (foldCharacters) {
        foldNamedCharacters(node, descriptors);
      }
      collectCommentRegion(node, document, descriptors);
    }

    for (ASTNode child : node.getChildren(null)) {
      collectRegionsRecursively(child, document, descriptors);
    }

  }

  /**
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

  /**
   * Takes a larger portion of text and inserts folding regions for named characters. This is necessary because comments
   * are one chunk of text while in code, each named character is a separate element.
   *
   * @param node        Node that might contain named characters
   * @param descriptors List of folding descriptions where the found named character regions are added
   */
  private void foldNamedCharacters(final ASTNode node, @NotNull List<FoldingDescriptor> descriptors) {
    final String text = node.getText();
    final Matcher matcher = namedCharacterPattern.matcher(text);
    while (matcher.find()) {
      if (matcher.end() - matcher.start() > 3) {
        final String key = text.substring(matcher.start(), matcher.end());
        final TextRange nodeRange = node.getTextRange();
        final TextRange range =
            TextRange.create(nodeRange.getStartOffset() + matcher.start(), nodeRange.getStartOffset() + matcher.end());
        if (information.isNamedCharacter(key)) {
          descriptors.add(
              new NamedFoldingDescriptor(node, range, NAMED_CHARACTER_GROUP, information.getNamedCharacter(key)));
        }
      }
    }
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull final ASTNode node) {
    return "<<...>>";
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull final ASTNode node) {
    return node.getElementType() == MathematicaElementTypes.IDENTIFIER ||
        node.getElementType() == MathematicaElementTypes.STRING_NAMED_CHARACTER;
  }

  private MathematicaCodeFoldingSettings getMathematicaFoldingSettings() {
    return MathematicaCodeFoldingSettingsImpl.getInstance();
  }


}
