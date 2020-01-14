## Built-in types and their semantics

- TODO: `KClass`?
- TODO: `KProperty`/`KFunction`?
- TODO: `Throwable`?
- TODO: `Comparable`?

Kotlin has several built-in classifier types, which are important for the rest of this document.
Some information about these types is given in the [type system section][Built-in types], here we extend it with additional non-type-system-relevant details.

> Note: these types may have regular declarations in the standard library, but they also introduce semantics not representable via Kotlin source code.

In this section we describe these types and their semantics.

> Note: this section is not meant to be a detailed description of all types available in the standard library, for that please refer to the standard library documentation (TODO: link).

### `kotlin.Any`

Besides being the [unified supertype][`kotlin.Any`-ts] of all non-nullable types, $\Any$ must also provide the following methods.

- `public open operator fun equals(other: Any?): Boolean`

    Returns `true` iff a value is equal to some other value.
    Implementations of `equals` must satisfy the properties of reflexivity (`x.equals(x)` is always true), symmetry (`x.equals(y) == y.equals(x)`), transitivity (if `x.equals(y)` is true and `y.equals(z)` is true, `x.equals(z)` is also true) and consistency (`x.equals(y)` should not change its result between multiple invocations).
    A non-null value also must never be considered equal to `null`, i.e., `x.equals(null)` must be `false`.

- `public open fun hashCode(): Int`

    Returns the hash code for a value.
    Implementations of `hashCode` must satisfy the following property: if two values are equals w.r.t. `equals`, `hashCode` must consistently produce the same result.

- `public open fun toString(): String`

    Returns a string representation of a value.

[`kotlin.Any`-ts]: #kotlin.any

### `kotlin.Nothing`

$\Nothing$ is an [uninhabited type][`kotlin.Nothing`-ts], which means the evaluation of an expression with $\Nothing$ type can never complete normally.
Therefore, it is used to mark special situations, such as

* non-terminating expressions
* exceptional control flow
* control flow transfer

Further details about how $\Nothing$ should be handled are available [here][Control- and data-flow analysis] and [here][Type inference].

[`kotlin.Nothing`-ts]: #kotlin.nothing

### `kotlin.Unit`

$\Unit$ is a unit type, i.e., a type with only one value $\Unit$; all values of type $\Unit$ should reference the same underlying $\Unit$ object.
It is somewhat similar in purpose to `void` return type in other programming languages, but has several minor differences, which fall outside the scope of this specification.

### `kotlin.Boolean`

`kotlin.Boolean` is the boolean logic type of Kotlin, representing a value which may be either `true` or `false`.
It is the type of [boolean literals][Boolean literals] as well as the type returned or expected by some built-in Kotlin operators.

### Built-in integer types

Kotlin has several built-in classifier types, which represent signed integer numbers of different bit size.
These types are important w.r.t. [type system][Built-in integer types-ts] and [integer literals][Integer literals].

The signed integer types are the following.

* `kotlin.Int`
* `kotlin.Short`
* `kotlin.Byte`
* `kotlin.Long`

> Note: Kotlin does not have a built-in arbitrary-precision integer type.

> Note: Kotlin does not have any built-in unsigned integer types.

TODO([Kotlin 1.3+, Experimental] Add unsigned types)

These types may or may not have different runtime representations, depending on the used platform and/or implementation.
Consult the specific platform reference for further details.

`kotlin.Int` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{31}$ to $2^{31} - 1$.
If an arithmetic operation on `kotlin.Int` results in arithmetic overflow, the result is unspecified.

`kotlin.Short` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{15}$ to $2^{15} - 1$.
If an arithmetic operation on `kotlin.Short` results in arithmetic overflow, the result is unspecified.

`kotlin.Byte` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{7}$ to $2^{7} - 1$.
If an arithmetic operation on `kotlin.Byte` results in arithmetic overflow, the result is unspecified.

`kotlin.Long` is the type of integer numbers that is required to be able to hold the values at least in the range from $-2^{63}$ to $2^{63} - 1$.
If an arithmetic operation on `kotlin.Long` results in arithmetic overflow, the result is unspecified.

> Note: by "arithmetic overflow" we assume both positive and negative integer overflows.

> Important: a platform implementation may specify behaviour for an arithmetic overflow.

[Built-in integer types-ts]: #built-in-integer-types

### Built-in floating point arithmetic types

There are two built-in classifier types which represent floating-point numbers: `kotlin.Float` and `kotlin.Double`.
These types may or may not have different runtime representations, depending on the used platform and/or implementation.
Consult the specific platform reference for further details.

`kotlin.Float` is the type of floating-point number that is able to contain all the numbers as a [IEEE 754][IEEE754] single-precision binary floating number with the same precision.

`kotlin.Double` is the type of floating-point number that is able to contain all the numbers as a [IEEE 754][IEEE754] double-precision binary floating number with the same precision.

TODO([Kotlin.JVM] Specify IEEE 754 quirks w.r.t. smart casts, cc @dmitry.petrov)

Platform implementations may give additional information on how these types are represented on a particular platform.

[IEEE754]: https://ieeexplore.ieee.org/document/8766229

### `kotlin.Char`

`kotlin.Char` is the built-in classifier type which represents a single Unicode symbol in [UCS-2][UCS-2] character encoding.
It is the type of [character literals][Character literals].

> Important: a platform implementation may *extend* the supported character encodings, e.g., to UTF-16.

### `kotlin.String`

`kotlin.String` is the built-in classifier type which represents a sequence of Unicode symbol in [UCS-2][UCS-2] character encoding.
It is the type of the result of [string interpolation][String interpolation expressions].

> Important: a platform implementation may *extend* the supported character encodings, e.g., to UTF-16.

[UCS-2]: https://standards.iso.org/ittf/PubliclyAvailableStandards/c069119_ISO_IEC_10646_2017.zip

### `kotlin.Enum`

`kotlin.Enum<T>` is the built-in parameterized classifier type which is used as a superclass for all [enum classes][Enum class declaration]: every enum class `E` is implicitly a subtype of `kotlin.Enum<E>`.

`kotlin.Enum<T>` has the following characteristics.

* `kotlin.Enum<T>` is a subtype of `kotlin.Comparable<T>`

`kotlin.Enum<T>` provides the following properties.

* `public final val name: String`

    Contains the name of this enumeration constant, exactly as declared in its declaration.

* `public final val ordinal: Int`

    Contains the ordinal of this enumeration constant, i.e., its position in the declaration, starting from zero.

`kotlin.Enum<T>` provides the following methods.

* `public override final fun compareTo(other: T): Int`
* `public override final fun equals(other: Any?): Boolean`
* `public override final fun hashCode(): Int`

    > Note: the presence of these final methods ensures the semantics of equality and comparison for the enumeration objects, as they cannot be overridden by the user.

* `protected final fun clone(): Any`

    > Note: the `clone()` implementation throws an exception, as enumeration objects cannot be copied.

### Built-in array types

`kotlin.Array<T>` is the built-in parameterized classifier type which is used to represent an indexed fixed-size collection of elements of type `T`.

It is final (i.e., cannot be inherited from) and has the following public constructor.

* `public inline constructor(size: Int, init: (Int) -> T)`

     Creates a new array with the specified size, where each element is calculated by calling the specified `init` function with the corresponding element's index.
     The function `init` is called for each array element sequentially starting from the first one.

`kotlin.Array<T>` provides the following methods and properties.

* `public operator fun get(index: Int): T`

    Returns the array element at the specified index.
    If the [index] is out of bounds of this array, throws an `IndexOutOfBoundsException`.

* `public operator fun set(index: Int, value: T): Unit`

    Sets the array element at the specified index to the specified value.
    If the [index] is out of bounds of this array, throws an `IndexOutOfBoundsException`.

* `public val size: Int`

    Returns the array size.

* `public operator fun iterator(): Iterator<T>`

    Creates an [iterator][Iterator types] for iterating over the elements of the array.

#### Specialized array types

In addition to the general `kotlin.Array<T>` type, Kotlin also has the following specialized array types:

* `kotlin.DoubleArray` (for `kotlin.Array<kotlin.Double>`)
* `kotlin.FloatArray` (for `kotlin.Array<kotlin.Float>`)
* `kotlin.LongArray` (for `kotlin.Array<kotlin.Long>`)
* `kotlin.IntArray` (for `kotlin.Array<kotlin.Int>`)
* `kotlin.ShortArray` (for `kotlin.Array<kotlin.Short>`)
* `kotlin.ByteArray` (for `kotlin.Array<kotlin.Byte>`)
* `kotlin.CharArray` (for `kotlin.Array<kotlin.Char>`)
* `kotlin.BooleanArray` (for `kotlin.Array<kotlin.Boolean>`)

These array types are similar to the corresponding `kotlin.Array<T>` type; i.e., `kotlin.IntArray` has the same methods and properties as `kotlin.Array<Int>`, with the following changes.

* `public constructor(size: Int)`

     Creates a new array with the specified size, where each element is set to the corresponding built-in type default value.

     > Note: default values are platform-specific.

* `public operator fun iterator(): {TYPE}Iterator`

    Creates a specialized [iterator][Iterator types] for iterating over the elements of the array.

### Iterator types

TODO(...)

TODO([Kotlin.*] `Appendable`/`StringBuilder`? Depends on how we plan to approach the interpolation expansion)
