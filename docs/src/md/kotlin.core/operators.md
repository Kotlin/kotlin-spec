## Operator overloading

TODO(This is a stub)

Some syntax forms in Kotlin are *defined by convention*, meaning that their semantics are defined through syntactic expansion of one syntax form into another syntax form.

Particular cases of definition by convention include:

- Arithmetic and comparison operators;
- Operator-form [assignments][Assignments];
- [For-loop statements][For-loop statement];
- [Delegated properties][Delegated property declaration];
- TODO(anything else?)

> Important: another case of definition by convention is [safe navigation][Navigation operators], which is covered in more detail in its respective section.

There are several points shared among all the syntax forms defined using definition by convention:

- The expansions are hygienic: if they introduce new identifiers that were not present in original syntax, all such identifiers are not accessible outside the expansion and cannot clash with any other declarations in the program;
- The expressions captured by an expansion are using *call-by-need* evaluation strategy, meaning that they are evaluated only once during first usage specified in the expansion even if the expansion itself has more than one usage of such an expression;
- An expansion may lead to another expansion, following the same rules;
- All call expressions that are produced by expansion are only allowed to use operator functions.

This expansion of a particular syntax form to a different piece of code is usually defined in the terms of *operator* functions.

Operator functions are function which are [declared][Function declaration] with a special keyword `operator` and are not different from regular functions when called via [function calls][Function calls and property access].
However, operator functions can also be used in definition by convention.

> Note: different platforms may add additional criteria on whether a function may be considered a suitable candidate for operator convention.

The details of individual expansions are available in the sections of their respective operators, here we would like to describe how they *interoperate*.

For example, take the following declarations:

```kotlin
class A {
    operator fun inc(): A { ... }
}

object B {
    operator fun get(i: Int): A { ... }
    operator fun set(i: Int, value: A) { ... }
}

object C {
    operator fun get(i: Int): B { ... }
}

```

The expression `C[0][0]++` is expanded (see the [Expressions][expressions] section for details) using the following rules:

- The operations are expanded in order of their priority (TODO(Where and how to specify this priority?)).

- First, the [increment operator][Postfix increment expression] is expanded, resulting in:
    
    ```kotlin
    C[0][0] = C[0][0].inc()
    ```
- Second, the [assignment][Assignments] to an indexing expression (produced by the previous expansion) is expanded, resulting in:
    
    ```kotlin
    C[0].set(C[0][0].inc())
    ```
- Third, the [indexing expressions][Indexing expressions] are expanded, resulting in:
    
    ```kotlin
    C.get(0).set(C.get(0).get(0).inc())
    ```

TODO(Specify when we run the overload resolution to know what we're expanding to)

> Important: although the resulting expression contains several instances of the subexpression `C.get(0)`, as all these instances were created from the same original syntax form, the subexpression is evaluated only once, making this code roughly equivalent to:
> 
> ```kotlin
> val $tmp = C.get(0)
> $tmp.set($tmp.get(0).inc())
> ```
