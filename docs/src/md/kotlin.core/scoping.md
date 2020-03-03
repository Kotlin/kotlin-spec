## Scopes and identifiers

Kotlin program is logically divided into _scopes_.

A scope is a syntactically-delimited region of code which constitutes a context in which entities and their names can be introduced.
Scopes can be nested, with entities introduced in outer scopes possibly available in the inner scopes if [linked][Linked scopes].

The top level of a Kotlin file is also a scope, containing all the scopes within a file.

All the scopes are divided into two kinds: declaration scopes and statement scopes.
These two kinds of scopes differ in how the identifiers may refer to the entities defined in the scopes.

Declaration scopes include:

- The project [modules][Modules];
- The project [packages][Packages and imports];
- The top level scopes of non-script Kotlin files;
- The bodies of [classifier declarations][Classifier declaration];
- The bodies of [object literals][Object literals];
- TODO(Anything else?)

Statement scopes include:

- The top level scopes of script Kotlin files;
- Scopes produced by control structure bodies of different [expressions][Expressions];
- The bodies of [function declarations][Function declaration];
- The bodies of [function literals][Function literals];
- The bodies of getters and setters of [properties][Property declaration];
- The bodies of [constructors][Constructor declaration];
- The bodies of instance initialization blocks in [classifier declarations][Classifier declaration];
- TODO(Anything else?)

All declarations in a particular scope introduce new _bindings_ of identifiers in this scope to their respective entities in the program.
These entities may be types or values, where values refer to objects, functions or properties (which may also be delegated).
Top-level scopes additionally allow to introduce new bindings using [`import` directives][Packages and imports] from other top-level scopes.

In most situations, it is not allowed to bind several values to the same identifier in the same scope, but it is allowed to bind a value to an identifier already available in the scope through linked scopes or imports.

An exception to this rule are [function declarations][Function declaration], which are matched by [signatures][Function signature] and allow defining several functions with the same name in the same scope.
When [calling functions][Call and property access expressions], a process called [overload resolution][Overload resolution] allows for differentiating between such functions.
Overload resolution also applies to properties if they are used as functions through `invoke`-convention, but it does not allow defining several properties with the same name in the same scope.

> Note: platforms may introduce additional restrictions on which identifiers may be declared together in the same or linked scopes.

The main difference between declaration scopes and statement scopes is that names in the statement scope are bound in the order of appearance.
It is not allowed to access a value through an identifier in code which (syntactically) precedes the binding itself.
On the contrary, in declaration scopes it is fully allowed, although initialization cycles may occur leading to possibly undefined behaviour.

> Note: Kotlin compiler may attempt to detect and report such initialization cycles as compile-time warnings or errors.

It also means that statement scopes nested inside declaration scopes may access values declared afterwards in parent declaration scopes, but any values declared inside a statement scope can be accessed only after their declaration point.

Examples:

- In declaration scope:
  ```kotlin
  // x refers to the property defined below 
  //   even if there is another property
  //   called x in outer scope or imported
  fun foo() = x + 2
  val x = 3
  ```
- In statement scope:
  ```kotlin
  // x refers to another property 
  //   defined in outer scope or imported
  //   or is a compile-time error
  fun foo() = x + 2
  val x = 3
  ```

TODO(qualified names?)

TODO(extensions?)

TODO(receivers)

TODO(rewrite expressions and statements as references to this part)

TODO(identifier lifetime & such)

### Linked scopes

Scopes `A` and `B` in a Kotlin program may be *downwards-linked* (`A ~> B`), meaning identifiers from `A` can be used in `B` without the need for additional qualification.
If scopes `A` and `B` are downwards-linked, scopes `B` and `A` are considered *upwards-linked* (`B <~ A`).

> Note: link relation is transitive, unless specified otherwise.

Scopes are downwards-linked (DLD) or upwards-linked (ULD) as follows:

- A statement scope is DLD to any directly nested scope;
- An [object declaration][Classifier declaration] scope is DLD to any nested scopes;
- An [object declaration][Classifier declaration] scope is non-transitively ULD to the companion object scopes of its superclasses;
- An [object declaration][Classifier declaration] scope is non-transitively ULD to the companion object scopes of its parent classifier superclasses;
- An [object declaration][Classifier declaration] scope is ULD to the companion object declaration scope of its parent classifier;
- A [companion object declaration][Classifier declaration] scope is DLD to any nested scopes;
- A [companion object declaration][Classifier declaration] scope is non-transitively ULD to the companion object scopes of its superclasses;
- A [companion object declaration][Classifier declaration] scope is non-transitively ULD to the companion object scopes of its parent classifier superclasses;
- A [companion object declaration][Classifier declaration] scope is ULD to the companion object declaration scope of the *parent* of its parent classifier;
- A [classifier or nested class declaration][Classifier declaration] scope is DLD to any nested statement scopes;
- A [classifier or nested class declaration][Classifier declaration] scope is ULD to its companion object declaration scope;
- An [inner class declaration][Nested and inner classifiers] scope is DLD to any nested statement scopes;
- An [inner class declaration][Nested and inner classifiers] scope is ULD to the classifier declaration scope of its parent classifier.

> Important: linked scopes **do not** cover cases when identifiers from supertypes are used in subtypes, as this is covered by the [inheritance][Inheritance] rules.

### Identifiers and paths

Kotlin program operates with different *entities*, such as classes, interfaces, values, etc.
An entity can be referenced using its *path*: a sequence of identifiers which references this entity in a given [scope][Scopes and identifiers].

Kotlin supports two kinds of paths.

* Simple paths `P`, which consist of a single identifier
* Qualified paths `P.m`, which consist of a path `P` and a member identifier `m`

Besides identifiers which are introduced by the developer (e.g., via declaring classes or introducing variables), there are several predefined identifiers with special semantics.

* `this` -- an identifier which references the default receiver available in the current scope, further details are available [here][This-expressions]
* `this@label` -- an identifier which references the default receiver available in the selected scope, further details are available [here][This-expressions]
* `super<Klazz>` -- an identifier which references the supertype `Klazz` available in the current scope, further details are available [here][Super-forms]
* `super<Klazz>@label` -- an identifier which references the supertype `Klazz` available in the selected scope, further details are available [here][Super-forms]

### Labels

Labels are special syntactic marks that mark certain code elements.
Any [expression][Annotated and labeled expression] (including [lambda expressions][Lambda literals]), as well as [loop statements][Loop statements], can be labeled, with label identifier assigned to the corresponding entity.
Labels can be **redeclared**, meaning that the same label identifier may be reused with different parts of code (or even on the same expression/loop) several times.
Labels are **scoped**, meaning that they are only available in the scope they were declared in.

Labels are used by certain expressions, such as [`break`][Break expression], [`continue`][Continue expression] and
[`return`][Return expressions] to specify exactly what entity the expression corresponds to.
Please refer to the corresponding sections for details.

When resolving labels (i.e. determining which label current expression refers to) which have been redeclared, the **closest** label is chosen, i.e. the label with the innermost scope which is syntactically the closest to the point of its usage.

TODO: this is a stub

### Visibility

TODO: remove this? See [Declaration visibility]

Any entity declared in a declaration scope has an implicitly or explicitly defined *visibility*.
The visibility of a declaration denotes whether the entity is *accessible* in other scopes of the program.
If the entity declared in scope `A` is not accessible in scope `B`, it cannot be referred to in that scope, making such reference a compiler error.
There are four basic types of visibility in kotlin: `public`, `private`, `internal` and `protected`, denoted by the corresponding visibility modifier keywords.

Entities that have `public` visibility are generally accessible anywhere outside or inside the scope they are declared in, by using imports or qualification when necessary.
Entities that have `private` visibility are only accessible inside the scope they are declared in or any nested scopes. 
For top-level scopes (i.e. kotlin files) it means that such entities are only accessible in the same file.
Entities with `internal` visibility are accessible inside the same [module][Modules] just like if the visibility was `public` and are inaccessible (as if the visibility was `private`) in any other scope.
Entities with `protected` visibility are only allowed in the body scope of class declarations and are used as a means of encapsulation: such entities are accessible inside any scope as if the visibility was `private`, but are also accessible by any derived classes' scopes as if they were declared `private` in the body scope of the derived class.
