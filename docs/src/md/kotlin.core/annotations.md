## Annotations

Annotations are a form of syntactically-defined metadata which may be associated with different entities in a Kotlin program.
Annotations are specified in the source code of the program and may be accessed on a particular platform using platform-specific mechanisms both by the compiler (and source-processing tools) and at runtime (using [reflection][Reflection] facilities).
Values of annotation types cannot be created directly, but can be operated on when accessed using platform-specific facilities.

### Annotation values

An annotation value is a value of a special [annotation type][Annotation declarations].
An annotation type is a special kind of class type which is allowed to include properties of the following types:

- [Integer types][Built-in integer types];
- [String type][`kotlin.String`];
- Other annotation types;
- [Arrays][Array types] of any type listed above.

Annotation classes are not allowed to have any member functions, constructors or mutable properties.
They are also not allowed to have base classes and are considered to be implicitly derived from `kotlin.Annotation`.

### Annotation retention

The retention level of an annotation declares which compilation artifacts a particular compiler on a particular platform *retain* this kind of annotation.
There are the following types of retention available:

- Source retention (accessible by source-processing tools);
- Binary retention (retained in compilation artifacts);
- Runtime retention (accessible at runtime).

    Each subsequent level inherits what is accessible on the previous levels.

For availability and particular ways of accessing the metadata specified by these annotations please refer to the corresponding platforms' documentation.

### Annotation targets

The *targets* of a particular type of annotations is the kind of program entity which this annotations may be placed on.
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

TODO(Anything else?)

### Built-in annotations

* Deprecated / ReplaceWith
* Suppress
* SinceKotlin
* UnsafeVariance
* DslMarker
* PublishedApi
* Experimental / UseExperimental
* Contract-related stuff???
