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

package de.halirutan.mathematica.parsing.prattparser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.parsing.prattparser.parselets.ImplicitMultiplicationParselet;
import de.halirutan.mathematica.parsing.prattparser.parselets.InfixParselet;
import de.halirutan.mathematica.parsing.prattparser.parselets.PrefixParselet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.halirutan.mathematica.parsing.MathematicaElementTypes.LINE_BREAK;
import static de.halirutan.mathematica.parsing.MathematicaElementTypes.WHITE_SPACES;
import static de.halirutan.mathematica.parsing.prattparser.ParseletProvider.getInfixParselet;
import static de.halirutan.mathematica.parsing.prattparser.ParseletProvider.getPrefixParselet;

/**
 * @author patrick (3/27/13)
 */
public class MathematicaParser implements PsiParser {

  private static final int MAX_RECURSION_DEPTH = 1024;
  private static final ImplicitMultiplicationParselet IMPLICIT_MULTIPLICATION_PARSELET = new ImplicitMultiplicationParselet();
  private final ImportantLineBreakHandler myImportantLinebreakHandler;
  private PsiBuilder myBuilder = null;
  private int myRecursionDepth;


  public MathematicaParser() {
    myRecursionDepth = 0;
    myImportantLinebreakHandler = new ImportantLineBreakHandler();
  }

  /**
   * Function to create information about a parsed expression. In a Pratt parser often you need the last parsed
   * expression to combine it into a new parse-node. So <code >expr1 + expr2</code> is combined into a new node by
   * recognizing the operator <code >+</code>, parsing <code >expr2</code> and combining everything into a new parse
   * node.
   * <p/>
   * Since IDEA uses markers to mark the sequential code into a tree-structure I use this {@link Result} which contains
   * additionally the {@link IElementType} of the last expression and whether the previous expression was parsed.
   *
   * @param mark
   *     The builder mark which was created and closed during the current parse
   * @param token
   *     The token type of the expression which was parsed, e.g. FUNCTION_CALL_EXPRESSION
   * @param parsedQ
   *     Whether the parsing of the expression was successful
   * @return The Result object with the given parsing information.
   */
  public static Result result(Marker mark, IElementType token, boolean parsedQ) {
    return new Result(mark, token, parsedQ);
  }

  /**
   * This is the return value of a parser when errors happened.
   *
   * @return A special return Result saying <em >the expression could not be parsed</em>. Note this does not mean that
   * the expression was parsed and errors occurred! It says the parser could do absolutely nothing. This is returned if
   * the parser could identify a meaningful operator from the token-stream. See {@link #parseExpression(int)} for use
   * cases.
   */
  public static Result notParsed() {
    return new Result(null, null, false);
  }

  /**
   * This is the main entry point for the parsing a file. Every tme
   *
   * @param root
   *     The root node of the AST
   * @param builder
   *     Through this, the AST is built up by placing markers.
   * @return The parsed AST
   */
  @NotNull
  @Override
  public ASTNode parse(IElementType root, PsiBuilder builder) {
    builder.setWhitespaceSkippedCallback(myImportantLinebreakHandler);
    Marker rootMarker = builder.mark();
    this.myBuilder = builder;
    try {
      while (!builder.eof()) {
        Result expr = parseExpression();
        if (!expr.isParsed()) {
          builder.error("The last expression could not be parsed correctly.");
          builder.advanceLexer();
        }
      }
      rootMarker.done(root);
    } catch (CriticalParserError criticalParserError) {
      rootMarker.rollbackTo();
      Marker newRoot = builder.mark();
      final Marker errorMark = builder.mark();
      while (!builder.eof()) {
        builder.advanceLexer();
      }
      errorMark.error(criticalParserError.getMessage());
      newRoot.done(root);
    }
    return builder.getTreeBuilt();
  }

  public Result parseExpression() throws CriticalParserError {
    return parseExpression(0);
  }

  public Result parseExpression(int precedence) throws CriticalParserError {
    if (myBuilder.eof()) return notParsed();

    if (myRecursionDepth > MAX_RECURSION_DEPTH) {
      throw new CriticalParserError("Maximal recursion depth exceeded during parsing.");
    }

    IElementType token = myBuilder.getTokenType();
    if (token == null) {
      return notParsed();
    }

    PrefixParselet prefix = getPrefixParselet(token);
    if (prefix == null) {
      return notParsed();
    }

    increaseRecursionDepth();
    Result left = prefix.parse(this);

    while (left.isParsed()) {
      token = myBuilder.getTokenType();
      InfixParselet infix = getInfixOrMultiplyParselet(token);
      if (infix == null) {
        break;
      }
      if (precedence >= infix.getMyPrecedence()) {
        break;
      }
      left = infix.parse(this, left);
    }
    decreaseRecursionDepth();
    return left;
  }

  @Nullable
  private InfixParselet getInfixOrMultiplyParselet(IElementType token) {
    InfixParselet infixParselet = getInfixParselet(token);
    PrefixParselet prefixParselet = getPrefixParselet(token);

    if (infixParselet != null) return infixParselet;

    if (prefixParselet == null) {
      return null;
    }

    if (myImportantLinebreakHandler.hadLineBreak()) {
      return null;
    }

    return IMPLICIT_MULTIPLICATION_PARSELET;
  }

  private int decreaseRecursionDepth() {
    return --myRecursionDepth;
  }

  private int increaseRecursionDepth() {
    return ++myRecursionDepth;
  }

  public Marker mark() {
    return myBuilder.mark();
  }

  public IElementType getTokenType() {
    return myBuilder.getTokenType();
  }

  public void advanceLexer() throws CriticalParserError {
    if (myBuilder.eof()) {
      myBuilder.error("More input expected");
      throw new CriticalParserError("Unexpected end of input.");
    }
    myImportantLinebreakHandler.reset();
    myBuilder.advanceLexer();
  }

  public boolean matchesToken(IElementType token) {
    final IElementType testToken = myBuilder.getTokenType();
    return (testToken != null && testToken.equals(token));
  }

  public boolean matchesToken(IElementType token1, IElementType token2) {
    final IElementType firstToken = myBuilder.lookAhead(0);
    final IElementType secondToken = myBuilder.lookAhead(1);
    return (firstToken != null && firstToken.equals(token1)) && (secondToken != null && secondToken.equals(token2));
  }

  /**
   * Wrapper for {@link PsiBuilder#error(String)}
   *
   * @param s
   *     Error message
   */
  public void error(String s) {
    myBuilder.error(s);
  }

  @SuppressWarnings({"BooleanMethodNameMustStartWithQuestion", "InstanceMethodNamingConvention"})
  public boolean eof() {
    return myBuilder.eof();
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isNextWhitespace() {
    final IElementType possibleWhitespace = myBuilder.rawLookup(1);
    return WHITE_SPACES.contains(possibleWhitespace);
  }

  /**
   * For the Pratt parser we need the left side which was already parsed. An instance of this will provide all necessary
   * information required to know what expression was parsed on the left of an infix operator.
   */
  public static final class Result {

    private final Marker myLeftMark;
    private final IElementType myLeftToken;
    private final boolean myParsed;

    private Result(Marker leftMark, IElementType leftToken, boolean parsed) {
      this.myLeftMark = leftMark;
      this.myLeftToken = leftToken;
      this.myParsed = parsed;
    }

    public Marker getMark() {
      return myLeftMark;
    }

    public IElementType getToken() {
      return myLeftToken;
    }

    /**
     * True, iff an expression could be parsed correctly. This method can be used to check, whether the result of the
     * parsing of a sub-expression was successful. For instance in <code >expr1 + expr2</code>: you can test if <code
     * >expr2</code> was parsed successfully and decide what to do in the parsing of Plus, if it wasn't
     *
     * @return true if an expression was parsed correctly.
     */
    public boolean isParsed() {
      return myParsed;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValid() {
      return (myLeftMark != null) && (myLeftToken != null);
    }
  }

  /**
   * Registers when a whitespace token was seen. This is important in order to find out whether an <em>implicit
   * multiplication</em> has arisen.
   */
  public class ImportantLineBreakHandler implements WhitespaceSkippedCallback {
    private boolean myLineBreakSeen = false;


    @Override
    public void onSkip(IElementType type, int start, int end) {
      if (type.equals(LINE_BREAK)) myLineBreakSeen = true;
    }

    public void reset() {
      myLineBreakSeen = false;
    }

    public boolean hadLineBreak() {
      return myLineBreakSeen;
    }

  }
}
