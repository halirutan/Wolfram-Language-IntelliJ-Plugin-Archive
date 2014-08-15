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

BeginPackage["FunctionInformation`"]
(* Exported symbols added here with SymbolName::usage *)

Begin["`Private`"] (* Begin Private Context *)

makeContextNames[context_String] :=
    Append[StringReplace[
      Names[RegularExpression[context <> "`\$?[A-Z]\\w*"]],
      context ~~ "`" ~~ rest__ :> rest], context];

names = Sort[
  Join[Names[RegularExpression["\$?[A-Z]\\w*"]],
    makeContextNames["JLink"],
    Names[RegularExpression["(Developer|Internal)`\$?[A-Z]\\w*"]]]];

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

f = FileNameJoin[{$InstallationDirectory,
  "/SystemFiles/Kernel/TextResources/English/FunctionInformation.m"}
];
info1 = Rule @@@ Import[f];

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


createInformation[name_String] := Module[{info},
  info = Cases[
    Context[name] /.
        info1, {ToHeldExpression[name] /.
        Hold[expr_] :> SymbolName[Unevaluated[expr]], __}];
  If[info === {}, info = "",
    info = ";" <> Riffle[ToString /@ First[info], ";"]
  ];
  isFunction[name] <> ";" <> getAttributes[name] <> StringJoin[info]
]

End[] (* End Private Context *)

EndPackage[]