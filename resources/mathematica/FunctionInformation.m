(* Mathematica Package         *)
(* Created by IntelliJ IDEA    *)

(* :Title: FunctionInformation     *)
(* :Context: FunctionInformation`  *)
(* :Author: patrick            *)
(* :Date: 8/14/14              *)

(* :Package Version: 1.0       *)
(* :Mathematica Version:       *)
(* :Copyright: (c) 2014 patrick *)
(* :Keywords:                  *)
(* :Discussion:                *)

BeginPackage["FunctionInformation`"];

CreateCompletionInformation::usage = "CreateCompletionInformation[] returns a list of strings where each element is an \
entry of the .properties file that is used to enable autocompletion in idea.";
CreateSymbolVersions::usage = "CreateSymbolVersions[] creates a list all symbols in the form {sym1 -> $VersionNumber, \
sym2 -> $VersionNumber}. CreateSymbolVersions[list] updates a list of versioned symbol with the current $VersionNumber.";
InitializeSymbolInformation::usage = "InitializeSymbolInformation[] loads all symbols we need in the Plugin for completion and resolving";

Begin["`Private`"];

$additionalSymbols = {
  "FEPrivate`AddSpecialArgCompletion"
}

<< JLink`;

(* For good code completion we need an ordering of all possible completions. This is done with the *)
(* function frequency list that comes with Mathematica nowadays. I just assign numbers according to the *)
(* place in this list. The higher the number, the more important and the more like is the completion result. *)
$functionFrequency = With[{file = First[FileNames["all_top_level.m", {$InstallationDirectory}, Infinity]]},
  Dispatch[Append[
    MapIndexed[Rule[#1, ToString @@ #2]&, Reverse[Get[file]]],
    _ -> "0"
  ]]
];

(* Call patterns, attributes and options of functions are available too and don't need to be extracted manually *)
$functionInformation = With[{file = First[FileNames["SystemFiles/Kernel/TextResources/English/FunctionInformation.m", {$InstallationDirectory}, Infinity]]},
  Rule @@@ Get[file]
];

makeContextNames[context_String] := Block[{$ContextPath = {context}},
  StringJoin[context, #]& /@ Names[RegularExpression[context <> "\$?[A-Z]\\w*"]]
];

InitializeSymbolInformation[] := Module[{},
  $builtInNames = Sort[Flatten[ makeContextNames /@ {"System`"} ]];
  $versionedNames = Sort[Flatten[ makeContextNames /@ {"System`", "Developer`", "Internal`", "JLink`"} ]];
  $allNames = Sort[Flatten[ makeContextNames /@ DeleteCases[Contexts[], "Global`" | "System`"]]];
]

CreateAuxNames[outDir_ /; DirectoryQ[outDir]] := Module[
  {
    contexts = Union[Context /@ $allNames]
  },
  Export[FileNameJoin[{outDir, "contexts.properties"}], contexts, "Table"];
  Export[FileNameJoin[{outDir, "contextSymbols.properties"}], Sort[Join[$additionalSymbols, $allNames]], "Table"];
];

CreateSymbolVersions[] := Thread[$versionedNames -> $VersionNumber];
CreateSymbolVersions[existingNames_List] := With[{currVersion = $VersionNumber},
  existingNames /. (Function[n, (n -> oldVersion_) :> (n -> Min[{currVersion, oldVersion}])] /@ $versionedNames)
];

isFunction[str_String] :=
    With[{usg =
        ToString[
          Function[s, MessageName[s, "usage"], HoldAll] @@
              ToHeldExpression[str]]},
      str <> " " <>
          If[StringMatchQ[usg, __ ~~ str ~~ "[" ~~ ___ ~~ "]" ~~ ___],
            " = true", " = false"]
    ]

getOptions[str_String] :=
    str <> " = " <>
        StringTrim[
          Function[expr,
            Riffle[ToString[#, InputForm] & /@ (First /@
                Options[Unevaluated[expr]]), " "] // StringJoin, HoldAll] @@
              ToHeldExpression[str], "{" | "}" | ","]

getAttributes[str_String] :=
    str <> " = " <>
        StringTrim[
          Function[expr,
            Riffle[ToString /@ Attributes[Unevaluated[expr]], " "] //
                StringJoin, HoldAll] @@ ToHeldExpression[str], "{" | "}"]

isFunction[str_String] :=
    With[{usg =
        ToString[
          Function[s, MessageName[s, "usage"], HoldAll] @@
              ToHeldExpression[str]]},
      str <> " " <>
          If[StringMatchQ[usg, __ ~~ str ~~ "[" ~~ ___ ~~ "]" ~~ ___],
            " = true", " = false"]
    ]
getAttributes[str_String] :=
    StringTrim[
      Function[expr,
        Riffle[ToString /@ Attributes[Unevaluated[expr]], " "] //
            StringJoin, HoldAll] @@ ToHeldExpression[str], "{" | "}"];
getOptions[str_String] :=
    StringTrim[
      Function[expr,
        Riffle[ToString[#, InputForm] & /@ (First /@
            Options[Unevaluated[expr]]), " "] // StringJoin, HoldAll] @@
          ToHeldExpression[str], "{" | "}" | ","]


createInformation[name_String] := Module[{importance, info, context},
  importance = StringReplace[name, "System`" ~~ n__ :> n] /. $functionFrequency;
  Check[
    context = Context[name];
    info = Cases[
      context /.
          $functionInformation, {ToHeldExpression[name] /.
          Hold[expr_] :> SymbolName[Unevaluated[expr]], __}];
    If[info === {}, info = "",
      info = ";" <> Riffle[ToString /@ First[info], ";"]
    ];
    name <> "=" <> importance <> ";" <> getAttributes[name] <> StringJoin[info],
    ""
  ]
];

CreateCompletionInformation[] := createInformation /@ $builtInNames;

End[];
(* End Private Context *)

EndPackage[];


