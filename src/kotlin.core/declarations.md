## Declarations

### Glossary

Entity
~ A distinguishable part of a program

Path
~ A sequence of names which identifies a program entity

### Identifiers, names and paths

TODO(Explain paths)

### Introduction

TODO(Examples)

Declarations in Kotlin are used to introduce entities (values, types, etc.); most declarations are *named*, i.e. they also assign an identifier to their own entity, however, some declarations may be *anonymous*.

Every declaration is accessible in a particular *scope*, which is dependent both on where the declaration is located and on the declaration itself.

### Classifier declaration

:::{.paste target=grammar-rule-classDeclaration}
:::
:::{.paste target=grammar-rule-objectDeclaration}
:::

Classifier declarations introduce new types to the program, of the forms described [here][Classifier types]. There are three kinds of classifier declarations:

* class declarations
* interface declarations
* object declarations

#### Class declaration

A simple class declaration consists of the following parts.

* name $c$
* primary constructor declaration $ptor$
* supertype specifiers $S_1, \ldots, S_s$
* body $b$, which may include the following
  - secondary constructor declarations $stor_1, \ldots, stor_c$
  - instance initialization block $init$
  - property declarations $prop_1, \ldots, prop_p$
  - function declarations $md_1, \ldots, md_m$
  - companion object declaration $companionObj$
  - nested classifier declarations $nested$

and creates a simple classifier type $c : S_1, \ldots, S_s$.

Supertype specifiers are used to create inheritance relation between the declared type and the specified supertype. You can use classes and interfaces as supertypes, but not objects.

It is allowed to inherit from a single class only, i.e., multiple class inheritance is not supported. Multiple interface inheritance is allowed.

Instance initialization block describes a block of code which should be executed during [object creation][Classifier initialization].

Property and function declarations in the class body introduce their respective entities in this class' scope, meaning they are available only on an entity of the corresponding class.

Companion object declaration `companion object CO { ... }` for class `C` introduces an object, which is available under this class' name or under the path `C.CO`. Companion object name may be omitted, in which case it is considered to be equal to `Companion`.

Nested classifier declarations introduce new classifiers, available under this class' path for all nested classifiers except for inner classes. Inner classes are available only on the corresponding class' entities. Further details are available [here][Inner and nested classes].

TODO(Examples)

A parameterized class declaration consists of the following parts.

* name $c$
* type parameter list $T_1, \ldots, T_m$
* primary constructor declaration $ptor$
* supertype specifiers $S_1, \ldots, S_s$
* body $b$, which may include the following
  - secondary constructor declarations $stor_1, \ldots, stor_c$
  - instance initialization block $init$
  - property declarations $prop_1, \ldots, prop_p$
  - function declarations $md_1, \ldots, md_m$
  - companion object declaration $companionObj$
  - nested classifier declarations $nested$

and extends the rules for a simple class declaration w.r.t. type parameter list. Further details are described [here][Declarations with type parameters].

##### Constructor declaration

There are two types of class constructors in Kotlin: primary and secondary.

A primary constructor is a concise way of describing class properties together with constructor parameters, and has the following form

$$ptor : (p_1, \ldots, p_n)$$

where each of $p_i$ may be one of the following:

* regular constructor parameter $name: type$
* read-only property constructor parameter $val name: type$
* mutable property constructor parameter $var name: type$

Property constructor parameters, together with being regular constructor parameters, also declare class properties of the same name and type. One can consider them to have the following syntactic expansion.

```kotlin
class Foo(i: Int, val d: Double, var s: String) : Super(i, d, s) {}

class Foo(i: Int, d_: Double, s_: String) : Super(i, d_, s_) {
  val d = d_
  var s = s_
}
```

When accessing property constructor parameters inside the class body, one works with their corresponding properties; however, when accessing them in the supertype specifier list (e.g., as an argument to a superclass constructor invocation), we see them as actual parameters, which cannot be changed.

If a class declaration has a primary constructor and also includes a class supertype specifier, that specifier must represent a valid invocation of the supertype constructor.

A secondary constructor describes an alternative way of creating a class instance and has only regular constructor parameters. If a class has a primary constructor, any secondary constructor must delegate to either the primary constructor or to another secondary constructor via `this(...)`.

If a class does not have a primary constructor, its secondary constructors must delegate to either the superclass constructor via `super(...)` (if the superclass is present in the supertype specifier list) or to another secondary constructor via `this(...)`. If the only superclass is `Any`, delegation is optional.

In all cases, it is forbidden if two or more secondary constructors form a delegation loop.

TODO(elaborate this `this(...)` and `super(...)` business)

TODO(default values in constructors???)

##### Nested and inner classifiers

If a classifier declaration $ND$ is *nested* in another classifier declaration $PD$, it creates a nested classifier type --- a classifier type available under the path $PD.ND$. In all other aspects, nested classifiers are equivalent to regular ones.

Inner classes are a special kind of nested classifiers, which introduce types of objects associated (linked) with other (parent) objects. An inner class declaration $ID$ nested in another classifier declaration $PD$ may reference an *object* of type $ID$ associated with it.

This association happens when instantiating an object of type $ID$, as its constructor may be invoked only when a receiver of type $PD$ is available, and this receiver becomes associated with the new instantiated object of type $ID$.

TODO(...)

##### Inheritance delegation

TODO(...)

#### Data class declaration

A data class $dataClass$ is a special kind of class, which represents a product type constructed from a number of data properties $(dp_1, \ldots, dp_m)$, described in its primary constructor. As such, it allows Kotlin to reduce the boilerplate and generate a number of additional data-relevant functions.

* `equals() / hashCode() / toString()` functions compliant with their contracts
    * TODO(Nope, we should explicitly specify the contracts here, especially for `toString`)
* A `copy()` function for shallow object copying
* A number of `componentN()` functions for destructive declaration

TODO(Some of these may be overriden, some cannot)

All these functions consider only data properties $\{dp_i\}$; e.g., your data class may include regular property declarations in its body, however, they will *not* be considered in the `equals()` implementation or have a `componentN()` generated for them.

To support these features, data classes have the following restrictions.

* Data classes are final and cannot be inherited from
* Data classes must have a primary constructor with only property constructor parameters, which become data properties for the data class

##### Data class generation

TODO(A more detailed explaination)

#### Enum class declaration

TODO(...)

#### Annotation class declaration

TODO(...)

#### Interface declaration

Interfaces differ from classes in that they cannot be directly instantiated in the program, they are meant as a way of describing a contract which should be satisfied by the interface's subtypes. In other aspects they are similar to classes, therefore we shall specify their declarations by specifying their differences from class declarations.

* An interface cannot have a class as its supertype
* An interface cannot have a constructor
* Interface properties cannot have initializers or backing fields
* An interface cannot have inner classes (but can have nested classes and companion objects)
* An interface and all its members are implicitly open
* All interface member properties and functions are implicitly public
    * Trying to declare a non-public member property or function in an interface is an error

TODO(Something else?)

#### Object declaration

Object declarations are used to support a singleton pattern and, thus, do two things at the same time. One, they (just like class declarations) introduce a new type to the program. Two, they create a singleton-like object of that type.

TODO(do we really need this ironic-ish statement about doing two things at the same time?)

Similarly to interfaces, we shall specify object declarations by highlighting their differences from class declarations.

* An object type cannot be used as a supertype for other types
* An object cannot have a constructor
* An object cannot have a companion object
* An object may not have inner classes
* An object cannot be parameterized, i.e., cannot have type parameters

TODO(Something else?)

> Note: this section is about declaration of _named_ objects. Kotlin also has a concept of _anonymous_ objects, or object literals, which are similar to their named counterparts, but are expressions rather than declarations and, as such, are described in the [corresponding section][Object literals].

#### Classifier initialization

When creating a class or object instance via one of its constructors $ctor$, it is initialized in a particular order, which we describe here.

First, a supertype constructor corresponding to $ctor$ is called with its respective parameters.

* If $ctor$ is a primary constructor, a corresponding supertype constructor is the one from the supertype specifier list
* If $ctor$ is a secondary constructor, a corresponding supertype constructor is the one ending the constructor delegation chain of $ctor$
* If an explicit supertype constructor is not available, `Any()` is implicitly used

After the supertype initialization is done, we continue the initialization by processing each inner declaration in its body, *in the order of their inclusion in the body*. If any initialization step creates a loop, it is considered an undefined behavior.

### Function declaration

:::{.paste target=grammar-rule-functionDeclaration}
:::
:::{.paste target=grammar-rule-functionBody}
:::

Function declarations assign names to functions --- blocks of code which may be called by passing them a number of arguments. Functions have special *function types* which are covered in more detail [here][Function types].

A simple function declaration consists of four main parts:

* name $f$
* parameter list $(p_1: P_1 = v_1, \ldots, p_n: P_n = v_n)$
* return type $R$
* body $b$

and creates a function type $f : (P_1, \ldots, P_n) \rightarrow R$.

Parameter list $(p_1: P_1 = v_1, \ldots, p_n: P_n = v_n)$ describes function parameters --- inputs needed to execute the declared function. Each parameter $p_i: P_i = v_i$ introduces $p_i$ as a name of value with type $P_i$ available inside function body $b$; therefore, parameters are final and cannot be changed inside the function. A function may have zero or more parameters.

A parameter may include a default value $v_i$, which is used if the corresponding argument is not specified in function invocation; $v_i$ should be an expression which evaluates to type $V <: P_i$.

Return type $R$ is optional, if function body $b$ is present and may be inferred to have a valid type $B : B \not \equiv kotlin.Nothing$, in which case $R \equiv B$. In other cases return type $R$ must be specified explicitly.

> As type $kotlin.Nothing$ has a [special meaning][`kotlin.Nothing`] in Kotlin type system, it must be specified explicitly, to avoid spurious $kotlin.Nothing$ function return types.

Function body $b$ is optional; if it is ommited, a function declaration creates an *abstract* function, which does not have an implementation. This is allowed only inside an [abstract classifier declaration][Classifier declaration]. If a function body $b$ is present, it should evaluate to type $B$ which should satisfy $B <: R$.

A parameterized function declaration consists of five main parts.

* name $f$
* type parameter list $T_1, \ldots, T_m$
* parameter list $(p_1: P_1 = v_1, \ldots, p_n: P_n = v_n)$
* return type $R$
* body $b$

and extends the rules for a simple function declaration w.r.t. type parameter list. Further details are described [here][Declarations with type parameters].

#### Named, positional and default parameters

Kotlin supports *named* parameters out-of-the-box, meaning one can bind an argument to a parameter in function invocation not by its position, but by its name, which is equal to the argument name.

```kotlin
fun bar(a: Int, b: Double, s: String): Double = a + b + s.toDouble()

fun main(args: Array<String>) {
    println(bar(b = 42.0, a = 5, s = "13"))
}
```

TODO(Argument names are resolved in compile time)

If one wants to mix named and positional arguments, the argument list must conform to the following form: $P_1, \ldots, P_M, N_1, \ldots, N_Q$, where $P_i$ is a positional argument, $N_j$ is a named argument; i.e., positional arguments must precede all of the named ones.

Kotlin also supports *default* parameters --- parameters which have a default value used in function invocation, if the corresponding argument is missing. Note that default parameters cannot be used to provide a value for positional argument *in the middle* of the positional argument list; allowing this would create an ambiguity of which argument for position $i$ is the correct one: explicit one provided by the developer or implicit one from the default value.

```kotlin
fun bar(a: Int = 1, b: Double = 42.0, s: String = "Hello"): Double =
    a + b + s.toDouble()

fun main(args: Array<String>) {
    // Valid call, all default parameters used
    println(bar())
    // Valid call, defaults for `b` and `s` used
    println(bar(2))
    // Valid call, default for `b` used
    println(bar(2, s = "Me"))

    // Invalid call, default for `b` cannot be used
    println(bar(2, "Me"))
}
```

In summary, argument list should have the following form:

* Zero or more positional arguments
* Zero or more named arguments

Missing arguments are bound to their default values, if they exist.

#### Variable length parameters

One of the parameters may be designated as being variable length (aka *vararg*). A parameter list $(p_1, \ldots, \text{vararg }p_i: P_i = v_i, \ldots, p_n)$ means a function may be called with any number of arguments in the i-th position. These arguments are represented inside function body $b$ as an [array of type $P_i$][Array types].

If a variable length parameter is not last in the parameter list, all subsequent arguments in the function invocation should be specified as named arguments. If a variable length parameter has a default value, it should be an expression which evaluates to an [array of type $P_i$][Array types].

An array of type $Q <: P_i$ may be *unpacked* to a variable length parameter in function invocation using [spread operator][Spread operator]; in this case array elements are considered to be separate arguments in the variable length parameter position. A function invocation may include several spread operator expressions corresponding to the vararg parameter.

#### Extension function declaration

An _extension function declaration_ is similar to a standard function declaration, but introduces an additional special function parameter, the _receiver parameter_. This parameter is designated by specifying the receiver type (the type before `.` in function name), which becomes the type of this receiver parameter. This parameter is not named and must always be supplied, e.g. it cannot be a variable-argument parameter, have a default value, etc.

Calling such a function is special because the receiver parameter is not supplied as an argument of the call, but as the [_receiver_][Receivers] of the call, be it implicit or explicit. This parameter is available inside the scope of the function as the implicit receiver or `this`-expression, while nested scopes may introduce additional receivers that take precedence over this one. See [the receiver section][Receivers] for details. This receiver is also available (as usual) in nested scope using labeled `this` syntax using the name of the declared function as the label.

> Note: when declaring extension functions inside classifier declarations, this receiver takes precedence over the classifier object, which is usually the current receiver inside nested functions

For all other purposes, extension functions are not different from non-extension functions.

Examples:

```kotlin
fun Int.foo() { println(this + 1) } // this has type Int

fun main(args: Array<String>) {
    2.foo() // prints "3"
}

class Bar {
    fun foo() { println(this) } // this has type Bar
    fun Int.foo() { println(this) } // this has type Int
}
```

### Property declaration

:::{.paste target=grammar-rule-propertyDeclaration}
:::

Property declarations are used to create read-only (`val`) or mutable (`var`) entities in their respective scope. Properties may also have custom getter or setter --- functions which are used to read or write the property value.

#### Read-only property declaration

A read-only property declaration `val x: T = e` introduces `x` as a name of the result of `e`. Both the right-hand value `e` and the type `T` are optional, however, at least one of them must be specified. More so, if the type of `e` cannot be [inferred][Type inference], the type `T` must be specified explicitly. In case both are specified, the type of `e` must be a subtype of `T` (see [subtyping][Subtyping] for more details).

A read-only property declaration may include a custom [getter][Getters and setters] in the form of

```kotlin
val x: T = e
    get() { ... }
```

in which case `x` is used as a synonym to the getter invocation.

#### Mutable property declaration

A mutable property declaration `var x: T = e` introduces `x` as a name of a mutable variable with type `T` and initial value equals to the result of `e`. The rules regarding the right-hand value `e` and the type `T` match those of a read-only property declaration.

A mutable property declaration may include a custom [getter][Getters and setters] and/or custom [setter][Getters and setters] in the form of

```kotlin
var x: T = e
    get(): TG { ... }
    set(value: TS) { ... }
```

in which case `x` is used as a synonym to the getter invocation when read from and to the setter invocation when written to.

#### Delegated property declaration

A delegated read-only property declaration `val x: T by e` introduces `x` as a name for the *delegation* result of property `x` to the entity `e`. One may view these properties as regular properties with a special *delegating* [getters][Getters and setters]. TODO(Type is optional if inferred?)

In case of a delegated read-only property, access to `x` is replaced with the call to a special function `getValue`, which must be available on `e`. This function has the following signature

```kotlin
operator fun getValue(thisRef: E, property: PropertyInfo): R
```

where

* `thisRef: E` is the reference to the enclosing entity
    - holds the enclosing class or object instance in case of classifier property
    - is `null` for [local properties][Local property declaration]
* `property: PropertyInfo` contains runtime-available information about the declared property, most importantly
    - `property.name` holds the property name

This convention implies the following requirements on the `getValue` function

* $S <: E$, where $S$ is the type of the enclosing entity
* $\text{KProperty<*>} <: \text{PropertyInfo}$
* $R$ should be in a supertype relation with the delegated property type $T$

> In case of the local property, enclosing entity has the type `Nothing?`

A delegated mutable property declaration `var x: T by e` introduces `x` as a name of a mutable entity with type `T`, access to which is *delegated* to the entity `e`. As before, one may view these properties as regular properties with special *delegating* [getters and setters][Getters and setters].

Read access is handeled using the same `getValue` function as for a delegated read-only property. Write access is processed using a special function `setValue`, which must be available on `e`. This function has the following signature

```kotlin
operator fun setValue(thisRef: E, property: PropertyInfo, value: R): U
```

where

* `thisRef: E` is the reference to the enclosing entity
    - holds the enclosing class or object instance in case of classifier property
    - is `null` for [local properties][Local property declaration]
* `property: PropertyInfo` contains runtime-available information about the declared property, most importantly
    - `property.name` holds the property name
* `value: R` is the new property value

This convention implies the following requirements on the `setValue` function

* $S <: E$, where $S$ is the type of the enclosing entity
* $\text{KProperty<*>} <: \text{PropertyInfo}$
* $R$ should be in a supertype relation with the delegated property type $T$
* $U$ is ignored

> In case of the local property, enclosing entity has the type `Nothing?`

The delegated property is expanded as follows.

```kotlin
/*
 * Actual code
 */
class C {
    var prop: Type by DelegateExpression
}

/*
 * Expanded code
 */
class C {
    private val prop$delegate = DelegateExpression
    var prop: Type
        get() = prop$delegate.getValue(this, this::prop)
        set(value: Type) = prop$delegate.setValue(this, this::prop, value)
}
```

TODO(provideDelegate)

#### Local property declaration

If a property declaration is local, it creates a local entity which follows most of the same rules as the ones for regular property declarations. However, local property declarations cannot have custom getters or setters.

Local property declarations also support *destructive* declaration in the form of

```kotlin
val (a: T, b: U, c: V, ...) = e
```

which is a syntactic sygar for the following expansion

```kotlin
val a: T = e.component1()
val b: U = e.component2()
val c: V = e.component3()
...
```

where `componentN()` should be a valid operator function available on the result of `e`. Each individual component property follows the rules for regular local property declaration.

#### Getters and setters

As mentioned before, a property declaration may include a custom getter and/or custom setter (together called *accessors*) in the form of

```kotlin
var x: T = e
    get(): TG { ... }
    set(anyValidArgumentName: TS): RT { ... }
```

These functions have the following requirements

* $TG \equiv T$
* $TS \equiv T$
* $RT \equiv \mathtt{kotlin.Unit}$
* Types $TG$, $TS$ and $RT$ are optional and may be omitted from the declaration
  
* Read-only properties may have a custom getter, but not a custom setter
* Mutable properties may have any combination of a custom getter and a custom setter
  
* Setter argument may have any valid identifier as argument name

> Note: Regular coding convention recommends `value` as the name for the setter argument

One can also ommit the accessor body, in which case a *default* implementation is used (also known as default accessor).

```kotlin
var x: T = e
    get
    set
```

> This notation is usually used if you need to change some aspects of an accessor (i.e., its visibility) without changing the default implementation.

Getters and setters allow one to customize how the property is accessed, and may need access to the property's *backing field*, which is responsible for actually storing the property data. It is accessed via the special `field` property available inside accessor body, which follows these conventions

* For a property declaration of type `T`, `field` has the same type `T`
* `field` is read-only inside getter body
* `field` is mutable inside setter body

However, the backing field is created for a property only in the following cases

* A property has no custom accessors
* A property has a default accessor
* A property has a custom accessor, and it uses `field` property
* A mutable property has a custom getter or setter, but not both

In all other cases a property has no backing field.

Read/write access to the property is replaced with getter/setter invocation respectively.

#### Extension property declaration

An _extension property declaration_ is similar to a standard property declaration, but, very much alike an [extension function][Extension function declaration], introduces an additional parameter to the property called _the receiver parameter_. This is different from usual property declarations, that do not have any parameters. There are other differences from standar property declarations:

- Extension properties cannot have initializers
- Extension properties cannot have backing fields
- Extension properties cannot have default accessors

> Note: informally, on can say that extension properties have no state of their own. Only properties that use other objects' storage facilities and/or uses constant data can be extension properties.

Aside from these differences, extension properties are similar to regular properties, but, when accessing such a property one always need to supply a [_receiver_][Receivers], implicit or explicit. Also, unlike regular properties, the type of the receiver must be a subtype of the receiver parameter, and the value that is supplied as the receiver is bound to the receiver parameter.

The receiver parameter can be accessed inside getter and setter scopes of the property as the implicit receiver or `this`. It may also be accessed inside nested scopes using [labeled `this` syntax][] using the name of the property declared as the label. For delegated properties, the value passed into the operator functions `getValue` and `setValue` as the receiver is the value of the receiver parameter, rather than the value of the outer classifier. This is also true for local extension properties: while regular local properties are passed `null` as the first argument of these operator functions, local extension properties are passed the value of the receiver argument instead.

> Note: when declaring extension properties inside classifier declarations, this receiver takes precedence over the classifier object, which is usually the current receiver inside nested properties

For all other purposes, extension properties are not different from non-extension properties.

Examples:

```kotlin
val Int.foo: Int get() = this + 1

fun main(args: Array<String>) {
    println(2.foo.foo) // prints "4"
}

class Bar {
    val foo get() = this // returns type Bar
    val Int.foo get() = this // returns type Int
}
```

TODO(More examples (delegation, at least))

#### Property initialization

All non-abstract properties must be definitely initialized before their first use. To guarantee this, Kotlin compiler uses a number of analyses which are described in more detail [here][Control- and data-flow analysis].

### Type alias

:::{.paste target=grammar-rule-typeAlias}
:::

Type alias introduces an alternative name for the specified type and supports both simple and parameterized types. If type alias is parameterized, its type parameters must be [unbounded][Type parameters]. Another restriction is that recursive type aliases are forbidden --- the type alias name cannot be used in its own right-hand side.

At the moment, Kotlin supports only top-level type aliases. The scope where it is accessible is defined by its [*visibility modifiers*][Visibility].

### Declarations with type parameters

TODO()

### Declaration modifiers

TODO(declaration scope)

TODO(`open`)

TODO(`abstract`)

TODO(`lateinit`)

TODO(`const`)

TODO(overriding vs overloading vs shadowing)
