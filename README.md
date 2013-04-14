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

**Unimplemented**

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
