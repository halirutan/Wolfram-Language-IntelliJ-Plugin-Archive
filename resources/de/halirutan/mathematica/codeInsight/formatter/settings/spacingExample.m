VectorFieldPlot[f_, {u_, u0_?numberQ, u1_?numberQ, du_ : Automatic},
  {v_, v0_?numberQ, v1_?numberQ, dv_ : Automatic}, opts___?OptionQ] := Module[
  {plotpoints, dua, dva, vecs, xpp, ypp, sf},
(* -- grab options -- *)
  {plotpoints, sf} = {PlotPoints, ScaleFactor} /. Flatten[{opts}] /.
      Options[VectorFieldPlot];
  If[Head[plotpoints] === List,
    xpp = First[plotpoints];
    ypp = Last[plotpoints],
  (* else *)
    xpp = ypp = plotpoints
  ];
  (* determine interval between bases of vectors *)
  If[!IntegerQ[xpp],
    dua = automatic[du, (u1 - u0) / 14],
    dua = (u1 - u0) / (xpp - 1)
  ];
  If[!IntegerQ[ypp],
    dva = automatic[dv, (v1 - v0) / 14],
    dva = (v1 - v0) / (ypp - 1)
  ];
  (* set the scaling factor based on the intervals if it is not
      explicitly None or a number *)
  If[ sf =!= None && !numberQ[sf],
    sf = N[Min[dua, dva]]
  ];
  (* -- determine the vectors -- *)
  vecs = Flatten[Table[{N[{u, v}], N[f]},
    Evaluate[{u, u0, u1, dua}], Evaluate[{v, v0, v1, dva}]], 1];
  (* call ListVectorFieldPlot *)
  ListVectorFieldPlot[vecs,
  (* note dependency on LPVF filtering its own opts quietly *)
    Flatten[{ScaleFactor -> sf, opts, Options[VectorFieldPlot]}]
  ] /; MatchQ[vecs, {{_?VectorQ, _?VectorQ}..}]
]