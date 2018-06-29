## Declarations

TODO(Examples)

Declarations in Kotlin are used to introduce entities (values, types, etc.); most declarations are *named*, i.e. they also assign an identifier to their own entity, however, some declarations may be *anonymous*.

Every declaration is accessible in a particular *scope*, which is dependent both on where the declaration is located and on the declaration itself.

### Classifier declaration

**_classDeclaration_:**  
  ~  [_modifierList_] (`class` | `interface`) {_NL_} _simpleIdentifier_   
      [{_NL_} _typeParameters_] [{_NL_} _primaryConstructor_]   
      [{_NL_} `:` {_NL_} _delegationSpecifiers_]   
      [{_NL_} _typeConstraints_]   
      [{_NL_} _classBody_ | {_NL_} _enumClassBody_]   

**_objectDeclaration_:**  
  ~  [_modifierList_] `object`   
      {_NL_} _simpleIdentifier_   
      [{_NL_} `:` {_NL_} _delegationSpecifiers_]   
      [{_NL_} _classBody_]   

### Function declaration

**_functionDeclaration_:**  
  ~  [_modifierList_]   
    `fun`   
    [{_NL_} _typeParameters_]   
    [{_NL_} _type_ {_NL_} `.`]
    ({_NL_} _simpleIdentifier_)   
    {_NL_} _functionValueParameters_   
    [{_NL_} `:` {_NL_} _type_]   
    [{_NL_} _typeConstraints_]   
    [{_NL_} _functionBody_]   

Function declarations assign names to such entities as functions --- blocks of code which may be called by passing them a number of arguments. Functions have special *function types* which are covered in more detail [here][Function types].

A simple function declaration consists of four main parts

* name $f$
* parameter list $(p_1: P_1 = v_1, \ldots, p_n: P_n = v_n)$
* return type $R$
* body $b$

and creates a function type $(P_1, \ldots, P_n) \rightarrow R$.

Parameter list $(p_1: P_1 = v_1, \ldots, p_n: P_n = v_n)$ describes function parameters --- inputs needed to execute the declared function. Each parameter $p_i: P_i = v_i$ introduces $p_i$ as a name of value with type $P_i$ available inside function body $b$. A function may have zero or more parameters.

A parameter may include a default value $v_i$, which is used if the corresponding argument is not specified in function invocation; $v_i$ should be an expression which evaluates to type $V <: P_i$.

Return type $R$ is optional, if function body $b$ is present and may be inferred to have a valid type $B : B \not \equiv kotlin.Nothing$, in which case $R \equiv B$. In other cases return type $R$ must be specified explicitly.

> As type $kotlin.Nothing$ has a [special meaning][`kotlin.Nothing`] in Kotlin type system, it must be specified explicitly, to avoid spurious $kotlin.Nothing$ function return types.

Function body $b$ is optional; if it is ommited, a function declaration creates an *abstract* function, which does not have an implementation. This is allowed only inside an [abstract classifier declaration][Classifier declaration]. If a function body $b$ is present, it should evaluate to type $B$ which should satisfy $B <: R$.

A parameterized function declaration consists of five main parts

* name $f$
* type parameter list $T_1, \ldots, T_m$
* parameter list $(p_1: P_1 = v_1, \ldots, p_n: P_n = v_n)$
* return type $R$
* body $b$

and extends the rules for a simple function declaration w.r.t. type parameter list.

TODO(type parameters)

#### Named and positional parameters

TODO()

#### Variable length parameters

One of the parameters may be designated as being variable length (aka *vararg*). A parameter list $(p_1, \ldots, \text{vararg }p_i: P_i = v_i, \ldots, p_n)$ means a function may be called with any number of arguments in the i-th position. These arguments are represented inside function body $b$ as an [array of type $P_i$][Array types].

If a variable length parameter is not last in the parameter list, all subsequent arguments in the function invocation should be specified as named arguments. If a variable length parameter has a default value, it should be an expression which evaluates to an [array of type $P_i$][Array types].

An array of type $Q <: P_i$ may be *unpacked* to a variable length parameter in function invocation using [spread operator][Spread operator]; in this case array elements are considered to be separate arguments in the variable length parameter position. A function invocation may include several spread operator expressions corresponding to the vararg parameter.

#### Function type parameters

TODO()

#### Extension function declaration

TODO()

### Property declaration

**_propertyDeclaration_:**  
  ~  [_modifierList_] (`val` | `var`)   
      [{_NL_} _typeParameters_]   
      [{_NL_} _type_ {_NL_} `.`]   
      ({_NL_} (_multiVariableDeclaration_ | _variableDeclaration_))   
      [{_NL_} _typeConstraints_]   
      [{_NL_} (`by` | `=`) {_NL_} _expression_]   
      [NL+ `;`] {_NL_} [[_getter_] ({_NL_} [_semi_] _setter_] | [_setter_] [{_NL_} [_semi_] _getter_])   

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
    set(anyValidArgumentName: TS) { ... }
```

These functions have the following requirements

* $TG \equiv T$
* $TS \equiv T$
* Both $TG$ and $TG$ types are optional and may be omitted from the declaration
  
* Read-only properties may have a custom getter, but not a custom setter
* Mutable properties may have any combination of a cusom getter and a custom setter
  
* Setter argument may have any valid argument name

> Regular coding convention recommends `value` as the name for the setter argument

One can also ommit the accessor body, in which case a *default* implementation is used (also known as default accessor).

```kotlin
var x: T = e
    get
    set
```

> This notation is usually used if you need to change some aspects of an accessor (i.e., its visibility) without changing the default implementation.

Getters and setters allow one to customize how the property is accessed, and may need access to the property's *backing field*, which is responsible for actually storing the property data. It is accessed via the special `field` property available inside accessor body, which follows these conventions

* For a property declaration of type `T`, field` has the same type `T`
* `field` is read-only inside getter body
* `field` is mutable inside setter body

However, the backing field is created for a property only in the following cases

* A property has no custom accessors
* A property has a default accessor
* A property has a custom accessor, and it uses `field` property
* A mutable property has a custom getter or setter, but not both

In all other cases a property has no backing field.

Read/write access to the property is replaced with getter/setter invocation respectively.

#### Property initialization

All non-abstract properties must be definitely initialized before their first use. To guarantee this, Kotlin compiler uses a number of analyses which are described in more detail [here][Control- and data-flow analysis].

TODO(Property declaration scope?)

TODO(lateinit?)

TODO(abstract?)

### Type alias

**_typeAlias_:**  
  ~  [_modifierList_] `typealias` {_NL_} _simpleIdentifier_ [{_NL_} _typeParameters_] {_NL_} `=` {_NL_} _type_   

Type alias introduces an alternative name for the specified type and supports both simple and parameterized types. If type alias is parameterized, its type parameters must be [unbounded][Type parameters]. Another restriction is that recursive type aliases are forbidden --- the type alias name cannot be used in its own right-hand side.

At the moment, Kotlin supports only top-level type aliases. The scope where it is accessible is defined by its [*visibility modifiers*][Visibility].
