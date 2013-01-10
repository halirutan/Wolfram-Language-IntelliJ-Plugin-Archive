package org.ipcu.mathematicaPlugin.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.ipcu.mathematicaPlugin.MathematicaElementTypes;
import org.ipcu.mathematicaPlugin.lexer.MathematicaLexer;
import org.ipcu.mathematicaPlugin.psi.impl.MathematicaExpressionImpl;
import org.ipcu.mathematicaPlugin.psi.impl.MathematicaPsiFileImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: patrick
 * Date: 1/3/13
 * Time: 11:20 AM
 * Purpose:
 */
public class MathematicaParserDefinition implements ParserDefinition {
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new MathematicaLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new MathematicaParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return MathematicaElementTypes.FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return MathematicaElementTypes.WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return MathematicaElementTypes.COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return MathematicaElementTypes.STRING_LITERALS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return new MathematicaExpressionImpl(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new MathematicaPsiFileImpl(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return null;
    }
}
