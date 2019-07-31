## Built-in types and their semantics

- TODO: `Appendable`/`StringBuilder`? Depends on how we plan to approach the interpolation expansion
- TODO: `{Builtin}Array` types?

In addition to the types defined in the [type system section][Built-in types], Kotlin has several built-in classifier types, which are important for the rest of this document.
These types have regular declarations in the standard library, but may also introduce special semantics.

In this section we introduce these types and describe their semantics.
We also describe additional semantics of built-in types described in the type system section.

> Note: this section is not meant to be a detailed description of all types available in the standard library, for that please refer to the standard library documentation (TODO: link).

### `kotlin.Any`

Besides being the [unified supertype][`kotlin.Any`-ts] of all non-nullable types, $\Any$ must also provide the following methods.

- `public open operator fun equals(other: Any?): Boolean`
  Returns `true` iff a value is equal to some other value.
  Implementations of `equals` must satisfy the properties of reflexivity (`x.equals(x)` is always true), symmetry (`x.equals(y) == y.equals(x)`), transitivity (if `x.equals(y)` and `y.equals(z)`, `x.equals(z)`) and consistency (`x.equals(y)` should not change between multiple invocations).
  A non-null value also must never be considered equal to `null`, i.e. `x.equals(null)` must be `false`.

- `public open fun hashCode(): Int`
  Returns a hash code for a value.
  Implementations of `hashCode` must satisfy the following property: if two values are equals w.r.t. `equals`, `hashCode` must produce the same result consistently.

- `public open fun toString(): String`
  Returns a string representation of a value.

[`kotlin.Any`-ts]: #kotlin.any

##### `kotlin.Nothing`

[$\Nothing$][`kotlin.Nothing`-ts] is an uninhabited type, which means the evaluation of an expression with $\Nothing$ type can never complete normally.
Therefore, it is used to mark special situations, such as

* non-terminating expressions
* exceptional control flow
* control flow transfer

Further details about how $\Nothing$ should be handled are available [here][Control- and data-flow analysis] and [here][Type inference].

[`kotlin.Nothing`-ts]: #kotlin.nothing

### `kotlin.Unit`

$\Unit$ is a unit type, i.e., a type with only one value $\Unit$; all values of type $\Unit$ should reference the same underlying $\Unit$ object.
It is somewhat similar in purpose to `void` return type in Java, but has several minor differences, which fall outside the scope of this specification.

### `kotlin.Boolean`

`kotlin.Boolean` is the boolean logic type of Kotlin, representing the value that may be either `true` or `false`.
It is the type of [boolean literals][Boolean literals] as well as the type returned or expected by some built-in Kotlin operators.

### Built-in integer types

Kotlin has several built-in classifier types, which represent signed integer numbers of different bit size.
The signed integer types are the following.

* `kotlin.Int`
* `kotlin.Short`
* `kotlin.Byte`
* `kotlin.Long`

> Note: Kotlin does not have a built-in arbitrary-precision integer type.

> Note: Kotlin does not have any built-in unsigned integer types.

These types may or may not have different runtime representations, depending on the used platform and/or implementation.
Consult the specific platform reference for further details.

`kotlin.Int` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{31}$ to $2^{31} - 1$.
If an arithmetic operation on `kotlin.Int` results in arithmetic overflow or underflow, the result is unspecified.

`kotlin.Short` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{15}$ to $2^{15} - 1$.
If an arithmetic operation on `kotlin.Short` results in arithmetic overflow or underflow, the result is unspecified.

`kotlin.Byte` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{7}$ to $2^{7} - 1$.
If an arithmetic operation on `kotlin.Byte` results in arithmetic overflow or underflow, the result is unspecified.

`kotlin.Long` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{63}$ to $2^{63} - 1$.
If an arithmetic operation on `kotlin.Long` results in arithmetic overflow or underflow, the result is unspecified.

> Important: a platform implementation may specify behaviour for an arithmetic overflow or underflow.

TODO([Kotlin 1.3] Add unsigned types)

### Built-in floating point arithmetic types

There are two built-in class types that represent floating-point numbers: `kotlin.Float` and `kotlin.Double`.
These types may or may not have different runtime representations. See your platform reference for details.

`kotlin.Float` is the type of floating-point number that is able to contain all the numbers as a IEEE754 (TODO: link) single-precision binary floating number with the same precision.
`kotlin.Double` is the type of floating-point number that is able to contain all the numbers as a IEEE754 (TODO: link) double-precision binary floating number with the same precision.

TODO: or do they?

Platform specification may give a more thorough information on how these types are represented on a particular platform.
For other traits of these types (such as the classes they inherit from, interfaces they may inherit from and their member functions) please refer to the standard library specification.

TODO: FP semantics are pretty hard, how much of that we want to put here?

### `kotlin.Char`

`kotlin.Char` is the built-in class type that represents a single unicode symbol in UTF-16 (TODO: link) character encoding. It is the type of [character literals][Character literals]. For other traits of this type (such as the classes it inherits from, interfaces it may inherit from and its member functions) please refer to the standard library specification.

TODO: UTF-16 or UCS-2?

### `kotlin.String`

`kotlin.String` is the built-in class type that represents a sequence of unicode symbol in UTF-16 (TODO: link) character encoding. It is the type of the result of [string interpolation][String interpolation expressions]. For other traits of this type (such as the classes it inherits from, interfaces it may inherit from and its member functions) please refer to the standard library specification.

TODO: UTF-16 or UCS-2?
