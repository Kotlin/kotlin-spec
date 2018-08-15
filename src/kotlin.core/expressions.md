## Expressions

### Glossary

CSB
~ [Control structure body][Code blocks]

### Introduction

TODO()

An expression may be *used as a statement* or *used as an expression* depending on the context.
As all expressions are valid [statements][Statements], free expressions may be used as single statements or inside code blocks.

An expression is used as an expression, if it is encountered in any position where a statement is not allowed, for example, as an operand to an operator or as an immediate argument for a function call.
An expression is used as a statement if it is encountered in any position where a statement is allowed.

Some expressions are only allowed to be used as statements, if certain restrictions are met; this may affect the semantics, the compile-time type information or/and the safety of these expressions.

TODO(strong/soft keywords?)

### Constant literals

Constant literals are expressions which describe constant values.
Every constant literal is defined to have a single standard library type, whichever it is defined to be on current platform.
All constant literals are evaluated immediately.

#### Boolean literals

**_BooleanLiteral_:**  
  ~  `true` | `false`

Keywords `true` and `false` denote boolean literals of the same values.
These are strong keywords which cannot be used as identifiers unless [escaped][Escaped identifiers].
Values `true` and `false` always have the type `kotlin.Boolean`.

#### Integer literals

**_IntegerLiteral_:**  
  ~  _DecDigitNoZero_ {_DecDigitOrSeparator_} _DecDigit_
      | _DecDigit_

**_HexLiteral_:**  
  ~  `0` (`x`|`X`) _HexDigit_ {_HexDigitOrSeparator_} _HexDigit_   
      | `0` (`x`|`X`) _HexDigit_

**_BinLiteral_:**  
  ~  `0` (`b`|`B`) _BinDigit_ {_BinDigitOrSeparator_} _BinDigit_   
      | `0` (`b`|`B`) _BinDigit_

**_DecDigitNoZero_:**  
  ~  _DecDigit_ - `0`

**_DecDigitOrSeparator_:**  
  ~  _DecDigit_ | _Underscore_

**_HexDigitOrSeparator_:**  
  ~  _HexDigit_ | _Underscore_

**_BinDigitOrSeparator_:**  
  ~  _BinDigit_ | _Underscore_

**_DecDigits_:**  
  ~  _DecDigit_ {_DecDigitOrSeparator_} _DecDigit_ | _DecDigit_

##### Decimal integer literals

A sequence of decimal digit symbols (`0` though `9`) is a decimal integer literal.
Digits may be separated by an underscore symbol, but no underscore can be placed before the first digit or after the last one.

> Note: unlike other languages, Kotlin does not support octal literals.
> Even more so, any decimal literal starting with digit `0` and containing more than 1 digit is not a valid decimal literal.

##### Hexadecimal integer literals

A sequence of hexadecimal digit symbols (`0` through `9`, `a` through `f`, `A` through `F`) prefixed by `0x` or `0X` is a hexadecimal integer literal.
Digits may be separated by an underscore symbol, but no underscore can be placed before the first digit or after the last one.

##### Binary integer literals

A sequence of binary digit symbols (`0` or `1`) prefixed by `0b` or `0B` is a binary integer literal.
Digits may be separated by an underscore symbol, but no underscore can be placed before the first digit or after the last one.

##### Long integer literals

Any of the decimal, hexadecimal or binary literals may be suffixed by the long literal mark (symbol `L`).
An integer literal with the long literal mark has type `kotlin.Long`; an integer literal without it has one of the types `kotlin.Int`/`kotlin.Short`/`kotlin.Byte` (the selected type is dependent on the context), if its value is in range of the corresponding type, or type `kotlin.Long` otherwise.

TODO(ranges for integer literals)

#### Real literals

**_RealLiteral_:**  
  ~  _FloatLiteral_ | _DoubleLiteral_

**_FloatLiteral_:**  
  ~  _DoubleLiteral_ (`f` | `F`)
      | _DecDigits_ (`f` | `F`)

**_DoubleLiteral_:**  
  ~  [_DecDigits_] `.` _DecDigits_ [_DoubleExponent_]
      | _DecDigits_ _DoubleExponent_

A *real literal* consists of the following parts: the whole-number part, the decimal point (ASCII period character `.`), the fraction part and the exponent.
Unlike other languages, Kotlin real literals may only be expressed in decimal numbers.
A real literal may also be followed by a type suffix (`f` or `F`).

The exponent is an exponent mark (`e` or `E`) followed by an optionaly signed decimal integer (a sequence of decimal digits).

The whole-number part and the exponent part may be omitted.
The fraction part may be omitted only together with the decimal point, if the whole-number part and either the exponent part or the type suffix are present.
Unlike other languages, Kotlin does not support omitting the fraction part, but leaving the decimal point in.

The digits of the whole-number part or the fraction part or the exponent may be optionally separated by underscores, but an underscore may not be placed between, before, or after these parts.
It also may not be placed before or after the exponent mark symbol.

A real literal without the type suffix has type `kotlin.Double`, a real literal
with the type suffix has type `kotlin.Float`.

> Note: this means there is no special suffix associated with type `kotlin.Double`.

#### Character literals

**_CharacterLiteral_:**  
  ~  `'` (_EscapeSeq_ | _<any character except CR, LF, `'` and `\`>_) `'`

**_EscapeSeq_:**  
  ~  _UnicodeCharacterLiteral_ | _EscapedCharacter_

**_UnicodeCharacterLiteral_:**  
  ~  `\` `u` _HexDigit_ _HexDigit_ _HexDigit_ _HexDigit_

**_EscapedCharacter_:**  
  ~  `\` (`t` | `b` | `r` | `n` | `'` | `"` | `\` | `$`)

A *character literal* defines a constant holding a unicode character value.
A simply-formed character literal is any symbol between two single quotation marks (ASCII single quotation character `'`), excluding newline symbols (*CR* and *LF*), the single quotation mark itself and the escaping mark (ASCII backslash character `\`).

A character literal may also contain an escaped symbol of two kinds: a simple escaped symbol or a unicode codepoint.
Simple escaped symbols include:

- `\t` --- the unicode TAB symbol (TODO());
- `\b` --- the unicode BACKSPACE symbol (TODO());
- `\r` --- *CR*;
- `\n` --- *LF*;
- `\'` --- the unicode single quotation symbol (TODO());
- `\"` --- the unicode double quotation symbol (TODO());
- `\\` --- the unicode backslash symbol symbol (TODO());
- `\$` --- the unicode DOLLAR symbol (TODO()).

A unicode codepoint escaped symbol is the symbol `\u` followed by exactly four hexadecimal digits.
It represents the unicode symbol with the codepoint equal to the number represented by these four digits.

> Note: this means unicode codepoint escaped symbols support only unicode symbols in range from U+0000 to U+FFFF.

All character literals have type `kotlin.Char`.

#### String literals

Kotlin supports [string interpolation][String interpolation] which supersedes traditional string literals.
For further details, please refer to the corresponding section.

#### Null literal

The keyword `null` denotes the **null reference**, which represents an absence of a value and is a valid value only for [nullable types][Nullable types].
Null reference has type [`kotlin.Nothing?`][`kotlin.Nothing`] and is, by definition, the only value of this type.

TODO(rearrange these sections)

### Try-expression

:::{.paste target=grammar-rule-tryExpression}
:::
:::{.paste target=grammar-rule-catchBlock}
:::
:::{.paste target=grammar-rule-finallyBlock}
:::

A *try-expression* is an expression starting with the keyword `try`. It consists of a [code block][Code blocks] (*try body*) and one or more of the following kinds of blocks: zero or more *catch blocks* and an optional *finally block*.
A *catch block* starts with the soft keyword `catch` with a single *exception parameter*, which is followed by a [code block][Code blocks].
A *finally block* starts with the soft keyword `finally`, which is followed by a [code block][Code blocks].
A valid try-expression must have at least one catch or finally block.

The try-expression evaluation evaluates its body; if any statement in the try body throws an exception (of type $E$), this exception, rather than being immediately propagated up the call stack, is checked for a matching catch block.
If a catch block of this try-expression has an exception parameter of type $T :> E$, this catch block is evaluated immediately after the exception is thrown and the exception itself is passed inside the catch block as the corresponding parameter.
If there are several catch blocks which match the exception type, the first one is picked.

TODO(Exception handling?)

If there is a finally block, it is evaluated after the evaluation of all previous try-expression blocks, meaning:

* If no exception is thrown during the evaluation of the try body, no catch blocks are executed, the finally block is evaluated after the try body, and the program execution continues as normal.
* If an exception was thrown, and one of the catch blocks matched its type, the finally block is evaluated after the evaluation of the matching catch block.
* If an exception was thrown, but no catch block matched its type, the finally block is evaluated before [propagating the exception][Exceptions] up the call stack.

The value of the try-expression is the same as the value of the [last expression][Code blocks] of the try body (if no exception was thrown) or the value of the last expression of the matching catch block (if an exception was thrown and matched).
All other situations mean that an exception is going to be propagated up the call stack, and the value of the try-expression becomes irrelevant.

> Note: as desribed, the finally block (if present) is executed regardless, but it has no effect on the value returned by the try-expression.

The type of the try-expression is the [least upper bound][Least upper bound] of the types of the last expressions of the try body and the last expressions of all the catch blocks (TODO(not that simple)).

> Note: these rules mean the try-expression always may be used as an expression, as it always has a corresponding result value.

### Conditional expression

:::{.paste target=grammar-rule-ifExpression}
:::

*Conditional expressions* use a boolean value of one expression (*condition*) to decide which of the two [control structure bodies][Code blocks] (*branches*) should be evaluated.
If the condition evaluates to `true`, the first branch (the *true branch*) is evaluated, otherwise the second branch (the *false branch*) is evaluated if it is present.

The value of the resulting expression is the same as the value of the chosen branch.

The type of the resulting expression is the [least upper bound][Least upper bound] of the types of two branches (TODO(not that simple)), if both branches are present.
If the false branches is omitted, the resulting conditional expression has type [`kotlin.Unit`][`kotlin.Unit`] and it may used only as a statement.

TODO(Examples?)

The type of the condition expression must be a subtype of `kotlin.Boolean`, otherwise it is an error.

> Note: when used as expressions, conditional expressions are special w.r.t. of operator precedence: they have the highest priority (the same as for all primary expressions) when placed on the right side of any binary expression, but when placed on the left side, they have the lowest priority.
> For details, see Kotlin [grammar][Syntax grammar].

### When expression

:::{.paste target=grammar-rule-whenExpression}
:::
:::{.paste target=grammar-rule-whenEntry}
:::
:::{.paste target=grammar-rule-whenCondition}
:::
:::{.paste target=grammar-rule-rangeTest}
:::
:::{.paste target=grammar-rule-typeTest}
:::

*When expression* is similar to a [conditional expression][Conditional expression] in that it allows one of several different [control structure bodies][Code blocks] (*cases*) to be evaluated, depending on some boolean conditions.
The key difference is exactly that a when expressions may include several different conditions with their corresponding control structure bodies.
When expression has two different forms: with bound value and without it.

**When expression without bound value** (the form where the expression enclosed in parantheses after the `when` keyword is absent) evaluates one of the different CSBs based on its condition from the *when entry*.
Each when entry consists of a boolean *condition* (or a special `else` condition) and its corresponding CSB.
When entries are checked and evaluated in their order of appearance.
If the condition evaluates to `true`, the corresponding CSB is evaluated and the value of when expression is the same as the value of the CSB.
All remaining conditions and expressions are not evaluated.

The `else` condition is a special condition which evaluates to `true` if none of the branches above it evaluated to `true`.
The `else` condition **must** also be in the last when entry of when expression, otherwise it is a compile-time error.

> Note: informally, you can always replace the `else` condition with an always-`true` condition (e.g., boolean literal `true`) with no change to the resulting semantics.

**When expression with bound value** (the form where the expression enclosed in parantheses after the `when` keyword is present) are similar to the form without bound value, but use a different syntax for conditions.
In fact, it supports three different condition forms:

- *Type test condition*: [type checking operator][Type-checking expression] followed by a type (`is T`).
  The resulting condition is a [type check expression][Type-checking expression] of the form `boundValue is T`.
- *Contains test condition*: [containment operator][Containment-checking expression] followed by an expression (`in Expr`).
  The resulting condition is a [containment check expression][Containment-checking expression] of the form `boundValue in Expr`.
- *Any other expression* (`Expr`).
  The resulting condition is an [equality check][Equality expressions] of the form `boundValue == Expr`.
- The `else` condition, which is a special condition which evaluates to `true` if none of the branches above it evaluated to `true`.
  The `else` condition **must** also be in the last when entry of when expression, otherwise it is a compile-time error.

> Note: the rule for "any other expression" means that if a when expression with bound value contains a boolean condition, this condition is **checked for equality** with the bound value, instead of being used directly for when entry selection.

TODO(Examples)

The type of the resulting expression is the [least upper bound][Least upper bound] of the types of all its entries (TODO(not that simple)).
If the when expression is not [exhaustive][Exhaustive when expressions], it has type [`kotlin.Unit`][`kotlin.Unit`] and may used only as a statement.

#### Exhaustive when expressions

A when expression is called **_exhaustive_** if at least one of the following is true:

- It has an `else` entry;
- It has a bound value and at least one of the following is true:
    - The bound expression is of type `kotlin.Boolean` and the conditions contain both:
        - A [constant expression][Constant expressions] evaluating to `true`;
        - A [constant expression][Constant expressions] evaluating to `false`;
    - The bound expression is of a [`sealed class`][Sealed classes] type and all of its subtypes are covered using type test conditions in this expression.
      This should include checks for all direct subtypes of this sealed class.
      If any of the direct subtypes is also a sealed class, there should either be a check for this subtype or all its subtypes should be covered;
    - The bound expression is of an [`enum class`][Enum classes] type and all its enumerated values are checked for equality using constant expression;
    - The bound expression is of a [nullable type][Nullability] $T?$ and one of the cases above is met for its non-nullable counterpart $T$ together with another condition which checks the bound value for equality with `null`.

> Note: informally, an exhaustive when expression is guaranteed to evaluate one of its CSBs regardless of the specific when conditions.

### Logical disjunction expression

:::{.paste target=grammar-rule-disjunction}
:::

Operator symbol `||` performs logical disjunction over two values of type `kotlin.Boolean`.
This operator is **lazy**, meaning that it does not evaluate the right hand side argument unless the left hand side argument evaluated to `false`.

Both operands of a logical disjunction expression must have a type which is a subtype of `kotlin.Boolean`, otherwise it is a type error.
The type of logical disjunction expression is `kotlin.Boolean`.

TODO(Types of errors? Compile-time, type, run-time, whatever?)

### Logical conjunction expression

:::{.paste target=grammar-rule-conjunction}
:::

Operator symbol `&&` performs logical conjunction over two values of type `kotlin.Boolean`.
This operator is **lazy**, meaning that it does not evaluate the right hand side argument unless the left hand side argument evaluated to `true`.

Both operands of a logical conjunction expression must have a type which is a subtype of `kotlin.Boolean`, otherwise it is a type error.
The type of logical disjunction expression is `kotlin.Boolean`.

### Equality expressions

:::{.paste target=grammar-rule-equality}
:::
:::{.paste target=grammar-rule-equalityOperator}
:::

Equality expressions are binary expressions involving equality operators. There are
two kinds of equality operators: *reference equality operators* and
*value equality operators*.

#### Reference equality expressions

*Reference equality expressions* are binary expressions employing reference equality operators:
`===` and `!==`. These expressions check if two values are equal *by reference*, meaning
that two values are equal (non-equal for operator `!==`) if and only if they represent the
same runtime value created using the same constructor call.

For values created without
construction calls, notably the constant literals and constant expressions composed
of those literals, the following holds:

- If these values are [non-equal by value][Value equality expressions], they are also
  non-equal by reference;
- Any instance of the null reference `null` is reference-equals to any other
  instance of the null reference;
- Otherwise, it is implementation-defined and must not be used as a means of comparing
  two such values.

Reference equality expressions always have type `kotlin.Boolean`.

#### Value equality expressions

*Value equality expressions* are binary expressions employing value equality operators:
`==` and `!=`. These operators are [overloadable][Overloadable operators] with the following
expansion:

- `A == B` is exactly the same as `A?.equals(B) ?: (B === null)` where `equals` is a valid
  operator function available in the current scope;
- `A != B` is exactly the same as `!(A?.equals(B) ?: (B === null))` where `equals` is a valid
  operator function available in the current scope.

> Please note that the class `kotlin.Any` has a built-in open operator member function called `equals`,
> meaning that there is always at least one available overloading candidate for any value equality expression.

Value equality expressions always have type `kotlin.Boolean`. If the corresponding operator function
has a different return type, it is invalid and a compiler error should be generated.

### Comparison expressions

:::{.paste target=grammar-rule-comparison}
:::
:::{.paste target=grammar-rule-comparisonOperator}
:::

*Comparison expressions* are binary expressions employing the comparison operators:
`<`, `>`, `<=` and `>=`. These operators are [overloadable][Overloadable operators] with the following
expansion:

- `A < B` is exactly the same as `A.compareTo(B) [<] 0`
- `A > B` is exactly the same as `0 [<] A.compareTo(B)`
- `A <= B` is exactly the same as `!(A.compareTo(B) [<] 0)`
- `A >= B` is exactly the same as `!(0 [<] A.compareTo(B))`

where `compareTo` is a valid operator function available in the current scope
and `[<]` (read "boxed less") is a special operator unavailable for in-code use in Kotlin and performing
integer "less-than" comparison of two integer numbers. The `compareTo` overloaded function
must have return type `kotlin.Int`, otherwise it's a compiler error.

All comparison expressions always have type `kotlin.Boolean`.

### Type-checking and containment-checking expressions

:::{.paste target=grammar-rule-infixOperation}
:::
:::{.paste target=grammar-rule-inOperator}
:::
:::{.paste target=grammar-rule-isOperator}
:::

#### Type-checking expression

A type checking expression employs the use of an type-checking operators `is` or `!is`
and has an expression as a left-hand side operand and a type name as a right-hand
side operand. The type must be [runtime-available][Runtime-available types], otherwise
a compiler error should be generated. The expression checks whether the runtime type of
the expression on the left is a subtype of (not the subtype of in the case of `!is`) the type denoted
by the right-hand side argument.

Type-checking expression always has type `kotlin.Boolean`.

> For example, the expression `null is T?` for any type `T` always evaluates to `true`, because
> the type of the left-hand side (`null`) is `kotlin.Nothing?`, which is a subtype of any
> nullable type `T?`

##### TODO()

- Smart casts!

#### Containment-checking expression

A *containment-checking expression* is a binary expression employing the containment operator
(`in` or `!in`). These are [overloadable][Overloadable operators] operators with the following expansion:

- `A in B` is exactly the same as `A.contains(B)`;
- `A !in B` is exactly the same as `!(A.contains(B))`;

where `contains` is a valid operator function available in the current scope. This
function must have return type `kotlin.Boolean`, otherwise a compiler error is generated.
Containment-checking expressions always have type `kotlin.Boolean`.

### Elvis operator expression

:::{.paste target=grammar-rule-elvisExpression}
:::

*Elvis operator expression* is a binary expression that employs the elvis operator (`?:`).
It checks whether the left-hand side expression is equal to `null`, and if it is,
evaluates and return the right-hand side expression.

This operator is **lazy**, meaning that if the left-hand side expression is not equal
to `null`, the right-hand side expression is never evaluated.

The type of elvis operator expression is the [least upper bound][The least upper bound]
of the non-nullable variant of the type of the left-hand side expression and the
type of the right-hand side expression. TODO(): not that simple, too

### Range expression

:::{.paste target=grammar-rule-rangeExpression}
:::

A *range expression* is a binary expression employing the range operator `..`.
It is an [overloadable][Overloadable operators] operator with the following expansion:

- `A..B` is exactly the same as `A.rangeTo(B)`

where `rangeTo` is a valid operator function available in the current scope.
The return type of this function is not restricted.
The range expression has the same type as the return type of the corresponding
`rangeTo` overload variant.

### Additive expression

:::{.paste target=grammar-rule-additiveExpression}
:::
:::{.paste target=grammar-rule-additiveOperator}
:::

An *additive expression* is a binary expression employing the addition (`+`) or subtraction (`-`) operators.
These are [overloadable][Overloadable operators] operators with the following expansions:

- `A + B` is exactly the same as `A.plus(B)`
- `A - B` is exactly the same as `A.minus(B)`

where `plus` or `minus` is a valid operator function available in the current scope.
The return type of this function is not restricted.
The range expression has the same type as the return type of the corresponding
operator function overload variant.

### Multiplicative expression

:::{.paste target=grammar-rule-multiplicativeExpression}
:::
:::{.paste target=grammar-rule-multiplicativeOperator}
:::

An *multiplicative expression* is a binary expression employing the multiplication (`*`), division (`/`) or remainder (`%`) operators.
These are [overloadable][Overloadable operators] operators with the following expansions:

- `A * B` is exactly the same as `A.times(B)`
- `A / B` is exactly the same as `A.div(B)`
- `A % B` is exactly the same as `A.rem(B)`

> As of Kotlin version 1.2.31, there exists an additional overloading function for
> `%` called `mod`, which is deprecated

where `times`, `div`, `rem` is a valid operator function available in the current scope.
The return type of this function is not restricted.
The range expression has the same type as the return type of the corresponding
operator function overload variant.

### Cast expression

:::{.paste target=grammar-rule-asExpression}
:::
:::{.paste target=grammar-rule-asOperator}
:::

A *cast expression* is a binary expression employing the cast operators (`as` or `as?`) and
receives an expression as the left-hand side operand and a type name as the right-hand side operand.

The form of cast expression employing the `as` operator is called *a unsafe cast* expression
This operator perform a runtime check whether runtime type of the expression
is a [subtype][Subtyping] of the type given on the right-hand side operand and
throws an exception otherwise. If the type on the right hand side is a [runtime-available][Runtime-available types]
type without generic parameters, then this exception is thrown immediately when
evaluating the expression, otherwise it is platform-dependent whether an exception
is thrown at this point.

> Even if the exception is not thrown when evaluating the cast expression, it is
> guaranteed to be thrown later when the value is used with any runtime-available type

The unsafe cast expression always has the same type as the type specified in the expression.

The form of cast expression employing the `as?` operator is called *a checked cast* expression
This operator is very similar to the unsafe cast expression, but does not throw an exception,
but returns `null` if the types don't match.
If the type specified on the right hand side of the expression is not
[runtime-available][Runtime-available types], then the check is not performed
and `null` is never returned, leading to potential runtime errors later in the
program execution. This situation should be reported with a compiler warning.

The checked cast expression always has the [nullable][Nullable types] variant of the type specified in the expression.

An *ascription expression* is a binary expression employing the ascription operator (`:`) and
receives an expression as the left-hand side operand and a type name as the right-hand side operand.

This operator does not perform any actions at runtime and evaluates to the same value
as its left hand operand. However, it does perform a compile-time check whether the
current type of the expression is a [subtype][Subtyping] of the type given on the right-hand
side operand and generates a compiler error otherwise.

The ascription expression always has the same type as the type specified in right-hand side of the expression.

#### TODO()

- Smart casts!

### Prefix expressions

:::{.paste target=grammar-rule-prefixUnaryExpression}
:::
:::{.paste target=grammar-rule-unaryPrefix}
:::
:::{.paste target=grammar-rule-prefixUnaryOperator}
:::

#### Annotated and labeled expression

Any expression in Kotlin may be prefixed with any number of [annotations][Annotations]
and [labels][Labels]. These do not change the value of the expression and can be used
by external tools and platform-dependent features.

#### Prefix increment expression

A *prefix increment* expression is an expression employing the prefix form of
operator `++`. It is an [overloadable][Overloadable operators] operator with the following expansion:

`++A` is exactly the same as evaluating the expression `A.inc()` where
`inc` is a suitable `operator` function, assigning the value to `A` and then
returning the value of `A` as the result of the expression.

The left-hand side of a postfix increment expression must be an [assignable expressions][Assignments].
Otherwise a compiler error must be generated.

The type of prefix increment is always equal to the type of the right-hand side
expression.

#### Prefix decrement expression

A *prefix decrement* expression is an expression employing the prefix form of
operator `--`. It is an [overloadable][Overloadable operators] operator with the following expansion:

`--`$A$ is exactly the same as evaluating the expression $A$`.dec()` where
`dec` is a suitable `operator` function, assigning the value to $A$ and then
returning the value of $A$ as the result of the expression.

The left-hand side of a prefix decrement expression must be an [assignable expressions][Assignments].
Otherwise a compiler error must be generated.

The type of prefix increment is always equal to the type of the right-hand side
expression.

#### Unary minus expression

An *unary minus* expression is an expression employing the prefix form of
operator `-`. It is an [overloadable][Overloadable operators] operator with the following expansion:

`-`$A$ is exactly the same as $A$`.unaryMinus()` where `unaryMinus` is a suitable `operator`
function, including its type. No additional restrictions apply.

#### Unary plus expression

An *unary plus* expression is an expression employing the prefix form of
operator `+`. It is an [overloadable][Overloadable operators] operator with the following expansion:

`+`$A$ is exactly the same as $A$`.unaryPlus()` where `unaryPlus` is a suitable `operator`
function, including its type. No additional restrictions apply.

#### Logical not expression

A *logical not* expression is an expression employing the prefix operator `!`.
It is an [overloadable][Overloadable operators] operator with the following expansion:

`!`$A$ is exactly the same as $A$`.not()` where `not` is a suitable `operator`
function, including its type. No additional restrictions apply.

### Postfix operator expressions

:::{.paste target=grammar-rule-postfixUnaryExpression}
:::
:::{.paste target=grammar-rule-postfixUnarySuffix}
:::
:::{.paste target=grammar-rule-postfixUnaryOperator}
:::

#### Postfix increment expression

A *postfix increment* expression is an expression employing the postfix form of
operator `++`. It is an [overloadable][Overloadable operators] operator with the following expansion:

$A$`++` is exactly the same as evaluating the expression $A$`.inc()` where
`inc` is a suitable `operator` function, assigning the value of $A$ to a temporary
location, assigning the result of `inc` to $A$ and returning the temporary.

It can also be represented with the following code:

```kotlin
val tmp = A;
A = A.inc();
return tmp;
```

The left-hand side of a postfix increment expression must be an [assignable expressions][Assignable expressions].
Otherwise a compiler error must be generated.

The type of postfix increment is always equal to the type of the right-hand side
expression.

#### Postfix decrement expression

A *postfix decrement* expression is an expression employing the postfix form of
operator `--`. It is an [overloadable][Overloadable operators] operator with the following expansion:

$A$`--` is exactly the same as evaluating the expression $A$`.dec()` where
`dec` is a suitable `operator` function, assigning the value of $A$ to a temporary
location, assigning the result of `inc` to $A$ and returning the temporary.

It can also be represented with the following code:

```kotlin
val tmp = A;
A = A.dec();
return tmp;
```

The left-hand side of a postfix decrement expression must be an [assignable expressions][Assignable expressions].
Otherwise a compiler error must be generated.

The type of prefix increment is always equal to the type of the right-hand side
expression.

### Not-null assertion expression

A *not-null assertion expression* is a postfix expression employing the use of
operator `!!`. For expressions of nullabe types, this expression checks whether
the value is equal to `null`, and if it is, throws a runtime exception.
If it is not equal to `null`, it evaluates to the same value as its
left-hand side expression.

Not-null assertion expressions have no effect on values of non-nullable types.

The type of non-null assertion expression is the [non-nullable][Nullable types] variant of the
type of its left-hand side expression. Note that this type may be non-denotable
in Kotlin and as such, may be [approximated][Type approximation] in some situations
involving [type inference][Type inference].

### Indexing expressions

:::{.paste target=grammar-rule-postfixUnaryExpression}
:::
:::{.paste target=grammar-rule-postfixUnarySuffix}
:::
:::{.paste target=grammar-rule-indexingSuffix}
:::

An *indexing expression* is a suffix expression employing the use of several
subexpressions *indices* between square brackets (`[` and `]`). At least one
index must be provided.

It is an [overloadable][Overloadable operators] operator with the following expansion:

$A$`[`$I_0$`,`$I_1$`,`$\ldots$`,`$I_N$`]` is exactly the same as
$A$`.get(`$I_0$`,`$I_1$`,`$\ldots$`,`$I_N$`)`, where `get` is a suitable `operator`
function.

A correct indexing expression has the same type as the corresponding `get` expression.

Indexing expressions are [assignable][Assignable expressions].
For a corresponding assignment form, see [indexing assignment][Indexing assignment].

### Call and property access expressions

:::{.paste target=grammar-rule-postfixUnaryExpression}
:::
:::{.paste target=grammar-rule-postfixUnarySuffix}
:::
:::{.paste target=grammar-rule-navigationSuffix}
:::
:::{.paste target=grammar-rule-callSuffix}
:::
:::{.paste target=grammar-rule-annotatedLambda}
:::
:::{.paste target=grammar-rule-valueArguments}
:::
:::{.paste target=grammar-rule-typeArguments}
:::
:::{.paste target=grammar-rule-typeProjection}
:::
:::{.paste target=grammar-rule-typeProjectionModifiers}
:::
:::{.paste target=grammar-rule-memberAccessOperator}
:::

#### The navigation operators

Expressions employing the navigation binary operators (`.`, `.?` or `::`) are all
syntactically similar, but, in fact, may have very different syntactic meaning.
`a.c` may have one of the following semantics when used as an expression:

- A fully-qualified type, property or object name. The left side of `.` must be a package name,
  while the right side corresponds to a declaration in that package. Note that qualification uses
  operator `.` only;
- A value property access. Here `a` is another value available in the current scope
  and `c` is the property name. If used with operator `::` this becomes a
  [property reference][Callable references]. The left-hand side expression may be a type name,
  which is similar to using the type's companion object as the left hand side expression;
- A member function call if followed by the call suffix (arguments enclosed in parentheses).
  These expressions adhere to the [overloading][Overload resolution] rules. If used
  with operator `::`, but without the call suffix,
  this becomes a [function reference][Callable references].

TODO() + Identifiers

### Function Literals

Kotlin supports using functions as values. This includes, among other things, being
able to use named functions (through [function references][Callable references])
as parts of expressions. Sometimes it does not make much sense to provide a separate
function declaration, but rather define a function in-place, using *function literals*.

There are two types of function literals in Kotlin: *lambda literals* and
*anonymous function declarations*. Both of these provide a way of defining a function
in-place, but have subtle differences.

#### Anonymous function declarations

:::{.paste target=grammar-rule-anonymousFunction}
:::

*Anonymous function declarations*, despite the name, are not actually declarations,
but rather an expression that resembles a function declaration. They have syntax
very similar to the function declaration syntax, but with a few differences:

- Anonymous functions do not have a name (obviously);
- Anonymous functions may not have type parameters (TODO(): check!!!);
- Anonymous functions may not have default parameters (TODO(): check!!!);
- Anonymous functions may have variable argument parameters, but they are automatically
  decayed to non-variable argument parameters of array type (TODO(): how does this really work?).

Anonymous function declaration may declare anonymous extension functions.
The type of an anonymous function declaration is the function type
constructed similarly to the corresponding
[named function declaration][Function declarations].

#### Lambda literals

:::{.paste target=grammar-rule-lambdaLiteral}
:::
:::{.paste target=grammar-rule-lambdaParameters}
:::
:::{.paste target=grammar-rule-lambdaParameter}
:::

Lambda literals TODO()

### Object literals

:::{.paste target=grammar-rule-objectLiteral}
:::

Object literals are a way of defining anonymous objects in Kotlin. Anonymous objects
are similar to regular objects, but they (obviously) have no name and thus can
(only) be used as expressions. Anonymous objects, just like regular object
declarations, can have at most one base class and many base interfaces declared
in its delegation specifiers.

The main difference between the regular object declaration and an anonymous
object is its type. The type of an anonymous object is a special kind of type
that is usable (and visible) only in the scope where it is declared. It is similar
to a type that could be normally declared with a corresponding object declaration,
but cannot be used outside the scope, leading to interesting effects.

When a value of this type escapes current scope:

- If the type has only one declared supertype, it is implicitly downcasted to
  this declared supertype;
- If the type has several declared supertypes, there must be an explicit cast to
  any suitable type visible outside the scope, otherwise a compiler error is generated.

Please not that in this context "escaping" current scope is performed immediately
if the corresponding value is declared as a global or classifier-scope property,
as those are a part of package interface.

### This-expressions

:::{.paste target=grammar-rule-thisExpression}
:::

This-expressions are a special kind of expressions used to access available receivers
in current scope. For more information about receivers, please refer to the
[overloading section][Receivers]. The basic form of this expression, denoted by
`this` keyword, is used to access the current implicit receiver according to
receiver overloading rules. In order to access other receivers, labeled `this`
expressions are used. These may be any of the following:

- `this@`$type$ where $type$ is a name of any classifier that is currently being
  declared (that is, this this-expression is located inside its declaration's
  inner scope) refers to the implicit object of the type being declared;
- `this@`$function$ where $function$ is a name of a function currently being declared
  (that is, this this-expression is located inside the function body)
  refers to the implicit receiver object of this function (if it is an extension
  function) or is illegal and generates a compiler error.

Any other form of this-expression is illegal and generates a compiler error.

### Super-forms

:::{.paste target=grammar-rule-superExpression}
:::

Super form is a special kind of expression that can only be used as the receiver
of a function or property access expression. Any usage of such an expression in
any other context is prohibited.

Super forms are used in classifier declarations to access the method implementations
from base classifier types without invoking overriding behaviour.

TODO()

### Jump expressions

:::{.paste target=grammar-rule-jumpExpression}
:::

*Jump expressions* are a group of expressions that redirect the order the program
is evaluated to a different program point when evaluated. All these expressions
have several things in common:

- They all have type `kotlin.Nothing`, effectively meaning that they never produce
  any runtime value;
- Any code that unconditionally follows such expression is never evaluated.

#### Throw expressions

TODO(): [Exceptions] go first

#### Return expressions

A *return expression*, when used inside a function body, immediately
stops evaluating the function and returns to the point where this function was
called, making the function call expression evaluate to the value specified
in this return expression (if any). A return expression with no value implicitly
returns the `kotlin.Unit` object.

There are two forms of return expression: a simple return expression, specified using
the `return` keyword, returning from the innermost
[function declaration][Function declaration] (or
[anonymous function expression][Anonymous function expression]) and the extended
return expression, using the form `return@`$Context$ where $Context$ may be one
of the following:

- The name of one of the enclosing function declarations to refer to this function.
  If several declarations match one name, an ambiguity compiler error is generated;
- If current expression is inside a lambda expression body, the name of the function
  using this lambda expression as a trailing lambda (TODO: Wut?) parameter may be used
  to refer to the lambda literal itself.

If returning from the referred function is allowed in current context, the return
is performed as usual. If returning from the referred function is not allowed,
a compiler error is generated.

#### Continue expression

A *continue expression* is a jump expression allowed only within loop bodies.
When evaluated, this expression passes the control to the start of the next loop
iteration.

There are two forms of continue expressions:

- A simple continue expression, specified using
  the `continue` keyword, which refers to the innermost loop statement in the current
  scope;
- An extended continue expression, denoted `continue@`$Loop$, where $Loop$ is a
  label referring to a labeled loop statement, which refers to the loop the label
  refers to.

TODO(): as a matter of fact, `continue` is not allowed inside `when` >_<

#### Break expression

A *break expression* is a jump expression allowed only within loop bodies.
When evaluated, this expression passes the control to the next program point
after the loop.

There are two forms of break expressions:

- A simple break expression, specified using
  the `break` keyword, which refers to the innermost loop statement in the current
  scope;
- An extended break expression, denoted `break@`$Loop$, where $Loop$ is a
  label referring to a labeled loop statement, which refers to the loop the label
  refers to.

TODO(): as a matter of fact, `break` is not allowed inside `when` >_<

### Operator expressions

#### Spread operator

### Safe call expression

### Type check expression

## TODOS()

- Overloadable operators && operator expansion
- Smart casts vs compile-time types
- What does `decaying` for vararg actually mean?

- !!! object literal typing looks just like restricted union types. Are there any traps hidden here?
- The whole last paragraph in [Object literals][Object literals] is pretty shady
- What does it mean for returning to be disallowed?
