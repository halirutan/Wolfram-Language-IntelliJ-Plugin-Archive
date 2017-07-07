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

package de.halirutan.mathematica.lang.psi.api.assignment;

import de.halirutan.mathematica.lang.psi.api.Symbol;

import java.util.Set;

/**
 * @author patrick (10/6/13)
 */
public interface Assignment {

  /**
   * All operators that assign values to <em>right hand sides</em> should implement this method to return all symbols
   * which are assigned. Some examples: <code>func[x_,y_] := x+y</code>, here <code>func</code> is assigned, while the
   * arguments x and y are not. <code>{x,y,z} = {1,2,3}</code>, here all three variables are assigned.
   *
   * @return The set of symbols which are assigned in this call.
   */
  public Set<Symbol> getAssignedSymbols();

}
