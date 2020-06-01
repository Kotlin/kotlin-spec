## Introduction

Kotlin is a multiplatform, statically typed, general-purpose programming language.

TODO(Explain these points)

Kotlin took inspiration from many programming languages, including (but not limited to) Java, C#, Groovy and TypeScript.
One of the main ideas behind Kotlin is being *pragmatic*, i.e., being a programming language useful for day-to-day development, which helps the users get the job done via its features and its tools.
Thus, a lot of design decisions were and still are influenced by how beneficial these decisions are for Kotlin users.

This specification covers Kotlin/Core, i.e., fundamental parts of Kotlin which should function *mostly* the same way irregardless of the underlying plarform.
These parts include such important things as language [expressions], [declarations], [type system] and [overload resolution].

> Important: due to the complexities of platform-specific implementations, platforms may extend, reduce or change the way some aspects of Kotlin/Core function.
> We mark these platform-dependent Kotlin/Core fragments in the specification to the best of our abilities.

Platform-specific parts of Kotlin will be covered in their respective sub-specifications, i.e., Kotlin/JVM, Kotlin/JS and Kotlin/Native.
