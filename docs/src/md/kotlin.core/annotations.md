## Annotations

Annotations are a form of syntactically-defined metadata which may be associated with different entities in a Kotlin program.
Annotations are specified in the source code of the program and may be accessed on a particular platform using platform-specific mechanisms both by the compiler (and source-processing tools) and at runtime (using [reflection][Reflection] facilities).
Values of annotation types cannot be created directly, but can be operated on when accessed using platform-specific facilities.

### Annotation values

An annotation value is a value of a special [annotation type][Annotation declarations].
An annotation type is a special kind of class type which is allowed to include read-only properties of the following types:

- [Integer types][Built-in integer types];
- [Enum types][Enum class declaration];
- [String type][`kotlin.String`];
- Other annotation types;
- [Arrays][Array types] of any type listed above.

Annotation classes are not allowed to have any member functions, constructors or mutable properties.
They are also not allowed to have declared supertypes and are considered to be implicitly derived from `kotlin.Annotation`.

### Annotation retention

The retention level of an annotation declares which compilation artifacts (for a particular compiler on a particular platform) *retain* this kind of annotation.
There are the following types of retention available:

- Source retention (accessible by source-processing tools);
- Binary retention (retained in compilation artifacts);
- Runtime retention (accessible at runtime).

  > Each subsequent level inherits what is accessible on the previous levels.

For availability and particular ways of accessing the metadata specified by these annotations please refer to the corresponding platform-specific documentation.

### Annotation targets

The *target* of a particular type of annotations is the kind of program entity which this annotations may be placed on.
There are the following targets available:

- A [class declaration][Class declaration] (including annotation classes);
- An [annotation class declaration][Annotation class declaration];
- A [type parameter][Type parameters];
- A [property declaration][Property declaration];
- A [property backing field][Getters and setters];
- A [property getter][Getters and setters];
- A [property setter][Getters and setters];
- A [local property declaration][Local property declaration];
- A value parameter in [function][Function declaration] or [constructor][Constructor declaration] declaration;
- A [constructor][Constructor declaration];
- A [function declaration][Function declaration];
- A [type][Type system] usage;
- An arbitrary [expression][Expressions];
- A [Kotlin file][Scopes and identifiers];
- A [type alias declaration][Type alias].

### Annotation declarations

Annotations are declared using [annotation class declarations][Annotation class declaration].
See the corresponding section for details.

Annotations may be declared *repeatable* (meaning that the same annotation may be applied to the same entity more than once) or *non-repeatable* (meaning that only one annotation of a particular type may be applied to the same entity).

### Built-in annotations

* `kotlin.annotation.Retention`

  `kotlin.annotation.Retention` is an annotation which is only used on annotation classes to specify their annotation retention level.
  It has the following single field:

  * ```kotlin
    val value: AnnotationRetention = AnnotationRetention.RUNTIME
    ```

    The retention level of the annotated annotation.
  
  `kotlin.annotation.AnnotationRetention` is an enum class with the following values (see [Annotation retention section][Annotation retention] for details):
  
  * `SOURCE`;
  * `BINARY`;
  * `RUNTIME`.

* `kotlin.annotation.Target`

  `kotlin.annotation.Target` is an annotation which is only used on annotation classes to specify targets those annotations are valid for.
  It has the following single field:
  
  * ```kotlin
    vararg val allowedTargets: AnnotationTarget
    ```
  
    The allowed annotation targets of the annotated annotation.

  `kotlin.annotation.AnnotationTarget` is an enum class with the following values (see [Annotation targets section][Annotation targets] for details):
  
  * `CLASS`;
  * `ANNOTATION_CLASS`;
  * `TYPE_PARAMETER`;
  * `PROPERTY`;
  * `FIELD`;
  * `LOCAL_VARIABLE`;
  * `VALUE_PARAMETER`;
  * `CONSTRUCTOR`;
  * `FUNCTION`;
  * `PROPERTY_GETTER`;
  * `PROPERTY_SETTER`;
  * `TYPE`;
  * `EXPRESSION`;
  * `FILE`;
  * `TYPEALIAS`.

* `kotlin.annotation.Repeatable`

  `kotlin.annotation.Repeatable` is an annotation which is only used on annotation classes to specify whether this particular annotation is repeatable.
  Annotations are non-repeatable by default.

* `kotlin.Experimental` / `kotlin.UseExperimental`

  `kotlin.Experimental` is an annotation class with a single field:

  * ```kotlin
    val level: Level = Level.ERROR
    ```

    The severity level of the experimental status with two possible values: `Level.WARNING` and `Level.ERROR`.

  This annotation is used to introduce implementation-defined experimental language or standard library features.

  `kotlin.UseExperimental` is an annotation class with a single field:

  * ```kotlin
    vararg val markerClass: KClass<out Annotation>
    ```

    The classes which this annotation allows to use.

  This annotation is used to explicitly mark declarations which use experimental features marked by `kotlin.Experimental`.

  It is implementation-defined how this annotation is processed.

  TODO(Experimental status is still experimental itself)

* `kotlin.Deprecated` / `kotlin.ReplaceWith`

  `kotlin.Deprecated` is an annotation class with the following fields:

  * ```kotlin
    val message: String
    ```

    A message supporting the deprecation.

  * ```kotlin
    val replaceWith: ReplaceWith = ReplaceWith("")
    ```

    An optional replacement for the deprecated code.

  * ```kotlin
    val level: DeprecationLevel = DeprecationLevel.WARNING
    ```

    The deprecation level with three possible values: `DeprecationLevel.WARNING`, `DeprecationLevel.ERROR` and `DeprecationLevel.HIDDEN`.

  `kotlin.ReplaceWith` is itself an annotation class containing the information on how to perform the replacement in case it is provided.
  It has the following fields:

  * ```kotlin
    val expression: String
    ```

    The replacement code.

  * ```kotlin
    vararg val imports: String
    ```

    An array of imports needed for the replacement code to work correctly.

  `kotlin.Deprecated` is a built-in annotation supporting the deprecation cycle for declarations: marking some declarations as outdated, soon to be replaced with other declarations, or not recommended for use.
  It is implementation-defined how this annotation is processed, with the following recommendations:

  * Attempting to use a declaration with deprecation level of `DeprecationLevel.WARNING` should produce a compile-time warning;
  * Attempting to use a declaration with deprecation level of `DeprecationLevel.ERROR` should produce a compile-time error.

* `kotlin.Suppress`

  `kotlin.Suppress` is an annotation class with the following single field:

  * ```kotlin
    vararg val names: String
    ```

    The names of features this annotation is suppressing.

  `kotlin.Suppress` is used to optionally mark any piece of code as suppressing some language feature, such as a compiler warning, an IDE mechanism or a language feature.
  The names of features which one can suppress with this annotation are implementation-defined, as is the processing of this annotation itself.

* `kotlin.SinceKotlin`

  `kotlin.SinceKotlin` is an annotation class with the following single field:

  * ```kotlin
    val version: String
    ```

    The version of Kotlin language.

  `kotlin.SinceKotlin` is used to mark a declaration which is only available since a particular version of the language.
  These mostly refer to standard library declarations.
  It is implementation-defined how this annotation is processed.

* `kotlin.UnsafeVariance`

  `kotlin.UnsafeVariance` is an annotation class with no fields which is only applicable to types.
  Any type instance marked by this annotation explicitly states that the [variance][Type parameter variance] errors arising for this particular type instance are to be ignored by the compiler.

* `kotlin.DslMarker`

  `kotlin.DslMarker` is an annotation class with no fields which is applicable only to other annotation classes.
  An annotation class annotated with `kotlin.DslMarker` is marked as a marker of a specific DSL (domain-specific language).
  Any type annotated with such a marker is said to belong to that specific DSL.
  This affects [overload resolution][Overload resolution] in the following way: no two implicit receivers with types belonging to the same DSL are available in the same scope.
  See [Overload resolution section][Receivers] for details.

* `kotlin.PublishedApi`

  `kotlin.PublishedApi` is an annotation class with no fields which is applicable to any declaration.
  It may be applied to any declaration with `internal` visibility to make it available to `public` `inline` declarations.
  See [Declaration visibility section][Declaration visibility] for details.
