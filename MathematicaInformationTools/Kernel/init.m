(* Mathematica Package *)
(* Created by Mathematica Plugin for IntelliJ IDEA, see http://wlplugin.halirutan.de/ *)

(* :Title: MathematicaInformationTools *)
(* :Context: MathematicaInformationTools` *)
(* :Author: Patrick Scheibe *)
(* :Date: 2018-09-30 *)

(* :Package Version: 0.1 *)
(* :Mathematica Version: 11.3 *)
(* :Copyright: (c) 2018 Patrick Scheibe *)
(* :Keywords: *)
(* :Discussion: *)

Package["MathematicaInformationTools`"]

ClearAll[
  $packageMacros,
  $contexts,
  $builtInNames,
  $symbolVersions,
  $allNames,
  $additionalSymbols
];

getContextNames[context_String] := Block[{$ContextPath = {context}},
  StringJoin[context, #]& /@ Names[RegularExpression[context <> "\$?[A-Z]\\w*"]]
];
getStrippedContextNames[context_String] := Block[{$ContextPath = {context}},
  Names[RegularExpression[context <> "\$?[A-Z]\\w*"]]
];

$packageMacros = {
  "Package",
  "PackageExport",
  "PackageScope",
  "PackageImport"
};

$additionalSymbols = {
  "FEPrivate`AddSpecialArgCompletion"
};

$contexts = Sort[
  Join[
    Select[Contexts[],
      StringFreeQ[#, { "`Private`", "`PackagePrivate`", StartOfString ~~ _?LowerCaseQ ~~ ___, StartOfString ~~ "$" ~~ ___ }] &],
    {"System`Private`"}
  ]
];
$builtInNames = Sort[Join[getStrippedContextNames["System`"], $packageMacros]];
$symbolVersions = Association @@ Get[FileNameJoin[{DirectoryName@System`Private`$InputFileName, "versionedSymbols.m"}]];
$allNames = Sort[Join[Flatten[getContextNames /@ $contexts], $additionalSymbols ]];

(* For good code completion we need an ordering of all possible completions. This is done with the *)
(* function frequency list that comes with Mathematica nowadays. I just assign numbers according to the *)
(* place in this list. The higher the number, the more important and the more like is the completion result. *)
PackageExport["$functionFrequency"]
$functionFrequency = With[{file = First[FileNames["all_top_level.m", {$InstallationDirectory}, Infinity]]},
  Dispatch[Append[
    MapIndexed[Rule["System`" <> #1, First[#2]]&, Reverse[Get[file]]],
    _ -> "0"
  ]]
];

namedCharacterQ[str_String] :=
    StringMatchQ[ToString@FullForm[str], "\"\\[" ~~ __ ~~ "]\""];

(* Call patterns, attributes and options of functions are available too and don't need to be extracted manually *)
PackageExport["$functionInformation"]
$functionInformation = With[{file = First[FileNames["SystemFiles/Kernel/TextResources/English/FunctionInformation.m", {$InstallationDirectory}, Infinity]]},
  DeleteCases[Rule @@@ Get[file], {_String?namedCharacterQ, __}, Infinity]
];

getContextNames[context_String] := Block[{$ContextPath = {context}},
  StringJoin[context, #]& /@ Names[RegularExpression[context <> "\$?[A-Z]\\w*"]]
];

ClearAll[isFunction, getOptions, getAttributes];
isFunction[symbol_String] := Not[TrueQ[Quiet@ToExpression[symbol, InputForm, ValueQ]]];
getOptions[symbol_String /; isFunction[symbol]] := Quiet@Keys[ToExpression[symbol, InputForm, Options]];
getOptions[__] := {};
getAttributes[symbol_String /; isFunction[symbol]] := With[
  {
    result = Quiet@ToExpression[symbol, InputForm, Attributes]
  },
  result /; Head[result === List]
];
getAttributes[__] := {};
getImportance[symbol_String] := ToExpression[(symbol /. $functionFrequency)];

cleanProperty[arg_List] := arg;
cleanProperty[__] := {};

PackageExport["getInformation"]
getInformation[context_, {name_, patt_, opts_, highlighting_, unknown_, _}] := Association[
  "context" -> context,
  "name" -> name,
  "functionQ" -> isFunction[context <> name] || Length[cleanProperty[patt]] > 0 || Length[cleanProperty[opts]] > 0,
  "options" -> cleanProperty@opts,
  "attributes" -> ToString /@ getAttributes[context <> name],
  "callPattern" -> ToString /@ cleanProperty@patt,
  (*"Highlighting" -> (cleanProperty[highlighting /. Infinity -> -1]),*)
  "importance" -> getImportance[context <> name]
];
getInformation[context_, {name_}] := getInformation[context, {name, {}, {}, {}, {}, None}];
getInformation[context_, {name_, patt_}] := getInformation[context, {name, patt, {}, {}, {}, None}];
getInformation[context_, {name_, patt_, opts_ }] := getInformation[context, {name, patt, opts, {}, {}, None}];
getInformation[context_, {name_, patt_, opts_, highlighting_}] := getInformation[context, {name, patt, opts, highlighting, {}, None}];
getInformation[context_, {name_, patt_, opts_, highlighting_, _}] := getInformation[context, {name, patt, opts, highlighting, {}, None}];

PackageExport["SymbolInformation"]
SymbolInformation[] := Association @@ Flatten[
  {
    Table[
      With[{context = First[entry]},
        context <> #[[1]] -> getInformation[context, #]& /@ entry[[2]]
      ], {entry, $functionInformation}
    ],
    # -> getInformation["", {#, {_}}] & /@ $packageMacros
  }
];

PackageExport["SaveSymbolInformation"]
SaveSymbolInformation[outputPath_String /; DirectoryQ[outputPath]] := Export[
  FileNameJoin[{outputPath, "SymbolInformation.json"}],
  SymbolInformation[],
  "JSON"
];

PackageExport["SaveContexts"]
SaveContexts[outputPath_String /; DirectoryQ[outputPath]] := Export[
  FileNameJoin[{outputPath, "Contexts.json"}],
  $contexts,
  "JSON"
];

PackageExport["SaveSymbolVersions"]
SaveSymbolVersions[outputPath_String /; DirectoryQ[outputPath]] := Export[
  FileNameJoin[{outputPath, "SymbolVersions.json"}],
  $symbolVersions,
  "JSON"
];

PackageExport["SaveSystemSymbols"]
SaveSystemSymbols[outputPath_String /; DirectoryQ[outputPath]] := Export[
  FileNameJoin[{outputPath, "SystemSymbolNames.json"}],
  $builtInNames,
  "JSON"
];

PackageExport["SaveContextSymbols"]
SaveContextSymbols[outputPath_String /; DirectoryQ[outputPath]] := Export[
  FileNameJoin[{outputPath, "ContextSymbolNames.json"}],
  $allNames,
  "JSON"
];

PackageExport["SaveCompleteMathematicaInformation"]
SaveCompleteMathematicaInformation[outputPath_String /; DirectoryQ[outputPath]] := Module[{},
  SaveSymbolInformation[outputPath];
  SaveContexts[outputPath];
  SaveSymbolVersions[outputPath];
  SaveSystemSymbols[outputPath];
  SaveContextSymbols[outputPath];
];