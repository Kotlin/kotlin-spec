## Declarations

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
    get() { ... }
    set(value) { ... }
```

in which case `x` is used as a synonym to the getter invocation when read from and to the setter invocation when written to.

#### Delegated property declaration

A delegated read-only property declaration `val x: T by e` introduces `x` as a name for the *delegation* result of property `x` to the entity `e`. One may view these properties as regular properties with a special *delegating* [getters][Getters and setters].

In case of a delegated read-only property, access to `x` is replaced with the call to a special function `getValue`, which must be available on `e`. This function has the following signature

```kotlin
operator fun getValue(thisRef: T, property: KProperty<*>): R
```

where

TODO...

TODO(Property declaration scope?)

TODO(lateinit?)

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

TODO(Backing field or no backing field)

#### Property initialization

All non-abstract properties must be definitely initialized before their first use. To guarantee this, Kotlin compiler uses a number of analyses which are described in more detail [here][Control- and data-flow analysis].

### Type alias

**_typeAlias_:**  
  ~  [_modifierList_] `typealias` {_NL_} _simpleIdentifier_ [{_NL_} _typeParameters_] {_NL_} `=` {_NL_} _type_   

Type alias introduces an alternative name for the specified type and supports both simple and parameterized types. If type alias is parameterized, its type parameters must be [unbounded][Type parameters]. Another restriction is that recursive type aliases are forbidden --- the type alias name cannot be used in its own right-hand side.

At the moment, Kotlin supports only top-level type aliases. The scope where it is accessible is defined by its [*visibility modifiers*][Visibility].
