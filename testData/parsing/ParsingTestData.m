Bar::usage = "Bar[x,y]";
Bar::argX = "Nothing here.";
Bar /: Plus[Bar, Foo] := myPlus[Bar, Foo];
Bar /: Plus[Bar, Foo] = Plus;
Times[Bar, Foo2] ^:= myTimes[Bar, Foo];
Bar[x_, y_] := {x, y};
Bar = 3;
Default[Bar] = 0;
N[myPi] = 3;
myVariable = 3;
Format[Bar[x_, y_]] := MatrixForm[{{x}, {y}}];
SyntaxInformation[Bar] = {};
Ber = 3;
SetAttributes[Bar, {Listable}];
head[Bar] ^= poing;

myHead = Boing;
f[x_myHead] := x^2;

Options[Bar] = {};

Message[Bar::usage]
For[k = 0, k > max, k++, body]

(p : IPCUProperties[props : {_Rule..}])[Write] := Module[{},test]