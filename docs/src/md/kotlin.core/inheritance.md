## Inheritance

TODO(This is a stub)

### Classifier type inheritance

Kotlin is an object-oriented language with its object model based on **inheritance**.
Classifier types may be inherited from each other: the type inherited *from* is called the base type, while the type that inherits the base type is called the derived type.
A class or object type is allowed to inherit from only one class type (called its **direct superclass**) and multiple interface types.
Object types cannot be inherited from.
As specified in the declaration section, if the superclass of a class or object type is not specified, it is assumed to be `kotlin.Any` as all the types are inherited from it.
This means, among other things, that every class or object type has a direct superclass type.

A class is called **closed** and is forbidden to be inherited from if it is declared not `open` or `abstract` (please note that classes are neither `open` nor `abstract` by default). 
A `data class`, `enum class` or `annotation class` cannot be declared `open` or `abstract` and cannot be inherited from.

An interface type may be inherited from any number of other interface types (and only interface types) without any limitation.

TODO: there are limitations, for example, generic interfaces with different parameters make things complicated

Inheritance is the primary mechanism of introducing [subtyping relations][Subtyping] between user-defined types in Kotlin.
When a classifier type $A$ is declared with base types $B_1, \dots, B_i$ it introduces subtyping relations $A <: B_1, \ldots, A <: B_i$, which are then used in  [overload resolution] and [type inference] mechanisms.

#### Abstract classes

A class declared `abstract` cannot be instantiated, i.e. an object of this class cannot be created directly.
Abstract classes are implicitly open and their primary purpose is to be inherited from.
Only abstract classes allow for `abstract` property and function declarations in its scope.

#### Sealed classes

A class may be declared `sealed`, making it special from the inheritance point-of-view:

- A `sealed` class is implicitly abstract (and these two modifiers are exclusive);
- It can only be inherited from by class and object types declared in the same file (including class and object types declared as nested classes for this class, but not nested classes for other classes);
- It allows for [exhaustiveness checking of when expressions][When-expressions] for values of this type.

#### Inheritance from built-in types

[Built-in types][Built-in types] follow the same rules user-defined types do.
Most of them are closed class types and cannot be inherited from. 
[Function types][Function types] are treated as interfaces and can be inherited from as such.

### Overriding

A callable declaration (that is, a property or member function declaration) inside a classifier declaration is said to be *overridable* if:

- Its visibility (and the visibility of its getter and/or setter) is not `private`;
- It is declared as `open`, `abstract` or `override` (interface methods and properties are implicitly `abstract` if they don't have a body or `open` if they do).

It is illegal for a declaration to be both `private` and `open`, `abstract` or `override` and should result in a compiler error.

A callable declaration inside a classifier declaration *subsumes* a corresponding declaration of the base classifier type if:

- Its return type is a subtype of the return type of the corresponding declaration;
- Its formal parameter types are supertypes of the types of corresponding parameters from the corresponding declaration;
- Its name is the same as the name of the corresponding declaration.

If the declaration of the base classifier type is overridable and the declaration of the derived classifier type subsumes it and it has an `override` modifier, it is *overriding* the base declaration.
If the base declaration is not overridable and/or the deriving declaration does not have an `override` modifier, it is not permitted and should result in a compile-time error.

If the overriding declaration does not have its visibility specified, its visibility is implicitly set to be the same as the visibility of the overriden declaration.
If the overriding declaration does have its visibility specified, it must be weaker than the visibility of the overriden declaration.

> Example: base class $B$ contains declaration 
> ```kotlin 
> protected open fun f() {}
> ```
> Class $C$ is derived from $B$ and contains the following declaration
> ```kotlin 
> override open fun f() {}
> ```
> This makes `f` `protected` inside $C$ even though the visibility is not specified directly.
> If `f` in $C$ was declared `public`, it would be correct as public visibility is weaker than protected visibility.

> Another example: base class $B$ contains declaration 
> ```kotlin 
> open fun g() {}
> ```
> Class $C$ is derived from $B$ and contains the following declaration
> ```kotlin 
> override open fun g() {}
> ```
> This makes `g` `public` inside $C$, and, if it was declared as `protected`, it would be a compiler error.

> Note: Kotlin does not have a concept of full shadowing of declarations.

Platforms may introduce additional cases of both *overridability* and *subsumption* of declarations, as well as limit the overriding mechanism due to internal representation limitations.

> Note: if a declaration binds a new function to the same name as was introduced in the base class, but does not subsume it, it is neither a compiler error nor an overriding declaration.
> In this case these two declarations just follow the normal rules of [function overloading][Overloading resolution].

TODO(...)
