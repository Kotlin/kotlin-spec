<#mode quote>

\newcommand{\opMathTT}[2]{%
  \expandafter\newcommand{#1}{\operatorname{\mathtt{#2}}}%
}

\newcommand{\opMathIT}[2]{%
  \expandafter\newcommand{#1}{\operatorname{\mathit{#2}}}%
}

\newcommand{\opMathTEXT}[2]{%
  \expandafter\newcommand{#1}{\operatorname{\text{#2}}}%
}

\newcommand{\llbracket}{\left[\!\left[}
\newcommand{\rrbracket}{\right]\!\right]}

\newcommand{\sbn}{\stackrel{null}{<:}}
\newcommand{\sbnn}{\stackrel{!}{<:}}

\newcommand{\amp}{\operatorname{\&}}

\opMathTEXT{\LUB}{LUB}
\opMathTEXT{\GLB}{GLB}

\opMathTT{\outV}{out\ }
\opMathTT{\invV}{inv\ }
\opMathTT{\inV}{in\ }

\opMathTT{\Any}{kotlin.Any}
\opMathTT{\AnyQ}{kotlin.Any?}

\opMathTT{\Nothing}{kotlin.Nothing}
\opMathTT{\NothingQ}{kotlin.Nothing?}

\opMathTT{\Unit}{kotlin.Unit}

\opMathTT{\Function}{kotlin.Function}
\opMathTEXT{\FunctionN}{FunctionN}
\opMathTEXT{\FT}{FT}
\opMathTEXT{\FTR}{FTR}
\opMathTEXT{\RT}{RT}

\opMathTT{\Array}{kotlin.Array}

\opMathTEXT{\ATS}{ATS}
\opMathTEXT{\LTS}{LTS}

\opMathTEXT{\SmartCastData}{SmartCastData}
\opMathTEXT{\Expression}{Expression}
\opMathTEXT{\SmartCastType}{SmartCastType}
\opMathTEXT{\Type}{Type}
\opMathTEXT{\Nullability}{Nullability}

\newcommand{\join}{\sqcup}
\newcommand{\meet}{\sqcap}

\opMathTT{\is}{is}
\opMathTT{\notIs}{!is}
\opMathTT{\as}{as}
\opMathTT{\notAs}{!as}
\opMathTT{\eqq}{==}
\opMathTT{\notEqq}{!\!\!=}
\opMathTT{\refEqq}{===}
\opMathTT{\notRefEqq}{!\!\!==}

\opMathIT{\assume}{assume}
\opMathIT{\killDataFlow}{killDataFlow}
\opMathIT{\swap}{swap}
\opMathIT{\isNullable}{isNullable}
\opMathIT{\smartCastTypeOf}{smartCastTypeOf}
\opMathIT{\typeOf}{typeOf}
\opMathIT{\approxNegationType}{approxNegationType}
