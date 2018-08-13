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

$\Gamma$
~ Type context

$T\lbrack S_1, \ldots, S_n\rbrack$
~ The result of type argument substitution for type $T$ with types $S_i$

$A \cup B$
~ Intersection type intersecting A and B

Type parameter
~ Formal type argument of parameterized type

Type argument
~ Actual type argument in parameterized type constructor application

PACT
~ Parameterized abstract classifier type

iPACT
~ Instantiated parameterized concrete classifier type

### Introduction

Kotlin has a type system with the following main properties.

* Hybrid static and gradual type checking
* Null safety
* No unsafe implicit conversions
* Unified root type
* Nominal subtyping with bounded parametric polymorphism and mixed-site variance

TODO(static type checking, gradual type checking)

Null safety is enforced by having two type universes --- _nullable_ (with nullable types $T?$) and _non-nullable_ (with non-nullable types $T!!$). All operations within the non-nullable type universe are safe, i.e., should never cause a runtime null pointer error.

Implicit conversions between types in Kotlin are limited to safe upcasts w.r.t. subtyping, meaning all other (unsafe) conversions must be explicit, done via either a conversion function or an [explicit cast][Cast expression]. However, Kotlin also supports smart casts --- a special kind of implicit conversions which are safe w.r.t. program control- and data-flow, which are covered in more detail [here][Smart casts].

The unified supertype type for all types in Kotlin is `kotlin.Any?`, a nullable version of [kotlin.Any][`kotlin.Any`].

Kotlin uses nominal subtyping, meaning subtyping relation is defined when a type is declared, with bounded parametric polymorphism, implemented as [generics][Generics] via [parameterized types][Parameterized classifier types]. Subtyping between these parameterized types is defined through [mixed-site variance][Mixed-site variance].

### Type kinds

For the purposes of this section, we establish the following type kinds --- different flavours of types which exist in the Kotlin type system.

* [Built-in types][Built-in types]
* [Classifier types][Classifier types]
* [Function types][Function types]
* [Array types][Array types]
* [Flexible types][Flexible types]
* [Nullable types][Nullable types]
* [Intersection types][Intersection types]
* TODO(Intersection and union types)
* TODO(Error / invalid types)

We distinguish between *concrete* and *abstract* types. Concrete types are types which are assignable to values; abstract types either need to be instantiated as concrete types before they can be used as value types, or are used internally by the type system and are not directly denotable.

#### Built-in types

Kotlin type system uses the following built-in types, which have special semantics and representation (or lack thereof).

##### `kotlin.Any`

`kotlin.Any` is the unified supertype ($\top$) for $\{T!!\}$, i.e., all non-nullable types are subtypes of `kotlin.Any`, either explicitly, implicitly, or by subtyping relation.

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

TODO(Compare to `void`?)

#### Classifier types

Classifier types represent regular types which are declared as [classes][Classes], [interfaces][Interfaces] or [objects][Objects]. As Kotlin supports [generics][Generics], there are two variants of classifier types: simple and parameterized.

##### Simple classifier types

A simple concrete classifier type

$$T : S_1, \ldots, S_m$$

consists of

* type name $T$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a valid simple concrete classifier type, $T : S_1, \ldots, S_m$ should satisfy the following conditions.

* $T$ is a valid type name
* $\forall i \in [1,m]: S_i$ must be concrete, non-nullable, valid type

##### Parameterized classifier types

A parameterized abstract classifier type (PACT)

$$T(F_1, \ldots, F_n) : S_1, \ldots, S_m$$

consists of

* type constructor $T$ which takes type arguments and returns an instantiated type
* type parameters $F_1, \ldots, F_n$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a valid PACT, $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$ should satisfy the following conditions.

* $T$ is a valid type name
* $\forall i \in [1,n]: F_i$ must be one of the following kinds
    - [unbounded type parameter][Unbounded type parameters]
    - [projected type parameter][Projected type parameters]
    - [bounded type parameter][Bounded type parameters]
* $\forall j \in [1,m]: S_j[F_1, \ldots, F_n]$ must be concrete, non-nullable, valid type

An instantiated parameterized concrete classifier type (iPACT)

$$T[A_1, \ldots, A_n]$$

consists of

* PACT $T$
* type arguments $A_1, \ldots, A_n$

To represent a valid iPACT, $T[A_1, \ldots, A_n]$ should satisfy the following conditions.

* $T$ is a valid PACT with $n$ type parameters
* $\forall i \in [1,n]: A_i$ must be one of the following kinds
    - valid concrete type
    - valid projected type
    - type parameter available in the current type context $\Gamma$  
      TODO(What is a type context?)  
      TODO(Inner vs nested contexts)
* $\forall i \in [1,n]: A_i <: F_i$ where $F_i$ is the respective type parameter of $T$

##### Type parameters

Type parameters are a special kind of abstract types, which are introduced by PACTs. They are valid only in the context of their declaring PACT.

When creating an iPACT from PACT, type parameters with their respective type arguments go through [capturing][Type capturing] and create *captured* type arguments, which follow special rules described in more detail below.

###### Bounded type parameters

A bounded type parameter is an abstract type which is used to specify upper type bounds for type parameters and is defined as $F <: B_1, \ldots, B_n$, where $B_i$ is an i-th upper bound on type parameter $F$.

To represent a valid bounded type parameter of PACT $T$, $F <: B_1, \ldots, B_n$ should satisfy the following conditions.

* $F$ is a type parameter of PACT $T$
* $\forall i \in [1,n]: B_i$ must be one of the following kinds
    - valid concrete type
    - a type parameter available in the current type context $\Gamma$

TODO(Single generic bound allowed)

TODO(Only one class bound allowed)

###### Mixed-site variance

To implement subtyping between parameterized types, Kotlin uses *mixed-site variance* --- a combination of declaration- and use-site variance, which is easier to understand and reason about, compared to wildcards from Java. Mixed-site variance means you can specify, whether you want your parameterized type to be co-, contra- or invariant on some type parameter, both in type parameter (declaration-site) and type argument (use-site). For more practical discussion about mixed-site variance, we readdress you to [generics][Generics].

###### Declaration-site variance

An invariant type parameter $F$ is an abstract type which may capture any valid type (see [subtyping][Subtyping] for more details on variance); if one needs co- or contravariant type parameter, they need to use projected type parameters.

To represent a valid invariant type parameter of PACT $T$, $F$ should satisfy the following conditions.

* $F$ is a type parameter available in the current type context $\Gamma$

Projected type parameters are abstract types which are used to declare a type parameter as *covariant* or *contravariant*. The variance information is used by [subtyping][Subtyping] and for checking allowed operations on values of co- and contravariant type parameters.

To represent a valid covariant type parameter $\triangleleft F$ of PACT $T$, $\triangleleft F$ should satisfy the following conditions.

* $F$ is a type parameter available in the current type context $\Gamma$

To represent a valid contravariant type parameter $\triangleright F$ of PACT $T$, $\triangleright F$ should satisfy the following conditions.

* $F$ is a type parameter available in the current type context $\Gamma$

TODO(type projections are not allowed on functions and properties)

TODO(no type projections on supertype type arguments)

TODO(conflicting projections)

###### Use-site variance

Kotlin also supports use-site variance, by specifying the variance for type arguments. Just like with projected type parameters, one can have projected type arguments being co-, contra- or invariant.

To represent a valid invariant type argument of iPACT $T$, $A$ should satisfy the following conditions.

* $A$ must be one of the following kinds
    - a valid concrete type
    - a type parameter available in the current type context $\Gamma$

To represent a valid covariant type argument $\triangleleft A$ of iPACT $T$, $\triangleleft A$ should satisfy the following conditions.

* $A$ must be one of the following kinds
    - a valid concrete type
    - a type parameter available in the current type context $\Gamma$

To represent a valid contravariant type argument $\triangleright A$ of iPACT $T$, $\triangleright A$ should satisfy the following conditions.

* $A$ must be one of the following kinds
    - a valid concrete type
    - a type parameter available in the current type context $\Gamma$

In case one cannot specify any valid type argument, but still needs to use PACT in a type-safe way, one may use *star-projected* type argument, which is roughly equivalent to a combination of $\triangleleft \texttt{kotlin.Any?}$ and $\triangleright \texttt{kotlin.Nothing}$ (for further details, see [here][Generics]).

TODO(Clean-up this mess)

##### Type capturing

Type capturing (similarly to Java capturing conversion) is used when instantiating parameterized types; it creates *captured* types based on the type information of both type parameters and arguments, which present a unified view on the resulting types and simplifies further reasoning.

For a given PACT $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$, its iPACT $T[A_1, \ldots, A_n]$ uses the following rules to create captured type $C_i$ from the type parameter $F_i$ and type argument $A_i$.

TODO(Does this set describe a type universe?)

TODO(Blah-blah about existential types?)

> NB: A captured type $C$ may be viewed as a set of its type constraints $\mathbb{C}$. **All** applicable rules are used to create the resulting constraint set.

* If $\triangleleft  F_i$ is a covariant type parameter and $A_i$ is not a concrete type, covariant or star-projected type argument, it is an error. Otherwise, $C_i <: A_i$.
* If $\triangleright F_i$ is a contravariant type parameter and $A_i$ is not a concrete type, contravariant or star-projected type argument, it is an error. Otherwise, $C_i :> A_i$.
* If $F_i <: B_1, \ldots, B_n$ is a bounded type parameter, $C_i <: B_i[C_1, \ldots, C_n]$
* If $\triangleleft A_i$ is a covariant type argument, $C_i <: A_i$
* If $\triangleright A_i$ is a contravariant type argument, $C_i :> A_i$
* If $\star A_i$ is a star-projected type argument, $kotlin.Nothing <: C_i <: kotlin.Any?$
* Otherwise, $C_i = A_i$

#### Function types

Kotlin has first-order functions; e.g., it supports function types, which describe the argument and return types of its corresponding function.

A function type FT

$$FT(A_1, \ldots, A_n) \rightarrow R$$

consists of

* argument types $A_i$
* return type $R$

and may be considered the following instantiation of a special parameterized abstract classifier type $FunctionN$

$$FunctionN(\triangleleft P_1, \ldots, \triangleleft P_n, \triangleright RT)$$

$$FT(A_1, \ldots, A_n) \rightarrow R \equiv FunctionN[A_1, \ldots, A_n, R]$$

These $FunctionN$ types follow the rules of regular PACTs w.r.t. subtyping.

A function type with receiver FTR

$$FTR(TH, A_1, \ldots, A_n) \rightarrow R$$

consists of

* receiver type $TH$
* argument types $A_i$
* return type $R$

From the type system's point of view, it is equivalent to the following function type

$$FTR(TH, A_1, \ldots, A_n) \rightarrow R \equiv FT(TH, A_1, \ldots, A_n) \rightarrow R$$

i.e., receiver is considered as yet another argument of its function type.

> Note: this means that, for example, these two types are equivalent
>
> * `Int.(Int) -> String`
> * `(Int, Int) -> String`

#### Array types

TODO(Everything...)

TODO(Primitive type array coercion)

#### Flexible types

Kotlin, being a multi-platform language, needs to support transparent interoperability with platform-dependent code. However, this presents a problem in that some platforms may not support null safety the way Kotlin does. To deal with this, Kotlin supports *gradual typing* in the form of flexible types.

A flexible type represents a range of possible types between type $L$ (lower bound) and type $U$ (upper bound), written as $(L..U)$. One should note flexible types are abstract and *non-denotable*, i.e., one cannot explicitly declare a variable with flexible type, these types are created by the type system when needed.

To represent a valid flexible type, $(L..U)$ should satisfy the following conditions.

* $L$ and $U$ are valid concrete types
* $L <: U$
* $\neg (L <: U)$
* $L$ and $U$ are **not** flexible types (but may contains other flexible types as part of their type signature)

As the name suggests, flexible types are flexible --- a value of type $(L..U)$ can be used in any context, where one of the possible types between $L$ and $U$ is needed (for more details, see [subtyping rules for flexible types][Subtyping]). However, the actual type will be a specific type between $L$ and $U$, thus making the substitution possibly unsafe, which is why Kotlin generates dynamic assertions, when it is impossible to prove statically the safety of flexible type use.

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

#### Intersection types

Intersection types are special non-denotable types that are used to express (loosely) that a value has two or more types to choose from.
Intersection type of two non-nullable types $A$ and $B$ is denoted $A \cup B$. Intersection types are used for [smart casting][Smart casts].
Intersection types are commutative and associative, meaning that $A \cup B$ is the same type as $B \cup A$ and $A \cup (B \cup C)$
is the same type as $A \cup B \cup C$.

For presentation purposes, we will normalize intersection type operands lexicographically based on their notation.

##### Type intersection

The primary operation on types that is used to construct intersection types is called type intersection (do not confuse the two).
Type intersection $A \times B$ of types $A$ and $B$ has the following properties:

- it is commutative and associative, just like set intersection (for simplicity, all other properties will be shown for only one order of operands, meaning they hold under both commutativity and associativity)
- it is idempotent, meaning that $A \times A = A$
- if $A <: B$ then $A \times B = A$
    - $\times$ has identity at `kotlin.Any`: $\forall T . T \times \mathtt{kotlin.Any} = T$
- if $A$ is non-nullable, than $A \times B$ is also non-nullable
- if both $A$ and $B$ are nullable, $A \times B = (A!! \times B!!)?$
- if type $A$ is nullable and type $B$ is not, $A \times B = A!! \times B$
- if both $A$ and $B$ are non-nullable and no other rules apply, $A \times B = A \cup B$

TODO(Intersection of flexible types)
TODO(If $A <: B$ and $B <: A$, what is $A \times B$???)

### Subtyping

Kotlin uses the classic notion of *subtyping* as *substitutability* --- if $S$ is a subtype of $T$ (denoted as $S <: T$), values of type $S$ can be safely used where values of type $T$ are expected. The subtyping relation $<:$ is:

* reflexive ($A <: A$)
* transitive ($A <: B \land B <: C \Rightarrow A <: C$)

Two types $A$ and $B$ are *equivalent* ($A \equiv B$), iff $A <: B \land B <: A$. Due to the presence of flexible types, this relation is **not** transitive (see [here][Subtyping for flexible types] for more details).

#### Subtyping rules

Subtyping for non-nullable, concrete types uses the following rules.

* $\forall T : \text{kotlin.Nothing} <: T <: \text{kotlin.Any}$
* For any simple classifier type $T : S_1, \ldots, S_m$ it is true that $\forall i \in [1,m]: T <: S_i$
* For any iPACT $\widehat{T} = T(F_1, \ldots, F_n)[A_1, \ldots, A_n] : S_1, \ldots, S_m$ with captured type arguments $C_1, \ldots, C_n$ it is true that $\forall i \in [1,m]: \widehat{T} <: S_i[C_1, \ldots, C_n]$
* For any two iPACTs $\widehat{T}$ and $\widehat{T^\prime}$ with captured type arguments $C_i$ and $C_i^\prime$ it is true that $\widehat{T} <: \widehat{T^\prime}$ if $\forall i \in [1,n]: C_i <: C_i^\prime$

Subtyping for non-nullable, abstract types uses the following rules.

* $\forall T : \text{kotlin.Nothing} <: T <: \text{kotlin.Any}$
* For any PACT $\widehat{T} = T(F_1, \ldots, F_n) : S_1, \ldots, S_m$ it is true that $\forall i \in [1,m]: \widehat{T} <: S_i$

TODO(Subtyping for type parameters)

---
# * For any covariant type argument $\triangleleft T$ it is true that $\forall U : T <: U \Rightarrow \triangleleft T <: U$
# * For any contravariant type argument $\triangleright T$ it is true that $\forall L : L <: T \Rightarrow L <: \triangleright T$
# * For any star type argument $\star T$, $kotlin.Nothing <: T <: kotlin.Any?$
# * For any bounded type argument $T <: B_1, \ldots, B_n$ it is true that $\forall i \in [1,n]: T <: B_i$
---

Subtyping for non-nullable, captured types uses rules of different kind, as captured type $C$ describes not one, but a set of types which satisfy its type constraints $\mathbb{C}$. Therefore, we use the following subtyping rules for captured types.

* $\forall C : \text{kotlin.Nothing} <: C <: \text{kotlin.Any}$
* For any two captured types $C$ and $C^\prime$, $C <: C^\prime$ if $\forall T : \mathbb{C}(T) \Rightarrow \mathbb{C^\prime}(T)$ (i.e., a set of types for $C$ is a subset of a set of types for $C^\prime$)

Subtyping for nullable types is checked separately and uses a special set of rules which are described [here][Subtyping for nullable types].

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

#### Subtyping for intersection types

Intersection types introduce several new rules for subtyping. Let $A, B, C, D$ be non-nullable types:

- $A \cup B <: A$
- $A \cup B <: B$
- $A <: C \land B <: D \Rightarrow A \cup B <: C \cup D$

#### Subtyping for nullable types

Subtyping for two possibly nullable types $A$ and $B$ is defined via *two* relations, both of which must hold.

* Regular subtyping $<:$ for non-nullable types $A!!$ and $B!!$
* Subtyping by nullability $\sbn$

Subtyping by nullability $\sbn$ for two possibly nullable types $A$ and $B$ uses the following rules.

* $A!! \sbn B$
* $A \sbn B$ if $\exists T!! : A <: T!!$
* $A \sbn B?$
* $A \sbn B$ if $\not \exists T!! : B <: T!!$

TODO(How the existence check works)

### Generics

TODO(Here be a lot of dragons...)

### References

1. Tate, Ross. "Mixed-site variance." FOOL, 2013.
