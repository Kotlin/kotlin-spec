## Type system

TODO(Add grammar snippets?)

### Glossary

$T$
~ Type (with unknown nullability)

$T!!$
~ [Non-nullable type][Nullable types]

$T?$
~ [Nullable type][Nullable types]

$\{T\}$
~ Universe of all possible types

$\{T!!\}$
~ Universe of non-nullable types

$\{T?\}$
~ Universe of nullable types

Well-formed type
~ A properly constructed type w.r.t. Kotlin type system

$\Gamma$
~ Type context

$A <: B$
~ A is a subtype of B

$A \sur B$
~ A and B are not related w.r.t. subtyping

Type constructor
~ An abstract type with one or more type parameters, which must be instantiated before use

Parameterized type
~ A concrete type, which is the result of type constructor instantiation

Type parameter
~ Formal type argument of a type constructor

Type argument
~ Actual type argument in a parameterized type

$T\lbrack A_1, \ldots, A_n\rbrack$
~ The result of type constructor $T$ instantiation with type arguments $A_i$

$T\lbrack\sigma\rbrack$
~ The result of type constructor $T(F_1, \ldots, F_n)$ instantiation with the assumed substitution $\sigma : F_1 = A_1, \ldots, F_n = A_n$

$\sigma T$
~ The result of type substitution in type $T$ w.r.t. substitution $\sigma$

$K_T(F, A)$
~ Captured type from the [type capturing][Type capturing] of type parameter $F$ and type argument $A$ in parameterized type $T$

$T\langle K_1, \ldots, K_n\rangle$
~ The result of type capturing for parameterized type $T$ with *captured* types $K_i$

$T\langle \tau \rangle$
~ The result of type capturing for parameterized type $T(F_1, \ldots, F_n)$ with *captured* substitution $\tau : F_1 = K_1, \ldots, F_n = K_n$

$A \amp B$
~ Intersection type of $A$ and $B$

$A \hor B$
~ Union type of $A$ and $B$

$\GLB$
~ Greatest lower bound

$\LUB$
~ Least upper bound

TODO(Not everything is in the glossary, make some criteria of what goes where)

TODO(Cleanup glossary)

### Introduction

Similarly to most other programming languages, Kotlin operates on data in the form of *values* or *objects*, which have *types* --- descriptions of what is the expected behaviour and possible values for their datum.
An empty value is represented by a special `null` object; most operations with it result in runtime [errors or exceptions][Exceptions].

Kotlin has a type system with the following main properties.

* Hybrid static, gradual and flow type checking;
* Null safety;
* No unsafe implicit conversions;
* Unified top and bottom types;
* Nominal subtyping with bounded parametric polymorphism and mixed-site variance.

Type safety (consistency between compile and runtime types) is verified *statically*, at compile time, for the majority of Kotlin types.
However, for better interoperability with platform-dependent code Kotlin also support a variant of *gradual types* in the form of [flexible types][Flexible types].
Even more so, in some cases the compile-time type of a value may *change* depending on the control- and data-flow of the program; a feature usually known as *flow typing*, represented in Kotlin as [smart casts][Smart casts].

Null safety is enforced by having two type universes: _nullable_ (with nullable types $T?$) and _non-nullable_ (with non-nullable types $T!!$).
A value of any non-nullable type cannot contain `null`, meaning all operations within the non-nullable type universe are safe w.r.t. empty values, i.e., should never result in a runtime error caused by `null`.

Implicit conversions between types in Kotlin are limited to safe upcasts w.r.t. subtyping, meaning all other (unsafe) conversions must be explicit, done via either a conversion function or an [explicit cast][Cast expression].
However, Kotlin also supports smart casts --- a special kind of implicit conversions which are safe w.r.t. program control- and data-flow, which are covered in more detail [here][Smart casts].

The unified supertype type for all types in Kotlin is $\AnyQ$, a [nullable][Nullable types] version of [$\Any$][`kotlin.Any`].
The unified subtype type for all types in Kotlin is [$\Nothing$][`kotlin.Nothing`].

Kotlin uses nominal subtyping, meaning subtyping relation is defined when a type is declared, with bounded parametric polymorphism, implemented as generics via [parameterized types][Parameterized classifier types].
Subtyping between these parameterized types is defined through [mixed-site variance][Mixed-site variance].

### Type kinds

For the purposes of this section, we establish the following type kinds --- different flavours of types which exist in the Kotlin type system.

* [Built-in types][Built-in types]
* [Classifier types][Classifier types]
* [Type parameters][Type parameters]
* [Function types][Function types]
* [Array types][Array types]
* [Flexible types][Flexible types]
* [Nullable types][Nullable types]
* [Intersection types][Intersection types]
* [Union types][Union types]

We distinguish between *concrete* and *abstract* types.
Concrete types are types which are assignable to values.
Abstract types need to be instantiated as concrete types before they can be used as types for values.

> Note: for brevity, we omit specifying that a type is concrete.
> All types not described as abstract are implicitly concrete.

We further distinguish *concrete* types between *class* and *interface* types; as Kotlin is a language with single inheritance, sometimes it is important to discriminate between these kinds of types.
Any given concrete type may be either a class or an interface type, but never both.

We also distinguish between *denotable* and *non-denotable* types.
The former are types which are expressible in Kotlin and can be written by the end-user.
The latter are special types which are *not* expressible in Kotlin and are used by the compiler in [type inference][Type inference], [smart casts][Smart casts], etc.

#### Built-in types

Kotlin type system uses the following built-in types, which have special semantics and representation (or lack thereof).

##### `kotlin.Any`

$\Any$ is the unified [supertype][Subtyping] ($\top$) for $\{T!!\}$, i.e., all non-nullable types are subtypes of $\Any$, either explicitly, implicitly, or by [subtyping relation][Subtyping].

> Note: additional details about $\Any$ are available [here][`kotlin.Any`-bi].

[`kotlin.Any`-bi]: #kotlin.any-1

##### `kotlin.Nothing`

$\Nothing$ is the unified [subtype][Subtyping] ($\bot$) for $\{T\}$, i.e., $\Nothing$ is a subtype of all well-formed Kotlin types, including user-defined ones.
This makes it an uninhabited type (as it is impossible for anything to be, for example, a function and an integer at the same time), meaning instances of this type can never exist at runtime; subsequently, there is no way to create an instance of $\Nothing$ in Kotlin.

> Note: additional details about $\Nothing$ are available [here][`kotlin.Nothing`-bi].

[`kotlin.Nothing`-bi]: #kotlin.nothing-1

##### `kotlin.Function`

$\Function(R)$ is the unified supertype of all [function types][Function types].
It is parameterized over function return type `R`.

##### Built-in integer types

Kotlin supports the following signed integer types.

* `kotlin.Int`
* `kotlin.Short`
* `kotlin.Byte`
* `kotlin.Long`

Besides their use as types, integer types are important w.r.t. [integer literal types][Integer literal types].

> Note: additional details about built-in integer types are available [here][Built-in integer types-bi].

[Built-in integer types-bi]: #built-in-integer-types-1

TODO([Kotlin 1.3] Add unsigned types)

##### Array types

Kotlin arrays are represented as a [parameterized type][Parameterized classifier types] $\Array(T)$, where $T$ is the type of the stored elements, which supports `get`/`set` operations.
The $\Array(T)$ type follows the rules of regular type constructors and parameterized types w.r.t. subtyping.

> Note: unlike Java, arrays in Kotlin are declared as invariant. To use them in a co- or contravariant way, one should use [use-site variance][Use-site variance].

In addition to the general $\Array(T)$ type, Kotlin also has the following specialized array types:

* `DoubleArray` (for $\Array(\Double)$)
* `FloatArray` (for $\Array(\Float)$)
* `LongArray` (for $\Array(\Long)$)
* `IntArray` (for $\Array(\Int)$)
* `ShortArray` (for $\Array(\Short)$)
* `ByteArray` (for $\Array(\Byte)$)
* `CharArray` (for $\Array(\Char)$)
* `BooleanArray` (for $\Array(\Boolean)$)

These array types structurally match the corresponding $\Array(T)$ type; i.e., `IntArray` has the same methods and properties as $\Array(\Int)$.
However, they are **not** related by subtyping; meaning one cannot pass a `BooleanArray` argument to a function expecting an $\Array(\Boolean)$.

> Note: the presence of such specialized types allows the compiler to perform additional array-related optimizations.

> Note: specialized and non-specialized array types match modulo their iterator types, which are also specialized; `Iterator<Int>` is specialized to `IntIterator`.

*Array type specialization* $\ATS(T)$ is a transformation of a generic $\Array(T)$ type to a corresponding specialized version, which works as follows.

* if $\Array(T)$ has a specialized version `TArray`, $\ATS(\Array(T)) = \texttt{TArray}$
* if $\Array(T)$ does not have a specialized version, $\ATS(\Array(T)) = \Array(T)$

$\ATS$ takes an important part in how [variable length parameters][Variable length parameters] are handled.

#### Classifier types

Classifier types represent regular types which are declared as [classes][Class declaration], [interfaces][Interface declaration] or [objects][Object declaration].
As Kotlin supports parametric polymorphism, there are two variants of classifier types: simple and parameterized.

##### Simple classifier types

A simple classifier type

$$T : S_1, \ldots, S_m$$

consists of

* type name $T$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a well-formed simple classifier type, $T : S_1, \ldots, S_m$ should satisfy the following conditions.

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
> * class `Int` $<:$ `Number`
> * class `Double` $<:$ `Number`

> Note: `Number` is actually a built-in abstract class; we use it as an interface for illustrative purposes.

TODO(We need to forbid situations like this:
     Inv<T> + A : Inv<String> + B : A, Inv<Int>)

##### Parameterized classifier types

A classifier type constructor

$$T(F_1, \ldots, F_n) : S_1, \ldots, S_m$$

describes an abstract type and consists of

* type name $T$
* type parameters $F_1, \ldots, F_n$
* (optional) list of supertypes $S_1, \ldots, S_m$

To represent a well-formed type constructor, $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$ should satisfy the following conditions.

* $T$ is a valid type name
* $\forall i \in [1,n]: F_i$ must be well-formed [type parameter][Type parameters]
* $\forall j \in [1,m]: S_j$ must be concrete, [non-nullable][Nullable types], well-formed type

To instantiate a type constructor, one provides it with type arguments, creating a concrete parameterized classifier type

$$T[A_1, \ldots, A_n]$$

which consists of

* type constructor $T$
* type arguments $A_1, \ldots, A_n$

To represent a well-formed parameterized type, $T[A_1, \ldots, A_n]$ should satisfy the following conditions.

* $T$ is a well-formed type constructor with $n$ type parameters
* $\forall i \in [1,n]: A_i$ must be well-formed concrete type
* $\forall i \in [1,n]:$ variance of $A_i$ does not [contradict][Use-site variance] variance of $F_i$
* $\forall i \in [1,n]: A_i <: \tau U_i$, where $U_i$ is the upper bound for $F_i$ and captured substitution $\tau : F_1 = K_1, \ldots, F_n = K_n$ manipulates [captured types][Type capturing].

> Example:
> 
> ```kotlin
> // A well-formed type constructor with no supertypes
> // A and B are unbounded type parameters
> interface Generic<A, B>
> 
> // A well-formed type constructor
> //   with a single parameterized supertype
> // Int and String are well-formed concrete types
> interface ConcreteDerived<P, Q> : Generic<Int, String>
> 
> // A well-formed type constructor
> //   with a single parameterized supertype
> // P and Q are type parameters of GenericDerived,
> //   used as type arguments of Generic
> interface GenericDerived<P, Q> : Generic<P, Q>
> 
> // An ill-formed type constructor,
> //   as abstract type Generic
> //   cannot be used as a supertype
> interface Invalid<P> : Generic
> 
> 
> // A well-formed type constructor with no supertypes
> // out A is a projected type parameter
> interface Out<out A>
> 
> 
> // A well-formed type constructor with no supertypes
> // S : Number is a bounded type parameter
> // (S <: Number)
> interface NumberWrapper<S : Number>
> 
> // A well-formed type constructor
> //   with a single parameterized supertype
> // NumberWrapper<Int> is well-formed,
> //   as Int <: Number
> interface IntWrapper : NumberWrapper<Int>
> 
> // An ill-formed type constructor,
> //   as NumberWrapper<String> is an ill-formed parameterized type
> //   (String not(<:>) Number)
> interface InvalidWrapper : NumberWrapper<String>
>```

#### Type parameters

Type parameters are a special kind of types, which are introduced by type constructors.
They are considered well-formed concrete types only in the type context of their declaring type constructor.

When creating a parameterized type from a type constructor, its type parameters with their respective type arguments go through [capturing][Type capturing] and create *captured* types, which follow special rules described in more detail below.

Type parameters may be either unbounded or bounded.
By default, a type parameter $F$ is unbounded, which is the same as saying it is a bounded type parameter of the form $F <: \AnyQ$.

A bounded type parameter additionally specifies upper type bounds for the type parameter and is defined as $F <: B_1, \ldots, B_n$, where $B_i$ is an i-th upper bound on type parameter $F$.

To represent a well-formed bounded type parameter of type constructor $T$, $F <: B_1, \ldots, B_n$ should satisfy either of the following sets of conditions.

* Bounded type parameter with regular bounds:
    * $F$ is a type parameter of type constructor $T$
    * $\forall i \in [1,n]: B_i$ must be concrete, non-type-parameter, well-formed type
    * No more than one of $B_i$ may be a class type

> Note: the last condition is a nod to the single inheritance nature of Kotlin: any type may be a subtype of no more than one class type.
> For any two class types, either these types are in a subtyping relation (and you should use the more specific type in the bounded type parameter), or they are unrelated (and the bounded type parameter is empty).
> 
> Actual support for multiple class type bounds would be needed only in very rare cases, such as the following example.
> 
> ```kotlin
> interface Foo
> interface Bar
> 
> open class A<T>
> class B<T> : A<T>
> 
> class C<T> where T : A<out Foo>, T : B<out Bar>
> // A convoluted way of saying T <: B<out Foo & Bar>,
> // which contains a non-denotable intersection type
> ```

* Bounded type parameter with type parameter bound:
    * $F$ is a type parameter of type constructor $T$
    * $i = 1$ (i.e., there is a single upper bound)
    * $B_1$ must be well-formed [type parameter][Type parameters]

From the definition, it follows $F <: B_1, \ldots, B_n$ can be represented as $F <: U$ where $U = B_1 \amp \ldots \amp B_n$ (aka [intersection type][Intersection types]).

##### Function type parameters

Function type parameters are a flavor of type parameters, which are used in [function declarations][Function declaration] to create parameterized functions.
They are considered well-formed concrete types only in the type context of their declaring function.

> Note: one may view such parameterized functions as a kind of function type constructors.

Function type parameters work similarly to regular type parameters, however, they have the following limitations.

* Function type parameters does not support specifying [mixed-site variance][Mixed-site variance].
* TODO(Anything else?)

##### Mixed-site variance

To implement subtyping between parameterized types, Kotlin uses *mixed-site variance* --- a combination of declaration- and use-site variance, which is easier to understand and reason about, compared to wildcards from Java.
Mixed-site variance means you can specify, whether you want your parameterized type to be co-, contra- or invariant on some type parameter, both in type parameter (declaration-site) and type argument (use-site).

> Info: *variance* is a way of describing how [subtyping][Subtyping] works for *variant* parameterized types.
> With declaration-site variance, for two [non-equivalent][Subtyping] types $A <: B$, subtyping between `T<A>` and `T<B>` depends on the variance of type parameter $F$ for some type constructor $T$.
> 
> * if $F$ is covariant ($\outV F$), `T<A>` $<:$ `T<B>`
> * if $F$ is contravariant($\inV F$), `T<A>` $:>$ `T<B>`
> * if $F$ is invariant (default), `T<A>` $\sur$ `T<B>`
> 
> Use-site variance allows the user to change the type variance of an *invariant* type parameter by specifying it on the corresponding type argument.
> $\outV A$ means covariant type argument, $\inV A$ means contravariant type argument; for two [non-equivalent][Subtyping] types $A <: B$ and an invariant type parameter $F$ of some type constructor $T$, subtyping for use-site variance has the following rules.
> 
> * `T<out A>` $<:$ `T<out B>`
> * `T<in A>` $:>$ `T<in B>`
> * `T<A>` $<:$ `T<out A>`
> * `T<A>` $<:$ `T<in A>`

> Note: Kotlin does not support specifying both co- and contravariance at the same time, i.e., it is impossible to have `T<out A in B>` neither on declaration- nor on use-site.

> Note: informally, covariant type parameter $\outV A$ of type constructor $T$ means "$T$ is a producer of $A$s and gets them out"; contravariant type parameter $\inV A$ of type constructor $T$ means "$T$ is a consumer of $A$s and takes them in".

For further discussion about mixed-site variance and its practical applications, we readdress you to [subtyping][Subtyping].

##### Declaration-site variance

A type parameter $F$ may be invariant, covariant or contravariant.

By default, all type parameters are invariant.

To specify a covariant type parameter, it is marked as $\outV F$.
To specify a contravariant type parameter, it is marked as $\inV F$.

The variance information is used by [subtyping][Subtyping] and for checking allowed operations on values of co- and contravariant type parameters.

> Important: declaration-site variance can be used only when declaring types, e.g., [function type parameters][Function type parameters] cannot be variant.

> Example:
> 
> ```kotlin
> // A type constructor with an invariant type parameter
> interface Invariant<A>
> // A type constructor with a covariant type parameter
> interface Out<out A>
> // A type constructor with a contravariant type parameter
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

##### Use-site variance

Kotlin also supports use-site variance, by specifying the variance for type arguments.
Similarly to type parameters, one can have type arguments being co-, contra- or invariant.

> Important: use-site variance cannot be used when declaring a supertype top-level type argument.

By default, all type arguments are invariant.

To specify a covariant type argument, it is marked as $\outV A$.
To specify a contravariant type argument, it is marked as $\inV A$.

Kotlin prohibits contradictory combinations of declaration- and use-site variance as follows.

* It is a compile-time error to use a covariant type argument in a contravariant type parameter
* It is a compile-time error to use a contravariant type argument in a covariant type parameter

In case one cannot specify any well-formed type argument, but still needs to use a parameterized type in a type-safe way, they may use *bivariant* type argument $\star$, which is roughly equivalent to a combination of $\outV \AnyQ$ and $\inV \Nothing$ (for further details, see [subtyping][Subtyping]).

> Note: informally, $T[\star]$ means "I can give out something very generic ($\AnyQ$) and cannot take in anything".

> Example:
> ```kotlin
> // A type constructor with an invariant type parameter
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
>             //   T<in Int> not(<:>) T<out Int>
>         }
>     }
> }
> ```

#### Type capturing

Type capturing (similarly to Java capture conversion) is used when instantiating type constructors; it creates *abstract captured* types based on the type information of both type parameters and arguments, which present a unified view on the resulting types and simplifies further reasoning.

The reasoning behind type capturing is closely related to variant parameterized types being a form of *bounded existential types*; e.g., `A<out T>` may be loosely considered as the following existential type: $\exists X : X <: T . A\langle X\rangle$.
Informally, a bounded existential type describes a *set* of possible types, which satisfy its bound constraints.
Before such a type can be used, it needs to be *opened* (or *unpacked*): existentially quantified type variables are lifted to fresh type variables with corresponding bounds.
We call these type variables *captured* types.

For a given type constructor $T(F_1, \ldots, F_n) : S_1, \ldots, S_m$, its instance $T[\sigma] = T\langle \tau \rangle$ uses the following rules to create captured type $K_i$ from the type parameter $F_i$ and type argument $A_i$, at least one of which should have specified variance to create a captured type.
In case both type parameter and type argument are invariant, their captured type is *equivalent* to $A_i$.

> Important: type capturing is **not** recursive.

> Note: **All** applicable rules are used to create the resulting constraint set.

* For a covariant type parameter $\outV  F_i$, if $A_i$ is an ill-formed type or a contravariant type argument, $K_i$ is an ill-formed type.
  Otherwise, $K_i <: A_i$.
* For a contravariant type parameter $\inV F_i$, if $A_i$ is an ill-formed type or a covariant type argument, $K_i$ is an ill-formed type.
  Otherwise, $K_i :> A_i$.
* For a bounded type parameter $F_i <: U_i \equiv B_1 \amp \ldots \amp B_m$, if $\neg (A_i <: \tau U_i)$, $K_i$ is an ill-formed type.
  Otherwise, $K_i <: \tau U_i$.

    > Note: captured substitution $\tau : F_1 = K_1, \ldots, F_n = K_n$ manipulates captured types.

* For a covariant type argument $\outV A_i$, if $F_i$ is a contravariant type parameter, $K_i$ is an ill-formed type.
  Otherwise, $K_i <: A_i$.
* For a contravariant type argument $\inV A_i$, if $F_i$ is a covariant type parameter, $K_i$ is an ill-formed type.
  Otherwise, $K_i :> A_i$.

* For a bivariant type argument $\star$, $\Nothing <: K_i <: \AnyQ$.

* Otherwise, $K_i \equiv A_i$.

By construction, every captured type $K$ has the following form:

$$\{L_1 <: K, \ldots, L_p <: K, K <: U_1, \ldots, K <: U_q\}$$

which can be represented as

$$L <: K <: U$$

where $L = L_1 \hor \ldots \hor L_p$ and $U = U_1 \amp \ldots \amp U_q$.

> Note: for implementation reasons the compiler may [approximate][Type approximation] $L$ and/or $U$; for example, in the current implementation $L$ is always approximated to be a single type.

> Note: as every captured type corresponds to a fresh type variable, two different captured types $K_i$ and $K_j$ which describe the same set of possible types (i.e., their constraint sets are equal) are *not* considered equal.
> However, in some cases [type inference][Type inference] may [approximate][Type approximation] a captured type $K$ to a concrete type $K^{\approx}$; in our case, it would be that $K_i^{\approx} \equiv K_j^{\approx}$.

TODO(Need to think more about this part)

#### Type containment

Type containment operator $\preceq$ is used to decide, whether a type $A$ is contained in another type $B$ denoted $A \preceq B$, for the purposes of establishing type argument [subtyping][Subtyping].

Let $A, B$ be concrete, well-defined non-type-parameter types, $K_A, K_B$ be captured types.

> Important: type parameters $F_i <: U_i$ are handled as if they have been converted to well-formed captured types $K_i : \Nothing <: K_i <: U_i$.

$\preceq$ is defined as follows.

* $A \preceq B$ if $A \equiv B$
* $A \preceq \outV B$ if $A <: B$
* $A \preceq \inV B$ if $A :> B$

* $\outV A \preceq \outV B$ if $A <: B$
* $\inV A \preceq \inV B$ if $A :> B$

Rules for captured types follow the same structure.

* $K_A \preceq K_B$ if $K_A \equiv K_B$
* $K_A \preceq \outV K_B$ if $K_A <: K_B$
* $K_A \preceq \inV K_B$ if $K_A :> K_B$

* $\outV K_A \preceq \outV K_B$ if $K_A <: K_B$
* $\inV K_A \preceq \inV K_B$ if $K_A :> K_B$

In case we need to establish type containment between regular type $A$ and captured type $K_B$, $A$ is converted to captured type $K_A : A <: K_A <: A$.

#### Function types

Kotlin has first-order functions; e.g., it supports function types, which describe the argument and return types of its corresponding function.

A function type $\FT$

$$\FT(A_1, \ldots, A_n) \rightarrow R$$

consists of

* argument types $A_i$
* return type $R$

and may be considered the following instantiation of a special type constructor $\FunctionN(\inV P_1, \ldots, \inV P_n, \outV R)$

$$\FT(A_1, \ldots, A_n) \rightarrow R \equiv \FunctionN[A_1, \ldots, A_n, R]$$

These $\FunctionN$ types follow the rules of regular type constructors and parameterized types w.r.t. subtyping.

A function type with receiver $\FTR$

$$\FTR(\RT, A_1, \ldots, A_n) \rightarrow R$$

consists of

* receiver type $\RT$
* argument types $A_i$
* return type $R$

From the type system's point of view, it is equivalent to the following function type

$$\FTR(\RT, A_1, \ldots, A_n) \rightarrow R \equiv \FT(\RT, A_1, \ldots, A_n) \rightarrow R$$

i.e., receiver is considered as yet another argument of its function type.

> Note: this means that, for example, these two types are equivalent w.r.t. type system
>
> * `Int.(Int) -> String`
> * `(Int, Int) -> String`
>
> However, these two types are **not** equivalent w.r.t. [overload resolution][Overload resolution], as it distinguishes between functions with and without receiver.

TODO(Specify other cases when these two types are **not** equivalent)

Furthermore, all function types $\FunctionN$ are subtypes of a general argument-agnostic type [$\Function$][`kotlin.Function`] for the purpose of unification; this subtyping relation is also used in [overload resolution][Determining function applicability for a specific call].

> Note: a compiler implementation may consider a function type $\FunctionN$ to have additional supertypes, if it is necessary.

> Example:
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

#### Flexible types

Kotlin, being a multi-platform language, needs to support transparent interoperability with platform-dependent code.
However, this presents a problem in that some platforms may not support null safety the way Kotlin does.
To deal with this, Kotlin supports *gradual typing* in the form of flexible types.

A flexible type represents a range of possible types between type $L$ (lower bound) and type $U$ (upper bound), written as $(L..U)$.
One should note flexible types are *non-denotable*, i.e., one cannot explicitly declare a variable with flexible type, these types are created by the type system when needed.

To represent a well-formed flexible type, $(L..U)$ should satisfy the following conditions.

* $L$ and $U$ are well-formed concrete types
* $L <: U$
* $L$ and $U$ are **not** flexible types (but may contain other flexible types as some of their type arguments)

As the name suggests, flexible types are flexible --- a value of type $(L..U)$ can be used in any context, where one of the possible types between $L$ and $U$ is needed (for more details, see [subtyping rules for flexible types][Subtyping for flexible types]).
However, the actual runtime type $T$ will be a specific type satisfying $\exists S : T <: S \land L <: S <: U$, thus making the substitution possibly unsafe, which is why Kotlin generates dynamic assertions, when it is impossible to prove statically the safety of flexible type use.

TODO([Kotlin.*] Details of assertion generation?)

##### Dynamic type

Kotlin includes a special `dynamic` type, which in many contexts can be viewed as a flexible type $(\Nothing..\AnyQ)$.
By definition, this type represents *any* possible Kotlin type, and may be used to support interoperability with dynamically typed libraries, platforms or languages.

However, as a platform may assign special meaning to the values of `dynamic` type, it may be handled differently from the regular flexible type.
These differences are to be explained in the corresponding platform-dependent sections of this specification.

##### Platform types

The main use cases for flexible types are *platform types* --- types which the Kotlin compiler uses, when interoperating with code written for another platform (e.g., Java).
In this case all types on the interoperability boundary are subject to *flexibilization* --- the process of converting a platform-specific type to a Kotlin-compatible flexible type.

For further details on how *flexibilization* is done, see:

* [Platform types for Java][TODO(need a way to have same section names in different parts of the spec)]

> Important: platform types should not be confused with *multi-platform projects* --- another Kotlin feature targeted at supporting platform interop.

#### Nullable types

Kotlin supports null safety by having two type universes --- nullable and non-nullable.
All classifier type declarations, built-in or user-defined, create non-nullable types, i.e., types which cannot hold `null` value at runtime.

To specify a nullable version of type $T$, one needs to use $T?$ as a type.
Redundant nullability specifiers are ignored --- $T?? \equiv T?$.

> Note: informally, question mark means "$T?$ may hold values of type $T$ or value `null`"

To represent a well-formed nullable type, $T?$ should satisfy the following conditions.

* $T$ is a well-formed concrete type

> Note: if an operation is safe regardless of absence or presence of `null`, e.g., assignment of one nullable value to another, it can be used as-is for nullable types.
> For operations on $T?$ which may violate null safety, e.g., access to a property, one has the following null-safe options:
> 
> 1. Use safe operations
>     * [safe call][Navigation operators]
> 2. Downcast from $T?$ to $T!!$
>     * [unsafe cast][Cast expression]
>     * [type check][Type-checking expression] combined with [smart casts][Smart casts]
>     * null check combined with [smart casts][Smart casts]
>     * [not-null assertion operator][Not-null assertion expression]
> 3. Supply a default value to use if `null` is present
>     * [elvis operator][Elvis operator expression]

#### Intersection types

Intersection types are special *non-denotable* types used to express the fact that a value belongs to *all* of *several* types at the same time.

Intersection type of two types $A$ and $B$ is denoted $A \amp B$ and is equivalent to the [greatest lower bound][Greatest lower bound] of its components $\GLB(A, B)$.
Thus, the normalization procedure for $\GLB$ may be used to *normalize* an intersection type.

> Note: this means intersection types are commutative and associative (following the GLB properties); e.g., $A \amp B$ is the same type as $B \amp A$, and $A \amp (B \amp C)$ is the same type as $A \amp B \amp C$.

> Note: for presentation purposes, we will henceforth order intersection type operands lexicographically based on their notation.

When needed, the compiler may *approximate* an intersection type to a *denotable concrete* type using [type approximation][Type approximation].

One of the main uses of intersection types are [smart casts][Smart casts].

#### Integer literal types

An integer literal type containing types $T_1, \ldots, T_N$, denoted $\LTS(T_1, \ldots, T_N)$ is a special *non-denotable* type designed for integer literals.
Each type $T_1, \ldots, T_N$ must be one of the [built-in integer types][Built-in integer types].

Integer literal types are the types of [integer literals][Integer literals] and have special handling w.r.t. [subtyping][Subtyping for integer literal types].

#### Union types

> Important: Kotlin does **not** have union types in its type system.
> However, they make reasoning about several type system features easier.
> Therefore, we decided to include a brief intro to the union types here.

Union types are special *non-denotable* types used to express the fact that a value belongs to *one* of *several* possible types.

Union type of two types $A$ and $B$ is denoted $A \hor B$ and is equivalent to the [least upper bound][Least upper bound] of its components $\LUB(A, B)$.
Thus, the normalization procedure for $\LUB$ may be used to *normalize* a union type.

Moreover, as union types are *not* used in Kotlin, the compiler always *decays* a union type to a *non-union* type using [type decaying][Type decaying].

### Type contexts and scopes

The way types and [scopes][Scopes and identifiers] interoperate is very similar to how values and scopes work; this includes [visibility][Visibility], accessing types via qualified names or [imports][Importing].
This means, in many cases, type contexts are equivalent to the corresponding scopes.
However, there are several important differences, which we outline below.

#### Inner and nested type contexts

[Type parameters][Type parameters] are well-formed types in the type context (scope) of their declaring type constructor, including inner type declarations.
However, type context for a [*nested* type declaration][Nested and inner classifiers] $\ND$ of a parent type declaration $\PD$ does **not** include the type parameters of $\PD$.

> Note: nested type declarations cannot capture parent type parameters, as  they simply create a regular type available under a nested path.

> Example:
> ```kotlin
> class Parent<T> {
>     class Nested(val i: Int)
> 
>     // Can use type parameter T as a type
>     // in an inner class
>     inner class Inner(val t: T)
> 
>     // Cannot use type parameter T as a type
>     // in a nested class
>     class Error(val t: T)
> }
> 
> fun main() {
>     val nested = Parent.Nested(42)
> 
>     val inner = Parent<String>().Inner("42")
> }
> ```

### Subtyping

Kotlin uses the classic notion of *subtyping* as *substitutability* --- if $S$ is a subtype of $T$ (denoted as $S <: T$), values of type $S$ can be safely used where values of type $T$ are expected.
The subtyping relation $<:$ is:

* reflexive ($A <: A$)
* *rigidly* transitive ($A <: B \land B <: C \Rightarrow A <: C$ for non-flexible types $A$, $B$ and $C$)

Two types $A$ and $B$ are *equivalent* ($A \equiv B$), iff $A <: B \land B <: A$.
Due to the presence of flexible types, this relation is also only *rigidly* transitive, e.g., holds only for non-flexible types (see [here][Subtyping for flexible types] for more details).

#### Subtyping rules

Subtyping for non-nullable, concrete types uses the following rules.

* $\forall T : \Nothing <: T <: \Any$
* For any simple classifier type $T : S_1, \ldots, S_m$ it is true that $\forall i \in [1,m]: T <: S_i$
* For any parameterized type $\widehat{T} = T\langle \tau \rangle: S_1, \ldots, S_m$ it is true that $\forall i \in [1,m]: \widehat{T} <: \tau S_i$
* For any two parameterized types $\widehat{T} = T\langle \tau \rangle$ and $\widehat{T^\prime} = T\langle \tau^\prime \rangle$ with captured type arguments $K_i$ and $K_i^\prime$ it is true that $\widehat{T} <: \widehat{T^\prime}$ if $\forall i \in [1,n]: K_i \preceq K_i^\prime$

Subtyping for captured types uses the following rules.

* $\forall K : \Nothing <: K <: \AnyQ$
* For any two captured types $L <: K <: U$ and $L^\prime <: K^\prime <: U^\prime$, it is true that $K <: K^\prime$ if $U <: L^\prime$

Subtyping for nullable types is checked separately and uses a special set of rules which are described [here][Subtyping for nullable types].

#### Subtyping for flexible types

Flexible types (being flexible) follow a simple subtyping relation with other rigid (i.e., non-flexible) types.
Let $T, A, B, L, U$ be rigid types.

* $L <: T \Rightarrow (L..U) <: T$
* $T <: U \Rightarrow T <: (L..U)$

This captures the notion of flexible type $(L..U)$ as something which may be used in place of any type in between $L$ and $U$.
If we are to extend this idea to subtyping between *two* flexible types, we get the following definition.

* $L <: B \Rightarrow (L..U) <: (A..B)$

This is the most extensive definition possible, which, unfortunately, makes the type equivalence relation non-transitive.
Let $A, B$ be two *different* types, for which $A <: B$.
The following relations hold:

* $A <: (A..B) \land (A..B) <: A \Rightarrow A \equiv (A..B)$
* $B <: (A..B) \land (A..B) <: B \Rightarrow B \equiv (A..B)$

However, $A \not \equiv B$.

#### Subtyping for intersection types

Intersection types introduce several new rules for subtyping.
Let $A, B, C, D$ be non-nullable types.

- $A \amp B <: A$
- $A \amp B <: B$
- $A <: C \land B <: D \Rightarrow A \amp B <: C \amp D$

Moreover, any type $T$ with supertypes $S_1, \ldots, S_N$ is also a subtype of $S_1 \amp \ldots \amp S_N$.

#### Subtyping for integer literal types

All integer literal type are equivalent w.r.t. subtyping, meaning that for any sets $T_1, \ldots, T_K$ and $U_1, \ldots, U_N$ of built-in integer types:

- $\LTS(T_1, \ldots, T_K) <: \LTS(U_1, \ldots, U_N)$
- $\LTS(U_1, \ldots, U_N) <: \LTS(T_1, \ldots, T_K)$
- $\forall T_i \in \{T_1, \ldots, T_K\} : \LTS(T_1, \ldots, T_K) <: T_i$
- $\forall T_i \in \{T_1, \ldots, T_K\} : T_i <: \LTS(T_1, \ldots, T_K)$

> Note: the last two rules mean $\LTS(T_1, \ldots, T_K)$ can be considered as an intersection type $T_1 \amp \ldots \amp T_K$ or as a union type $T_1 \hor \ldots \hor T_K$, depending on the context.
> Viewing $\LTS$ as intersection type allows us to use integer literals where built-in integer types are expected.
> Making $\LTS$ behave as union type is needed to support cases when they appear in contravariant position.

> Example:
> ```kotlin
> interface In<in T>
> 
> fun <T> T.asIn(): In<T> = ...
>
> fun <S> select(a: S, b: In<S>): S = ...
> 
> fun ltsAsIntersection() {
>     val a: Int = 42 // LTS(Byte, Short, Int, Long) <: Int
>     
>     fun foo(a: Short) {}
>     
>     foo(1377) // LTS(Short, Int, Long) <: Short
> }
> 
> fun ltsAsUnion() {
>     val a: Short = 42
>     
>     select(a, 1337.asIn())
>         // For argument a:
>         //   Short <: S
>         // For argument b:
>         //   In<LTS(Short, Int, Long)> <: In<S> =>
>         //     S <: LTS(Short, Int, Long)
>         // Solution: S =:= Short
> }
> ```

#### Subtyping for nullable types

Subtyping for two possibly nullable types $A$ and $B$ is defined via *two* relations, both of which must hold.

* Regular subtyping $<:$ for non-nullable types $A!!$ and $B!!$
* Subtyping by nullability $\sbn$

Subtyping by nullability $\sbn$ for two possibly nullable types $A$ and $B$ uses the following rules.

* $A!! \sbn B$
* $A \sbn B$ if $\exists T!! : A <: T!!$
* $A \sbn B?$
* $A \sbn B$ if $\nexists T!! : B <: T!!$

> Example:
> ```diagram
>
>     A    B?   C!!          A
>     |    |     |           |
>     |    +--+--+    -->    |
>     |       |              |
>     v       v              v
>     B       T              T
>
> ```
> This example shows a situation, when the subtyping by nullability relation from $T <: C!!$ is used to prove $T <: A$.

### Upper and lower bounds

A type $U$ is an _upper bound_ of types $A$ and $B$ if $A <: U$ and $B <: U$.
A type $L$ is a _lower bound_ of types $A$ and $B$ if $L <: A$ and $L <: B$.

> Note: as the type system of Kotlin is bounded by definition (the upper bound of all types is $\AnyQ$, and the lower bound of all types is $\Nothing$), any two types have at least one lower bound and at least one upper bound.

#### Least upper bound

The _least upper bound_ $\LUB(A, B)$ of types $A$ and $B$ is an upper bound $U$ of $A$ and $B$ such that there is no other upper bound of these types which is less by subtyping relation than $U$.

> Note: $\LUB$ is commutative, i.e., $\LUB(A, B) = \LUB(B, A)$.
> This property is used in the subsequent description, e.g., other properties of $\LUB$ are defined only for a specific order of the arguments.
> Definitions following from commutativity of $\LUB$ are implied.

$\LUB(A, B)$ has the following properties, which may be used to *normalize* it.
This normalization procedure, if finite, creates a *canonical* representation of LUB.

> Important: $A$ and $B$ are considered to be non-flexible, unless specified otherwise.

- $\LUB(A, A) = A$

- if $A <: B$, $\LUB(A, B) = B$

- if $A$ is nullable, $\LUB(A, B) = \LUB(A!!, B!!)?$

- if $A = T\langle K_{A,1}, \ldots, K_{A,n}\rangle$ and $B = T\langle K_{B,1}, \ldots, K_{B,n}\rangle$, $\LUB(A, B) = T\langle \phi(\eta(K_{A,1}), \eta(K_{B,1})), \ldots, \phi(\eta(K_{A,n}), \eta(K_{B,n}))\rangle$, where $\eta(T)$ and $\phi(X, Y)$ are defined as follows:

    $$
    \begin{aligned}
        \eta(\invV X) &= \{\outV X, \inV X\} \\
        \eta(\outV X) &= \{\outV X, \inV \Nothing\} \\
        \eta(\inV X)  &= \{\outV \AnyQ, \inV X\} \\
        \eta(\star)   &= \{\outV \AnyQ, \inV \Nothing\}
    \end{aligned}
    $$
    
    $$
    \begin{aligned}
        &\phi(\{\outV X_{out}, \inV X_{in}\}, \{\outV Y_{out}, \inV Y_{in}\}) = \\
        & \qquad \eta^{-1} (\{\outV \LUB(X_{out}, Y_{out}), \inV \GLB(X_{in}, Y_{in})\})
    \end{aligned}
    $$

- if $A = (L_A..U_A)$ and $B = (L_B..U_B)$, $\LUB(A, B) = (\LUB(L_A, L_B)..\LUB(U_A, U_B))$
- if $A = (L_A..U_A)$ and $B$ is not flexible, $\LUB(A, B) = (\LUB(L_A, B)..\LUB(U_A, B))$

TODO(actual algorithm for computing LUB)

TODO(LUB for 3+ types)

TODO(what do we do if this procedure loops?)

#### Greatest lower bound

The _greatest lower bound_ $\GLB(A, B)$ of types $A$ and $B$ is a lower bound $L$ of $A$ and $B$ such that there is no other lower bound of these types which is greater by subtyping relation than $L$.

> Note: $\GLB$ is commutative, i.e., $\GLB(A, B) = \GLB(B, A)$.
> This property is used in the subsequent description, e.g., other properties of $\GLB$ are defined only for a specific order of the arguments.
> Definitions following from commutativity of $\GLB$ are implied.

$\GLB(A, B)$ has the following properties, which may be used to *normalize* it.
This normalization procedure, if finite, creates a *canonical* representation of GLB.

> Important: $A$ and $B$ are considered to be non-flexible, unless specified otherwise.

- $\GLB(A, A) = A$

- if $A <: B$, $\GLB(A, B) = A$

- if $A$ is non-nullable, $\GLB(A, B) = \GLB(A!!, B!!)$

- if $A = T\langle K_{A,1}, \ldots, K_{A,n}\rangle$ and $B = T\langle K_{B,1}, \ldots, K_{B,n}\rangle$, $\GLB(A, B) = T\langle \phi(\eta(K_{A,1}), \eta(K_{B,1})), \ldots, \phi(\eta(K_{A,n}), \eta(K_{B,n}))\rangle$, where $\eta(T)$ and $\phi(X, Y)$ are defined as follows:
    
    $$
    \begin{aligned}
        \eta(\invV X) &= \{\outV X, \inV X\} \\
        \eta(\outV X) &= \{\outV X, \inV \Nothing\} \\
        \eta(\inV X)  &= \{\outV \AnyQ, \inV X\} \\
        \eta(\star)   &= \{\outV \AnyQ, \inV \Nothing\} 
    \end{aligned}
    $$
    
    $$
    \begin{aligned}
       & {\phi(\{\outV X_{out}, \inV X_{in}\}, \{\outV Y_{out}, \inV Y_{in}\}) = } \\
       & \qquad {(\eta^{-1} \circ \Omega)} (\{\outV \GLB(X_{out}, Y_{out}), \inV \LUB(X_{in}, Y_{in})\}) \\
       & {\Omega(\{\outV A, \inV B\}) = } \\
       & \qquad
         \begin{cases}
            \{\outV A, \inV B\}        & \text{if } A :> B \\
            \{\outV A, \inV \Nothing\} & \text{if } A <: B \land A \not \equiv B
         \end{cases}
    \end{aligned}
    $$
    
> Note: the $\Omega$ function preserves type system consistency; $\forall A, B : A <: B \land A \not\equiv B$, type $T\langle \{\outV A, \inV B\}\rangle$ is the evidence of type $T\langle X\rangle : X <: A <: B <: X$, which makes the type system inconsistent.
> To avoid this situation, we overapproximate $\inV B$ with $\inV \Nothing$ when needed.
> Further details are available in the ["Mixed-site variance" paper][References].

- if $A = (L_A..U_A)$ and $B = (L_B..U_B)$, $\GLB(A, B) = (\GLB(L_A, L_B)..\GLB(U_A, U_B))$
- if $A = (L_A..U_A)$ and $B$ is not flexible, $\GLB(A, B) = (\GLB(L_A, B)..\GLB(U_A, B))$

TODO(actual algorithm for computing GLB)

TODO(GLB for 3+ types)

TODO(what do we do if this procedure loops?)

### Type approximation

As we mentioned [before][Type kinds], Kotlin type system has denotable and non-denotable types.
In many cases, we need to *approximate* a non-denotable type, which appeared, for example, during type inference, into a denotable type, so that it can be used in the program.
This is achieved via *type approximation*, which we describe below.

> Important: at the moment, type approximation is applied only to [intersection][Intersection types] and [union][Union types] types.

Type approximation function $\alpha$ is defined as follows.

* $\alpha(A\langle \tau_A \rangle \amp B\langle \tau_B \rangle) = (\alpha {\downarrow} \circ \GLB)(S\langle \tau_{A \rightarrow S} \rangle, S\langle \tau_{B \rightarrow S} \rangle)$, where type $S$ is the least common supertype of $A$ and $B$, substitution $\tau_{P \rightarrow Q}$ is the result of chain applying substitutions from type $P$ to type $Q :> P$, $\alpha {\downarrow}$ is a function which applies type approximation function to the type arguments if needed;
* $\alpha(A\langle \tau_A \rangle \hor B\langle \tau_B \rangle) = \alpha(\delta(A\langle \tau_A \rangle \hor B\langle \tau_B \rangle))$, where $\delta$ is the [type decaying][Type decaying] function.

TODO(Type approximation for captured types)

### Type decaying

All [union types][Union types] are subject to *type decaying*, when they are converted to a specific [intersection type][Intersection types], representable within Kotlin type system.

> Important: at the moment, type decaying is applied only to [union][Union types] types.
> Note: type decaying is comparable to how *least upper bound* computation works in Java.

Type decaying function $\delta$ is defined as follows.

* $\delta(A\langle \tau_A \rangle \hor B\langle \tau_B \rangle) = \amp_{S \in \mathbb{S}(A, B)} (\delta {\downarrow} \circ \GLB)(S\langle \tau_{A \rightarrow S} \rangle, S\langle \tau_{B \rightarrow S} \rangle)$, where substitution $\tau_{P \rightarrow Q}$ is the result of chain applying substitutions from type $P$ to type $Q :> P$, $\delta {\downarrow}$ is a function which applies type decaying function to the type arguments if needed, $\mathbb{S}(A, B)$ is a set of most specific common supertypes of $A$ and $B$.

> Note: a set of most specific common supertypes $\mathbb{S}(A, B)$ is a reduction of a set of all common supertypes $\mathbb{U}(A, B)$, such that it excludes all types $T \in \mathbb{U}$ such that $\exists V \in \mathbb{U} : V \neq T \land V <: T$.

TODO(Specify when we drop type arguments when doing type approximation / decaying business)

### References

1. Ross Tate. "Mixed-site variance." FOOL, 2013.
2. Ross Tate, Alan Leung, and Sorin Lerner. "Taming wildcards in Java's type system." PLDI, 2011.

TODO(the big TODO for the whole chapter: we need to clearly decide what kind of type system we want to specify: an algo-driven ts vs a full declarational ts, operation-based or relation-based. An example of the second distinction would be difference between $(A?)!!$ and $((A!!)?)!!$. Are they the same type? Are they different, but equivalent? Same goes for $(A..B)?$ vs $(A?..B?)$ and such.)

TODO(another big question is: do we want to formally prove all the different thing here?)
