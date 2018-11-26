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

$allNames;
isFunction;
getOptions;
getAttributes;
$functionFrequency;
$functionInformation;
$symbolVersions;

Begin["`Private`"];
<< JLink`;

$symbolVersions = Get[FileNameJoin[{DirectoryName@System`Private`$InputFileName, "versionedSymbols.m"}]];

$contexts = Sort[Join[Select[Contexts[], StringFreeQ[#, {
  "`Private`",
  "`PackagePrivate`",
  StartOfString ~~ _?LowerCaseQ ~~ ___,
  StartOfString ~~ "$" ~~ ___
}] &], {"System`Private`"}]];


(* For good code completion we need an ordering of all possible completions. This is done with the *)
(* function frequency list that comes with Mathematica nowadays. I just assign numbers according to the *)
(* place in this list. The higher the number, the more important and the more like is the completion result. *)
$functionFrequency = With[{file = First[FileNames["all_top_level.m", {$InstallationDirectory}, Infinity]]},
  Dispatch[Append[
    MapIndexed[Rule["System`" <> #1, First[#2]]&, Reverse[Get[file]]],
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
  $allNames = Sort[Flatten[ makeContextNames /@ $contexts]];
]

CreateAuxNames[outDir_ /; DirectoryQ[outDir]] := Module[
  {
    contexts = Union[Context /@ $allNames]
  },
  Export[FileNameJoin[{outDir, "MathematicaContexts.properties"}], contexts, "Table"];
  Export[FileNameJoin[{outDir, "MathematicaContextSymbols.properties"}], Sort[Join[$additionalSymbols, $allNames]], "Table"];
];

CreateSymbolVersions[] := Thread[$versionedNames -> $VersionNumber];
CreateSymbolVersions[existingNames_List] := With[{currVersion = $VersionNumber},
  existingNames /. (Function[n, (n -> oldVersion_) :> (n -> Min[{currVersion, oldVersion}])] /@ $versionedNames)
];

ClearAll[isFunction, getOptions, getAttributes];
isFunction[symbol_String] := Not[TrueQ[Quiet@ToExpression[symbol, InputForm, ValueQ]]];
getOptions[symbol_String /; isFunction[symbol]] := Quiet@Keys[ToExpression[symbol, InputForm, Options]];
getOptions[__] := {};
getAttributes[str_String /; isFunction[str]] := Quiet@ToExpression[str, InputForm, Attributes];
getAttributes[__] := {};


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


