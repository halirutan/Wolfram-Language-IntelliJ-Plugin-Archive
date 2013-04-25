Mathematica language plugin for Intellij IDEA
=============================================

Feature list
------------

- Syntax highlighter
- CamelHump autocompletion for all built-in Mathematica symbols
- Smart expansion depending whether it is a function or not
- Importance sorted completion suggestions
- Highlighting brace/bracket/parenthesis matcher
- Don't insert closing braces when the next token is the same closing brace
- Smart insertion of quotes
- Expand selection by analysing expression structure
- Comment/uncomment line or marked region
- Customizable color scheme
- Creation of complete Mathematica projects
- Creation of new Mathematice files/packages

Detailed usage of features
--------------------------

1. Autocompletion

- When die completion window pops up and you haven't pressed Ctrl+Space so that no entry in the list is highlighted
  you have several choices:
    (1) Enter just inserts the item on top and the cursor is directly behind the word (Ctrl + . does the same)
    (2) Ctrl+Enter inserts the item on top but if it is a function, it inserts brackets and puts the cursor in the middle
  If you press Ctrl+Space during writing or go down the choice-list, so that an entry is highlighted, you can press
  additionally Space to insert the item and put a space behind it.

2. Inserting braces and highlighting brace matcher

- Pressing any kind of brace ( { [ automatically inserts a matching closing brace
- If you are infront of a closing brace and close this brace again, no additional closing brace is inserted. Instead
  the cursor jumps over the closing brace.
- When your braces are not balanced, then an additional brace *is* inserted. Consider this example: f[g[x]
  Go behind the "x" and press a closing ] twice and see what happens.
- Matching braces are highlighted when you are infront of an opening or behind a closing brace
- If are try to highlight a closing brace where the opening one is outside the editor window, the line containing the
  opening braces is shown on top of the editor window.

3. Documentation lookup

- With Ctrl+Q (Cmd+j I think on Mac) you can get documentation of built-in Mathematica functions from System`
- Shown are the html/MathML styled usage, Attributes and Options. Additionally, you can click the link to navigate to
  the online documentation of Wolfram
- If a symbol has no online documentation, you automatically search this site for the symbol name and context
- Currently, usage info of contexts Developer`, Experimental` and JLink` are provided too but you have to give the full
  qualified name for this, e.g. JLink`AddToClassPath (Sorry, currently I don't extract the current contexts from the parse
  tree. Additionally, autocompletion of contexts is not implemented yet).
- Documentation even works on operators which have more than one letter. Try for instance /@ or =!= or __. The current
  limitation that only operators with more than 2 letters are supported is only because then I can extract the token
  easily. By inspecting the AST we can surely support this for all operators.

4. Commenting

- With Ctrl+/ you can comment or uncomment lines or selected blocks
- With Ctrl+Shift+/ you can comment a selected text region.

5. Selection

- You can expand the current selection by pressing Ctrl+W. This selects step after step the next surrounding expression
  by walking up the parse tree.
- Shrinking the selection works the same way with Ctrl+Shift+W

6. Colors

- If you go to Settings->Editor->Colors&Fonts->Mathematica you can now adjust the colors used for highlighting


Unimplemented features
----------------------

- Completion of own function and variable definitions
- Reformat code
- Live templates like function definitions with Module/Block f[x_,..]:=Module[{..},]
- Surround with code constructs like Module, Block, If, Switch..
- Annotate semantic errors by inspecting the AST
- Special handling of Options completion. Analyse surrounding function and suggest matching options with higher priority
- Information of function arguments (parameter info)
- Show quick doc and call external Wolfram documentation
- Introduce new variables inside Block/Module or even in the global context
- Show FullForm or convert to FullForm of expression with operators
- Check syntax of file through JLink with Mathematica


**Internal Unimplemented**

- Create Mathematica expressions to check validity of parser
-
