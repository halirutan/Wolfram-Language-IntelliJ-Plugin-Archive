BeginPackage["IDEAPlugin`"];

CreateHTMLUsage::usage = "CreateHTMLUsage[function_String] creates HTML/MathML code of the usage message of the symbol.";
CreateAllHTMLUsages::usage = "CreateAllHTMLUsages[file] exports all usages to one file.";

CreateHtmlUsageForContext::usage = "CreateHtmlUsageForContext[context, exportPath] creates html usage files for all defined \
defined functions in context and exports them to folder exportPath";


(*Begin["Private`"];*)

a_ + b_;

Function[ Null,
#+#,  {Listable}  ]

(*	Here we replace Mathematica box expressions with HTML constructs. If we lack of some things we just use a
	a string representation like with UnderscriptBox *)
boxRules = {
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

(*	Repeatedly replacing box expressions until nothing is left, then we join everything into a big String *)
convertBoxExpressionToHTML[boxExpr_] := StringJoin[ToString /@ Flatten[ReleaseHold[MakeExpression[boxExpr]//.boxRules]]]

(* 	We need to take care to not evaluate symbols like Black (which is ev to RGBColor[0,0,0]) before we extract the
	usage message.
*)
extractUsage[str_] := With[{usg = Function[expr, expr::usage, HoldAll] @@ MakeExpression[str]},
	If[Head[usg] === String, usg, ""]];

extractUsage[str_String, context_String] :=
  With[{usg =
     Function[expr, expr::usage, HoldAll] @@
      MakeExpression[context <> str, StandardForm]},
   If[Head[usg] === String, usg, ""]];

replaceNestedStyleString[str_]:=StringReplace[
    str,
    {Shortest["\\\"\\!\\(\\*StyleBox[\\\""~~name__~~"\\\""~~__~~"\\_"~~n_~~"\\)\\\""] :>
        "&quot;<em>"~~name~~"<sub>"~~n~~"</sub></em>&quot;",
    Shortest["\\\"\\!\\(\\*StyleBox[\\\""~~name__~~"\\\""~~__~~"\\)\\\""] :>
        "&quot;<em>"~~name~~"</em>&quot;"
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


createOptionString[s_] := With[{opts = Function[expr, Options[expr], HoldAll] @@ MakeExpression[s]},
  	If[opts === {},
  		"</p><b>Symbol has no options.</b>",
  		"</p><b>Options: </b>" <> StringJoin@Riffle[ToString[First[#]] & /@ opts, ", "]
   	]
];

replaceNestedStyleString[str_] := StringReplace[str,
                                   Shortest["\\\"\\!\\(\\*StyleBox[\\\"" ~~ name__ ~~ "\\\"" ~~ __ ~~
                                      "\\_" ~~ n_ ~~ "\\)\\\""] :>
                                    "\\\"<em>" ~~ name ~~ "<sub>" ~~ n ~~ "</sub></em>\\\""];

CreateHTMLUsage[s_String] := Module[{
  usg = extractUsage[s],
  attr = Attributes[s],
  link, linkName},

	{linkName, link} = createLinkName[s];
 	result = "<h3><a href=\"http://reference.wolfram.com/mathematica/ref/" <>
    link <> ".html\">" <> linkName <> "</a></h3>" <> If[usg =!= "",
      "<ul><li>" <> StringReplace[StringReplace[usg, {Shortest["\!\(\*" ~~ content__ ~~ "\)"] :>
    			convertBoxExpressionToHTML[replaceNestedStyleString[content]], "\n" :> "<li>"}
    		], {"\[Null]" :> "", a_?(StringMatchQ[ToString@FullForm[#], "\"\\[" ~~ __ ~~ "]\""] &) :>
    			StringReplace[ToString[a, MathMLForm], {WhitespaceCharacter :> ""}]}
    	] <> "</ul>", ""] <> "<b>Attributes:</b> " <> StringJoin[ToString /@ Riffle[attr, ", "]] <> "</p>" <> createOptionString[s] <> "\n";
  {linkName, result}

];

CreateAllHTMLUsages[file_String] := Module[{names},
  names = Names["System`*"];
  Export[file, StringJoin[createHtmlUsage /@ names], "Text"]
];

createOnlineLink[symbol_String, context_String] := Module[
    {charPart = "", middle, symbolPart, linkName, finalLink,
   root = "http://reference.wolfram.com/mathematica/"},
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
  If[URLFetch[finalLink, "StatusCode"] === 404,
   finalLink =
    "http://reference.wolfram.com/documentation-search.html?query=" <>
      linkName <> "%20" <> StringDrop[context, -1] <>
     "&collection=reference&lang=en"
   ];


  {symbolPart,
   "<a href=\"" <> finalLink <> "\">" <> linkName <> "</a>"}
  ]


createOptionString[s_] :=
  With[{opts =
     Function[expr, Options[expr], HoldAll] @@ MakeExpression[s]},
   If[opts === {}, "<p><b>Symbol has no options.</b></p>",
    "<p><b>Options: </b>" <>
     StringJoin@Riffle[ToString[First[#]] & /@ opts, ", "] <> "</p>"
    ]];

CreateHTMLUsageString[s_String, context_String] := Module[{
    usg = extractUsage[s, context],
    attr = With[{full = context <> s}, Attributes[full]],
    link, name},

   {name, link} = createOnlineLink[s, context];
   {name,
    "<h3>" <> link <> "</h3>" <>
     If[usg =!= "",
      "<ul><li>" <>
       StringReplace[
        StringReplace[
         usg, {Shortest["\!\(\*" ~~ content__ ~~ "\)"] :>
           StringReplace[

            convertBoxExpressionToHTML[
             StringReplace[replaceNestedStyleString[content],
              "\n" :> ""]],
            {"<>" -> "&lt;&gt;"}]
          , "\n" :> "<li>"}], {"\[Null]" :> "",
         a_?(StringMatchQ[ToString@FullForm[#],
              "\"\\[" ~~ __ ~~ "]\""] &) :>
          StringReplace[
           ToString[a, MathMLForm], {WhitespaceCharacter :> ""}]}] <>
       "</ul>", ""] <> "<p><b>Attributes:</b> " <>
     StringJoin[ToString /@ Riffle[attr, ", "]] <> "</p>" <>
     createOptionString[s]}
   ];

createHtmlUsageForContext[context_String, path_String] := Module[{
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

(*End[];*)
EndPackage[];
