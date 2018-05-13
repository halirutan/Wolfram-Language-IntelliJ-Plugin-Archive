#Change Log

## Todo

- Inspection for using the same variable in the e.g. Module definition list several times
- Quick fix for adding usages and other messages
- Update to Mathematica version 11.2
- Quick fix for pushing a variable inside the Module definition list
- Completion for file names inside strings

## Version 3.0

- Reimplementation of how Mathematica projects and modules are created.
- Support Mathematica Language version on per IDEA module. This allows for checking built-in functions that are only available
in a later version of Mathematica basing on the module settings in the project settings panel.
- Keeping a leading * when pressing enter inside comments.
- Quick Documentation lookup for own functions. This will show the usage message of a function if available.
- Performance improvement through caching.
- Support for Libraries. Libraries are basically a package folder with Mathematica source code. This code is indexed and
can now be used for completion and navigation. The highlighter shows which functions come from a different file.
- Support for project-wide navigation and resolving of symbols. Now you can refactor, navigate and complete functions
that are located in a different package file.
- Completion inside comments to insert symbols from file.
- Implementation of better local variable support. Especially, "With" supports now several declaration lists.
- Fixing "Go to related symbol". Finds all usages of a symbol and gives file, line and a snip of the code there. You can
directly navigate to any of these places.
- Implemented "Go to Declaration" so that the user can navigate to all targets where a function is given a definition (
I know that go to _declaration_ is misleading for Mathematica, but it is the easier short-cut and widely used).
- Heavy reimplementation of the core algorithms for resolving references.
- Goto Declaration now shows all places, where a functions gets some value. Be it several patterns, usage messages, options, or others.
- Now there is a "Goto Symbol" function that lets you search for symbols that have a usage messages throughout your project