## Type system

TODO(Add examples)
TODO(Add grammar snippets?)

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

$\Gamma$
~ Type context

$K_T(F, A)$
~ Captured type from the [type capturing][Type capturing] of type parameter $F$ and type argument $A$ in parameterized type $T$

$A <: B$
~ A is a subtype of B

$A <:> B$
~ A and B are not related w.r.t. subtyping

$T\lbrack S_1, \ldots, S_n\rbrack$
~ The result of type argument substitution for type $T$ with types $S_i$

$A \amp B$
~ Intersection type of $A$ and $B$

Type parameter
~ Formal type argument of a parameterized type

Type argument
~ Actual type argument in a parameterized type constructor application

PACT
~ Parameterized abstract classifier type

iPACT
~ Instantiated parameterized concrete classifier type

TODO(Not everything is in the glossary, make some criteria of what goes where)
TODO(Cleanup glossary)

### Introduction

Similarly to most other programming languages, Kotlin operates on data in the form of *values* or *objects*, which have *types* --- descriptions of what is the expected behaviour and possible values for their datum.
An empty value is represented by a special `null` object; most operations with it result in runtime [errors or exceptions][Exceptions].

Kotlin has a type system with the following main properties.

* Hybrid static and gradual type checking
* Null safety
* No unsafe implicit conversions
* Unified root type
* Nominal subtyping with bounded parametric polymorphism and mixed-site variance

TODO(static type checking, gradual type checking)

Null safety is enforced by having two type universes --- _nullable_ (with nullable types $T?$) and _non-nullable_ (with non-nullable types $T!!$).
A value of any non-nullable type cannot contain `null`, meaning all operations within the non-nullable type universe are safe, i.e., should never cause a runtime error.

Implicit conversions between types in Kotlin are limited to safe upcasts w.r.t. subtyping, meaning all other (unsafe) conversions must be explicit, done via either a conversion function or an [explicit cast][Cast expression].
However, Kotlin also supports smart casts --- a special kind of implicit conversions which are safe w.r.t. program control- and data-flow, which are covered in more detail [here][Smart casts].

The unified supertype type for all types in Kotlin is `kotlin.Any?`, a nullable version of [kotlin.Any][`kotlin.Any`].

Kotlin uses nominal subtyping, meaning subtyping relation is defined when a type is declared, with bounded parametric polymorphism, implemented as [generics][Generics] via [parameterized types][Parameterized classifier types].
Subtyping between these parameterized types is defined through [mixed-site variance][Mixed-site variance].

### Type kinds

For the purposes of this section, we establish the following type kinds --- different flavours of types which exist in the Kotlin type system.

* [Built-in types][Built-in types]
* [Classifier types][Classifier types]
* [Function types][Function types]
* [Array types][Array types]
* [Flexible types][Flexible types]
* [Nullable types][Nullable types]
* [Intersection types][Intersection types]
* [Union types][Union types]
* TODO(GLB, LUB)
* TODO(Error / invalid types)

We distinguish between *concrete* and *abstract* types.
Concrete types are types which are assignable to values; abstract types either need to be instantiated as concrete types before they can be used as types for values, or are used internally by the type system and are not directly denotable.

We also may further distinguish *concrete* types between *class* and *interface* types; as Kotlin is a language with single inheritance, sometimes it is important to discriminate between these kinds of types.
Any given concrete type may be either a class or an interface type, but never both.

#### Built-in types

Kotlin type system uses the following built-in types, which have special semantics and representation (or lack thereof).

##### `kotlin.Any`

`kotlin.Any` is the unified [supertype][Subtyping] ($\top$) for $\{T!!\}$, i.e., all non-nullable types are subtypes of `kotlin.Any`, either explicitly, implicitly, or by [subtyping relation][Subtyping].

TODO(`kotlin.Any` members?)

##### `kotlin.Nothing`

`kotlin.Nothing` is the unified [subtype][Subtyping] ($\bot$) for $\{T!!\}$, i.e., `kotlin.Nothing` is a subtype of all non-nullable types, including user-defined ones.
This makes it an uninhabited type (as it is impossible for anything to be, for example, a function and an integer at the same time), meaning instances of this type can never exist at runtime; subsequently, there is no way to create an instance of `kotlin.Nothing` in Kotlin.

As the evaluation of an expression with `kotlin.Nothing` type can never complete normally, it is used to mark special situations, such as:

* non-terminating expressions
* exceptional control flow
* control flow transfer

Additional details about how `kotlin.Nothing` should be processed are available [here][Control- and data-flow analysis].

##### `kotlin.Unit`

`kotlin.Unit` is a unit type, i.e., a type with only one value `kotlin.Unit`; all values of type `kotlin.Unit` should reference the same underlying `kotlin.Unit` object.

TODO(Compare to `void`?)

##### `kotlin.Function`

`kotlin.Function<R>` is the unified supertype of all [function types][Function types]. It is parameterized over function return type `R`.

#### Classifier types

Classifier types represent regular types which are declared as [classes][Classes], [interfaces][Interfaces] or [objects][Objects].
As Kotlin supports [generics][Generics], there are two variants of classifier types: simple and parameterized.

##### Simple classifier types

A simple concrete classifier type

$$T : S_1, \ldots, S_m$$

consists of

* type name $T$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a well-formed simple concrete classifier type, $T : S_1, \ldots, S_m$ should satisfy the following conditions.

* $T$ is a valid type name
* $\forall i \in [1,m]: S_i$ must be concrete, [non-nullable][Nullable types], well-formed type

> Example:
> 
> ```kotlin
> // A well-formed type with no supertypes
> interface Base
> 
> // A well-formed type with a single supertype Base
> interface Derived : Base
> 
> // An ill-formed type,
> // as nullable type cannot be a supertype
> interface Invalid : Base?
> ```

> Note: for the purpose of different type system examples, we assume the presence of the following well-formed concrete types:
> 
> * class `String`
> * interface `Number`
> * class `Int` <: `Number`
> * class `Double` <: `Number`

##### Parameterized classifier types

A parameterized *abstract* classifier type (PACT)

$$T(F_1, \ldots, F_n) : S_1, \ldots, S_m$$

consists of

* type constructor $T$ which takes type arguments and returns an instantiated type
* type parameters $F_1, \ldots, F_n$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a well-formed PACT, $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$ should satisfy the following conditions.

* $T$ is a valid type name
* $\forall i \in [1,n]: F_i$ must be one of the following kinds
    - [unbounded type parameter][Type parameters]
    - [bounded type parameter][Type parameters]
    - [projected type parameter][Declaration-site variance]
* $\forall j \in [1,m]: S_j[F_1, \ldots, F_n]$ must be concrete, non-nullable, well-formed type

An instantiated parameterized *concrete* classifier type (iPACT)

$$T[A_1, \ldots, A_n]$$

consists of

* PACT $T$
* type arguments $A_1, \ldots, A_n$

To represent a well-formed iPACT, $T[A_1, \ldots, A_n]$ should satisfy the following conditions.

* $T$ is a valid PACT with $n$ type parameters
* $\forall i \in [1,n]: A_i$ must be one of the following kinds
    - well-formed concrete type
    - well-formed [projected type][Use-site variance]
    - type parameter available in the current [type context][Type context] $\Gamma$  
* $\forall i \in [1,n]: K_T(F_i, A_i)$ is a well-formed captured type, where $K$ is a [type capturing][Type capturing] operator

> Example:
> 
> ```kotlin
> // A well-formed PACT with no supertypes
> // A and B are unbounded type parameters
> interface Generic<A, B>
> 
> // A well-formed PACT with a single iPACT supertype
> // Int and String are well-formed concrete types
> interface ConcreteDerived<P, Q> : Generic<Int, String>
> 
> // A well-formed PACT with a single iPACT supertype
> // P and Q are type parameters of Derived2,
> //   used as type arguments of Generic
> interface GenericDerived<P, Q> : Generic<P, Q>
> 
> // An ill-formed PACT,
> //   as an abstract type Generic
> //   cannot be used as a supertype
> interface Invalid<P> : Generic
> 
> 
> // A well-formed PACT with no supertypes
> // out A is a projected type parameter
> interface Out<out A>
> 
> 
> // A well-formed PACT with no supertypes
> // S : Number is a bounded type parameter
> // (S <: Number)
> interface NumberWrapper<S : Number>
> 
> // A well-formed type with a single iPACT supertype
> // NumberWrapper<Int> is well-formed,
> //   as Int <: Number
> interface IntWrapper : NumberWrapper<Int>
> 
> // An ill-formed type,
> //   as NumberWrapper<String> is an ill-formed iPACT
> //   (String <:> Number)
> interface InvalidWrapper : NumberWrapper<String>
>```

##### Type parameters

Type parameters are a special kind of abstract types, which are introduced by PACTs.
They are valid only in the type context of their declaring PACT.

When creating an iPACT from PACT, type parameters with their respective type arguments go through [capturing][Type capturing] and create *captured* type arguments, which follow special rules described in more detail below.

Type parameters may be either unbounded or bounded.
By default, a type parameter $F$ is unbounded, which is the same as saying it is a bounded type parameter of the form $F <: kotlin.Any?$.

A bounded type parameter is an abstract type which is used to specify upper type bounds for type parameters and is defined as $F <: B_1, \ldots, B_n$, where $B_i$ is an i-th upper bound on type parameter $F$.

To represent a valid bounded type parameter of PACT $T$, $F <: B_1, \ldots, B_n$ should satisfy either of the following sets of conditions.

* Bounded type parameter with concrete bounds:
    * $F$ is a type parameter of PACT $T$
    * $\forall i \in [1,n]: B_i$ must be a well-formed concrete type
    * No more than one of $B_i$ may be a class type

> Note: the last condition is a nod to the single inheritance nature of Kotlin; as any type may be a subtype of no more than one class type, it makes no sense to support several class type bounds.
> For any two class types, either these types are in a subtyping relation (and you should use the more specific type in the bounded type parameter), or they are unrelated (and the bounded type parameter is empty).

* Bounded type parameter with abstract bounds:
    * $F$ is a type parameter of PACT $T$
    * $i = 1$ (i.e., there is a single upper bound)
    * $B_1$ is a type parameter available in the current type context $\Gamma$

###### Mixed-site variance

To implement subtyping between parameterized types, Kotlin uses *mixed-site variance* --- a combination of declaration- and use-site variance, which is easier to understand and reason about, compared to wildcards from Java.
Mixed-site variance means you can specify, whether you want your parameterized type to be co-, contra- or invariant on some type parameter, both in type parameter (declaration-site) and type argument (use-site).

> Info: *variance* is a way of describing how [subtyping][Subtyping] works for *variant* parameterized types.
> With declaration-site variance, for two types $A <: B$, subtyping between `T<A>` and `T<B>` depends on the variance of type parameter $F$ of some paraneterized type $T$.
> 
> * if $F$ is covariant, `T<A> <: T<B>`
> * if $F$ is contravariant, `T<A> :> T<B>`
> * if $F$ is invariant, `T<A> <:> T<B>`
> 
> Use-site variance allows the user to change the type variance of an *invariant* type parameter by specifying it on the corresponding type argument.
> `out A` means covariant type argument, `in A` means contravariant type argument; for two types $A <: B$ and an invariant type parameter $F$ of some parameterized type $T$, subtyping for use-site variance has the following rules.
> 
> * `T<out A> <: T<out B>`
> * `T<in A> :> T<in B>`
> * `T<A> <: T<out A>`
> * `T<A> <: T<in A>`
> * `T<in A> <:> T<out A>`

> Note: Kotlin does not support specifying both co- and contravariance at the same time, i.e., it is impossible to have `T<in A out B>`.

For further discussion about mixed-site variance and its practical applications, we readdress you to [subtyping][Subtyping] and [generics][Generics].

###### Declaration-site variance

An invariant type parameter $F$ is an abstract type which may capture any well-formed type (see [subtyping][Subtyping] for more details on variance); if one needs co- or contravariant type parameter, they need to use projected type parameters.

To represent a valid invariant type parameter of PACT $T$, $F$ should satisfy the following conditions.

* $F$ is a type parameter available in the current type context $\Gamma$

Projected type parameters are abstract types which are used to declare a type parameter as *covariant* or *contravariant*.
The variance information is used by [subtyping][Subtyping] and for checking allowed operations on values of co- and contravariant type parameters.

To represent a valid covariant type parameter $\triangleleft F$ of PACT $T$, $\triangleleft F$ should satisfy the following conditions.

* $F$ is a type parameter available in the current type context $\Gamma$

To represent a valid contravariant type parameter $\triangleright F$ of PACT $T$, $\triangleright F$ should satisfy the following conditions.

* $F$ is a type parameter available in the current type context $\Gamma$

> Note: a mnemonic to remember co- and contravariant type parameter notation is as follows: $\triangleleft F$ allows to covariantly get the value *out* of $F$, $\triangleright F$ allows to contravariantly put the value *in* to $F$.

> Important: declaration-site variance can be used only when declaring types, e.g., type parameters of functions cannot be variant.

> Example:
> 
> ```kotlin
> // A parameterized type with an invariant type parameter
> interface Invariant<A>
> // A parameterized type with a covariant type parameter
> interface Out<out A>
> // A parameterized type with a contravariant type parameter
> interface In<in A>
> 
> fun testInvariant() {
>     var invInt: Invariant<Int> = ...
>     var invNumber: Invariant<Number> = ...
>     
>     if (random) invInt = invNumber // ERROR
>     else invNumber = invInt // ERROR
>     
>     // Invariant type parameters do not create subtyping
> }
>
> fun testOut() {
>     var outInt: Out<Int> = ...
>     var outNumber: Out<Number> = ...
>     
>     if (random) outInt = outNumber // ERROR
>     else outNumber = outInt // OK
>     
>     // Covariant type parameters create "same-way" subtyping
>     //   Int <: Number => Out<Int> <: Out<Number>
>     // (more specific type Out<Int> can be assigned
>     //  to a less specific type Out<Number>)
> }
> 
> fun testIn() {
>     var inInt: In<Int> = ...
>     var inNumber: In<Number> = ...
>     
>     if (random) inInt = inNumber // OK
>     else inNumber = inInt // ERROR
>     
>     // Contravariant type parameters create "opposite-way" subtyping
>     //   Int <: Number => In<Int> :> In<Number>
>     // (more specific type In<Number> can be assigned
>     //  to a less specific type In<Int>)
> }
> ```

###### Use-site variance

Kotlin also supports use-site variance, by specifying the variance for type arguments.
Just like with projected type parameters, one can have projected type arguments being co-, contra- or invariant.

To represent a valid invariant type argument $A$, corresponding to a type parameter $F$ of iPACT $T$, it should satisfy the following conditions.

* $A$ must be one of the following kinds
    - a well-formed concrete type
    - a type parameter available in the current type context $\Gamma$

To represent a valid covariant type argument $\triangleleft A$, corresponding to a type parameter $F$ of iPACT $T$, it should satisfy the following conditions.

* $A$ must be one of the following kinds
    - a well-formed concrete type
    - a non-contravariant type parameter available in the current type context $\Gamma$
* $F$ must *not* be a contravariant type parameter

To represent a valid contravariant type argument $\triangleright A$, corresponding to a type parameter $F$ of iPACT $T$, it should satisfy the following conditions.

* $A$ must be one of the following kinds
    - a well-formed concrete type
    - a non-covariant type parameter available in the current type context $\Gamma$
* $F$ must *not* be a covariant type parameter

> Note: these rules mean it is impossible to have a type parameter or argument in both co- and contravariant positions at the same time.

In case one cannot specify any valid type argument, but still needs to use PACT in a type-safe way, one may use *bivariant* type argument $\star A$, which is roughly equivalent to a combination of $\triangleleft \texttt{kotlin.Any?}$ and $\triangleright \texttt{kotlin.Nothing}$ (for further details, see [subtyping][Subtyping] and [generics][Generics]).

TODO(Specify how this combination of co- and contravariant parameters works from the practical PoV)

> Important: use-site variance cannot be used when declaring a supertype.

> Example:
> ```kotlin
> // A parameterized type with an invariant type parameter
> interface Inv<A>
> 
> fun test() {
>     var invInt: Inv<Int> = ...
>     var invNumber: Inv<Number> = ...
>     var outInt: Inv<out Int> = ...
>     var outNumber: Inv<out Number> = ...
>     var inInt: Inv<in Int> = ...
>     var inNumber: Inv<in Number> = ...
>     
>     when (random) {
>         1 -> {
>             inInt = invInt    // OK
>             // T<in Int> :> T<Int>
>             
>             inInt = invNumber // OK
>             // T<in Int> :> T<in Number> :> T<Number>
>         }
>         2 -> {
>             outNumber = invInt    // OK
>             // T<out Number> :> T<out Int> :> T<Int>
>             
>             outNumber = invNumber // OK
>             // T<out Number> :> T<Number>
>         }
>         3 -> {
>             invInt = inInt  // ERROR
>             invInt = outInt // ERROR
>             // It is invalid to assign less specific type
>             // to a more specific one
>             //   T<Int> <: T<in Int>
>             //   T<Int> <: T<out Int>
>         }
>         4 -> {
>             inInt = outInt    // ERROR
>             inInt = outNumber // ERROR
>             // types with co- and contravariant type parameters
>             // are not connected by subtyping
>             //   T<in Int> <:> T<out Int>
>         }
>     }
> }
> ```

##### Type capturing

Type capturing (similarly to Java capture conversion) is used when instantiating parameterized types; it creates *captured* types based on the type information of both type parameters and arguments, which present a unified view on the resulting types and simplifies further reasoning.

The reasoning behind type capturing is closely related to variant parameterized types being a form of *bounded existential types*; e.g., `A<out T>` may be loosely considered as the following existential type: $\exists X : X <: T . \texttt{A<X>}$.
Informally, a bounded existential type describes a *set* of possible types, which satisfy its bound constraints.
Before such a type can be used, it needs to be *opened* (or *unpacked*): existentially quantified type variables are lifted to fresh type variables with corresponding bounds.
We call these type variables *captured* types.

For a given PACT $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$, its iPACT $T[A_1, \ldots, A_n]$ uses the following rules to create captured type $C_i$ from the type parameter $F_i$ and type argument $A_i$.

> Note: **All** applicable rules are used to create the resulting constraint set.

* For a covariant type parameter $\triangleleft  F_i$, if $A_i$ is an ill-formed type or a contravariant type argument, $C_i$ is an ill-formed type.
  Otherwise, $C_i <: A_i$.
* For a contravariant type parameter $\triangleright F_i$, if $A_i$ is an ill-formed type or a covariant type argument, $C_i$ is an ill-formed type.
  Otherwise, $C_i :> A_i$.
* For a bounded type parameter $F_i <: B_1, \ldots, B_n$, $C_i <: B_i[C_1, \ldots, C_n]$.
* For a covariant type argument $\triangleleft A_i$, if $F_i$ is a contravariant type parameter, $C_i$ is an ill-formed type.
  Otherwise, $C_i <: A_i$.
* For a contravariant type argument $\triangleright A_i$, if $F_i$ is a covariant type parameter, $C_i$ is an ill-formed type.
  Otherwise, $C_i :> A_i$.
* For a bivariant type argument $\star A_i$, $kotlin.Nothing <: C_i <: kotlin.Any?$.
* Otherwise, $C_i = A_i$.

> Note: as every captured type corresponds to a fresh type variable, two different captured types $C_i$ and $C_j$ which describe the same set of possible types (i.e., their constraint sets are equals) are *not* considered equal.
> However, in some cases [type inference][Type inference] may approximate (or close) a captured type $C$ to a concrete type $C^{\approx}$; in our case, $C_i^{\approx} \equiv C_j^{approx}$.

TODO(Need to think more about this part)

#### Function types

Kotlin has first-order functions; e.g., it supports function types, which describe the argument and return types of its corresponding function.

A function type FT

$$FT(A_1, \ldots, A_n) \rightarrow R$$

consists of

* argument types $A_i$
* return type $R$

and may be considered the following instantiation of a special parameterized abstract classifier type $FunctionN$

$$FunctionN(\triangleright P_1, \ldots, \triangleright P_n, \triangleleft RT)$$

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

> Note: a compiler implementation may consider a function type $FunctionN$ to have additional supertypes, if it is neccessary.
> For example, all function types $FunctionN$ may be subtypes of a general argument-agnostic type $Function$ for the purpose of unification.

> Example:
> 
> ```kotlin
> // A function of type Function1<Number, Number>
> //   or (Number) -> Number
> fun foo(i: Number): Number = ...
> 
> // A valid assignment w.r.t. function type variance
> // Function1<in Int, out Any> :> Function1<in Number, out Number>
> val fooRef: (Int) -> Any = ::foo
> 
> // A function with receiver of type Function1<Number, Number>
> //   or Number.() -> Number
> fun Number.bar(): Number = ...
> 
> // A valid assignment w.r.t. function type variance
> // Receiver is just yet another function argument
> // Function1<in Int, out Any> :> Function1<in Number, out Number>
> val barRef: (Int) -> Any = Number::bar
> ```

#### Array types

Kotlin arrays are represented as a parameterized type `Array<T>`, where `T` is the type of the stored elements, which supports `get`/`set` operations.
The `Array<T>` type follows the rules of regular PACTs w.r.t. subtyping.

> Note: unlike Java, arrays in Kotlin are declared as invariant. To use them in a co- or contravariant way, one should use [use-site variance][Use-site variance].

In addition to the general `Array<T>` type, Kotlin also has the following specialized array types:

* `DoubleArray` (for `Array<Double>`)
* `FloatArray` (for `Array<Float>`)
* `LongArray` (for `Array<Long>`)
* `IntArray` (for `Array<Int>`)
* `ShortArray` (for `Array<Short>`)
* `ByteArray` (for `Array<Byte>`)
* `CharArray` (for `Array<Char>`)
* `BooleanArray` (for `Array<Boolean>`)

These array types structurally match the corresponding `Array<T>` type; i.e., `IntArray` has the same methods and properties as `Array<Int>`.
However, they are **not** related by subtyping; meaning one cannot pass a `BooleanArray` argument to a function expecting an `Array<Boolean>`.

> Note: the presense of such specialized types allows the compiler to perform additional array-related optimizations.

*Array type specialization* `ATS(T)` is a transformation of a generic `Array<T>` type to a corresponding specialized version, which works as follows.

* If `Array<T>` has a specialized version `TArray`, `ATS(Array<T>) = TArray`
* If `Array<T>` does not have a specialized version, `ATS(Array<T>) = Array<T>`

$ATS$ takes an important part in how [variable length parameters][Variable length parameters] are handled.

#### Flexible types

Kotlin, being a multi-platform language, needs to support transparent interoperability with platform-dependent code.
However, this presents a problem in that some platforms may not support null safety the way Kotlin does.
To deal with this, Kotlin supports *gradual typing* in the form of flexible types.

A flexible type represents a range of possible types between type $L$ (lower bound) and type $U$ (upper bound), written as $(L..U)$.
One should note flexible types are *non-denotable*, i.e., one cannot explicitly declare a variable with flexible type, these types are created by the type system when needed.

To represent a well-formed flexible type, $(L..U)$ should satisfy the following conditions.

* $L$ and $U$ are well-formed concrete types
* $L <: U$
* $\neg (L <: U)$
* $L$ and $U$ are **not** flexible types (but may contains other flexible types as part of their type signature)

As the name suggests, flexible types are flexible --- a value of type $(L..U)$ can be used in any context, where one of the possible types between $L$ and $U$ is needed (for more details, see [subtyping rules for flexible types][Subtyping for flexible types]).
However, the actual type will be a specific type between $L$ and $U$, thus making the substitution possibly unsafe, which is why Kotlin generates dynamic assertions, when it is impossible to prove statically the safety of flexible type use.

TODO(Details of assertion generation?)

##### Dynamic type

Kotlin includes a special *dynamic* type, which is a flexible type $(\texttt{kotlin.Nothing}..\texttt{kotlin.Any?})$.
By definition, this type represents *any* possible Kotlin type, and may be used to support interoperability with dynamically typed libraries, platforms or languages.

##### Platform types

The main use cases for flexible types are *platform types* --- types which the Kotlin compiler uses, when interoperating with code written for another platform (e.g., Java).
In this case all types on the interoperability boundary are subject to *flexibilization* --- the process of converting a platform-specific type to a Kotlin-compatible flexible type.

For further details on how *flexibilization* is done, see:

* [Platform types for Java][TODO(need a way to have same section names in different parts of the spec)]

> Important: platform types should not be confused with *multi-platform projects* --- another Kotlin feature targeted at supporting platform interop.

#### Nullable types

Kotlin supports null safety by having two type universes --- nullable and non-nullable.
All classifier type declarations, built-in or user-defined, create non-nullable types, i.e., types which cannot hold `null` value at runtime.

To specify a nullable version of type `T`, one needs to use `T?` as a type.
Redundant nullability specifiers are ignored --- `T???` is equivalent to `T?`.

> Note: informally, question mark means "$T?$ may hold values of type $T$ or value `null`"

To represent a well-formed nullable type, $T?$ should satisfy the following conditions.

* $T$ is a well-formed type

If an operation is safe regardless of absence or presence of `null`, e.g., assignment of one nullable value to another, it can be used as-is for nullable types.
For operations on $T?$ which may violate null safety, e.g., access to a property, one has the following null-safe options:

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

Intersection types are special *non-denotable* types used to express the fact that a value belongs to *several* types at the same time.

Intersection type of two types $A$ and $B$ is denoted $A \amp B$ and is defined as the [greatest lower bound][Greatest lower bound] of its components $GLB(A, B)$.
Thus, the procedure to calculate GLB may be used to *normalize* an intersection type.

This also means intersection types are commutative and associative, meaning that $A \amp B$ is the same type as $B \amp A$, and $A \amp (B \amp C)$ is the same type as $A \amp B \amp C$.

> Note: for presentation purposes, we will order intersection type operands lexicographically based on their notation.

One of the main uses of intersection types are [smart casts][Smart casts].

#### Union types

TODO(Everything)

### Type context

TODO(Type contexts and their relation to scopes)
TODO(Inner vs nested type contexts)

### Subtyping

TODO(Need to change the way we think about subtyping)

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
comment: |
    * For any covariant type argument $\triangleleft T$ it is true that $\forall U : T <: U \Rightarrow \triangleleft T <: U$
    * For any contravariant type argument $\triangleright T$ it is true that $\forall L : L <: T \Rightarrow L <: \triangleright T$
    * For any star type argument $\star T$, $kotlin.Nothing <: T <: kotlin.Any?$
    * For any bounded type argument $T <: B_1, \ldots, B_n$ it is true that $\forall i \in [1,n]: T <: B_i$
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

- $A \amp B <: A$
- $A \amp B <: B$
- $A <: C \land B <: D \Rightarrow A \amp B <: C \amp D$

More, any type $T$ with supertypes $S_1, S_2, S_3, \ldots, S_N$ is also a subtype of $S_1 \amp S_2 \amp S_3 \amp \ldots \amp S_N$.

#### Subtyping for nullable types

TODO(Why can't we just say that $\forall T : T <: T?$ and $\forall T : T!! <: T$ and be done with it?)

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

TODO(How is generics different from type parameters? Or are we going to get into deep technical detail?)

TODO(Here be a lot of dragons...)

### Upper and lower bounds

A type $U$ is an _upper bound_ of types $A$ and $B$ if $A <: U$ and $B <: U$.
A type $L$ is a _lower bound_ of types $A$ and $B$ if $L <: A$ and $L <: B$.

> Note: as the type system of Kotlin is bounded by definition (the upper bound of all types is $\text{kotlin.Any}?$, and the lower bound of all types is $\text{kotlin.Nothing}$), any two types have at least one lower bound and at least one upper bound.

#### Least upper bound

The _least upper bound_ of types $A$ and $B$ is an upper bound $U$ of $A$ and $B$ such that there is no other upper bound of these types that is less (by subtyping relation) than $U$. Note that among the supertypes of $A$ and $B$ there may be several types that adhere to these properties and are not related by subtyping. In such situation, an intersection of these types is the least upper bound of $A$ and $B$, as, by definition, the intersection $I$ of types $X$ and $Y$ is less than both $X$ and $Y$.

- TODO(but what if there are equivalent types arising?)
- TODO(check this for shady cases)
- TODO(actual algorithm for computing LUB)
- TODO(generics, especially contravariant TP, make the enumeration impossible, but GLB saves the day)
- TODO(LUB for flexible types)

#### Greatest lower bound

The _greatest lower bound_ $GLB(A, B)$ of types $A$ and $B$ is a lower bound $L$ of $A$ and $B$ such that there is no other lower bound of these types which is greater by subtyping relation than $L$.
Enumerating all subtypes of a given type is impossible in general, but in the presense of [intersection types][Intersection types], $GLB(A, B) \equiv A \amp B$.

$GLB(A, B)$ has the following properties, which may be used to *normalize* or *simplify* it.

- it is commutative and associative: $GLB(A, B) = GLB(B, A)$
- it is idempotent: $GLB(A, A) = A$
- if $A <: B$, $GLB(A, B) = A$
- if $A$ is non-nullable, $GLB(A, B)$ is also non-nullable
- if both $A$ and $B$ are nullable, $GLB(A, B) = GLB(A!!, B!!)?$
- if $A$ is nullable and $B$ is not, $GLB(A, B) = GLB(A!!, B)$
- if no other rules apply, $GLB(A, B) = A \amp B$

> Note: these properties follow from the [subtyping relation][Subtyping] and the definition of greatest lower bound.

---
comment: |
    The properties of $GLB$ have several important implications:

    - $\forall A, B, C : C <: (A \times C) <: B \implies C <: (A \times B)$

---

- TODO(relation between LUB and GLB in contravariant cases)
- TODO(again, what to do with equivalent types?)
- TODO(GLB for flexible types)
- TODO(GLB for parameterized types)
- TODO(If $A <: B$ and $B <: A$, what is GLB($A, B)$???)

### References

1. Ross Tate. "Mixed-site variance." FOOL, 2013.
2. Ross Tate, Alan Leung, and Sorin Lerner. "Taming wildcards in Java's type system." PLDI, 2011.

TODO(the big TODO for the whole chapter: we need to clearly decide what kind of type system we want to specify: an algo-driven ts vs a full declarational ts, operation-based or relation-based. An example of the second distinction would be difference between $(A?)!!$ and $((A!!)?)!!$. Are they the same type? Are they different, but equivalent? Same goes for $(A..B)?$ vs $(A?..B?)$ and such.)
