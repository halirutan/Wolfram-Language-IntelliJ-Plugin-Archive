/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.lang.psi.api;

import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.lang.psi.LocalizationConstruct.MScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * {@link PsiElement} of function calls. In Mathematica, this is everything that looks like
 * <code>head[arg1, arg2, ...]</code>
 * @author patrick
 * Date: 3/28/13 Time: 12:33 AM Purpose:
 */
public interface FunctionCall extends Expression {

  /**
   * Returns true if the function call is any function which scopes some variables like Module, Block, Table, and so
   * on.
   *
   * @return true if it is a scoping construct.
   */
  boolean isScopingConstruct();


  /**
   * Extracts the PsiElement which represents the Head of a function call.
   *
   * @return The head
   */
  PsiElement getHead();

  /**
   * Tests whether the function call has a head the matches the argument. The provide string can be a pattern that is
   * matched against the head of the function.
   *
   * @param head The head which should be tested.
   * @return True, if head matches the Head of the function call.
   */
  boolean matchesHead(String head);

  /**
   * Compares the head of the function with a string
   *
   * @param otherHead other function head to test
   *
   * @return true if both heads are equal
   */
  boolean hasHead(@NotNull final String otherHead);

  /**
   * Compares the head of the function with a list of possible heads.
   *
   * @param otherHeads list of heads to test
   * @return true if any head matches
   */
  boolean hasHead(@NotNull final String[] otherHeads);


  /**
   * Returns the type of scoping construct, if the function call is e.g. <code >Module[..]</code>
   *
   * @return The scoping construct or MScope.NULL_SCOPE if it is no scoping construct.
   */
  @NotNull
  MScope getScopingConstruct();


  /**
   * Returns the n'th argument of a function call <code >f[arg1, arg2, ...]</code> or null, if it does not exist.
   *
   * @param n Argument number, where 0 is the head of the function.
   * @return The PsiElement of the argument or null if it does not exist.
   */
  @Nullable
  PsiElement getArgument(int n);

  /**
   * Provides a list of all arguments to the function call. The first entry is the PsiElement of the head of the
   * function.
   *
   * @return list of arguments
   */
  @NotNull
  List<Expression> getArguments();

  /**
   * Returns a list of PsiElements that are the parameters for the function call. The head of the function is not
   * included in this list.
   *
   * @return MList of parameters
   */
  @NotNull
  List<Expression> getParameters();



}
