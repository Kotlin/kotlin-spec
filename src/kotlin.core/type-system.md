## Type system

### Glossary

$T$
~ Type

$T!!$
~ Non-nullable type

$T?$
~ Nullable type

$\{T!!\}$
~ Universe of non-nullable types

$\{T?\}$
~ Universe of nullable types

$\langle T\rangle$
~ Value of type $T$

PACT
~ Parametrized abstract classifier type

iPACT
~ Instantiated parametrized concrete classifier type

### Introduction

Kotlin has a type system with the following main properties:

* hybrid static and gradual type checking
* null safety
* no unsafe implicit conversions
* unified root type
* nominal subtyping with bounded parametric polymorphism

TODO(static type checking, gradual type checking)

Null safety is enforced by having two type universes --- _nullable_ (with nullable types $T?$) and _non-nullable_ (with non-nullable types $T!!$). All operations^[Except for TODO()] which are allowed on nullable types are safe, i.e., should never cause a runtime null pointer error.

Implicit conversions between types in Kotlin are limited to safe upcasts w.r.t. subtyping, meaning all other (unsafe) conversions must be explicit, done via either a conversion function or an [explicit cast][Cast expression]. However, Kotlin also supports smart casts --- a special kind of implicit conversions which are safe w.r.t. program control- and data-flow, which are covered in more detail [here][Smart casts].

The unified supertype type for all types in Kotlin is `kotlin.Any?`, a nullable version of [kotlin.Any][`kotlin.Any`].

Kotlin uses nominal subtyping, meaning subtyping relation is defined when a type is declared, with bounded parametric polymorphism, implemented as [generics][Generics].

### Type kinds

For the purposes of this section, we establish the following notation w.r.t. type kinds --- different flavours of types which exist in the Kotlin type system.

* [Classifier types][Classifier types]
* [Flexible types][Flexible types]
* [Captured types][Captured types]
* [Projected types][Projected types]
* [Bounded types][Bounded types]
* [Nullable types][Nullable types]
* Intersection and union types

We distinguish between *concrete* and *abstract* types. Concrete types are types which are assignable to values; abstract types either need to be instantiated as concrete types before they may be used as value types, or are used internally by the type system and are not directly denotable.

#### Built-in types

Kotlin type system uses the following built-in types, which have special semantics and representation (or lack thereof).

##### `kotlin.Any`

`kotlin.Any` is the unified supertype ($\top$) for $\{T!!\}$, i.e., all types are subtypes of `kotlin.Any`, either explicitly, implicitly, or by subtyping relation.

##### `kotlin.Nothing`

`kotlin.Nothing` is the unified subtype ($\bot$) for $\{T!!\}$, i.e., `kotlin.Nothing` is a subtype of all non-nullable types, including user-defined ones. This makes it an uninhabited type (as it is impossible for anything to be a function and an integer at the same time), meaning instances of this type can never exist at runtime; subsequently, there is no way to create an instance of `kotlin.Nothing` in Kotlin.

As the evaluation of an expression with `kotlin.Nothing` type can never complete normally, it is used to mark special situations, such as:

* non-terminating expressions
* exceptional control flow
* control flow transfer

##### `kotlin.Unit`

`kotlin.Unit` is a unit type, i.e., a type with only one value `kotlin.Unit`; all values of type `kotlin.Unit` should reference the same underlying `kotlin.Unit` object.

TODO(Compare to `void`)

#### Classifier types

Classifier types represent regular types which are declared as [classes][Classes], [interfaces][Interfaces] or [objects][Objects]. As Kotlin supports [generics][Generics], there are two variants of classifier types: simple and parametrized.

##### Simple classifier types {.unnumbered}

A simple concrete classifier type

$$T : S_1, \ldots, S_m$$

consists of

* type name $T$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a valid concrete classifier type, $T : S_1, \ldots, S_m$ should satisfy the following conditions.

* $T$ is a valid type name
* $\forall i \in [1,m]: S_i$ must be concrete, non-nullable, valid type

##### Parametrized classifier types {.unnumbered}

A parametrized abstract classifier type (PACT)

$$T[F_1, \ldots, F_n] : S_1, \ldots, S_m$$

consists of

* type constructor $T$ which takes actual type arguments and returns an instantiated type
* (optional) formal type arguments $F_1, \ldots, F_n$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a valid parametrized abstract classifier type, $T[F_1, \ldots, F_n] : S_1, \ldots, S_m$ should satisfy the following conditions.

* $\forall i \in [1,n]: F_i$ must be one of the following kinds
    - unbounded type variable
    - projected type variable
    - bounded type variable
* $\forall j \in [1,m]: S_j$ must be concrete, non-nullable, valid type w.r.t. type argument substitution

An instantiated parametrized concrete classifier type (iPACT)

$$T(A_1, \ldots, A_n)$$

consists of

* parametrized abstract classifier type $T$
* actual type arguments $A_1, \ldots, A_n$

To represent a valid instantiated parametrized concrete classifier type, $T(A_1, \ldots, A_n)$ should satisfy the following conditions.

* $T$ is a valid parametrized abstract classifier type with $n$ formal type parameters
* $\forall i \in [1,n]: A_i$ must be one of the following kinds
    - concrete type
    - projected type
* $\forall i \in [1,n]: A_i <: F_i$ where $F_i$ is the corresponding formal type argument from $T$

#### Flexible types

Kotlin, being a multi-platform language, needs to support transparent interoperability with platform-dependent code. However, this presents a problem in that some platforms may not support null safety the way Kotlin does. To deal with this, Kotlin supports *gradual typing* in the form of flexible types.

A flexible type represents a range of possible types between type $L$ (lower bound) and type $U$ (upper bound), written as $(L..U)$. One should note flexible types are abstract and *non-denotable*, i.e., one cannot explicitly declare a variable with flexible type, these types are created by the type system when needed.

To represent a valid flexible type, $(L..U)$ should satisfy the following conditions.

* $L$ and $U$ are valid types
* $L <: U$
* $\neg (L <: U)$
* $L$ and $U$ are **not** flexible types (but may contains other flexible types as part of their type signature)

As the name suggests, flexible types are flexible --- a value of type $(L..U)$ can be used in any context, where one of the possible types between $L$ and $U$ is needed (for more details, see [subtyping rules for flexible types][Subtyping]). This conversion is unsafe in many cases, which is why Kotlin generates dynamic assertions, when it is impossible to prove statically the safety of flexible type use.

##### Platform types

TODO(Platform types as flexible types)

TODO(Reference for different platforms)

#### Captured types

TODO

#### Projected types

Type projections are used to support declaration- and use-site variance for parametrized types. Kotlin supports three flavours of type projections.

TODO(in, out, star)

TODO(type projections are not allowed on functions and properties)

#### Bounded types

A bounded type is an abstract, non-denotable type which is used to represent bounds on type arguments and is defined as $A <: B_1, \ldots, B_n$, where $B_i$ is an i-th upper bound on type $A$.

To represent a valid bounded type, $A <: B_1, \ldots, B_n$ should satisfy the following conditions.

* $A$ is a type argument of PACT $T$
* $\forall i \in [1,n]: B_i$ must be one of the following kinds
    - concrete type
    - a type argument of $T$

TODO(Single generic bound allowed)

TODO(Only one class bound allowed)

#### Nullable types

Kotlin supports null safety by having two kinds of types --- nullable and non-nullable. All classifier type declarations, built-in or user-defined, create non-nullable types, i.e., types which cannot hold `null` value at runtime.

To specify a nullable version of type `T`, one needs to use `T?` as a type. Redundant nullability specifiers are ignored --- `T???` is equivalent to `T?`.

> Informally, question mark means "$T?$ may hold values of type $T$ or value `null`"

To represent a valid nullable type, $T?$ should satisfy the following conditions.

* $T$ is a valid type

If an operation is safe regardless of absence or presence of `null`, i.e., assignment of one nullable value to another, it can be used as-is for nullable types. For operations on $T?$ which may violate null safety, one has the following null-safe options:

1. Use safe operations
    * [safe call][Safe call expression]
2. Downcast from $T?$ to $T!!$
    * [unsafe cast][Cast expression]
    * [type check][Type check expression] combined with [smart casts][Smart casts]
    * null check combined with [smart casts][Smart casts]
    * [not-null assertion operator][Not-null assertion operator expression]
3. Supply a default value to use instead of `null`
    * [elvis operator][Elvis operator expression]

### Subtyping

Kotlin uses the classic notion of *subtyping* as *substituability* --- if $S$ is a subtype of $T$ (denoted as $S <: T$), values of type $S$ can be safely used where values of type $T$ are expected. The subtyping relation $<:$ is:

* reflexive ($A <: A$)
* transitive ($A <: B \land B <: C \Rightarrow A <: C$)

Two types $A$ and $B$ are *equivalent* ($A \equiv B$), iff $A <: B \land B <: A$. Due to the presence of flexible types, this relation is **not** transitive (see [here][Subtyping for flexible types] for more details).

#### Subtyping rules

Subtyping for non-nullable, concrete types uses the following rules.

* $\forall T : \text{kotlin.Nothing} <: T <: \text{kotlin.Any}$
* For any simple classifier type $T : S_1, \ldots, S_m$ it is true that $\forall i \in [1,m]: T <: S_i$
* For any instantiated parametrized classifier type $\widehat{T} = T[F_1, \ldots, F_n](A_1, \ldots, A_n) : S_1, \ldots, S_m$ it is true that $\forall i \in [1,m]: \widehat{T} <: S_i(A_1, \ldots, A_n)$
* TODO

#### Subtyping for flexible types

Flexible types (being flexible) follow a simple subtyping relation with other inflexible types. Let $T, A, B, L, U$ be inflexible types.

* $L <: T \Rightarrow (L..U) <: T$
* $T <: U \Rightarrow T <: (L..U)$

This captures the notion of flexible type $(L..U)$ as something which may be used in place of any type in between $L$ and $U$. If we are to extend this idea to subtyping between *two* flexible types, we get the following definition.

* $L <: B \Rightarrow (L..U) <: (A..B)$

This is the most extensive definition possible, which, unfortunately, makes the type equivalence relation non-transitive. Let $A, B$ be two *different* types, for which $A <: B$. The following relations hold:

* $A <: (A..B) \land (A..B) <: A \Rightarrow A \equiv (A..B)$
* $B <: (A..B) \land (A..B) <: B \Rightarrow B \equiv (A..B)$

However, $A \not \equiv B$.

#### Subtyping for nullability

### Generics

TODO(Here be a lot of dragons)
