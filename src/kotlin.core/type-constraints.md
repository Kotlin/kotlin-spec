## Kotlin type constraints

Some complex tasks that need to be solved when compiling Kotlin code are
formulated best using *constraint systems* on Kotlin types. These are solved
using constraint solvers.

### Type constraint definition

A *type constraint* in general is an inequation of the following form: $T <: U$
where $T$ and $U$ are Kotlin types (see [type system][Type system]).
It is important, however, that Kotlin has parameterized types and type parameters
of $T$ and $U$ (or type parameters of their parameters, or $T$ and $U$ themselves)
may be *type variables*, that are unknown types that may be substituted by any
other type in Kotlin.

Please note that, in general, type variables of the constraint system are not the
same as type parameters of a type or a callable. Some type parameters may be
*bound* in the constraint system, meaning that, although they are not known yet in
Kotlin code, they are not type variables and are not to be substituted.

When such an ambiguity arises, we will use the notation $T_i$ for a type
variable and $\widetilde{T_i}$ for a bound type parameter. The main difference
between bound parameters and concrete types is that different concrete types
may not be equal, but a bound parameter may be equal to another bound parameter
or a concrete type.

> Several examples of valid type constraints:
>
> - $\mathtt{List}\left<\widetilde{X}\right> <: Y$
> - $\mathtt{List}\left<\widetilde{X}\right> <: \mathtt{List}\left<\mathtt{List}\left<\mathtt{Int}\right>\right>$
> - $\widetilde{X} <: Y$

Every constraint system has implicit constraints $\mathtt{Any} <: T_j$ and
$T_j <: \mathtt{Nothing?}$ for every type $T_j$ mentioned in constraint,
including type variables.

### Type constraint solving

There are two tasks that a type constraint solver may perform: checking constraint
system for soundness and solving the system, e.g. inferring values for all
the type variables that have themselves no type variables in them.

Checking a constraint system for soundness can be viewed as a simpler case of solving
a constraint, as if there is a solution, than the system is sound. It is, however,
a much simpler task with only two possible outcomes. Solving a constraint system, on
the other hand, may have several different results as there may be several
valid solutions.

> Constraint examples that are sound yet no relevant solutions exist:
>
> - $X <: Y$
> - $\mathtt{List}\left<X\right> <: \mathtt{Collection}\left<X\right>$

#### Checking constraint system soundness

TODO?

#### Finding optimal solution

TODO?
