(* Mathematica Package         *)
(* Created by IntelliJ IDEA    *)

(* :Title: IDEAPlugin     *)
(* :Context: IDEAPlugin`  *)
(* :Author: patrick            *)
(* :Date: 8/14/14              *)

(* :Package Version: 1.0       *)
(* :Mathematica Version:       *)
(* :Copyright: (c) 2014 patrick *)
(* :Keywords:                  *)
(* :Discussion: the functions in here work pretty well. There are only a handful of symbols that contain wrongly quoted
   strings or are in the wrong format in their usage message and therefore lead to an error. Those are
      {"AbsoluteTime", "DateList", "DeclarePackage", "FindList", "StringReplacePart", "URLSave", "$DefaultFrontEnd"}
   For the rest of the symbols it works pretty decent. *)

BeginPackage["IDEAPlugin`"];

CreateHTMLUsageString::usage = "CreateHTMLUsageString[symbol, context, opts] creates HTML/MathML code of the usage message of the symbol.";
CreateHtmlUsageForContext::usage = "CreateHtmlUsageForContext[context_String, path_String] creates html files for all usages of all \
symbols in context.";

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
  Export[FileNameJoin[{outDir, "MathematicaContexts.properties"}], contexts, "Table"];
  Export[FileNameJoin[{outDir, "MathematicaContextSymbols.properties"}], Sort[Join[$additionalSymbols, $allNames]], "Table"];
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

(* ::Section:: *)
(* Creating html usage messages *)

(*	Here we replace Mathematica box expressions with HTML constructs. If we lack of some things we just use a
	a string representation like with UnderscriptBox *)
$boxRules = {
  StyleBox[f_, "TI"] :> {"<em>", f, "</em>"},
  StyleBox[f_, ___] :> {f},
  RowBox[l_] :> {l},
  SubscriptBox[a_, b_] :> {a, "<sub>", b, "</sub>"},
  SuperscriptBox[a_, b_] :> {a, "<sup>", b, "</sup>"},
  RadicalBox[x_, n_] :> {x, "<sup>1/", n, "</sup>"},
  FractionBox[a_, b_] :> {"(", a, ")/(", b, ")"},
  SqrtBox[a_] :> {"&radic;(", a, ")"},
  CheckboxBox[a_, ___] :> {"<u>", a, "</u>"},
  OverscriptBox[a_, b_] :> {"Overscript[", a, b, "]"},
  OpenerBox[a__] :> {"Opener[", a, "]"},
  RadioButtonBox[a__] :> {"RadioButton[", a, "]"},
  UnderscriptBox[a_, b_] :> {"Underscript[", a, b, "]"},
  UnderoverscriptBox[a_, b_, c_] :> {"Underoverscript[", a, b, c,
    "]"},
  SubsuperscriptBox[a_, b_, c_] :> {a, "<sub><small>", b,
    "</small></sub><sup><small>", c, "</small></sup>"}
};

(* The situation is weird. On Linux there are some symbols that are not displayed correctly, like &#10869; while *)
(* on other systems this works fine. I will only fix very few since it should work in general *)
$specialHtmlCharacterRules = {
  "<>" -> "&lt;&gt;",
  "&#62754;" -> "&rarr;",
  "&#61715;" -> "&lt;&#x7c;",
  "&#61716;" -> "&#x7c;&gt;",
  "&#10740;" -> ":>",
  "&#10869;" -> "=="
};

$referenceURL = "http://reference.wolfram.com/language/";
$searchURL = "http://reference.wolfram.com/search/?q=";


(*	Repeatedly replacing box expressions until nothing is left, then we join everything into a big String *)
convertBoxExpressionToHTML[boxExpr_] := StringJoin[ToString /@ Flatten[ReleaseHold[MakeExpression[boxExpr] //. $boxRules]]]

(* 	We need to take care to not evaluate symbols like Black (which is ev to RGBColor[0,0,0]) before we extract the
	usage message.
*)
hasUsage[str_] := With[{usg = ToExpression[str, InputForm, Function[s, MessageName[s, "usage"], HoldFirst]]},
  Head[usg] =!= MessageName
];

getUsage[str_] := With[{usg = ToExpression[str, InputForm, Function[s, MessageName[s, "usage"], HoldFirst]]},
  If[Head[usg] =!= MessageName,
    usg, ""
  ]
];

extractUsage[str_] := With[{usg = Function[expr, expr::usage, HoldAll] @@ MakeExpression[str]},
  If[Head[usg] === String, usg, ""]];

extractUsage[str_String, context_String] :=
    With[{usg =
        Function[expr, expr::usage, HoldAll] @@
            MakeExpression[context <> str, StandardForm]},
      If[Head[usg] === String, usg, ""]];

replaceNestedStyleString[str_] := StringReplace[
  str,
  {Shortest["\\\"\\!\\(\\*StyleBox[\\\"" ~~ name__ ~~ "\\\"" ~~ __ ~~ "\\_" ~~ n_ ~~ "\\)\\\""] :>
      "&quot;<em>" ~~ name ~~ "<sub>" ~~ n ~~ "</sub></em>&quot;",
    Shortest["\\\"\\!\\(\\*StyleBox[\\\"" ~~ name__ ~~ "\\\"" ~~ __ ~~ "\\)\\\""] :>
        "&quot;<em>" ~~ name ~~ "</em>&quot;"
  }];

namedCharacterQ[str_String] :=
    StringMatchQ[ToString@FullForm[str], "\"\\[" ~~ __ ~~ "]\""];

fixNamedCharacterLink[str_] := {StringReplace[
  ToString@FullForm[str], {"\"" :> "", "\\" -> "\\\\"}],
  StringReplace[
    ToString@FullForm[str], {"\"" :> "", "\\[" ~~ c__ ~~ "]" :> c}]};

createLinkName[s_] := If[StringMatchQ[ToString@FullForm[s], "\"\\[" ~~ __ ~~ "]\""],
  {
    StringReplace[ToString@FullForm[s], {"\"" :> "", "\\" -> "\\\\"}],
    StringReplace[ToString@FullForm[s], {"\"" :> "", "\\[" ~~ c__ ~~ "]" :> "character/" ~~ c}]
  },
  {s, s}
];



replaceNestedStyleString[str_] := StringReplace[str,
  Shortest["\\\"\\!\\(\\*StyleBox[\\\"" ~~ name__ ~~ "\\\"" ~~ __ ~~
      "\\_" ~~ n_ ~~ "\\)\\\""] :>
      "\\\"<em>" ~~ name ~~ "<sub>" ~~ n ~~ "</sub></em>\\\""];


createOnlineLink[symbol_String, context_String, checkUrl_] := Module[
  {charPart = "", middle, symbolPart, linkName, finalLink,
    root = $referenceURL},
  If[namedCharacterQ[symbol],
    {linkName, symbolPart} = fixNamedCharacterLink[symbol];
    charPart = "character/",
    {linkName, symbolPart} = {symbol, symbol}
  ];

  If[
    context === "System`",
    middle = "",
    middle = StringDrop[context, -1] <> "/"
  ];
  finalLink =
      root <> middle <> "ref/" <> charPart <> symbolPart <> ".html";
  If[TrueQ[checkUrl] && URLFetch[finalLink, "StatusCode"] === 404,
    finalLink =
        $searchURL <> linkName
  ];


  {symbolPart,
    "<a href=\"" <> finalLink <> "\">" <> linkName <> "</a>"}
]


createOptionString[s_] := With[{opts = Function[expr, Options[Unevaluated[expr]], HoldAll] @@ MakeExpression[s]},
  If[opts === {},
    "<p><b>Symbol has no options.</b></p>",
    "<p><b>Options: </b>" <> StringJoin@Riffle[ToString[First[#]] & /@ opts, ", "] <> "</p>"
  ]
];

convertUsageStringToHTML[usg_] := Module[{},
  Quiet@Check[
    StringSplit[
      StringReplace[StringReplace[
        StringReplace[
          usg, {Shortest["\!\(\*" ~~ content__ ~~ "\)"] :>
            StringReplace[
              convertBoxExpressionToHTML[StringReplace[replaceNestedStyleString[content], "\n" :> ""]],
              "<>" -> "&lt;&gt;"] }], {

          "\[Null]" :> "",
          a_?(StringMatchQ[ToString@FullForm[#], "\"\\[" ~~ __ ~~ "]\""] &) :> StringReplace[ToString[a, MathMLForm], {WhitespaceCharacter :> ""}]}
      ], $specialHtmlCharacterRules], "\n"],
    ""
  ]
];

Options[CreateHTMLUsageString] = {
  "CheckURL" -> False
};

CreateHTMLUsageString[s_String, context_String, OptionsPattern[]] := Module[{
  usg = extractUsage[s, context],
  attr = With[{full = context <> s}, Attributes[full]],
  link, name, html},

  {name, link} = createOnlineLink[s, context, OptionValue["CheckURL"]];
  html = Quiet[Check[convertUsageStringToHTML[usg], usg]];
  {name, StringJoin[
    "<h3>", link, "</h3>",
    If[usg =!= "",
      "<ul><li>" <> convertUsageStringToHTML[usg] <> "</ul>",
      ""
    ],
    "<p><b>Attributes:</b>",
    StringJoin[ToString /@ Riffle[attr, ", "]],
    "</p>",
    createOptionString[s]
  ]}
];

CreateHtmlUsageForContext[context_String, path_String] := Module[{
  names, outPath},
  outPath = FileNameJoin[{path, StringDrop[context, -1]}];
  If[! DirectoryQ[outPath],
    CreateDirectory[outPath]
  ];
  Begin[context];
  Block[{$ContextPath = {}},
    names = Names[context <> "*"];
  ];
  Do[
    With[{res = CreateHTMLUsageString[name, context]},
      Export[FileNameJoin[{outPath, res[[1]] <> ".html"}],
        res[[2]], "Text"];
    ], {name, names}];
  End[];
]

End[];
EndPackage[];
