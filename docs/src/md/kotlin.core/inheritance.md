## Inheritance

TODO(This is a stub)

### Classifier type inheritance

Kotlin is an object-oriented language with its object model based on **inheritance**.
Classifier types may be inherited from each other: the type inherited *from* is called the *base type*, while the type which inherits the base type is called the *derived type*.

The following limitations are imposed on the possible inheritance structure.

A class or object type is allowed to inherit from only one class type (called its **direct superclass**) and multiple interface types.
As specified in the [declaration section][Class declaration], if the superclass of a class or object type is not specified, it is assumed to be [`kotlin.Any`][`kotlin.Any`].
This means, among other things, that every class or object type always has a direct superclass.

A class is called **closed** and cannot be inherited from if it is not explicitly declared as either `open` or `abstract`.

> Note: classes are neither `open` nor `abstract` by default.

A [`data class`][Data class declaration], [`enum class`][Enum class declaration] or [`annotation class`][Annotation class declaration] cannot be declared `open` or `abstract`, i.e., are always closed and cannot be inherited from.
Declaring a class `sealed` also implicitly declares it `abstract`.

An interface type may be inherited from any number of other interface types (and only interface types), if the resulting type is [well-formed][Parameterized classifier types].

Object types cannot be inherited from.

Inheritance is the primary mechanism of introducing [subtyping relations][Subtyping] between user-defined types in Kotlin.
When a classifier type $A$ is declared with base types $B_1, \dots, B_m$, it introduces subtyping relations $A <: B_1, \ldots, A <: B_m$, which are then used in [overload resolution][Overload resolution] and [type inference][Type inference] mechanisms.

#### Abstract classes

A class declared `abstract` cannot be instantiated, i.e., an object of this class cannot be created directly.
Abstract classes are implicitly `open` and their primary purpose is to be inherited from.
Only abstract classes allow for `abstract` [property][Property declaration] and [function][Function declaration] declarations in their scope.

#### Sealed classes

A class may be declared `sealed`, making it special from the inheritance point-of-view.

- A `sealed` class is implicitly `abstract` (and these two modifiers are exclusive);
- It can only be inherited from by class and object types declared in the same file (including class and object types declared as nested classes for this class, but not nested classes in other classes);
- It allows for exhaustiveness checking of [when expressions][When expression] for values of this type.

#### Inheritance from built-in types

[Built-in types][Built-in types] follow the same rules as user-defined types do.
Most of them are closed class types and cannot be inherited from.
[Function types][Function types] are treated as interfaces and can be inherited from as such.

### Overriding

A callable declaration (that is, a [property][Property declaration] or [member function][Function declaration] declaration) inside a classifier declaration is said to be *overridable* if:

- Its visibility (and the visibility of its getter and setter, if present) is not `private`;
- It is declared as `open`, `abstract` or `override` (interface methods and properties are implicitly `abstract` if they don't have a body or `open` if they do).

It is illegal for a declaration to be both `private` and either `open`, `abstract` or `override`, such declarations should result in a compile-time error.

A callable declaration $D$ inside a classifier declaration *subsumes* a corresponding declaration $B$ of the base classifier type if the [function signature][Function signature] of $D$ (if any) matches the function signature of $B$ (if any).

If the declaration $B$ of the base classifier type is overridable, the declaration $D$ of the derived classifier type subsumes it, and $D$ has an `override` modifier, $D$ is *overriding* the base declaration $B$.

A declaration $D$ which overrides declaration $B$ should satisfy the following conditions.

- Return type of $D$ is a subtype of return type of $B$;
- [Suspendability][Coroutines] of $D$ and $B$ must be the same.

Otherwise, it is a compile-time error.

If the base declaration is not overridable and/or the overriding declaration does not have an `override` modifier, it is not permitted and should result in a compile-time error.

If the overriding declaration *does not* have its visibility specified, its visibility is implicitly set to be the same as the visibility of the overridden declaration.

If the overriding declaration *does* have its visibility specified, it must not be stronger than the visibility of the overridden declaration.

> Examples:
> ```kotlin 
> open class B {
>     protected open fun f() {}
> }
> class C : B() {
>     open override fun f() {}
>     // `f` is protected, as its visibility is
>     //   inherited from the base declaration
> }
> class D : B() {
>     public open override fun f() {}
>     // this is correct, as public visibility is
>     //   weaker that protected visibility
>     //   from the base declaration
> }
> 
> open class P {
>     open fun g() {}
> }
> 
> class Q : P() {
>     protected open override fun g() {}
>     // this is an error, as protected visibility is
>     //   stronger that public visibility
>     //   from the base declaration
> }
> ```

> Important: platforms may introduce additional cases of both *overridability* and *subsumption* of declarations, as well as limit the overriding mechanism due to implementation limitations.

> Note: Kotlin does not have a concept of full hiding (or shadowing) of declarations.

> Note: if a declaration binds a new function to the same name as was introduced in the base class, but which does not subsume it, it is neither a compile-time error nor an overriding declaration.
> In this case these two declarations follow the normal rules of [overloading][Overload resolution].
> However, these declarations may still result in a compile-time error as a result of [conflicting overload][Conflicting overloads] detection.
