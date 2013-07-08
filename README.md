Mathematica language plugin for IntelliJ IDEA
=============================================

Important Links
---------------

- The official website for plugin users is [hosted here](http://halirutan.de/start).
- The most recent plugin is [Mathematica-IntelliJ-Plugin.jar](https://github.com/halirutan/Mathematica-IntelliJ-Plugin/raw/master/Mathematica-IntelliJ-Plugin.jar)
  in the root of this repository.
- If you find bugs or experience weird behavior please [create a new issue](http://halirutan.myjetbrains.com/youtrack/issues#newissue=yes)
  on the [*Mathematica* bugtracker page](http://halirutan.myjetbrains.com/youtrack/issues/MMAP).
- If you want to participate (there is a lot more to do then just code Java!) please read in [Mathematica Plugin Wiki](https://github.com/halirutan/Mathematica-IntelliJ-Plugin/wiki)
  first, where I 'will' publish documentation which helps to get a smooth start. When you think you need to discuss stuff directly with
  me, you can always reach me in the main chat at mathematica.stackexchange.com or in the
  [IntelliJIDEA Mathematica plugin room](http://chat.stackexchange.com/rooms/8636/intellijidea-plugin-for-mathematica) which
  was created for that purpose.
- The extracted java documentation can be found under [halirutan.github.io/Mathematica-IntelliJ-Plugin](http://halirutan.github.io/Mathematica-IntelliJ-Plugin/).
- Finally, you may want to read the [official post](http://mathematica.stackexchange.com/q/24556/187) on mathematica.stackexchange.com
  where I announced the plugin.



Feature list
------------

- Syntax highlighter
- CamelHump autocompletion for all built-in Mathematica symbols
- Smart completion of built in options of functions
- Completion of locally defined variables, like Module/Block variables or pattern arguments in function definitions.
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

### Autocompletion

- When die completion window pops up and you haven't pressed Ctrl+Space so that no entry in the list is highlighted
  you have several choices:
	(1) Enter just inserts the item on top and the cursor is directly behind the word (Ctrl + . does the same)
	(2) Ctrl+Enter inserts the item on top but if it is a function, it inserts brackets and puts the cursor in the middle
  If you press Ctrl+Space during writing or go down the choice-list, so that an entry is highlighted, you can press
  additionally Space to insert the item and put a space behind it.

### Inserting braces and highlighting brace matcher

- Pressing any kind of brace ( { [ automatically inserts a matching closing brace
- If you are infront of a closing brace and close this brace again, no additional closing brace is inserted. Instead
  the cursor jumps over the closing brace.
- When your braces are not balanced, then an additional brace *is* inserted. Consider this example: f[g[x]
  Go behind the "x" and press a closing ] twice and see what happens.
- Matching braces are highlighted when you are infront of an opening or behind a closing brace
- If are try to highlight a closing brace where the opening one is outside the editor window, the line containing the
  opening braces is shown on top of the editor window.

### Documentation lookup

- With `Ctrl+Q` (`Cmd+j` I think on Mac) you can get documentation of built-in Mathematica functions from the
  <code>System`</code> context.
- Shown are the html/MathML styled usage, Attributes and Options. Additionally, you can click the link to navigate to
  the online documentation of Wolfram
- If a symbol has no online documentation, you automatically search this site for the symbol name and context
- Currently, usage info of contexts ``Developer` ``, ``Experimental` `` and ``JLink` ``
  are provided too but you have to give the full qualified name for this, e.g. ``JLink`AddToClassPath``
  (Sorry, currently I don't extract the current contexts from the parse tree. Additionally, autocompletion of contexts is not implemented yet).
- Documentation even works on operators (other than simple arithmetic like `+`).
  Try for instance `/@` or `=!=` or `__`.
### Commenting

- With Ctrl+/ you can comment or uncomment lines or selected blocks
- With Ctrl+Shift+/ you can comment a selected text region.

### Selection

- You can expand the current selection by pressing Ctrl+W. This selects step after step the next surrounding expression
  by walking up the parse tree.
- Shrinking the selection works the same way with Ctrl+Shift+W

### Colors

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

