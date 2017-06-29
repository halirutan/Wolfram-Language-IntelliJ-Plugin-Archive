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

/**
 * Provides region folding. That is that some expressions or constructs in the code can be folded together so that they
 * don't occupy space. This is handy if you have long lists or Modules. While the implementation for functions and lists
 * is simple, there are two special cases.
 * <p>
 * First, we provide small fold-regions for Mathematica's special characters like \[Alpha]. If folded, those special
 * characters are displayed as their UTF8 counterpart which makes the code more readable. I strongly advise against the
 * massive use of such things in normal code which should be easily readable as ASCII text, but especially in GUI
 * elements a nice rendering inside Mathematica is often required and in such situations the named-character folding
 * becomes very handy.
 * <p>
 * The second, more complex folding regions we support are Mathematica's special sectioning-comments. The sectioning
 * comments like
 * <p>
 * <code> (* ::Chapter:: *)<br> (*this is the text of the chapter*) </code>
 * <p>
 * should always contain the "text" as separate comment directly in the next line. Mathematica is very strict about the
 * sectioning comments and they always need EXACTLY one space around the style specifier (here ::Chapter::)! If you
 * don't follow this rule, then it won't be rendered appropriately inside the Mathematica front end. The best approach
 * to create a valid section-comment is to use Ctrl+/ to create a new empty (**) followed by pressing Ctrl+Space to get
 * a completion for all possible Chapter, Section, Subsection, Item, etc. Note that correct section comments are always
 * displayed in bold-face and slightly brighter than other comments.
 * <p>
 * The folding of such comments takes care of the "level", meaning if you fold a Section, it will always take the region
 * to the next Section or any element that has a higher level like "Chapter" or "Subchapter". If you have several
 * Subsections between two sections, then you can (a) fold each Subsection separately.
 * <p>
 * Finally, section-comments can contain some style specifiers, namely ::Closed::, ::Bold::, ::Italic::. Here,
 * ::Closed:: is maybe the only really useful because it indicates that a section should be displayed as closed when
 * opened in Mathematica. Note that it has no influence of the folding! A correct example is for instance <code> (*
 * ::Text::Bold::Italic:: *) </code>
 * <p>
 * The supported section-comments can be found in {@link de.halirutan.mathematica.parsing.psi.util.Comments.CommentStyle}.
 */
package de.halirutan.mathematica.codeinsight.folding;