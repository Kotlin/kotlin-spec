## Type inference

Kotlin has a concept of *type inference* for compile-time type information, meaning some type information in the code may be omitted, to be inferred by the compiler.
There are two kinds of type inference supported by Kotlin.

- Local type inference, for inferring types of expressions locally, in statement/expression scope;
- Function signature type inference, for inferring types of function return values and/or parameters.

> Note: type inference is a [type constraint][Kotlin type constraints] problem, and is usually solved by a type constraint solver.

TODO(write about when type inference works and when it does not)

### Smart casts

Kotlin introduces a limited form of flow-sensitive typing called *smart casts*.
Flow-sensitive typing means some expressions in the program may introduce changes to the compile-time types of variables.
This allows one to avoid unneeded explicit casting of values in cases when their runtime types are guaranteed to conform to the expected compile-time types.

Flow-sensitive typing may be considered a specific instance of traditional data-flow analysis.
Therefore, before we discuss it further, we need to establish the data-flow framework, which we will use for smart casts.

#### Data-flow framework

##### Smart cast lattices

We assume our data-flow analysis is run on a classic control-flow graph (CFG) structure, where most non-trivial expressions and statements are simplified and/or desugared.

TODO(Explain how this simplification is done?)

Our data-flow domain is a map lattice $\SmartCastData = \Expression \rightarrow \SmartCastType$, where $\Expression$ is any Kotlin expression and $\SmartCastType = \Type \times \Type$ sublattice is a product lattice of smart cast data-flow facts of the following kind.

* First component describes the type, which an expression definitely **has**
* Second component describes the type, which an expression definitely **does not have**

The sublattice order, join and meet are defined as follows.

$$
P_1 \times N_1 \sqsubseteq
P_2 \times N_2
  \Leftrightarrow P_1 <: P_2 \land N_1 :> N_2
$$

\begin{align*}
P_1 \times N_1 \join
P_2 \times N_2
  &= \LUB(P_1, P_2) \times \GLB(N_1, N_2) \\
P_1 \times N_1 \meet
P_2 \times N_2
  &= \GLB(P_1, P_2) \times \LUB(N_1, N_2)
\end{align*}

> Note: a well-informed reader may notice the second component is behaving very similarly to a *negation* type.
> 
> \begin{alignat*}{2}
> (P_1 \amp \neg N_1) | (P_2 \amp \neg N_2)
>   &\sqsubseteq (P_1 | P_2) \amp (\neg N_1 | \neg N_2)
>   &&= (P_1 | P_2) \amp \neg (N_1 \amp N_2)
>   \\
> (P_1 \amp \neg N_1) \amp (P_2 \amp \neg N_2)
>   &= (P_1 \amp P_2) \amp (\neg N_1 \amp \neg N_2) 
>   &&= (P_1 \amp P_2) \amp \neg (N_1 | N_2)
> \end{alignat*}
> 
> This is as intended, as "type which an expression definitely does not have" is exactly a negation type.
> In smart casts, as Kotlin [type system][Type system] does not have negation types, we overapproximate them when needed.

##### Smart cast transfer functions

The data-flow information uses the following transfer functions.

TODO(Add compile-time types of expressions to the transfer functions)

\begin{alignat*}{2}
&\llbracket \assume(x \is T) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (T \times \top)]
\\
&\llbracket \assume(x \notIs T) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (\top \times T)]
\\
\\
&\llbracket x \as T \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (T \times \top)]
\\
&\llbracket x \notAs T) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (\top \times T)]
\\
\\
&\llbracket \assume(x \eqq null) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (\NothingQ \times \top)]
\\
&\llbracket \assume(x \notEqq null) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (\top \times \NothingQ)]
\\
\\
&\llbracket \assume(x \refEqq null) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (\NothingQ \times \top)]
\\
&\llbracket \assume(x \notRefEqq null) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet (\top \times \NothingQ)]
\\
\\
&\llbracket \assume(x \eqq y) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet s(y),
      y \rightarrow s(x) \meet s(y)]
\\
&\llbracket \assume(x \notEqq y) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet \swap(\isNullable(s(y))),
      y \rightarrow s(y) \meet \swap(\isNullable(s(x)))]
\\
\\
&\llbracket \assume(x \refEqq y) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet s(y),
      y \rightarrow s(x) \meet s(y)]
\\
&\llbracket \assume(x \notRefEqq y) \rrbracket(s)
&&= s[x \rightarrow s(x) \meet \swap(\isNullable(s(y))),
      y \rightarrow s(y) \meet \swap(\isNullable(s(x)))]
\\
\\
&\llbracket x = y \rrbracket(s)
&&= s[x \rightarrow s(y)]
\\
\\
&\llbracket \killDataFlow(x) \rrbracket(s)
&&= s[x \rightarrow (\top \times \top)]
\\
\\
&\llbracket l \rrbracket(s)
&&= \bigsqcup_{p \in predecessor(l)} \llbracket p \rrbracket(s)
\end{alignat*}

where

\begin{alignat*}{1}
\swap(P \times N) &= N \times P
\\
\isNullable(s) &=
\left.
  \begin{cases}
    (\NothingQ \times \top) & \text{if } s \sqsubseteq (\NothingQ \times \top) \\
    (\top \times \top)      & \text{otherwise}
  \end{cases}
\right.
\end{alignat*}

> Important: transfer function for `==` and `!=` are used only if the corresponding [`equals` implementation][Value equality expressions] is known to be equivalent to [reference equality check][Reference equality expressions].
> For example, generated `equals` implementation for [data classes][Data class declaration] is considered to be equivalent to reference equality check.

TODO(A complete list of when `equals` is OK?)

> Note: in some cases, after the CFG simplification a program location $l$ may be duplicated and associated with several locations $l_1, \ldots, l_N$ in the resulting CFG.
> If so, the data-flow information for $l$ is calculated as
> 
> $$\llbracket l \rrbracket = \bigsqcup_{i=1}^N \llbracket l_i \rrbracket$$

> Note: a $\killDataFlow$ instruction is used to reset the data-flow information in cases, when a compiler deems necessary to stop its propagation.
> For example, it may be used in loops to speed up data-flow analysis convergence.
> This is the current behaviour of the Kotlin compiler.

After the data-flow analysis is done, for a program location $l$ we have its data-flow information $\llbracket l \rrbracket$, which contains data-flow facts $\llbracket l \rrbracket[e] = (P \times N)$ for an expression $e$.

#### Smart cast types

The data-flow information is used to produce the smart cast type as follows.

First, smart casts may influence the compile-time type of an expression $e$ (called *smart cast sink*) only if the sink is [*stable*][Smart cast sink stability].

Second, for a stable smart cast sink $e$ we calculate the overapproximation of its possible type.

$$
\llbracket l \rrbracket[e] = (P \times N)
  \Rightarrow
  \smartCastTypeOf(e) = \typeOf(e) \amp P \amp \approxNegationType(N)
\\
\approxNegationType(N) =
\left.
  \begin{cases}
    \Any      & \text{if } \NothingQ <: N \\
    \AnyQ     & \text{otherwise}
  \end{cases}
\right.
$$

As a result, $\smartCastTypeOf(e)$ is used as a compile-time type of $e$ for most purposes (including, but not limited to, function overloading and type inference of other values).

> Note: the most important exception to when smart casts are used in type inference is direct property declaration.
> ```
> fun noSmartCastInInference() {
>     var a: Any? = null
> 
>     if (a == null) return
> 
>     var c = a // Direct property declaration
> 
>     c // Declared type of `c` is Any?
>       // However, here it's smart casted to Any
> }
> 
> fun <T> id(a: T): T = a
> 
> fun smartCastInInference() {
>     var a: Any? = null
> 
>     if (a == null) return
> 
>     var c = id(a)
> 
>     c // Declared type of `c` is Any
> }
> ```

Smart casts are introduced by the following Kotlin constructions.

- Conditional expressions (`if` and `when`);
- Elvis operator (operator `?:`);
- Safe navigation operator (operator `?.`);
- Logical conjunction expressions (operator `&&`);
- Logical disjunction expressions (operator `||`);
- Not-null assertion expressions (operator `!!`);
- Direct casting expression (operator `as`);
- Direct assignments;
- Platform-specific cases: different platforms may add other kinds of expressions which introduce additional smart cast sources.

> Note: property declarations are not listed here, as their types are derived from initializers.

> Note: for the purposes of smart casts, most of these constructions are simplified and/or desugared, when we are building the program CFG for the data-flow analysis.
> We informally call such constructions *smart cast sources*, as they are responsible for creating smart cast specific instructions.

#### Smart cast sink stability

A smart cast sink is *stable* for smart casting if its value cannot be changed via means external to the CFG; this guarantees the smart cast conditions calculated by the data-flow analysis still hold at the sink.
This is one of the necessary conditions for smart cast to be applicable to an expression.

Smart cast sink stability breaks in the presence of the following aspects.

* concurrent writes;
* separate module compilation;
* custom getters;
* delegation.

The following smart cast sinks are considered stable.

1. Immutable local or classifier-scope properties without delegation or custom getters;
1. Immutable properties of stable properties without delegation or custom getters;
1. Mutable local properties without delegation or custom getters, if the compiler can prove that they are [effectively immutable][Effectively immutable smart cast sinks], i.e., cannot be changed by external means.

##### Effectively immutable smart cast sinks

We will call redefinition of $e$ ***direct*** redefinition, if it happens in the same declaration scope as the definition of $e$.
If $e$ is redefined in a nested declaration scope (w.r.t. its definition), this is a ***nested*** redefinition.

> Note: informally, a nested redefinition means the property has been captured in another scope and may be changed from that scope in a concurrent fashion.

We define ***direct*** and ***nested*** smart cast sinks in a similar way.

> Example:
> ```kotlin
> fun example() {
>     // definition
>     var x: Int? = null
> 
>     if (x != null) {
>         run {
>             // nested smart cast sink
>             x.inc()
> 
>             // nested redefinition
>             x = ...
>         }
>         // direct smart cast sink
>         x.inc()
>     }
> 
>     // direct redefinition
>     x = ...
> }
> ```

A mutable local property $P$ defined at $D$ is considered effectively immutable at a direct sink $S$, if there are no nested redefinitions on any CFG path between $D$ and $S$.

A mutable local property $P$ defined at $D$ is considered effectively immutable at a nested sink $S$, if there are no nested redefinitions of $P$ and all direct redefinitions of $P$ precede $S$ in the CFG.

> Example:
> ```kotlin
> fun directSinkOk() {
>     var x: Int? = 42 // definition
>     if (x != null)   // smart cast source
>         x.inc()      // direct sink
>     run {
>         x = null     // nested redefinition
>     }
> }
> 
> fun directSinkBad() {
>     var x: Int? = 42 // definition
>     run {
>         x = null     // nested redefinition
>                      //   between a definition
>                      //   and a sink
>     }
>     if (x != null)   // smart cast source
>         x.inc()      // direct sink
> }
> 
> fun nestedSinkOk() {
>     var x: Int? = 42     // definition
>     x = getNullableInt() // direct redefinition
>     run {
>         if (x != null)   // smart cast source
>             x.inc()      // nested sink
>     }
> }
> 
> fun nestedSinkBad01() {
>     var x: Int? = 42     // definition
>     run {
>         if (x != null)   // smart cast source
>             x.inc()      // nested sink
>     }
>     x = getNullableInt() // direct redefinition
>                          //   after the nested sink
> }
> 
> fun nestedSinkBad02() {
>     var x: Int? = 42     // definition
>     run {
>         x = null         // nested redefinition
>     }
>     run {
>         if (x != null)   // smart cast source
>             x.inc()      // nested sink
>     }
> }
> ```

#### Loop handling

As mentioned before, a compiler may use $\killDataFlow$ instructions in loops to avoid slow data-flow analysis convergence.
In the general case, a loop body may be evaluated zero or more times, which, combined with $\killDataFlow$ instructions, causes the smart cast sources from the loop body to *not* propagate to the containing scope.
However, some loops, for which we can have static guarantees about how their body is evaluated, may be handled differently.
For the following loop configurations, we consider their bodies to be definitely evaluated *one or more* times.

* `while (true) { ... }`
* `do { ... } while (condition)`

> Note: in the current implementation, only the exact `while (true)` form is handled as described; e.g., `while (true == true)` does not work.

> Note: one may extend the number of loop configurations, which are handled by smart casts, if the compiler implementation deems it necessary.

> Example:
> ```kotlin
> fun breakFromInfiniteLoop() {
>     var a: Any? = null
>
>     while (true) {
>         if (a == null) return
>
>         if (randomBoolean()) break
>     }
>
>     a // Smart cast to Any
> }
>
> fun doWhileAndSmartCasts() {
>     var a: Any? = null
> 
>     do {
>         if (a == null) return
>     } while (randomBoolean())
>     
>     a // Smart cast to Any
> }
> 
> fun doWhileAndSmartCasts2() {
>     var a: Any? = null
> 
>     do {
>         sink(a)
>     } while (a == null)
> 
>     a // Smart cast to Any
> }
> ```

#### Bound smart casts

TODO(Everything)

### Local type inference

Local type inference in Kotlin is the process of deducing the compile-time types of expressions, lambda expression parameters and properties.
As mentioned before, type inference is a [type constraint][Kotlin type constraints] problem, and is usually solved by a type constraint solver.

In addition to the types of intermediate expressions, local type inference also performs deduction and substitution for generic type parameters of functions and types involved in every expression.
You can use the [Expressions][Expressions] part of this specification as a reference point on how the types for different expressions are constructed.

However, there are some additional clarifications on how these types are constructed.
First, the additional effects of [smart casts][Smart casts] are considered in local type inference, if applicable.
Second, there are several special cases.

- If a type $T$ is described as the least upper bound of types $A$ and $B$, it is represented as a pair of constraints $A <: T$ and $B <: T$;
- TODO(Are there other special cases?)

Type inference in Kotlin is bidirectional; meaning the types of expressions may be derived not only from their arguments, but from their usage as well.
Note that, albeit bidirectional, this process is still local, meaning it processes one statement at a time, strictly in the order of their appearance in a scope; e.g., the type of property in statement $S_1$ that goes before statement $S_2$ cannot be inferred based on how $S_1$ is used in $S_2$.

As solving a type constraint system is not a definite process (there may be more than one valid solution for a given [constraint system][Type constraint solving]), type inference may create several valid solutions.
In particular, one may always derive a constraint $A <: T <: B$ for every type variable $T$, where types $A$ and $B$ are both valid solutions.
One of these types is always picked as a solution in Kotlin (although from the constraint viewpoint, there are usually more solutions available); this choice is done according to the following rules:

- TODO(What are the rules?)

> Note: this is valid even if $T$ is a variable without any explicit constraints, as every type in Kotlin has an implicit constraint $\mathtt{kotlin.Nothing} <: T <: \mathtt{kotlin.Any?}$.

### TODO

- Type approximation for public API
- Lambda analysis order (and the order of overloading vs type inference in general)
