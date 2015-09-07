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

package de.halirutan.mathematica.parsing.psi.api;

import com.intellij.psi.PsiElement;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created with IntelliJ IDEA. User: patrick Date: 3/28/13 Time: 12:33 AM Purpose:
 */
public interface FunctionCall extends PsiElement {

//  public static final Set<String> SCOPING_CONSTRUCTS = new HashSet<String>(Arrays.asList(
//      new String[]{"Module", "Block", "With", "Function", "Table", "Do", "Integrate", "NIntegrate"}));

  /**
   * Returns true if the function call is any function which scopes some variables like Module, Block, Table, and so
   * on.
   *
   * @return true if it is a scoping construct.
   */
  public boolean isScopingConstruct();

  /**
   * Extracts the PsiElement which represents the Head of a function call.
   *
   * @return The head
   */
  public PsiElement getHead();

  /**
   * Tests whether the function call has a head the matches the argument.
   *
   * @param head
   *     The head which should be tested.
   * @return True, if head matches the Head of the function call.
   */
  public boolean matchesHead(String head);


  /**
   * Returns the type of scoping construct, if the function call is e.g. <code >Module[..]</code>
   *
   * @return The scoping construct or ConstructType.NULL if it is no scoping construct.
   */
  public LocalizationConstruct.ConstructType getScopingConstruct();


  /**
   * Returns the n'th argument of a function call <code >f[arg1, arg2, ...]</code> or null, if it does not exist.
   *
   * @param n
   *     Argument number, where 0 is the first argument.
   * @return The PsiElement of the argument or null if it does not exist.
   */
  @Nullable
  PsiElement getArgument(int n);

  @Nullable
  List<PsiElement> getAllArguments();
}
