## Declarations

### Glossary {-}

Entity
~ Distinguishable part of a program

Identifier
~ Name of a program entity

Path
~ Sequence of identifiers which references a program entity in a given [scope][Scopes and identifiers]

### Introduction {-}

TODO(Examples)

Declarations in Kotlin are used to introduce entities (values, types, etc.); most declarations are *named*, i.e. they also assign an identifier to their own entity, however, some declarations may be *anonymous*.

Every declaration is accessible in a particular *scope*, which is dependent both on where the declaration is located and on the declaration itself.

### Classifier declaration

:::{.paste target=grammar-rule-classDeclaration}
:::
:::{.paste target=grammar-rule-objectDeclaration}
:::

Classifier declarations introduce new types to the program, of the forms described [here][Classifier types]. 
There are three kinds of classifier declarations:

* class declarations;
* interface declarations;
* object declarations.

#### Class declaration

A simple class declaration consists of the following parts.

* Name $c$;
* Optional primary [constructor declaration] $\ptor$;
* Optional supertype specifiers $S_1, \ldots, S_s$;
* Optional body $b$, which may include the following:
  - secondary [constructor declarations][Constructor declaration] $\stor_1, \ldots, \stor_c$;
  - instance initialization blocks $\init_1, \ldots, \init_i$;
  - property declarations $\prop_1, \ldots, \prop_p$;
  - function declarations $\md_1, \ldots, \md_m$;
  - companion object declaration $\companionObj$;
  - nested classifier declarations $\nested$.

and creates a simple classifier type $c : S_1, \ldots, S_s$.

Supertype specifiers are used to create inheritance relation between the declared type and the specified supertype. You can use classes and interfaces as supertypes, but not objects.

> Note: if supertype specifiers are absent, the declared type is considered to be implicitly derived from `kotlin.Any`.

It is allowed to inherit from a single class only, i.e., multiple class inheritance is not supported. 
Multiple interface inheritance is allowed.

Instance initialization block describes a block of code which should be executed during [object creation][Classifier initialization].

Property and function declarations in the class body introduce their respective entities in this class' scope, meaning they are available only on an entity of the corresponding class.

Companion object declaration `companion object CO { ... }` for class `C` introduces an object, which is available under this class' name or under the path `C.CO`. 
Companion object name may be omitted, in which case it is considered to be equal to `Companion`.

Nested classifier declarations introduce new classifiers, available under this class' name.
Further details are available [here][Nested and inner classifiers].

A parameterized class declaration, in addition to what constitutes a simple class declaration, also has a type parameter list $T_1, \ldots, T_m$ and extends the rules for a simple class declaration w.r.t. this type parameter list. 
Further details are described [here][Declarations with type parameters].

> Examples:
> 
> ```kotlin
> // An open class with no supertypes
> //
> open class Base
> 
> // A class inherited from `Base`
> //
> // Has a single read-only property `i`
> //   declared in its primary constructor
> //
> class B(val i: Int) : Base()
> 
> // An open class with no superclasses
> //
> // Has a single read-only property `i`
> //   declared in its body
> //
> // Initial value for the property is calculated
> //   in the init block
> //
> open class C(arg: Int) {
>     val i: Int
>     
>     init {
>         i = arg * arg
>     }
> }
> 
> // A class inherited from `C`
> //   Does not have a primary constructor,
> //   thus does not need to invoke the supertype constructor
> //
> // The secondary constructor delegates to the supertype constructor
> class D : C {
>     constructor(s: String) : super(s.toInt())
> }
> 
> // An open class inherited from `Base`
> //
> // Has a companion object with a mutable property `name`
> class E : Base() {
>     companion object /* Companion */ {
>         var name = "I am a companion object of E!"
>     }
> }
> ```

> Example:
> 
> ```kotlin
> class Pair(val a: Int, val b: Int) : Comparable<Pair> {
> 
>     fun swap(): Pair = Pair(b, a)
> 
>     override fun compareTo(other: Pair): Int {
>         val f = a.compareTo(other.a)
>         if (f != 0) return f
>         return b.compareTo(other.b)
>     }
> 
>     companion object {
>         fun duplet(a: Int) = Pair(a, a)
>     }
> }
> ```

##### Constructor declaration

There are two types of class constructors in Kotlin: primary and secondary.

A primary constructor is a concise way of describing class properties together with constructor parameters, and has the following form

$$\ptor : (p_1, \ldots, p_n)$$

where each of $p_i$ may be one of the following:

* regular constructor parameter $\name: \type$;
* read-only property constructor parameter $\texttt{val}\ \name: \type$;
* mutable property constructor parameter $\texttt{var}\ \name: \type$.

Property constructor parameters, together with being regular constructor parameters, also declare class properties of the same name and type.

> Important: if a property constructor parameter with type $T$ is specified as `vararg`, its corresponding class property type is the result of [*array type specialization*][Array types] of type `Array<`$\outV T$`>`.

One can consider primary constructor parameters to have the following syntactic expansion.

```kotlin
class Foo(i: Int, vararg val d: Double, var s: String) : Super(i, d, s) {}

class Foo(i: Int, vararg d_: Double, s_: String) : Super(i, d_, s_) {
  val d = d_
  var s = s_
}
```

When accessing property constructor parameters inside the class body, one works with their corresponding properties; however, when accessing them in the supertype specifier list (e.g., as an argument to a superclass constructor invocation), we see them as actual parameters, which cannot be changed.

If a class declaration has a primary constructor and also includes a class supertype specifier, that specifier must represent a valid invocation of the supertype constructor.

A secondary constructor describes an alternative way of creating a class instance and has only regular constructor parameters.

If a class has a primary constructor, any secondary constructor must delegate to either the primary constructor or to another secondary constructor via `this(...)`.

If a class does not have a primary constructor, its secondary constructors must delegate to either the superclass constructor via `super(...)` (if the superclass is present in the supertype specifier list) or to another secondary constructor via `this(...)`. 
If the only superclass is `kotlin.Any`, delegation is optional.

In all cases, it is forbidden if two or more secondary constructors form a delegation loop.

Class constructors (both primary and secondary) may have variable-argument parameters and default parameter values, just as regular functions.
Please refer to the [function declaration reference][Function declaration] for details.

If a class does not have neither primary, nor secondary constructors, it is assumed to implicitly have a default parameterless primary constructor.
This also means that, if a class declaration includes a class supertype specifier, that specifier must represent a valid invocation of the supertype constructor.

> Examples:
> 
> ```kotlin
> open class Base
> 
> class POKO : Base() {}
> 
> class NotQuitePOKO : Base {
>     constructor() : super() {}
> }
> 
> class Primary(val s: String) : Base() {
>     constructor(i: Int) : this(i.toString()) {}
> 
>     constructor(d: Double) : this(d.toInt()) {}
> 
>     // Error, has primary ctor,
>     //   needs to delegate to primary or secondary ctor
>     // constructor() : super() {}
> }
> 
> class Secondary : Base {
>     constructor(i: Int) : super() {}
> 
>     constructor(s: String) : this(s.toInt()) {}
> 
>     // Ok, no primary ctor,
>     //   can delegate to `super(...)`
>     constructor() : super() {}
> }
> ```

##### Nested and inner classifiers

If a classifier declaration $\ND$ is *nested* in another classifier declaration $\PD$, it creates a nested classifier type --- a classifier type available under the path $\PD.\ND$. 
In all other aspects, nested classifiers are equivalent to regular ones.

*Inner* classes are a special kind of nested classifiers, which introduce types of objects associated (linked) with other (parent) objects.
An inner class declaration $\ID$ nested in another classifier declaration $\PD$ may reference an *object* of type $\ID$ associated with it.

This association happens when instantiating an object of type $\ID$, as its constructor may be invoked only when a receiver of type $\PD$ is available, and this receiver becomes associated with the new instantiated object of type $\ID$.

For information on how type parameters of parent and nested / inner classifiers interoperate, we delegate you to the [type system][Inner and nested type contexts] section of the specification.

> Note: inner classes cannot be declared in [objects][Classifier declaration].

##### Inheritance delegation

In a classifier (an object or a class) declaration $C$, any supertype $I$ inheritance may be *delegated to* an arbitrary value $v$ if:

- The supertype $I$ is an interface type;
- $v$ has type $T$ such that $T <: I$.

The inheritance delegation uses a syntax similar to [property delegation][Delegated property declaration] using the `by` keyword, but is specified in the classifier declaration header and is a very different concept.
If inherited using delegation, each method $M$ of $I$ (whether they have a default implementation or not) is delegated to the corresponding method of $v$ as if it was overridden in $C$ with all the parameter values directly passed to the corresponding method in $v$, unless the body of $C$ itself has a suitable override of $M$ (see the [method overriding][Overriding] section).

The particular means on how $v$ is stored inside the classifier object is platform-defined.

Due to the [initialization order of a classifier object][Classifier initialization], the expression used to construct $v$ can not access any of the classifier object properties or methods excluding the parameters of the primary constructor.

> Example:
>
> ```kotlin
> interface I {
>     fun foo(value: Int): Double
>     val bar: Long
> }
> interface J : I {
>     fun fee(): Int
> }
> 
> class C(delegatee: I): I by delegatee
> ```
> 
> is expanded to
>
> ```kotlin
> interface I {
>     fun foo(value: Int): Double
>     val bar: Long
> }
> interface J : I {
>     fun fee(): Int
> }
> 
> class C(delegatee: I): I {
>     val I$delegate = delegate
> 
>     override fun foo(value: Int): Double = I$delegate.foo(value)
>     override val bar: Long
>         get() = I$delegate.bar
> }
> ```
> 
> Please note that the expression used as delegate is accessed exactly once when creating the object, e.g. if the delegate expression contains a mutable property access, this mutable property is accessed once during object construction and its subsequent changes do not affect the delegated interface functions.
> See [classifier initialization section][Classifier initialization] for details on the evaluation order of classifier initialization entities.
> 
> For example (assuming interface `I` from the previous example is defined):
>
> ```kotlin
> var mut = object: J {...}
> 
> class D: I by mut // D delegates I to mutable property
> ```
> 
> is expanded to
>
> ```kotlin
> var mut = object: J {...}
> class D: I {
>     val I$delegate = mut // mut is accessed only once
> 
>     override fun foo(value: Int): Double = I$delegate.foo(value)
>     override val bar: Long
>         get() = I$delegate.bar
> }
> ```
> 
> ```kotlin
> mut = x1
> val d1 = D() // d1 methods are delegated to x1
> mut = x2
> val d2 = D() // d2 methods are delegated to x2
> // but d1 methods are still delegated to x1
> ```

##### Abstract classes

A [class declaration][Class declaration] can be marked `abstract`.
Such classes *cannot* be instantiated directly; they are used as superclasses for other classes or objects.

Abstract classes may contain one or more abstract members: members without implementation, which should be implemented in a subtype of this abstract class.

#### Data class declaration

A data class $\dataClass$ is a special kind of class, which represents a product type constructed from a number of data properties $(\dataClassParam_1, \ldots, \dataClassParam_m)$, described in its primary constructor.
Non-property constructor parameters are not allowed in the primary constructor of a data class.
As such, data classes allow Kotlin to reduce the boilerplate and generate a number of additional data-relevant functions.

* `equals() / hashCode() / toString()` functions compliant with [their contracts][`kotlin.Any`-bi]:
    - `equals(that)` returns true iff:
        - `that` has the same runtime type as `this`;
        - `this.prop == that.prop` returns `true` for every data property `prop`;
    - `hashCode()` returns the same numbers for objects `A` and `B` if they are equal w.r.t. the generated `equals`;
    - `toString` returns a string representations which is guaranteed to include the class name along with all the data properties' string representations.
* A `copy()` function for shallow object copying with the following properties:
    - It has the same number of parameters as the primary constructor with the same names and types;
    - It calls the primary constructor with the corresponding parameters at the corresponding positions;
    - It has defaults for all the parameters defaulting to the value of the corresponding property in `this` object.
* A number of `componentN()` functions for [destructuring declaration][Local property declaration]:
    - For the data property at position $N$ (**starting from 1**), the generated `component`$N$ function has the same type as this property and returns the value of this property;
    - It has an `operator` modifier, allowing it to be used in [destructuring declarations][Local property declaration];
    - The number of these functions is the same as the number of data properties.

All these functions consider only data properties $\{\dataClassParam_i\}$; e.g., your data class may include regular property declarations in its body, however, they will *not* be considered in the `equals()` implementation or have a `componentN()` generated for them.

There are several rules as to how these generated functions may be explicified or inherited.

> Note: a generated function is explicified, if its implementation is provided explicitly in the body of the data class.
> A generated function is inherited, if its implementation is taken from a supertype of the data class.

The declarations of `equals`, `hashCode` and `toString` may be explicified similarly to how overriding works in normal classes.
If a correct explicit implementation is available, no function is generated.
Other functions (`copy`, `componentN`) **cannot** be explicified.

The declarations of `equals`, `hashCode` and `toString` may be inherited from the base class, if it provides a `final` version with a [matching signature][Function signature].
If a correct inherited implementation is available, no function is generated.
Other functions (`copy`, `componentN`) **cannot** be inherited.

In addition, for every generated function, if any of the base types provide an open function with a [matching signature][Function signature], it is automatically overridden by the generated function as if it was generated with an `override` modifier.

> Note: base classes may also have functions that have a conflicting signature with the same function name.
> As expected, these cases result in override or overload conflicts the same way they would with a normal class declaration.

Data classes have the following restrictions:

* Data classes are closed and cannot be [inherited][Inheritance] from;
* Data classes must have a primary constructor with property constructor parameters only, which become data properties for the data class;
* There must be at least one data property in the primary constructor;
* Data properties cannot be specified as `vararg` constructor arguments.

[`kotlin.Any`-bi]: #kotlin.any-1

> For example, the following data class declaration
> ```kotlin
> data class DC(val x: Int, val y: Double)
> ``` 
> 
> may be equivalent to
>
> ```kotlin
> class DC(val x: Int, val y: Double) {
>     override fun equals(other: Any?): Boolean {
>          if(other !is DC) return false
>          return x == other.x && y == other.y
>     }
> 
>     override fun hashCode(): Int = x.hashCode() + 31 * y.hashCode()
> 
>     override fun toString(): String = "DC(x=$x,y=$y)"
>     
>     operator fun component1(): Int = x
> 
>     operator fun component2(): Double = y
> 
>     fun copy(x: Int = this.x, y: Double = this.y): DC = DC(x, y)
> }
> ```
>

TODO(more examples)

#### Enum class declaration

Enum class $E$ is a special kind of class with the following properties:

- It has a number of predefined values that are declared in the class itself (*enum entries*);
- No other values of this class can be constructed;
- It implicitly inherits the built-in class [`kotlin.Enum<E>`][`kotlin.Enum`] (and cannot have any other base classes);
- It it implicitly final and cannot be inherited from;
- It has special syntax to accommodate for the properties described above.

Enum class body uses special kind of syntax (see grammar) to declare enum entries in addition to all other declarations inside the class body.
Enum entries have their own bodies that may contain their own declarations, similar to [object declarations][Classifier declaration].

> Note: an enum class can have zero enum entries.
> This makes objects of this class impossible to construct.

Every enum entry of class `E` implicitly overrides members of `kotlin.Enum<E>` in the following way:

- ```kotlin
  public final val name: String
  ```

  defined to be the same as the name of the entry as declared in code;

- ```kotlin
  public final val ordinal: Int
  ```

  defined to be the ordinal of the entry, e.g. the position of this entry in the list of entries, starting with 0;

- ```kotlin
  public override final fun compareTo(other: E): Int
  ```

  (a member of `kotlin.Comparable<E>`) defined by default to compare entries by their ordinals, but may be overridden to have different behaviour both in the enum class declaration and in entry declarations;

- ```kotlin
  public override fun toString(): String
  ```

  (a member of `kotlin.Any`) defined by default to return the entry name, but may be overridden  to have different behaviour both in the enum class declaration and in entry declarations.

In addition to these, every enum class type `E` has the following **static** member functions declared implicitly:

- ```kotlin
  public final static fun valueOf(value: String): E
  ```

   returning an object corresponding to the entry with the name equal to `value` parameter of the call;

- ```kotlin
  public final static fun values(): kotlin.Array<E>
  ```
  
  returning an [array][Array types] of all possible enum values.
  Every invocation of this function returns a new array to disallow changing its contents.

> Important: `static` is not a valid Kotlin keyword and is only used here for clarity

> Note: these static member functions are handled differently by the [overload resolution][Overload resolution].

> Note: Kotlin standard library introduces another function to access all enum values for a specific enum class called `kotlin.enumValues<T>`.
> Please refer to the standard library documentation for details.

#### Annotation class declaration

Annotations class is a special kind of class that is used to declare [annotations][Annotations].
Annotation classes have the following properties:

- They cannot have any secondary constructors;
- All the primary constructor parameters must use the property syntax;
- They implicitly inherit `kotlin.Annotation` class (and cannot have any other base classes);
- They cannot implement interfaces;
- They are implicitly closed and cannot be inherited from;
- They may not have any member functions, properties not declared in the primary constructor or any overriding declarations;
- They cannot have companion objects;
- They cannot have nested classes;
- They cannot have type parameters;
- The types of primary constructor parameters are limited to:
    - `kotlin.String`;
    - `kotlin.KClass`;
    - [Built-in number types][Built-in types];
    - Other annotation types;
    - Arrays of any other allowed type.

Annotation classes cannot be constructed directly, but their primary constructors are used when specifying [code annotations][Annotations] for other entities.

#### Interface declaration

Interfaces differ from classes in that they cannot be directly instantiated in the program, they are meant as a way of describing a contract which should be satisfied by the interface's subtypes. 
In other aspects they are similar to classes, therefore we shall specify their declarations by specifying their differences from class declarations.

* An interface cannot have a class as its supertype;
* An interface cannot have a constructor;
* Interface properties cannot have initializers or backing fields;
* Interface properties cannot be delegated;
* An interface cannot have inner classes (but can have nested classes and companion objects);
* An interface and all its members are implicitly open;
* All interface member properties and functions are implicitly public;
    * Trying to declare a non-public member property or function in an interface is an compile-time error.

##### Functional interface declaration

A *functional interface* is an interface with a **single** abstract function and no other abstract properties or functions.

A function interface declaration is marked as `fun interface`.
It has the following additional restrictions compared to regular [interface declarations][Interface declaration].

* A functional interface can have only one abstract member function, which must be non-parameterized;
* A functional interface cannot have any abstract member properties;

A functional interface has an associated [function type][Function types], which is the same as the function type of its single abstract member function.

> Important: the associated function type of a functional interface is different from the type of said functional interface.

If one needs an object of a functional interface type, they can use the regular ways of implementing an interface, either via an [anonymous object declaration][Object literals] or as a complete [class][Classifier declaration].
However, as functional interface essentially represents a single function, Kotlin supports the following additional ways of providing a functional interface implementation from function values (expressions with function type).

* If a lambda literal `L` is preceded with a functional interface name `T`, and the type of `L` is a subtype of the associated function type of `T`, this expression creates an instance of `T` with lambda literal `L` used as its abstract member function implementation.

> Example:
> 
> ```kotlin
> fun interface FI {
>      fun bar(s: Int): Int
> }
> 
> fun foo() {
>     val fi = FI { it }
>     val fi2 = FI { s: Int -> s + 42 }
>     val fi3 = FI { s: Number -> s.toInt() }
> }
> ```

* If an expression `L` is used as an argument of functional type `T` in a [function call][Function calls and property access], and the type of `L` is a subtype of the associated function type of `T`, this argument is considered as an instance of `T` with expression `L` used as its abstract member function implementation.

> Example:
> 
> ```kotlin
> fun interface FI {
>     fun bar(s: Int): Int
> }
> 
> fun doIt(fi: FI) {}
> 
> fun foo() {
>     doIt { it }
>     doIt { s: Int -> s + 42 }
>     doIt { s: Number -> s.toInt() }
> 
>     doIt(fun(s): Int { return s; })
> 
>     val l = { s: Number -> s.toInt() }
> 
>     doIt(l)
> }
> ```

> Informally: this feature is known as "Single Abstract Method" (SAM) conversion.

> Note: in Kotlin version 1.3 and earlier, SAM conversion was not available for Kotlin functional interfaces.
> It was, however, available on Kotlin/JVM for Java functional interfaces.

#### Object declaration

Object declarations are similar to class declaration in that they introduce a new classifier type, but, unlike class or interface declarations, they also introduce a value of this type in the same declaration.
No other values of this type may be declared, making object a single existing value of its type.

> Note: This is similar to *singleton pattern* common to object-oriented programming in introducing a type which includes a single global value.

Similarly to interfaces, we shall specify object declarations by highlighting their differences from class declarations.

* An object can only be declared in a declaration scope;
* An object type cannot be used as a supertype for other types;
* An object cannot have an explicit primary or secondary constructor;
* An object cannot have a companion object;
* An object may not have inner classes;
* An object cannot be parameterized, i.e., cannot have type parameters.

> Note: an object is assumed to implicitly have a default parameterless primary constructor.

> Note: this section is about declaration of _named_ objects. 
> Kotlin also has a concept of _anonymous_ objects, or object literals, which are similar to their named counterparts, but are expressions rather than declarations and, as such, are described in the [corresponding section][Object literals].

#### Classifier initialization

When creating a class or object instance via one of its constructors $ctor$, it is initialized in a particular order, which we describe here.

A primary $pctor$ or secondary constructor $ctor$ has a corresponding superclass constructor $sctor$ defined as follows.

* For primary constructor $pctor$, a corresponding superclass constructor $sctor$ is the one from the supertype specifier list;
* For secondary constructor $ctor$, a corresponding supertype constructor $sctor$ is the one ending the constructor delegation chain of $ctor$;
* If an explicit superclass constructor is not available, `Any()` is implicitly used.

When a classifier type is initialized using a particular secondary constructor $ctor$ delegated to primary constructor $pctor$ which, in turn, is delegated to the corresponding superclass constructor $sctor$, the following happens, in this *initialization order*:

- $pctor$ is invoked using the specified parameters, initializing all the properties declared by its property parameters *in the order of appearance in the constructor declaration*;
- The superclass object is initialized as if created by invoking $sctor$ with the specified parameters;
- Interface delegation expressions are invoked and the result of each is stored in the object to allow for interface delegation, *in the order of appearance of delegation declarations in the supertype specifier list*;
- Each property initialization code as well as the initialization blocks in the class body are invoked *in the order of appearance in the class body*;
- $ctor$ body is invoked using the specified parameters.

> Note: this means that if an `init`-block appears between two property declarations in the class body, its body is invoked between the initialization code of these two properties.

The initialization order stays the same if any of the entities involved are omitted, in which case the corresponding step is also omitted (e.g., if the object is created using the primary constructor, the body of the secondary one is not invoked).

If any step in the initialization order creates a loop, it is considered to be undefined behaviour.

If any of the properties are accessed before they are initialized w.r.t initialization order (e.g., if a method called in an initialization block accesses a property declared *after* the initialization block), the value of the property is undefined.

> Note: this can also happen if a property is captured in a lambda expression used in some way during subsequent initialization steps.

> Examples:
> 
> ```kotlin
> open class Base(val v: Any?) {
>     init {
>         println("2: $this")
>     }
> }
> 
> class Init(val a: Number /* (1) */) : Base(0xC0FFEE) /* (2) */ {
> 
>     init {
>         println("3: $this") /* (3) */
>     }
> 
>     constructor(v: Int) : this(v as Number) {
>         println("10: $this") /* (10) */
>     }
> 
>     val b: String = a.toString() /* (4) */
> 
>     init {
>         println("5: $this") /* (5) */
>     }
> 
>     var c: Any? = "b is $b" /* (6) */
> 
>     init {
>         println("7: $this") /* (7) */
>     }
> 
>     val d: Double = 42.0 /* (8) */
> 
>     init {
>         println("9: $this") /* (9) */
>     }
> 
>     override fun toString(): String {
>         return "Init(a=$a, b='$b', c=$c, d=$d)"
>     }
> }
> 
> fun main() {
>     Init(5)
>     // 2: Init(a=null, b='null', c=null, d=0.0)
>     // 3: Init(a=5, b='null', c=null, d=0.0)
>     // 5: Init(a=5, b='5', c=null, d=0.0)
>     // 7: Init(a=5, b='5', c=b is 5, d=0.0)
>     // 9: Init(a=5, b='5', c=b is 5, d=42.0)
>     // 10: Init(a=5, b='5', c=b is 5, d=42.0)
>     
>     // Here we can see how the undefined values for
>     //   uninitialized properties may leak outside
> }
> ```

### Function declaration

:::{.paste target=grammar-rule-functionDeclaration}
:::
:::{.paste target=grammar-rule-functionBody}
:::

Function declarations assign names to functions --- blocks of code which may be called by passing them a number of arguments. 
Functions have special *function types* which are covered in more detail [here][Function types].

A simple function declaration consists of four main parts:

* Name $f$;
* Parameter list $(p_1: P_1 [= v_1], \ldots, p_n: P_n [= v_n])$;
* Return type $R$;
* Body $b$.

and has a function type $f : (p_1: P_1, \ldots, p_n: P_n) \rightarrow R$.

Parameter list $(p_1: P_1 [= v_1], \ldots, p_n: P_n [= v_n])$ describes function parameters, i.e. inputs needed to execute the declared function. 
Each parameter $p_i: P_i = v_i$ introduces $p_i$ as a name of value with type $P_i$ available inside function body $b$; therefore, parameters are final and cannot be changed inside the function. 
A function may have zero or more parameters.

A parameter may include a default value $v_i$, which is used if the corresponding argument is not specified in function invocation; $v_i$ must be an expression which evaluates to type $V <: P_i$.

Return type $R$, if omitted, is calculated as follows.

* If function body $b$ is present in the expression form and it may be inferred to have a valid type $B : B \not \equiv \Nothing$, $R \equiv B$.
* If function body $b$ is present in the block form, $R \equiv \Unit$.

In other cases return type $R$ cannot be omitted and must be specified explicitly.

> As type `kotlin.Nothing` has a [special meaning][`kotlin.Nothing`] in Kotlin type system, it must be specified explicitly, to avoid spurious `kotlin.Nothing` function return types.

Function body $b$ is optional; if it is omitted, a function declaration creates an *abstract* function, which does not have an implementation.
This is allowed only inside an [abstract class][Abstract classes].
If a function body $b$ is present, it should evaluate to type $B$ which should satisfy $B <: R$.

TODO([Kotlin 1.3+, Experimental] `expect` and `external` functions also do not have implementations)

A parameterized function declaration consists of five main parts.

* Name $f$;
* Type parameter list $T_1, \ldots, T_m$;
* Parameter list $(p_1: P_1 = v_1, \ldots, p_n: P_n = v_n)$;
* Return type $R$;
* Body $b$.

and extends the rules for a simple function declaration w.r.t. type parameter list. Further details are described [here][Declarations with type parameters].

#### Function signature

In some cases we need to establish whether one function declaration *matches* another, e.g., for checking [overridability][Overriding].
To do that, we compare *function signatures*, which consist of the following.

* Name $f$;
* Type parameter list $T_1, \ldots, T_m$ (if present);
* Parameter list $P_1, \ldots, P_n$.

Two function signatures $A$ and $B$ are considered *matching*, if the following is true.

* Name of $A$ is the same as the name of $B$;
* Formal parameter types of $A$ are pairwise equal to the formal parameter types of $B$ w.r.t. possible type parameter substitutions;
* If the number of type parameters is the same, type parameters of $A$ must be pairwise [equivalent][Subtyping] to the type parameters of $B$.

> Important: a platform implementation may change which function signatures are considered matching, depending on the platform's specifics.

#### Named, positional and default parameters

Kotlin supports *named* parameters out-of-the-box, meaning one can bind an argument to a parameter in function invocation not by its position, but by its name, which is equal to the argument name.

```kotlin
fun bar(a: Int, b: Double, s: String): Double = a + b + s.toDouble()

fun main(args: Array<String>) {
    println(bar(b = 42.0, a = 5, s = "13"))
}
```

> Note: it is prohibited to bind the same named parameter to an argument several times, such invocations should result in a compile-time error.

All the names of named parameters are resolved at compile-time, meaning that performing a call with a parameter name not used at declaration-site is a compile-time error.

If one wants to mix named and positional arguments, the argument list must conform to the following form: $PoN_1, \ldots, PoN_M, N_1, \ldots, N_Q$, where $PoN_i$ is an i-th argument in either positional or named form, $N_j$ is a named argument irregardless of its position.

> Note: in Kotlin version 1.3 and earlier, $PoN_i$ were restricted to positional arguments only.

If one needs to provide a named argument to a [variable length parameter][Variable length parameters], it can be achieved via either regular named argument `arg = arr` or a spread operator expression form `arg = *arr`.
In both cases type of `arr` must be a subtype of [$\ATS(\Array(\outV T))$][Array types] for a variable length parameter of type $T$.

> Note: in Kotlin version 1.3 and earlier, only the spread operator expression form for named variable length arguments was supported.

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

* Zero or more arguments in either positional or named form;
* Zero or more named arguments.

Missing arguments are bound to their default values, if they exist.

#### Variable length parameters

One of the parameters may be designated as being variable length (aka *vararg*).
A parameter list $(p_1, \ldots, \text{vararg }p_i: P_i = v_i, \ldots, p_n)$ means a function may be called with any number of arguments in the i-th position.
These arguments are represented inside function body $b$ as a value $p_i$ of type, which is the result of [*array type specialization*][Array types] of type $\Array(\outV P_i)$.

> Important: we also consider variable length parameters to have such types for the purposes of type inference and calls with named parameters.

If a variable length parameter is not last in the parameter list, all subsequent arguments in the function invocation should be specified as named arguments.

If a variable length parameter has a default value, it should be an expression which evaluates to a value of type, which is the result of [*array type specialization*][Array types] of type  $\Array(\outV P_i)$.

A value of type $Q <: \ATS(\Array(\outV P_i))$ may be *unpacked* to a variable length parameter in function invocation using [spread operator][Spread operator expressions]; in this case array elements are considered to be separate arguments in the variable length parameter position.

> Note: this means that, for variable length parameters corresponding to specialized array types, unpacking is possible only for these specialized versions; for a variable length parameter of type `Int`, for example, unpacking is valid only for `IntArray`, and not for `Array<Int>`.

A function invocation may include several spread operator expressions corresponding to the vararg parameter.

#### Extension function declaration

An _extension function declaration_ is similar to a standard function declaration, but introduces an additional special function parameter, the _receiver parameter_. 
This parameter is designated by specifying the receiver type (the type before `.` in function name), which becomes the type of this receiver parameter. 
This parameter is not named and must always be supplied (either explicitly or implicitly), e.g. it cannot be a variable-argument parameter, have a default value, etc.

Calling such a function is special because the receiver parameter is not supplied as an argument of the call, but as the [_receiver_][Receivers] of the call, be it implicit or explicit. 
This parameter is available inside the scope of the function as the implicit receiver or `this`-expression, while nested scopes may introduce additional receivers that take precedence over this one. 
See [the receiver section][Receivers] for details. 
This receiver is also available (as usual) in nested scope using labeled `this` syntax using the name of the declared function as the label.

For more information on how a particular receiver for each call is chosen, please refer to the [overloading section][Overload resolution].

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

#### Inlining

A function may be declared `inline` using a special `inline` modifier.
This allows the compiler to inline the function at call-site, replacing the call with the body of the function with arguments mapped to corresponding parameters.
It is unspecified whether inlining will actually be performed, however.

Declaring a function `inline` has two additional effects:

- It allows type parameters of the function to be declared `reified`, making them [runtime-available][Runtime-available types] and allowing usage of specific expressions involving these parameters, such as [type checks][Type-checking expression] and [class literals][Class literals].
  Calling such a function is only allowed in a context where a particular type argument provided for this type parameter is also a runtime-available type.
- Any parameter of this function of a [function type][Function types] is treated as *inlined* parameter unless it has one of two special modifiers: `crossinline` or `noinline`.
  If a particular argument corresponding to inline parameter is a [lambda literal][Lambda literals], this lambda literal is considered *inlined* and, in particular, affects the way the [return expressions][Return expressions] are handled in its body. See the corresponding section for details.

Inlined parameters are not allowed to escape the scope of the function body, meaning that they cannot be stored in variables, returned from the function or captured by other values.
They may only be called inside the function body.

Crossinline parameters may not be stored or returned from the function, but may be captured (for example, by [object literals][Object literals] or other non-inlined lambda literals).

Noinline parameters may be treated as any other values.

Particular platforms may introduce additional restrictions or guarantees for the inlining mechanism.

#### Tail recursion optimization

A function may be declared *tail-recursive* by using a special `tailrec` modifier.
A tail-recursive function that contains a recursive call to itself may be optimized to a non-recursive form by a particular platform in order to avoid problems of recursion such as a possibility of stack overflows possible on some platforms.

In order to be applicable for such an optimization, the function must adhere to tail recursive form: for all paths containing recursive calls the result of the recursive call must also be the result of the function.
If a function declaration is marked with the `tailrec` modifier, but is not actually applicable for the optimization, it must produce a compile-time warning.

### Property declaration

:::{.paste target=grammar-rule-propertyDeclaration}
:::

Kotlin uses *properties* to represent object-like entities, such as local variables, class fields or top-level values.

Property declarations may create read-only (`val`) or mutable (`var`) entities in their respective scope.

Properties may also have custom getter or setter --- special functions which are used to read or write the property value.
Getters and setters cannot be called directly, but rather define how the corresponding properties are evaluated when accessed.

#### Read-only property declaration

A read-only property declaration `val x: T = e` introduces `x` as a name of the result of `e`. 

A read-only property declaration may include a custom [getter][Getters and setters] in the form of

```kotlin
val x: T = e
    get(): T { ... } // (1)
```

or

```kotlin
val x: T = e
    get(): T = ... // (2)
```

in which case `x` is used as a synonym to the getter invocation. 
All of the right-hand value `e`, the type `T` in both positions, and the getter are optional, however, at least one of them must be specified. 
More so, if we cannot infer the resulting property type from the type of `e` or from the type of getter in expression form (2), the type `T` must be specified explicitly either as the property type, or as the getter return type.
In case both `e` and `T` are specified, the type of `e` must be a subtype of `T` (see [subtyping][Subtyping] for more details).

The initializer expression `e`, if given, serves as the starting value for the property backing field (see [getters and setters section][Getters and setters] for details) and is evaluated when the property is created.
Properties that are not allowed to have backing fields (see [getters and setters section][Getters and setters] for details) are also not allowed to have initializer expressions.

> Note: although a property with an initializer expression looks similar to an [assignment][Assignments], it is different in several key ways: first, a read-only property cannot be assigned, but may have an initializer expression; second, the initializer expression never invokes the property setter, but assigns the property backing field value directly.

#### Mutable property declaration

A mutable property declaration `var x: T = e` introduces `x` as a name of a mutable variable with type `T` and initial value equals to the result of `e`. 
The rules regarding the right-hand value `e` and the type `T` match those of a read-only property declaration.

A mutable property declaration may include a custom [getter][Getters and setters] and/or custom [setter][Getters and setters] in the form of

```kotlin
var x: T = e
    get(): TG { ... }
    set(value: TS) { ... }
```

in which case `x` is used as a synonym to the getter invocation when read from and to the setter invocation when written to.

#### Local property declaration

If a property declaration is local, it creates a local entity which follows most of the same rules as the ones for regular property declarations.
However, local property declarations cannot have custom getters or setters.

Local property declarations also support [*destructuring* declaration][Destructuring declarations] in the form of

```kotlin
val (a: T, b: U, c: V, ...) = e
```

which is a syntactic sugar for the following expansion

```kotlin
val a: T = e.component1()
val b: U = e.component2()
val c: V = e.component3()
...
```

where `componentN()` should be a valid operator function available on the result of `e`.
Some of the entries in the destructuring declaration may be replaced with an *ignore marker* `_`, which signifies that no variable is declared and no `componentN()` function is called.

As with regular property declaration, type specification is optional, in which case the type is inferred from the corresponding `componentN()` function.
Destructuring declarations cannot use getters, setters or delegates and must be initialized in-place.

#### Getters and setters

As mentioned before, a property declaration may include a custom getter and/or custom setter (together called *accessors*) in the form of

```kotlin
var x: T = e
    get(): TG { ... }
    set(anyValidArgumentName: TS): RT { ... }
```

These functions have the following requirements

* $TG \equiv T$;
* $TS \equiv T$;
* $RT \equiv \mathtt{kotlin.Unit}$;
* Types $TG$, $TS$ and $RT$ are optional and may be omitted from the declaration;
  
* Read-only properties may have a custom getter, but not a custom setter;
* Mutable properties may have any combination of a custom getter and a custom setter
  
* Setter argument may have any valid identifier as argument name.

> Note: Regular coding convention recommends `value` as the name for the setter argument

One can also omit the accessor body, in which case a *default* implementation is used (also known as default accessor).

```kotlin
var x: T = e
    get
    set
```

> This notation is usually used if you need to change some aspects of an accessor (i.e., its visibility) without changing the default implementation.

Getters and setters allow one to customize how the property is accessed, and may need access to the property's *backing field*, which is responsible for actually storing the property data. 
It is accessed via the special `field` property available inside accessor body, which follows these conventions

* For a property declaration of type `T`, `field` has the same type `T`
* `field` is read-only inside getter body
* `field` is mutable inside setter body

However, the backing field is created for a property only in the following cases

* A property has no custom accessors;
* A property has a default accessor;
* A property has a custom accessor, and it uses `field` property;
* A mutable property has a custom getter or setter, but not both.

In all other cases a property has no backing field.
Properties without backing fields are not allowed to have initializer expressions.

Read/write access to the property is replaced with getter/setter invocation respectively. 
Getters and setters allow for some modifiers available for function declarations (for example, they may be declared `inline`, see grammar for details).
Properties themselves may also be declared `inline`, meaning that both getter and setter of said property are `inline`.

#### Delegated property declaration

A delegated read-only property declaration `val x: T by e` introduces `x` as a name for the *delegation* result of property `x` to the entity `e` or to the delegatee of `e` provided by [`provideDelegate`](#provide-delegate).
For the former, one may consider these properties as regular properties with a special *delegating* [getters][Getters and setters]:

```kotlin
val x: T by e
```

is the same as

```kotlin
val x$delegate = e
val x: T
    get(): T = x$delegate.getValue(thisRef, ::x)
```

Here every access to such property (`x` in this case) becomes an [overloadable][Operator overloading] form which is expanded into the following:

```kotlin
e.getValue(thisRef, property)
```

where 

- `e` is the delegating entity; the compiler needs to make sure that this is accessible in any place `x` is accessible;
- `getValue` is a suitable operator function available on `e`;
- `thisRef` is the [receiver][Receivers] object for the property.
  This argument is `null` for local properties;
- `property` is an object of the type `kotlin.KProperty<*>` that contains information relevant to `x` (for example, its name, see standard library documentation for details).

A delegated mutable property declaration `var x: T by e` introduces `x` as a name of a mutable entity with type `T`, access to which is *delegated* to the entity `e` or to the delegatee of `e` provided by [`provideDelegate`](#provide-delegate).
As before, one may view these properties as regular properties with special *delegating* [getters and setters][Getters and setters]:

```kotlin
var x: T by e
```

is the same as

```kotlin
val x$delegate = e
var x: T
    get(): T = x$delegate.getValue(thisRef, ::x)
    set(value: T) { x$delegate.setValue(thisRef, ::x, value) }
```

Read access is handled the same way as for a delegated read-only property. 
Any write access to `x` (using, for example, an assignment operator `x = y`) becomes an overloadable form with the following expansion:

```kotlin
e.setValue(thisRef, property, y)
```

where 

- `e` is the delegating entity; the compiler needs to make sure that this is accessible in any place `x` is accessible;
- `getValue` is a suitable operator function available on `e`;
- `thisRef` is the [receiver][Receivers] object for the property.
  This argument is `null` for local properties;
- `property` is an object of the type `kotlin.KProperty<*>` that contains information relevant to `x` (for example, its name, see standard library documentation for details);
- `y` is the value `x` is assigned to.
  In case of complex assignments (see the [assignment][Assignments] section), as they are all overloadable forms, first the assignment expansion is performed, and after that, the expansion of the delegated property using normal assignment.

The type of a delegated property may be omitted at the declaration site, meaning that it may be [inferred][Type inference] from the delegating function itself, as it is with regular getters and setters.
If this type is omitted, it is inferred as if it was assigned the value of its expansion.
If this inference fails, it is a compile-time error.

::: {#provide-delegate}
If the delegate expression has a suitable operator function called `provideDelegate`, a *provided* delegate is used instead.
The provided delegate is accessed using the following expansion:

```kotlin
val x: T by e
```

is the same as

```kotlin
val x$delegate = e.provideDelegate(thisRef, ::x)
val x: T
    get(): T = x$delegate.getValue(thisRef, ::x)
```

and

```kotlin
var x: T by e
```

is the same as

```kotlin
val x$delegate = e.provideDelegate(thisRef, ::x)
val x: T
    get(): T = x$delegate.getValue(thisRef, ::x)
    set(value) { x$delegate.setValue(thisRef, ::x, value) }
```

where `provideDelegate` is a suitable operator function available using the receiver `e`, while `getValue` and `setValue` work the same way they do with normal property delegation.
As is the case with`setValue` and `getValue`,  `thisRef`  is a reference to the receiver of the property or `null` for local properties, but there is also a special case: for extension properties `thisRef` supplied to `provideDelegate` is `null`, while `thisRef` provided to `getValue` and `setValue` is the actual receiver.
This is due to the fact that, during the creation of the property, no receiver is available.
:::

For both provided and standard delegates, the generated delegate value is placed in the same context as its corresponding property.
This means that for a class member property it will be a synthetic member, for a local property it is a local value in the same scope as the property and for top-level (both extension and non-extension) properties it will be a top-level value.
This affects this value's lifetime in the same way normal value lifetime works.

> Example:
> 
> ```kotlin
> operator fun <V, R : V> Map<in String, V>.getValue(
>         thisRef: Any?, property: KProperty<*>): R =
>     getOrElse(property.name) {
>         throw NoSuchElementException()
>     } as R
> 
> operator fun <V> MutableMap<in String, V>.setValue(
>         thisRef: Any?, property: KProperty<*>, newValue: V) =
>     set(property.name, newValue)
> 
> fun handleConfig(config: MutableMap<String, Any?>) {
>     val parent by config       // Any?
>     val host: String by config // String
>     var port: Int by config    // Int
> 
>     // Delegating property accesses to Map.getValue
>     // Throwing NSEE as there is no "port" key in the map
>     // println("$parent: going to $host:$port")
> 
>     // Delegating property access to Map.setValue
>     port = 443
>     // Map now contains "port" key
> 
>     // Delegating property accesses to Map.getValue
>     // Not throwing NSEE as there is "port" key in the map
>     println("$parent: going to $host:$port")
> }
> 
> fun main() {
>     handleConfig(mutableMapOf(
>         "parent" to "",
>         "host" to "https://kotlinlang.org/"
>     ))
> }
> ```

> Example with `provideDelegate`:
> 
> ```kotlin
> operator fun <V> MutableMap<in String, V>.provideDelegate(
>         thisRef: Any?,
>         property: KProperty<*>): MutableMap<in String, V> =
>     if (containsKey(property.name)) this
>     else throw NoSuchElementException()
> 
> operator fun <V, R : V> Map<in String, V>.getValue(
>         thisRef: Any?, property: KProperty<*>): R = ...
> 
> operator fun <V> MutableMap<in String, V>.setValue(
>         thisRef: Any?, property: KProperty<*>, newValue: V) = ...
> 
> fun handleConfig(config: MutableMap<String, Any?>) {
>     val parent by config       // Any?
>     val host: String by config // String
>     var port: Int by config    // Int
>     // Throwing NSEE here as `provideDelegate`
>     //   checks for "port" key in the map
> 
>     ...
> }
> 
> fun main() {
>     handleConfig(mutableMapOf(
>         "parent" to "",
>         "host" to "https://kotlinlang.org/"
>     ))
> }
> ```

#### Extension property declaration

An _extension property declaration_ is similar to a standard property declaration, but, very much alike an [extension function][Extension function declaration], introduces an additional parameter to the property called _the receiver parameter_. 
This is different from usual property declarations, that do not have any parameters. There are other differences from standard property declarations:

- Extension properties cannot have initializers;
- Extension properties cannot have backing fields;
- Extension properties cannot have default accessors.

> Note: informally, on can say that extension properties have no state of their own. 
> Only properties that use other objects' storage facilities and/or uses constant data can be extension properties.

Aside from these differences, extension properties are similar to regular properties, but, when accessing such a property one always need to supply a [_receiver_][Receivers], implicit or explicit. 
Like for regular properties, the type of the receiver must be a subtype of the receiver parameter, and the value that is supplied as the receiver is bound to the receiver parameter. 
For more information on how a particular receiver for each access is chosen, please refer to the [overloading section][Overload resolution].

The receiver parameter can be accessed inside getter and setter scopes of the property as the implicit receiver or `this`. 
It may also be accessed inside nested scopes using [labeled `this` syntax][This-expressions] using the name of the property declared as the label. 
For delegated properties, the value passed into the operator functions `getValue` and `setValue` as the receiver is the value of the receiver parameter, rather than the value of the outer classifier. 
This is also true for local extension properties: while regular local properties are passed `null` as the first argument of these operator functions, local extension properties are passed the value of the receiver argument instead.

> Note: when declaring extension properties inside classifier declarations, this receiver takes precedence over the classifier object, which is usually the current receiver inside nested properties

For all other purposes, extension properties are not different from non-extension properties.

> Examples:
> 
> ```kotlin
> val Int.foo: Int get() = this + 1
> 
> fun main(args: Array<String>) {
>     println(2.foo.foo) // prints "4"
> }
> 
> class Bar {
>     val foo get() = this // returns type Bar
>     val Int.foo get() = this // returns type Int
> }
> ```

#### Property initialization

All non-abstract properties must be definitely initialized before their first use. 
To guarantee this, Kotlin compiler uses a number of analyses which are described in more detail [here][Control- and data-flow analysis].

#### Constant properties

A property may be declared **constant**, meaning that its value is known during compilation, by using the special `const` modifier. 
In order to be declared `const`, a property must meet the following requirements:

- Its type is one of the following:
    - One of the [the built-in integral types][Built-in integer types];
    - One of the [the built-in floating types][Built-in floating point arithmetic types];
    - `kotlin.Boolean`;
    - `kotlin.Char`;
    - `kotlin.String`;
- It is declared in the top-level scope or inside [an object declaration][Classifier declaration];
- It has an initializer expression and this initializer expression can be evaluated at compile-time.
  Integer literals and string interpolation expressions without evaluated expressions, as well as built-in arithmetic/comparison operations and string concatenation operations on those are such expressions, as well as other constant properties, but it is implementation-defined which other expressions qualify for this;
- It does not have getters, setters or delegation specifiers.

> Example:
> ```kotlin
> // Correct constant properties
> const val answer = 2 * 21
> const val msg = "Hello World!"
> const val calculated = answer + 45
> 
> // Incorrect constant property
> const val emptyStringHashCode = "".hashCode()
> ```

#### Late-initialized properties

A mutable member property can be declared with a special `lateinit` modifier, effectively turning off the [property initialization checks][Property initialization] for it.
Such a property is called late-initialized and may be used for values that are supposed to be initialized not during object construction, but during some other time (for example, a special initialization function).
This means, among other things, that it is the responsibility of the programmer to guarantee that the property is initialized before its usage.

A property may be declared late-initialized if:

- It has no custom getters, setters or delegation;
- It is a member or a top-level property;
- It is mutable;
- It has declared non-nullable type which is also not one of the following types:
    + One of the [built-in integer types][Built-in integer types];
    + One of the [built-in floating types][Built-in floating point arithmetic types];
    + [`kotlin.Boolean`][`kotlin.Boolean`];
    + [`kotlin.Char`][`kotlin.Char`].

### Type alias

:::{.paste target=grammar-rule-typeAlias}
:::

Type alias introduces an alternative name for the specified type and supports both simple and parameterized types. 
If type alias is parameterized, its type parameters must be [unbounded][Type parameters]. 
Another restriction is that recursive type aliases are forbidden --- the type alias name cannot be used in its own right-hand side.

At the moment, Kotlin supports only top-level type aliases. 
The scope where it is accessible is defined by its [*visibility modifiers*][Declaration visibility].

### Declarations with type parameters

Most declarations may be introduced as *generic*, introducing type parameters that must be explicitly specified or [inferred][Type inference] when the corresponding declaration is used.
For declarations that introduce new types this mechanism provides the means of introducing a [parameterized type][Parameterized classifier types].
Please refer to the corresponding section for details.

Type parameters may be used as types inside the scope introduced by the declaration.
When such a declaration is used, the parameters are substituted by types available inside the scope the declaration is used in.

The following declarations are not allowed to have type parameters:

- Non-extension property declarations;
- Object declarations (including companion object declarations);
- Constructor declarations;
- Getters and setters of property declarations;
- Enum class declarations;
- Annotation class declarations;
- Classifier declarations inheriting from `kotlin.Throwable`.

Type parameters are allowed to specify *subtyping restrictions* on them in the form `T : U`, meaning $T <: U$ where $T$ is a type parameter and $U$ is some other type available in the scope the declaration is declared in.
These either are written directly at the parameter placement syntax or using a special `where` syntax.
Any number of restrictions is allowed on a single type, however, for a given type parameter `T`, only one restriction `T : U` can have `U` to be another type parameter.

These restrictions are turned into corresponding [type constraints][Kotlin type constraints] when the type parameters are substituted with types and are employed during [type inference][Type inference] and [overload resolution][Overload resolution] of any usage of the corresponding declaration.
See the corresponding sections for details.

Type parameters do not introduce [runtime-available types][Runtime-available types] unless declared `reified`.
Only type parameters of [inline functions][Inlining] can be declared `reified`.

#### Type parameter variance

The [declaration-site variance][Mixed-site variance] of a particular type parameter for a classifier declaration is specified using special keywords `in` (for covariant parameters) and `out` (for contravariant parameters).
If the variance is not specified, the parameter is implicitly declared invariant.
See [the type system section][Mixed-site variance] for details.

A type parameter is **used in covariant position** in the following cases:

- It is used as an argument in another generic type and the corresponding parameter in that type is covariant;
- It is the return type of a function;
- It is a type of a property.

A type parameter is **used in contravariant position** in the following cases:

- It is used as an argument in another generic type and the corresponding parameter in that type is contravariant;
- It is a type of an parameter of a function;
- It is a type of a mutable property.

A type parameter is used in an invariant position if it is used as an argument in another generic type and the corresponding parameter in that type is invariant.

A usage of a contravariant type parameter in a covariant or invariant position, as well as usage of a covariant type parameter in a contravariant or invariant position, results in **variance conflict** and a compiler error, unless the containing declaration is private to the type parameter owner (in which case its visibility is restricted, see the [visibility][Declaration visibility] section for details).

This restrictions may be lifted in particular cases by [annotating][Annotations] the corresponding type parameter usage with a special built-in annotation `kotlin.UnsafeVariance`.
By supplying this annotation the author of the code explicitly declares that safety features that variance checks provide are not needed in this particular declarations.

TODO: examples

#### Reified type parameters

Type parameters of inline function declarations (and only those) can be declared `reified` using the corresponding keyword.
A reified type parameter is a [runtime-available][Runtime-available types] type inside the function scope, see the corresponding section for details.
Reified type parameters can only be substituted by other [runtime-available types][Runtime-available types] when using such functions.

### Declaration visibility

Each declaration has a visibility property relative to the scope it is declared in.
By default, all the declarations are `public`, meaning that they can be accessed from any other scope their outer scope can be accessed from.
The only exception to this rule are [overriding declarations][Overriding] that by default inherit the visibility from the declaration they override.
Declarations may be also marked `public` explicitly.

Declarations marked as `private` can only be accessed from the same scope they are declared in.
For example, all `private` top-level declarations in a file may only be accessed by code from the same file.

Some `private` declarations are special in that they have an even more restricted visibility, called "`private` to `this`".
These include declarations that are allowed to lift certain [variance][Mixed-site variance] rules in their types as long as they are never accessed outside `this` object, meaning that they can be accessed using `this` as the receiver, but are not visible on other instances of the same class even in the methods of this class.
For example, for a class declaration $C$ with type parameter $T$ it is not allowed to introduce declarations involving $T$ with conflicting variance, unless they are declared `private`.
That is, if $T$ is declared as covariant, any declarations with a type using $T$ in a contravariant position (including properties with type $T$ itself if they are mutable) and if $T$ is declared as contravariant, any declarations with a type using $T$ in a covariant position (including properties with type $T$ itself) are forbidden, unless they are declared using `private` visibility, in which case they are instead treated as "`private` to `this`".

> Note: the above does not account for `@UnsafeVariance` annotation that lifts any variance restrictions on type parameters

TODO: examples

Declarations marked as `internal` may only be accessed from the same [module][Modules], treated as `public` from inside the module and as `private` from outside the module.

Declarations in classifier declaration scope can also be declared `protected`, meaning that they can only be accessed from the same classifier type as well as any types [inheriting][Inheritance] from this type regardless of the scope they are declared in.

There is a partial order of *weakness* between different visibility modifiers:

- `protected` and `internal` are weaker than `private`;
- `public` is weaker than `protected` and `internal`.

> Note: there is a certain restriction regarding `inline` functions that have a different visibility from entities they access.
> In particular, an `inline` function cannot access entities with a stronger visibility (i.e. `public inline` function accessing a `private` property).
> There is one exception to this: a `public inline` function can access `internal` entities which are marked with a special builtin [annotation][Annotations] `@PublishedApi`.



