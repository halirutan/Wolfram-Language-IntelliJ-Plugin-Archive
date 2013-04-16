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

convertBoxExpressionToHTML[boxexpr_] :=
 StringJoin[
  ToString /@
   Flatten[ReleaseHold[MakeExpression[boxexpr] //. boxRules]]]

extractUsage[str_] :=
 With[{usg =
    Function[expr, expr::usage, HoldAll] @@ MakeExpression[str]},
  If[Head[usg] === String, usg, ""]]

createLinkName[s_] :=
 If[StringMatchQ[ToString@FullForm[s], "\"\\[" ~~ __ ~~ "]\""],
  {StringReplace[ToString@FullForm[s], {"\"" :> "", "\\" -> "\\\\"}],
   StringReplace[
    ToString@FullForm[s], {"\"" :> "",
     "\\[" ~~ c__ ~~ "]" :> "character/" ~~ c}]},
  {s, s}]


createOptionString[s_] :=
 With[{opts =
    Function[expr, Options[expr], HoldAll] @@ MakeExpression[s]},
  If[opts === {},
   "</p><b>Symbol has no options.</b>",
   "</p><b>Options: </b>" <>
    StringJoin@Riffle[ToString[First[#]] & /@ opts, ", "]
   ]
  ]


ClearAll[createHtmlUsage];
createHtmlUsage[s_String] := Module[{
   usg = extractUsage[s],
   attr = Attributes[s],
   link, linkname},
  {linkname, link} = createLinkName[s];

  "<h3><a href=\"http://reference.wolfram.com/mathematica/ref/" <>
   link <> ".html\">" <> linkname <> "</a></h3>" <> If[usg =!= "",
    "<ul><li>" <>
     StringReplace[
      StringReplace[
       usg, {Shortest["\!\(\*" ~~ content__ ~~ "\)"] :>
         convertBoxExpressionToHTML[content],
        "\n" :> "<li>"}
       ], {"\[Null]" :> "",
       a_?(StringMatchQ[ToString@FullForm[#],
            "\"\\[" ~~ __ ~~ "]\""] &) :>
        StringReplace[
         ToString[a, MathMLForm], {WhitespaceCharacter :> ""}]}
      ] <> "</ul>", ""] <> "<b>Attributes:</b> " <>
   StringJoin[ToString /@ Riffle[attr, ", "]] <>
   createOptionString[s] <> "\n"
  ]

  names = Names["System`*"];

  Export["/Users/patrick/Documents/workspace/Mathematica_IDEA_plugin/\
  resources/codeInsight/usageMessages.html",
   StringJoin[createHtmlUsage /@ names], "Text"]