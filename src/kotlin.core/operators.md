## Overloadable operators

TODO(rename this and all the refs to smth)

Some syntax forms in Kotlin are defined *by convention*, meaning that their semantics are defined through syntactic expansion of current syntax form into another syntax form. 
The expansion of a particular syntax form is a different piece of code usually defined in the terms of operator functions. 
Operator functions are function that are [declared][Function declaration] with a special keyword `operator` and are not different from normal functions when called normally, but allow themselves to be employed by syntactic expansion. 
Different platforms may add other criteria on whether a function may be considered a suitable candidate for operator convention.

Particular cases of definition by convention include:

- Arithmetic and comparison operators;
- Operator-form [assignments][Assignments];
- [For-loop statements][For-loop statement];
- [Delegated properties][Delegated property declaration];
- TODO(anything else?)

There are several common points among all the syntax forms defined using this mechanism:

- The expansions are hygenic, meaning that even if they seemingly introduce new identifiers that were not present in original syntax, all such identifiers are not accessible outside the expansion and cannot clash with any other declarations in the program;
- All the new call expressions that are produced by expansion are only allowed to use operator functions.

TODO()
