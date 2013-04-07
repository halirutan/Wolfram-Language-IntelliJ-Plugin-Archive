package de.halirutan.mathematica.parsing.prattParser;

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
import de.halirutan.mathematica.parsing.MathematicaElementTypes;
import de.halirutan.mathematica.lexer.MathematicaLexer;
import de.halirutan.mathematica.parsing.psi.impl.MathematicaPsiFileImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (3/27/13)
 *
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
        return MathematicaElementTypes.Factory.create(node);
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
