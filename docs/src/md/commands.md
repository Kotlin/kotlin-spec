<#mode quote>

\newcommand{\opMathTT}[2]{%
  \expandafter\newcommand{#1}{\operatorname{\texttt{#2}}}%
}

\newcommand{\opMathIT}[2]{%
  \expandafter\newcommand{#1}{\operatorname{\mathit{#2}}}%
}

\newcommand{\opMathTEXT}[2]{%
  \expandafter\newcommand{#1}{\operatorname{\text{#2}}}%
}

\newcommand{\llbracket}{\left[\!\left[}
\newcommand{\rrbracket}{\right]\!\right]}

\newcommand{\sbn}{\mathrel{\operatorname{\stackrel{null}{<:}}}}
\newcommand{\sbnn}{\mathrel{\operatorname{\stackrel{!}{<:}}}}

\newcommand{\amp}{\mathbin{\operatorname{\&}}}
\newcommand{\hor}{\mathbin{\operatorname{|}}}

\newcommand{\sur}{\mathrel{\operatorname{\cancel{<:>}}}}

\newcommand{\Ss}{\mathbb{S}}

\opMathTEXT{\LUB}{LUB}
\opMathTEXT{\GLB}{GLB}

\opMathTT{\outV}{out\,}
\opMathTT{\invV}{inv\,}
\opMathTT{\inV}{in\,}

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

\opMathTT{\Double}{kotlin.Double}
\opMathTT{\Float}{kotlin.Float}
\opMathTT{\Long}{kotlin.Long}
\opMathTT{\Int}{kotlin.Int}
\opMathTT{\Short}{kotlin.Short}
\opMathTT{\Byte}{kotlin.Byte}
\opMathTT{\Char}{kotlin.Char}
\opMathTT{\Boolean}{kotlin.Boolean}

\opMathTT{\Enum}{kotlin.Enum}

\opMathTEXT{\ATS}{ATS}
\opMathTEXT{\LTS}{LTS}

\opMathTEXT{\PD}{PD}
\opMathTEXT{\ND}{ND}
\opMathTEXT{\ID}{ID}

\opMathIT{\ptor}{ptor}
\opMathIT{\stor}{stor}
\opMathIT{\init}{init}
\opMathIT{\prop}{prop}
\opMathIT{\md}{md}
\opMathIT{\companionObj}{companionObj}
\opMathIT{\nested}{nested}
\opMathIT{\dataClass}{dataClass}
\opMathIT{\dataClassParam}{dp}

\opMathIT{\name}{name}
\opMathIT{\type}{type}

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
\opMathTT{\backedge}{backedge}
\opMathIT{\swap}{swap}
\opMathIT{\isNullable}{isNullable}
\opMathIT{\smartCastTypeOf}{smartCastTypeOf}
\opMathIT{\typeOf}{typeOf}
\opMathIT{\approxNegationType}{approxNegationType}

\opMathIT{\Assigned}{Assigned}
\opMathIT{\Unassigned}{Unassigned}
