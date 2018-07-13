## Type inference

Kotlin has a concept of *type inference* for compile-time type information,
meaning that some type information in the code may be omitted, to be inferred by
the compiler. Type inference is a [type constraint][Kotlin type constraints] problem,
solved by the type constraint solver.

There are two kinds of type inference supported by kotlin:

- Local type inference, inferring types of expressions locally, in statement scope;
- Function signature type inference, inferring types for function return values
  and/or parameters.

### Smart casts

Kotlin introduces a limited form of flow-dependent typing called
*smart casting*. Flow-dependent typing means that some statements in the program
may introduce changes to the compile-time types of properties. This allows
to avoid unnecessary casting of these values in cases where the runtime types
are guaranteed to conform to expected compile-time types.

Smart casting is dependent on the *smart cast conditions* that are boolean predicates
about program values. If some condition involving a program value *dominates*
some program scope, the type of this value is mutated inside that scope.

There are two kinds of smart cast conditions: nullity conditions and type conditions.
Nullity conditions signify that some value is not nullable, e.g. it's value
is guaranteed to not be `null`. Type conditions signify that some value's runtime
type conforms to a constraint of $RT <: T$ where $T$ is the assumed type and
$RT$ is the runtime type.

> Nullity conditions may be viewed as a subcase of type conditions with
> assumed type `kotlin.Any`

There are also negated forms of both conditions that do not affect the typing
in any way, but may be negated again to introduce non-negated forms of the same conditions.

The actual compile type of a value that is subject of smart casting (see below)
for any purpose (including, but not limited to, function overloading and
further type inference of other values) if it is dominated by a smart casting
condition, is, for every condition:

- If the condition is a nullability condition, the intersection of the type
  it had before (including the results of smart casting performed for other conditions) and
  type `kotlin.Any`;
- If the condition is a type condition, the intersection of the type it had before
  (including the results of smart casting performed for other conditions) and
  the assumed type of the condition.

The following values are subject to smart casting:

- Immutable local or classifier-scope properties without delegation or custom getters;
- Immutable properties of other such properties that too do not have delegation or
  custom getters;
- Mutable local properties without delegation or custom getters as soon as the compiler can prove
  that they cannot be mutated by external means:
    - Any properties that are captured in non-inlining lambda expressions or anonymous objects
      are not applicable.

TODO(): the rest is really shaky

Smart casting conditions are introduced by:

- Conditional expressions (`if` and `when`):
    - Smart cast conditions derived from expression condition are active inside
      the positive branch scope;
    - Smart cast conditions derived from negated expression condition are active
      inside the negative branch scope;
    - If all the branches except one are unreachable, that branch's condition is
      also propagated over to the scope containing the conditional expression,
      after the conditional expression;
- Elvis operator (operator `?:`): if the right-hand branch of elvis operator
  is unreachable, a nullability condition for the left-hand side expression
  (if applicable) is introduced for the rest of the containing scope;
- Logical conjunction expressions (operator `&&`): all conditions derived from
  left-hand expression are applied to the right-hand expression;
- Logical disjunction expressions (operator `||`): all condtions derived from
  left-hand expression are applied negated to the right-hand expression;
- Not-null assertion expressions (operator `!!`): the left-hand side value
  (if applicable) introduces a nullability condtion for the rest of the scope
  the expression is contained in;
- Direct casting expression (operator `as`): the left-hand side expression
  (if applicable) introduces a type condition for the rest of the scope
  the expression is contained in with the assumed type
  being the same as the right-hand side type of the casting expression;
- Direct assignments: if both sides of the assignment are applicable expressions,
  all the conditions currently applying to the right-hand side are also applied to the left-hand
  side of the assignment for the rest of the containing scope;
- Platform-specific cases: different platforms may add other kinds of expressions
  that introduce smart-casting conditions.

> Property declarations are not listed here because their types are naturally
> derived from initializers

Smart cast conditions are derived from boolean expressions in the following way:

- $x$` is `$T$ where $x$ is an applicable expression implies a
  type condition for $x$ with assumed type $T$;
- $x$` !is `$T$ where $x$ is an applicable expression implies a
  negated type condition for $x$ with assumed type $T$;
- $x$` == null` (or reversed) where $x$ is an applicable expression implies a
  a nullability condition for $x$;
- $x$` != null` (or reversed) where $x$ is an applicable expression implies a
  a negated nullability condition for $x$;
- `!`$x$ where $x$ implies all the conditions implied by $x$, but in
  negated form;
- $x$` && `$y$ implies all the non-negated conditions implied by $x$ and $y$
  and the intersection of all the negated conditions implied by $x$ and $y$;
- $x$` || `$y$ implies all the negated conditions implied by $x$ and $y$
  and the intersection of all the non-negated conditions implied by $x$ and $y$;
- $x$` == `$y$ (or reversed) where $x$ is an applicable expression and $y$ is a known non-nullable
  value (that is, has a non-nullable compile-time type) implies the nullability
  condition for $x$.

TODO(): is there more than that?

### Local type inference

Local type inference in Kotlin is the process of deducing the compile-time types of
expressions, lambda expression parameters and properties.
As already mentioned above, type inference is a [type constraint][Kotlin type constraints] problem,
solved by the type constraint solver.

In addition to the types of intermediate expressions, local type inference also must
perform deduction and substitution for generic type parameters of functions and types
involved in every expression. You can use the [expressions][Expressions] part of this
document as a reference point on how the types for different expressions are constructed
(please note the effects of [smart casting][Smart casts] that are not mentioned in that part).

It does, however, need some clarification as those types are given as definitions, not
as type constraints:

- If the type $T$ is described as the least upper bound of types $A$ and $B$,
  it gets promoted to a pair of constraints: $A <: T$ and $B <: T$;
- TODO: are there other cases?)

Type inference in kotlin is also a bidirectional process, meaning that types of
expressions may not only be derived from their arguments, but their usage as well.
Please note that, albeit bidirectional, this process is still local, meaning that
it processes one statement at a time, in the order of appearance in a scope, so
a type of property in statement $S_1$ that goes before statement $S_2$ cannot be
inferred based on usage information from $S_2$.

Unlike checking satisfiability for a type constraint system, actually solving it
is not a definite process, as there may be more than one valid solution
(see [type constraint solving][Type constraint solving]). This means, among other
things, that type inference in general may have several valid solutions as well.
In particular, one may always derive a system $A <: T <: B$ for every type variable $T$
where $A$ and $B$ are both valid solution types. One of these types is always the
solution in Kotlin (although from the constraint viewpoint, there are usually more
solutions available), but choosing between them is done according to
special rules:

- TODO(): What are the rules?)

> Note that this is valid even if $T$ is a variable without any constraints,
> as every type in kotlin has an implicit constraint
> $\mathtt{kotlin.Nothing} <: T <: \mathtt{kotlin.Any?}$.
