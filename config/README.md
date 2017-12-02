# Config files for IDEA

After a long struggle between different systems and using Gradle in combination with IDEA's build system for plugins,
I decided to leave all compilation-related files and modules for IDEA out of the repository. The folders here are
configurations unrelated to compilation. It doesn't matter if you use Gradle or IDEA's built-in compilation, you
can copy these folders into the .idea path, once your set-up is finished.

In detail, the settings in the folder will adjust the following things:

- `codestyle/` The code-style I'm using. Nothing spectacular and I'm not a codestyle-fanatic, but I use an default indent
of 2 which is uncommon. Additionally, I adjusted some line wrapping and how code is reformatted.
- `copyright/` I'm using two copyrights. The MIT license for the plugin code and files that I extracted from Mathematica
get the Wolfram license.
- `inspectionProfiles/` Again nothing special but I try to keep my files clean as much as possible of inspection warnings.
- `scopes/` The scopes are import because they define which license is applied to which file

# Building the plugin

There are two ways: You use Gradle or you set up an IDEA Plugin project. Gradle is easier to set up but doesn't integrate
as well as a real IDEA project. To use Gradle, simply import the code as "new from existing source" and check Gradle
in the following dialog. You should turn on "auto-import" and make sure that Gradle uses Java 1.8. Everything else
should be working out of the box. Please check the doc of the IntelliJ Gradle plugin on GitHub.

Setting up an IDEA Plugin project contains the following steps

- Clone the repo
- IDEA, new project and use Plugin Project. Use a JDK 1.8.0_152 or later. I haven't tried with Java 9!
- When the project is ready, close it again
- copy the folders here into your .idea path
- In Project Settings -> Modules, mark the folders appropriately:
    - `src/` is Source
    - `resources/` is Resources
    - `tests/` is Test
    - `testData/` is TestResources
- Under Libraries, create a new Java library and select the jars from inside the `lib/` folder. Except of `usages.jar`
- `usage.jar` are the bundled usage messages that are used when you want documentation for a symbol. For this, go back to 
Modules, Dependencies and attach a new Jar library on the little green plus sign in the top right corner. Select `usages.jar`
and choose `Classes` in the following dialog. In the table where the library appears, select `Runtime` and `Export`.
- Finally, go to Settings, Compiler, Kotlin and set the JVM target version to 1.8