## Kotlin type constraints

Some complex tasks that need to be solved when compiling Kotlin code are formulated best using *constraint systems* over Kotlin types.
These are solved using constraint solvers.

### Type constraint definition

A *type constraint* in general is an inequation of the following form: $T <: U$ where $T$ and $U$ are [concrete Kotlin types][Type system].
As Kotlin has [parameterized types][Parameterized classifier types], $T$ and $U$ may be *free type variables*: unknown types which may be substituted by any other type in Kotlin.

Please note that, in general, not all type parameters are considered as free type variables in a constraint system.
Some type variables may be *fixed* in a constraint system; for example, type parameters of a parameterized class *inside* its body are not concrete types, but are not free type variables either.
A fixed type variable describes an unknown, but fixed type which is not to be substituted.

We will use the notation $T_i$ for a type variable and $\tilde{T_i}$ for a fixed type variable.
The main difference between fixed type variables and concrete types is that different concrete types may not be equal, but a fixed type variable may be equal to another fixed type variable or a concrete type.

> Examples of valid type constraints:
>
> - $\texttt{List<}\widetilde{X}\texttt{>} <: Y$
> - $\texttt{List<}\widetilde{X}\texttt> <: \texttt{List<}\texttt{List<}\texttt{Int}\texttt>\texttt>$
> - $\widetilde{X} <: Y$

Every constraint system has general implicit constraints $T_j <: \AnyQ$ and $\Nothing <: T_j$ for every type $T_j$ mentioned in the system, including type variables.

### Type constraint solving

There are two tasks which a type constraint solver may perform: [checking constraint system for soundness][Checking constraint system soundness], i.e., if a solution exists, and [solving constraint system][Finding optimal constraint system solution], i.e., inferring a satisfying substitution of concrete types for all free type variables.

Checking a constraint system for soundness can be viewed as a much simpler case of solving that constraint system: if there is a solution, the system is sound, meaning there are only two possible outcomes.
Solving a constraint system, on the other hand, may have multiple possible outcomes, as there may be multiple valid solutions.

> Example: constraint systems which are sound yet no relevant solutions exist.
>
> - $X <: Y$
> - $\texttt{List<}X\texttt> <: \texttt{Collection}\texttt<X\texttt>$

#### Checking constraint system soundness

Checking constraint system soundness is a satisfiability problem.
That is, given a number of constraints in the form $S <: T$ containing zero or more *free type variables* (also called inference type variables), it needs to determine if these constraints are non-contradictory, i.e. there exists a possible instantiation of these free variables that makes all given constraints valid.

This boils down to finding a set of lower and upper bounds for each of these variables and determining if these bounds are non-contradictory.
The algorithm of finding this bounds is implementation-defined and is not guaranteed to prove the satisfiability of given constraints in all possible cases.

TODO: sample algorithm

#### Finding optimal constraint system solution

As any constraint system may have multiple valid solutions, finding one which is "optimal" in some sense is not possible in general, because the notion of the best solution for a task depends on the said task. 
To deal with this, a constraint system allows two additional types of constraints:

- A *pull-up* constraint for type variable $T$, denoted $\uparrow T$, signifying that when finding a substitution for this variable, the optimal solution is the largest one according to [subtyping relation][Subtyping];
- A *push-down* constraint for type variable $T$, denoted $\downarrow T$, signifying that when finding a substitution for this variable, the optimal solution is the smallest one according to [subtyping relation][Subtyping].

If a variable has no constraints of these kinds associated with it, it is assumed to have a pull-up implicit constraint.
The process of instantiating the free variables of a constraint system starts by finding the bounds for each free variable (as mentioned in the previous section) and then, given these bounds, continues to pick the right type from them.
Excluding other free variables, this boils down to:

- For a variable with a push-down constraint, the solution is the [greatest lower bound] of all upper bounds for this variable, excluding other free variables;
- For a variable with a pull-up constraint, the solution is the [least upper bound] of all lower bounds for this variable, excluding other free variables;
- For a variable with both or none, the solution is also the [least upper bound] of all lower bounds for this variable, excluding other free variables.

TODO: approximation?

#### The relations on types as constraints

In other sections (for example, [Expressions] and [Statements]) the relations between types may be expressed using the type operations found in the [type system section][Type system] of this document.

The [greatest lower bound] of two types is converted directly as-is, as the greatest lower bound is always an intersection type.

The [least upper bound] of two types is converted as follows.
If type $T$ is defined to be the least upper bound of $A$ and $B$, the following constraints are produced:

- $A <: T$
- $B <: T$
- $\downarrow T$
- $\uparrow A$
- $\uparrow B$

> Important: the results of finding GLB or LUB via a constraint system may be different from the results of finding them via a normalization procedure (i.e., imprecise); however, they are sound w.r.t. bound, meaning a constraint system GLB is still a lower bound and a constraint system LUB is still an upper bound.

> Example:
> 
> Let's assume we have the following code:
> 
> ```kotlin
> val e = if (c) a else b
> ```
> 
> where `a`, `b`, `c` are some expressions with unknown types (having no other type constraints besides the implicit ones).
> 
> Assume the type variables generated for them are $A$, $B$ and $C$ respectively, the type variable for `e` is $E$.
> According to [the conditional expression rules][Conditional expression], this produces the following relations:
>
> - $C <: \texttt{kotlin.Boolean}$
> - $E = \LUB(A, B)$
> 
> These, in turn, produce the following explicit constraints:
> 
> - $C <: \texttt{kotlin.Boolean}$
> - $A <: E$
> - $B <: E$
> - $\downarrow E$
> - $\uparrow A$
> - $\uparrow B$
>
> which, w.r.t. general and pull-up implicit constraints, produce the following solution:
>
> - $C \rightarrow \texttt{kotlin.Boolean}$
> - $A \rightarrow \AnyQ$
> - $B \rightarrow \AnyQ$
> - $E \rightarrow \AnyQ$
