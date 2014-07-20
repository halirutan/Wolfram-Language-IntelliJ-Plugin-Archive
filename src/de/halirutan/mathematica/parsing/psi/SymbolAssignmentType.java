/*
 * Copyright (c) 2014 Patrick Scheibe
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

package de.halirutan.mathematica.parsing.psi;

/**
 * A list of possible assignment types which can easily be extracted by the plugin. I introduced this for the
 * StructureView where I need to distinguish the different assignment types to make them visually pleasing.
 *
 * @author patrick (7/20/14)
 */
public enum SymbolAssignmentType {
  SET_DELAYED_ASSIGNMENT,
  SET_ASSIGNMENT,
  UP_SET_ASSIGNMENT,
  UP_SET_DELAYED_ASSIGNMENT,
  TAG_SET_ASSIGNMENT,
  TAG_SET_DELAYED_ASSIGNMENT,
  OPTIONS_ASSIGNMENT,
  MESSAGE_ASSIGNMENT,
  ATTRIBUTES_ASSIGNMENT,
  FORMAT_ASSIGNMENT,
  SYNTAX_INFORMATION_ASSIGNMENT,
  DEFAULT_ASSIGNMENT,
  N_ASSIGNMENT,
  UNKNOWN
}
