## Annotations

Annotations are a form of syntactically-defined metadata that may be associated with different entities in a Kotlin program.
Annotations are specified in the source code of the program and may be accessed on a particular platform using platform-specific mechanisms both by the compiler (and source-processing tools) and during runtime (using [reflection][Reflection] facilities).
Values of annotation types cannot be created directly, but can be operated when accessed using platform-specific facilities.

### Annotation values

An annotation value is a value of a special [Annotation type][Annotation types]. 
An annotation type is a special kind of class type that is allowed to include properties of the following types:

- [Integer types][Built-in integer types];
- String type;
- Other annotation types;
- [Arrays][Array types] of any type listed above.

Annotation classes are not allowed to have any member functions, constructors or mutable properties.
They are also not allowed to have base classes besides `kotlin.Annotation`.

### Annotation retention

The retention level of an annotation declares which compilation artifacts a particular compiler on a particular platform do retain this kind of annotation.
There are the following types of retention available:

- Source retention (accessible by source-processing tools);
- Binary retention (retained in compilation artifacts);
- Runtime retention (accessible during runtime).

For availability and particular ways of accessing the metadata specified by these annotations please refer to the corresponding platforms' documentation.

### Annotation targets

The *targets* of a particular type of annotations is the kind of entity which this annotations may be placed on. There are the following targets available:

- A [class declaration][Class declaration] (including annotation classes);
- An [annotation class declaration][Annotation class declaration];
- A [type][Type system] parameter;
- A [property declaration][Property declaration];
- A property backing field;
- A property getter;
- A property setter;
- A local property declaration;
- A value parameter ([function][Function declaration] or [constructor][Constructor declaration] declaration);
- A [constructor][Constructor declaration];
- A [function declaration][Function declaration];
- A type usage;
- An [expression][Expression];
- A [Kotlin file][Kotlin file scope];
- A [type alias declaration][Type alias declaration].

### Annotation declarations

Annotations are declared using [annotation class declarations][Annotation class declaration].
See the corresponding section for details.

TODO()

### Builtin annotations

* Deprecated / ReplaceWith
* Suppress
* SinceKotlin
* UnsafeVariance
* DslMarker
* PublishedApi
* Contract-related stuff???
