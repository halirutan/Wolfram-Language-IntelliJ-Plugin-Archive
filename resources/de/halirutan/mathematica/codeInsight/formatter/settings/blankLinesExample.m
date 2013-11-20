GradientFieldPlot[function_,
  {u_, u0__},
  {v_, v0__},
  options___] :=
    VectorFieldPlot[Evaluate[{D[function, u], D[function, v]}],
      {u, u0},
      {v, v0},
      options, Options[GradientFieldPlot]
    ]




Options[HamiltonianFieldPlot] = Options[VectorFieldPlot];





HamiltonianFieldPlot[function_,
  {u_, u0__},
  {v_, v0__},
  options___] :=
    VectorFieldPlot[Evaluate[{D[function, v], - D[function, u]}],
      {u, u0},
      {v, v0},
      options, Options[HamiltonianFieldPlot]
    ]