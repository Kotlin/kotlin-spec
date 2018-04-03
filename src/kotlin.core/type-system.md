## Type system

TODO(Add examples)

TODO(Add grammar snippets?)

### Glossary

TODO(Cleanup)

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

$T\lbrack S_1, \ldots, S_n\rbrack$
~ The result of type argument substitution for type $T$ with types $S_i$

PACT
~ Parameterized abstract classifier type

iPACT
~ Instantiated parameterized concrete classifier type

### Introduction

Kotlin has a type system with the following main properties.

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

* [Built-in types][Built-in types]
* [Classifier types][Classifier types]
* [Flexible types][Flexible types]
* [Nullable types][Nullable types]
* TODO(Intersection and union types)
* TODO(Error / invalid types)

We distinguish between *concrete* and *abstract* types. Concrete types are types which are assignable to values; abstract types either need to be instantiated as concrete types before they may be used as value types, or are used internally by the type system and are not directly denotable.

#### Built-in types

Kotlin type system uses the following built-in types, which have special semantics and representation (or lack thereof).

##### `kotlin.Any`

`kotlin.Any` is the unified supertype ($\top$) for $\{T!!\}$, i.e., all types are subtypes of `kotlin.Any`, either explicitly, implicitly, or by subtyping relation.

TODO(`kotlin.Any` members?)

##### `kotlin.Nothing`

`kotlin.Nothing` is the unified subtype ($\bot$) for $\{T!!\}$, i.e., `kotlin.Nothing` is a subtype of all non-nullable types, including user-defined ones. This makes it an uninhabited type (as it is impossible for anything to be, for example, a function and an integer at the same time), meaning instances of this type can never exist at runtime; subsequently, there is no way to create an instance of `kotlin.Nothing` in Kotlin.

As the evaluation of an expression with `kotlin.Nothing` type can never complete normally, it is used to mark special situations, such as:

* non-terminating expressions
* exceptional control flow
* control flow transfer

Additional details about how `kotlin.Nothing` should be processed are available [here][Control- and data-flow analysis].

##### `kotlin.Unit`

`kotlin.Unit` is a unit type, i.e., a type with only one value `kotlin.Unit`; all values of type `kotlin.Unit` should reference the same underlying `kotlin.Unit` object.

TODO(Compare to `void`)

#### Classifier types

Classifier types represent regular types which are declared as [classes][Classes], [interfaces][Interfaces] or [objects][Objects]. As Kotlin supports [generics][Generics], there are two variants of classifier types: simple and parameterized.

##### Simple classifier types

A simple concrete classifier type

$$T : S_1, \ldots, S_m$$

consists of

* type name $T$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a valid concrete classifier type, $T : S_1, \ldots, S_m$ should satisfy the following conditions.

* $T$ is a valid type name
* $\forall i \in [1,m]: S_i$ must be concrete, non-nullable, valid type

##### Parameterized classifier types {.unnumbered}

A parameterized abstract classifier type (PACT)

$$T(F_1, \ldots, F_n) : S_1, \ldots, S_m$$

consists of

* type constructor $T$ which takes actual type arguments and returns an instantiated type
* (optional) formal type arguments $F_1, \ldots, F_n$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a valid PACT, $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$ should satisfy the following conditions.

* $\forall i \in [1,n]: F_i$ must be one of the following kinds
    - [unbounded type argument][Unbounded type arguments]
    - [projected type argument][Projected type arguments]
    - [bounded type argument][Bounded type arguments]
* $\forall j \in [1,m]: S_j$ must be concrete, non-nullable, valid type w.r.t. type argument substitution

An instantiated parameterized concrete classifier type (iPACT)

$$T[A_1, \ldots, A_n]$$

consists of

* PACT $T$
* actual type arguments $A_1, \ldots, A_n$

To represent a valid iPACT, $T[A_1, \ldots, A_n]$ should satisfy the following conditions.

* $T$ is a valid PACT with $n$ formal type parameters
* $\forall i \in [1,n]: A_i$ must be one of the following kinds
    - valid concrete type
    - valid projected type
    - type argument available in the type context of $T$  
      TODO(What is a type context?)
* $\forall i \in [1,n]: A_i <: F_i$ where $F_i$ is the corresponding formal type argument from $T$

##### Type arguments

Type arguments are a special kind of abstract types, which are introduced by PACTs. They are valid only in the context of their declaring PACT.

When creating an iPACT from PACT, actual type arguments go through [capturing][Type argument capturing] and create *captured* type arguments, which follow special rules described in more detail below.

###### Unbounded type arguments

An unbounded type argument $F$ is an abstract type which may capture any valid type. It is also *invariant* by default (see [subtyping][Subtyping] for more details on variance); if one needs co- or contravariant type argument, they need to use [projected type arguments][Projected type arguments].

To represent a valid unbounded type argument of PACT $T$, $F$ should satisfy the following conditions.

* $F$ is a type argument of PACT $T$

###### Projected type arguments

Projected type arguments are abstract types which are used to support declaration-site variance for parameterized types, i.e., the ability to declare a type argument as *covariant* or *contravariant*. The variance information is used by [subtyping][Subtyping] and for checking allowed operations on values of co- and contravariant type arguments.

> Kotlin also supports use-site variance, which is covered in more detail [here][Use-site variance].

To represent a valid covariant type argument $\text{out} F$ of PACT $T$, $\text{out} F$ should satisfy the following conditions.

* $F$ is a type argument of PACT $T$

To represent a valid contravariant type argument $\text{in} F$ of PACT $T$, $\text{in} F$ should satisfy the following conditions.

* $F$ is a type argument of PACT $T$

TODO(type projections are not allowed on functions and properties)

TODO(no type projections on supertype type arguments)

###### Bounded type arguments

A bounded type argument is an abstract type which is used to add upper type bounds to type arguments and is defined as $F <: B_1, \ldots, B_n$, where $B_i$ is an i-th upper bound on type argument $F$.

To represent a valid bounded type argument of PACT $T$, $F <: B_1, \ldots, B_n$ should satisfy the following conditions.

* $F$ is a type argument of PACT $T$
* $\forall i \in [1,n]: B_i$ must be one of the following kinds
    - concrete type
    - a type argument of $T$

TODO(Single generic bound allowed)

TODO(Only one class bound allowed)

##### Use-site variance

TODO(star)

TODO(stuff)

##### Type argument capturing

Type argument capturing (similarly to Java capturing conversion) is used when instantiating parameterized types; it creates *captured* types based on the type information of both formal and actual type arguments, which present a unified view on the resulting types and simplifies further reasoning.

For a given PACT $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$, its iPACT $T[A_1, \ldots, A_n]$ uses the following rules to create captured type $C_i$ from the formal $F_i$ and actual $A_i$ argument.

> NB: **All** applicable rules are used to create the resulting captured type.

* If $\triangleleft  F_i$ is a covariant type argument and $A_i$ **is not** a concrete type or a covariant type, it is an error. Otherwise, $C_i <: A_i$.
* If $\triangleright F_i$ is a contravariant type argument and $A_i$ **is not** a concrete type or a contravariant type, it is an error. Otherwise, $C_i :> A_i$.
* If $F_i <: B_1, \ldots, B_n$ is a bounded type argument, $C_i$ is bounded on $B_i[C_1, \ldots, C_n]$ (i.e., by upper bounds with formal type arguments substituted with captured ones)
* If $\triangleleft A_i$ is a covariant type, $C_i <: A_i$
* If $\triangleright A_i$ is a contravariant type, $C_i :> A_i$
* If $\star A_i$ is a star type, $Nothing <: C_i <: Any?$
* Otherwise, $C_i = A_i$

#### Flexible types

Kotlin, being a multi-platform language, needs to support transparent interoperability with platform-dependent code. However, this presents a problem in that some platforms may not support null safety the way Kotlin does. To deal with this, Kotlin supports *gradual typing* in the form of flexible types.

A flexible type represents a range of possible types between type $L$ (lower bound) and type $U$ (upper bound), written as $(L..U)$. One should note flexible types are abstract and *non-denotable*, i.e., one cannot explicitly declare a variable with flexible type, these types are created by the type system when needed.

To represent a valid flexible type, $(L..U)$ should satisfy the following conditions.

* $L$ and $U$ are valid types
* $L <: U$
* $\neg (L <: U)$
* $L$ and $U$ are **not** flexible types (but may contains other flexible types as part of their type signature)

As the name suggests, flexible types are flexible --- a value of type $(L..U)$ can be used in any context, where one of the possible types between $L$ and $U$ is needed (for more details, see [subtyping rules for flexible types][Subtyping]). However, the actual type will be a specific type between $L$ and $U$, thus making the substitution unsafe in many cases, which is why Kotlin generates dynamic assertions, when it is impossible to prove statically the safety of flexible type use.

TODO(Details of assertion generation?)

##### Platform types

TODO(Platform types as flexible types)

TODO(Reference for different platforms)

#### Nullable types

Kotlin supports null safety by having two type universes --- nullable and non-nullable. All classifier type declarations, built-in or user-defined, create non-nullable types, i.e., types which cannot hold `null` value at runtime.

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
* For any iPACT $\widehat{T} = T(F_1, \ldots, F_n)[A_1, \ldots, A_n] : S_1, \ldots, S_m$ with captured type arguments $C_1, \ldots, C_n$ it is true that $\forall i \in [1,m]: \widehat{T} <: S_i[C_1, \ldots, C_n]$
* For any two iPACTs $\widehat{T} = T(F_1, \ldots, F_n)[A_1, \ldots, A_n]$ and $\widehat{T^\prime} = T(F_1, \ldots, F_n)[A_1^\prime, \ldots, A_n^\prime]$ it is true that $\widehat{T} <: \widehat{T^\prime}$ iff $\forall i \in [1,n]: C_i <: C_i^\prime$

Subtyping for non-nullable, abstract types and type arguments uses the following rules.

* $\forall T : \text{kotlin.Nothing} <: T <: \text{kotlin.Any}$
* For any PACT $\widehat{T} = T(F_1, \ldots, F_n) : S_1, \ldots, S_m$ it is true that $\forall i \in [1,m]: \widehat{T} <: S_i[F_1, \ldots, F_n]$
* For any covariant type $\triangleleft T$ it is true that $\forall U : T <: U \Rightarrow \triangleleft T <: U$
* For any contravariant type $\triangleright T$ it is true that $\forall L <: T \Rightarrow L <: \triangleright T$
* For any star type $\star T$, $Nothing <: C_i <: Any?$
* For any bounded type $T <: B_1, \ldots, B_n$ it is true that $\forall i \in [1,n]: T <: B_i$

Subtyping for nullable types is checked separately and uses a special procedure, which is described [here][Subtyping for nullability].

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

TODO(Nullability subtyping rules)

### Generics

TODO(Here be a lot of dragons...)
