#Mathematica (Wolfram Language) support for IntelliJ IDEA

![teaser](http://i.stack.imgur.com/N5KVt.gif)

This is an easy to install plug-in for almost all IntelliJ based IDEs like IDEA or PyCharm. It will turn the IDE you love into into a powerful development tool for Mathematica and Wolfram Language  code. Since the plug-in and the Community Edition of IDEA  is open-source, you can use everything completely free of charge.
People, who just want to use the plug-in should take a look at the official website [wlplugin.halirutan.de](http://wlplugin.halirutan.de/).

For a very quick start just download and install the free [Community Edition of IDEA](https://www.jetbrains.com/idea/download/). After you have done that, open the *Settings* by pressing <kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>S</kbd> (<kbd>Cmd</kbd>+<kbd>,</kbd> on OS X) and navigate to *Plugins* in the right column. On the right side you find the button *Browse repositories* and there you use the search box to find the **Mathematica Support**. With a right-click you can install it and after the suggested restart of IDEA everything is set up.

**If you use the plug-in**, please leave a comment and rating at the [IntelliJ IDEA Plugins page](https://plugins.jetbrains.com/plugin/writeComment?pr=idea&pluginId=7232)
 (note that you don't have to provide a username. Just leave the field blank).
##![Docs][doc-image]Documentation

There are two kinds of documentation: For **users** there is the [documentation page](http://wlplugin.halirutan.de/index.php/features) and a very detailed [blog post](http://wlplugin.halirutan.de/index.php/blog/7-how-to-use-idea-effectively-with-mathematica-code) on the official website. There, you will learn what features the plug-in provides and how you can use them.

For **developers**, the first action is to set up the build-environment to compile the code into an IDEA plug-in. There is a [very detailed how-to](http://wlplugin.halirutan.de/index.php/blog/5-plugin-development-how-to-start) on the official website that explains every step. Additionally, you will find a screen-cast there, where the procedure is shown step by step. The code itself contains java-documentation that is uploaded to [the repository's IO page](http://halirutan.github.io/Mathematica-IntelliJ-Plugin/).

Finally, there I have posted information on [stack exchange](http://mathematica.stackexchange.com/questions) and the [Wolfram Community](http://community.wolfram.com/). Here is a list of probably interesting readings:

- the [official post](http://mathematica.stackexchange.com/q/24556/187) on mathematica.stackexchange.com where I announced the plug-in
- the [first announcement](http://community.wolfram.com/groups/-/m/t/139047?p_p_auth=Lp2pA68y) on the Wolfram Community and a very detailed [new version post](http://community.wolfram.com/groups/-/m/t/353812?p_p_auth=Lp2pA68y)


##![dev image][dev-image] Development  ![travis](https://travis-ci.org/halirutan/Mathematica-IntelliJ-Plugin.svg?branch=develop)

### Code

When you want to crawl through the code, you should know that the [master branch](https://github.com/halirutan/Mathematica-IntelliJ-Plugin/tree/master) only contains the latest stable release. The current development will (almost) always take place in feature or bugfix branches that derive from the [develop branch](https://github.com/halirutan/Mathematica-IntelliJ-Plugin). This kind of follows the [GitFlow branching model](http://nvie.com/posts/a-successful-git-branching-model/).

###Testing and Continuous Integration

Testing the plugin is not as simple as writing unit test since most features need the [environment of the IDE](https://confluence.jetbrains.com/display/IDEADEV/Testing+IntelliJ+IDEA+Plugins). Currently, I'm still working on including tests in a [separate branch](https://github.com/halirutan/Mathematica-IntelliJ-Plugin/tree/code_testing) that will soon be merged into the main develop line.

On each push the code is compiled and checked for errors by [Travis CI](https://travis-ci.org/halirutan/Mathematica-IntelliJ-Plugin).
When a feature is finished or a bugfix ready, I will tag this commit and Travis CI will automatically create a [release from this](https://github.com/halirutan/Mathematica-IntelliJ-Plugin/releases) which can be installed and tested by everyone.

###You want to help but don't know Java?

No problem. If you like to contribute something, then there are a lot more ways than just with code! Here is a small list:

- We need people that *heavily test* the plug-in and [report bugs](http://halirutan.myjetbrains.com/youtrack/newissue). This can be as simple as just using the plug-in for your work, but when you come across weird behavior, you try to reproduce it and report it with a small example of what you expect.
- Are you a design savant? Do you understand the beauty of carefully chosen colors and icons? Currently, I'm doing this by myself but neither do I have the time I would like to spend for this, nor do I have the experience. We need [icons and syntax highlighting](http://community.wolfram.com/c/portal/getImageAttachment?filename=structureView.png&userId=44126) that fit perfectly into the Darcula and Default IDEA color schemes.
- Are you a work-flow optimizer with a good feeling for how features should work to make them as intuitive as possible? Drop by and explain how we can make the plug-in even more awesome.


##![bug image][issues-image] Reporting issues

For issue- and bug-tracking [Jetbrains YouTrack](http://halirutan.myjetbrains.com/youtrack/issues) is used.
If you find bugs, experience weird behavior or have feature suggestions please [create a new issue there](http://halirutan.myjetbrains.com/youtrack/issues#newissue=yes). Of course I will answer any issues that are reported through [GitHub](https://github.com/halirutan/Mathematica-IntelliJ-Plugin/issues) as well.

Note that the plug-in itself contains a bug-reporter. When an unhandled exception is thrown, you are notified by IDEA and
you can give as much details as possibles which are then automatically turned into a new issue in the bug-tracker.

##![contact image][contact-image] Credits, Contact and Licensing
###Credits
A lot of people helped to bring this project to life. Without them, it would probably have died a long time ago. Especially 4 people supported the plug-in from its very buggy first version. They were never tired of discussing feature details, testing new stuff, reporting bugs and suggesting future improvements:

- [rsmenon:](https://github.com/rsmenon) Provided parts of the Module and Color Scheme implementations, testing. Improvements of the parsing for *Mathematica* version 10.

- [Szabolcs Horvát:](https://github.com/szhorvat) Heavy testing. Convinced me to implement a *Structure View* provider and discussed every detail, especially how it should work.

- [Leonid Shifrin:](https://github.com/lshifr) Most interesting discussions about implementation design and details.

- [Rolf Mertig:](http://www.gluonvision.com/) Testing and discussions about important features that are available in Wolfram Workbench.

In addition to that, a special thanks goes to [Aliaksandr Dubrouski](https://github.com/dubrousky) and [Jakub Kuczmarski](https://github.com/jkuczm). The first on pointed me to Travis CI and explained how I can add support for it. The second one discussed details of the ``MUnit` `` support in Wolfram Workbench.

###Contact

The easiest way to make contact is to write in the the [dedicated stackexchange chat](http://chat.stackexchange.com/rooms/8636/intellijidea-plugin-for-mathematica). There, you should ping me (@halirutan) so that I get notified. If you don't have enough reputation to talk there, feel free to write me an email. My address can be seen by evaluating this in *Mathematica*

    Uncompress["1:eJxTTMoPChZhYGAoSCwpykzOdshIzMksKi1JzNNLSQUAgX0JmQ=="]

If you have specific feature questions, please don't hesitate to [open a new issue](http://halirutan.myjetbrains.com/youtrack/newissue) in YouTrack.

###Licensing

The general license for the IntelliJ IDEA Plugin is the [MIT License](https://github.com/halirutan/Mathematica-IntelliJ-Plugin/blob/develop/LICENSE). Users should note that *Wolfram Research Inc.* (WRI) has generously granted this project the right to use the internal documentation of *Mathematica* and make it available as part of the plugin. The only requirement was to put the legal information in a place where end-users find it. Therefore, please note that..

> This software includes information that is Copyright © 2013 Wolfram Research, Inc.
> Such information is used with permission and for the limited purpose of enabling
> code completion and hints in the Mathematica IntelliJIDEA Plugin.  Any other use
> requires written permission from the copyright holder.

----

<div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a>         is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div>


[doc-image]: http://i.stack.imgur.com/erf8e.png
[dev-image]: http://i.stack.imgur.com/D9G2G.png
[issues-image]: http://i.stack.imgur.com/K4fGd.png
[contact-image]: http://i.stack.imgur.com/tCbmW.png
