## Expressions

TODO()

An expression may be *used as a statement* or *used as an expression* depending on the context.
As all expressions are valid statements [see the statements section][Statements],
free expressions may be used as single statements or inside code blocks.

An expression is used as an expression if it is encountered at any position
where a statement is not allowed, for example, as an operand to an operator or
as an immediate argument for a function call.
An expression is used as a statement if it is encountered at any position where
a statement is allowed.

Some expressions are only allowed to be used
as statements unless certain restrictions are met and this may affect the semantics,
the compile-type type information and the safety of these expressions.
All expressions are allowed to be used as statements.

TODO()

### Constant literals

Constant literals are expressions that correspond to constant, non-changing values.
Every constant literal is defined to have a single standard library type, whichever
it is defined to be on current platform.

#### Boolean literals

**_BooleanLiteral_:**  
  ~  `true` | `false`

Keywords `true` and `false` denote boolean literals of corresponding values.
These are two strong keywords and as such cannot be used as identifiers unless [escaped][Escaped identifiers].
Values `true` and `false` always have type `kotlin.Bool`.

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
Digits may be separated by the underscore symbol, but no underscore can be placed
before the first digit or after the last one. Please note that unlike other
languages Kotlin does not support octal literals. Even more, any decimal literal
starting with digit `0` and containing more than 1 digit is not a valid decimal literal.

Decimal literals may be suffixed by the long literal mark (`L` symbol).
A decimal literal with the mark has type `kotlin.Long`, while a literal without it
has type `kotlin.Int` if its value is below $2^{31}-1$ or type `kotlin.Long` otherwise.

##### Hexadecimal integer literals

A sequence of hexadecimal digit symbols (`0` through `9`, `a` through `f`, or `A` through `F`)
prefixed by `0x` or `0X` is a hexadecimal integer literal. Digits may be separated by the underscore symbol,
but no underscore can be placed before the first digit or after the last one.

Hexadecimal literals may be suffixed by the long literal mark (`L` symbol).
A hexadecimal literal with the mark has type `kotlin.Long`, while a literal without it
has type `kotlin.Int` if its value is below $2^{31}-1$ or type `kotlin.Long` otherwise.

##### Binary integer literals

A sequence of binary digit symbols (`0` or `1`)
prefixed by `0b` or `0B` is a binary integer literal. Digits may be separated by the underscore symbol,
but no underscore can be placed before the first digit or after the last one.

Binary literals may be suffixed by the long literal mark (`L` symbol).
A binary literal with the mark has type `kotlin.Long`, while a literal without it
has type `kotlin.Int` if its value is below $2^{31}-1$ or type `kotlin.Long` otherwise.

#### Real literals

**_RealLiteral_:**  
  ~  _FloatLiteral_ | _DoubleLiteral_

**_FloatLiteral_:**  
  ~  _DoubleLiteral_ (`f` | `F`)
      | _DecDigits_ (`f` | `F`)

**_DoubleLiteral_:**  
  ~  [_DecDigits_] `.` _DecDigits_ [_DoubleExponent_]
      | _DecDigits_ _DoubleExponent_

A *real literal* consists of the following parts: the whole-number part, a
decimal point (represented by the ASCII period character (`.`)), a fraction part
and an exponent. Unlike other languages, Kotlin real literals may only be expressed
in decimal numbers. The number also may be followed by type suffix (`f` or `F`).

The exponent is an exponent mark (`e` or `E`) followed by an optionaly signed
decimal integer (a sequence of decimal digits).

The whole-number part and the exponent part may be omitted. The fraction part may
only be omitted together with the decimal point if the whole part and either the
exponent part or type suffix are present. Unlike other languages, Kotlin does not
support omitting the fraction part, but leaving the decimal point in.

The digits of the whole-number part or the fraction part or the exponent may be optionally
separated by underscores, but an underscore may not be placed between, before, or after
these parts. It also may not be placed before or after the exponent sign symbol.

A real literal without a type suffix has type `kotlin.Double`, while a real literal
with the type suffix does have type `kotlin.Float`. There is no special suffix
attributed to the `kotlin.Double` type.

#### Character literals

**_CharacterLiteral_:**  
  ~  `'` (_EscapeSeq_ | _<any character except CR, LF, `'` and `\`>_) `'`

**_EscapeSeq_:**  
  ~  _UnicodeCharacterLiteral_ | _EscapedCharacter_

**_UnicodeCharacterLiteral_:**  
  ~  `\` `u` _HexDigit_ _HexDigit_ _HexDigit_ _HexDigit_

**_EscapedCharacter_:**  
  ~  `\` (`t` | `b` | `r` | `n` | `'` | `"` | `\` | `$`)

A **character literal** defines a constant holding a unicode character value.
A simply-formed character literal is any symbol between two single quotation mark
symbols (ASCII single quotation `'`), excluding newline symbols (*CR* and *LF*),
the single quotation symbol itself and the escaping mark (the ASCII backslash symbol `\`).

A character literal may also contain an escaped symbol of two kinds: a simple
escaped symbol or a unicode codepoint. Simple escaped symbols include:

- `\t` --- the unicode TAB symbol (TODO());
- `\b` --- the unicode BACKSPACE symbol(TODO());
- `\r` --- *CR*;
- `\n` --- *LF*;
- `\'` --- the unicode single quotation symbol(TODO());
- `\"` --- the unicode double quotation symbol(TODO());
- `\\` --- the unicode backslash symbol symbol(TODO());
- `\$` --- the unicode DOLLAR symbol.

A unicode codepoint escape sequence is the symbols `\u` followed by exactly four
hexadecimal digits. It represents the unicode symbol with the codepoint equal
to the number represented by these digits.
Please note that unicode escapes support only unicode symbols
in range U+0000 to U+FFFF.

Any character literal has type `kotlin.Char`.

#### String literals

Kotlin supports [string interpolation][String Interpolation] mechanisms
that supersede traditional string literals. Please refer to the corresponding
section.

#### Null literal

The keyword `null` signifies the **null reference**, which is a valid value for all
[nullable types][Nullable types].
Null reference implicitly has the nullable `kotlin.Nothing?` type and is, by definition,
the only valid value for this type (see [the corresponding section][`kotlin.Nothing`]).

TODO(): reshuffle these sections

### Try-expression

**_tryExpression_:**  
  ~  `try` {_NL_} _block_ {{_NL_} _catchBlock_} [{_NL_} _finallyBlock_]   

**_catchBlock_:**  
  ~  `catch` {_NL_} `(` {_annotation_} _simpleIdentifier_ `:` _userType_ `)` {_NL_} _block_   

**_finallyBlock_:**  
  ~  `finally` {_NL_} _block_   

A *try-expression* is an expression starting with the keyword `try`. It consists of
a code block (*try body*) and several optional additional blocks: one of more *catch blocks*,
starting with the soft keyword `catch` with a single parameter called *exception parameter*
followed by another code block
and a single optional *finally block*, starting with the soft keyword `finally`
and yet another code block. At least one catch or finally block must exist,
otherwise the expression is ill-formed.

The try-expression evaluates its body as normally, but if any statement in
the body throws an exception, the exception, rather than being propagated up
the call stack, gets checked for its type. If there exists any catch block
which parameter type is valid for the checked exception, this catch block
is evaluated immediately after the exception is thrown and the exception itself
is passed inside the catch block as the corresponding parameter.
If there are several catch blocks with suitable parameter types, the first one
is picked.

If there is a finally block, it gets evaluated after any evaluated catch block, or,
if no catch block was encountered, after the exception was thrown. If no catch
block was selected, the exception is [propagated as usual][Exceptions] up the call stack
after the finally block (if any) is evaluated. If no exception is thrown during
the evaluation of the try body, no catch blocks are checked, but the finally
block is executed anyway and program execution continues as normal.

The value of the try-expression is the same as the value of the last expression of
the try body (if no exception was thrown) or the value of the last expression of
the selected catch block (if one was selected). All other situations mean that
an exception is propagated up the call stack, so the value of the try-expression
becomes irrelevant. The finally block does get executed as described above, but
has no effect on the value returned by the try-expression.

The type of the try-expression is
the [least upper bound][Least upper bound] of the types of the last expressions of
the try body and the last expressions of all the catch blocks (TODO(): not that simple).
If any of the blocks have no valid last expression, the type is inferred to be
`kotlin.Unit`, but the try-expression may be used as an expression anyway.

### Conditional expression

**_ifExpression_:**  
  ~  `if` {_NL_} `(` {_NL_} _expression_ {_NL_} `)` {_NL_} _controlStructureBody_ [[`;`] {_NL_} `else` {_NL_} _controlStructureBody_]   
    | `if` {_NL_} `(` {_NL_} _expression_ {_NL_} `)` {_NL_} [`;` {_NL_}] `else` {_NL_} _controlStructureBody_   

**Conditional expressions** use the boolean value of one expression (*condition*) to decide
which of two control structure bodies (*branches*) should be evaluated.
If the condition evaluates to `true`, than the first branch (the true branch) is
evaluated, otherwise the second branch is.
The value of the resulting expression is the same as the value of the chosen branch.
The type of the resulting expression is
the [least upper bound][Least upper bound] of the types of two branches (TODO(): not that simple).
If one of the branches is omitted (see the grammar entry above), the resulting expression
has type [`kotlin.Unit`][`kotlin.Unit`] and the whole construct may not be used as an expression,
but only as a statement.

The condition expression must have type `kotlin.Boolean` (TODO(): or be smartcasted to it!),
otherwise it is a type error.

> When used as expressions, conditional expressions are special in the sense of operator
> precedence: they have the highest (same as all primary expressions) priority when
> placed on the right side of any binary expression, but when placed on the left side,
> they have the lowest priority. For details, see the [grammar][Syntax grammar]

### When expression

**_whenExpression_:**  
  ~  `when` {_NL_} [`(` _expression_ `)`] {_NL_} `{` {_NL_} {_whenEntry_ {_NL_}} {_NL_} `}`   

**_whenEntry_:**  
  ~  _whenCondition_ {{_NL_} `,` {_NL_} _whenCondition_} {_NL_} `->` {_NL_} _controlStructureBody_ [_semi_]   
    | `else` {_NL_} `->` {_NL_} _controlStructureBody_ [_semi_]   

**_whenCondition_:**  
  ~  _expression_   
    | _rangeTest_   
    | _typeTest_   

**_rangeTest_:**  
  ~  _inOperator_ {_NL_} _expression_   

**_typeTest_:**  
  ~  _isOperator_ {_NL_} _type_   

**When expression** is alike a **conditional expression** in the sense that it allows
several different control structure bodies (*cases*) to be evaluated depending on boolean conditions.
The key difference, however, is that when expressions may include several different
conditions. When expression has two different forms: with bound value and without it.

**When expression without bound value** (the form where the expression enclosed in parantheses is absent)
evaluates one of the many different expressions based on corresponding conditions present
in the same *when entry*. Each entry consists of a boolean *condition* (or a special `else` condition),
each of which is checked and evaluated in order of appearance. If the current condition
evaluates to `true`, the corresponding expression is evaluated and the value of
when expression is the same as the evaluated expression. All remaining conditions and expressions
are not evaluated. The `else` branch is a special branch that evaluates if none of
the branches above it evaluated to `true`.

> Informally speaking, you can always replace the `else` branch with literal `true` and
> the semantics of the entry would not change

The `else` entry is also special in the sense that it **must** be the last entry
in the expression, otherwise a compiler error must be generated.

**When expression with bound value** (the form where the expression enclosed in parantheses is present)
are very similar to the form without bound value, but use different syntax for conditions.
In fact, it supports three different condition forms:

- *Type test condition*: type checking operator [TODO: link] followed by type. The
  condition generated is a type check expression [TODO: link] with the same operator
  and the same type, but an implicit left hand side, which has the same value as the bound
  expression.
- *Contains test condition*: containment operator [TODO: link] followed by an expression; The
  condition generated is a containment check expression [TODO: link] with the same operator
  and the same right hand side expression, but an implicit left hand side, which has the same value as the bound
  expression.
- *Any other expression*. The condition generated is an equality operator [TODO: link], with
  the left hand side being the bound expression, and the right hand side being the expression placed inside
  the entry.
- The `else` condition, which works the exact same way as it would in the form
  without bound expression.

> This also means that if this form of `when` contains a boolean expression, it is not
> checked directly as if it would be in the other form, but rather checked for **equality**
> with the bound variable, which is not the same thing.

The type of the resulting expression is
the [least upper bound][Least upper bound] of the types of all the entries (TODO(): not that simple).
If the expression is not [exhaustive][Exhaustive when expressions], it
has type [`kotlin.Unit`][`kotlin.Unit`] and the whole construct may not be used as an expression,
but only as a statement.

#### Exhaustive when expressions

A when expression is called **_exhaustive_** if at least one of the following is true:

- It has an `else` entry;
- It has a bound value and at least one of the following holds:
    - The bound expression is of type `kotlin.Boolean` and the conditions contain
      both:
        - A [constant expression][Constant expressions] evaluating to value `true`;
        - A [constant expression][Constant expressions] evaluating to value `false`;
    - The bound expression is of a [`sealed class`][Sealed classes] type and all its possible subtypes
      are covered using type test conditions of this expression;
    - The bound expression is of an [`enum class`][Enum classes] type and all enumerated values
      are checked for equality using constant conditions;
    - The bound expression is of a nullable type and one of the cases above is met for
      its non-nullable counterpart and, in addition, there is a condition containing literal `null`.

### Logical disjunction expression

**_disjunction_:**  
  ~  _conjunction_ {{_NL_} `||` {_NL_} (_conjunction_ | _ifExpression_)}   

Operator symbol `||` performs logical disjunction over two values of type `kotlin.Boolean`.
Note that this operator is **lazy**, meaning that it does not evaluate the right hand side
argument unless the left hand side argument evaluated to `false`.

### Logical conjunction expression

**_conjunction_:**  
  ~  _equality_ {{_NL_} `&&` {_NL_} (_equality_ | _ifExpression_)}   

Operator symbol `&&` performs logical conjunction over two values of type `kotlin.Boolean`.
Note that this operator is **lazy**, meaning that it does not evaluate the right hand side
argument unless the left hand side argument evaluated to `true`.

### Equality expressions

**_equality_:**  
  ~  _comparison_ {_equalityOperator_ {_NL_} (_comparison_ | _ifExpression_)}   

**_equalityOperator_:**  
  ~  `!=`   
    | `!==`   
    | `==`   
    | `===`   

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

- $A$ `==` $B$ is exactly the same as $A$`?.equals(`$B$`) ?: (`$B$` === null)` where `equals` is a valid
  operator function available in the current scope;
- $A$ `!=` $B$ is exactly the same as `!(`$A$`?.equals(`$B$`) ?: (`$B$` === null))` where `equals` is a valid
  operator function available in the current scope.

> Please note that the class `kotlin.Any` has a built-in open operator member function called `equals`,
> meaning that there is always at least one available overloading candidate for any value equality expression.

Value equality expressions always have type `kotlin.Boolean`. If the corresponding operator function
has a different return type, it is invalid and a compiler error should be generated.

### Comparison expressions

**_comparison_:**  
  ~  _infixOperation_ [_comparisonOperator_ {_NL_} (_infixOperation_ | _ifExpression_)]   

**_comparisonOperator_:**  
  ~  `<`   
    | `>`   
    | `<=`   
    | `>=`   

*Comparison expressions* are binary expressions employing the comparison operators:
`<`, `>`, `<=` and `>=`. These operators are [overloadable][Overloadable operators] with the following
expansion:

- $A$`<`$B$ is exactly the same as $A$`.compareTo(`$B$`) [<] 0`
- $A$`>`$B$ is exactly the same as `0 [<] `$A$`.compareTo(`$B$`)`
- $A$`<=`$B$ is exactly the same as `!(`$A$`.compareTo(`$B$`) [<] 0)`
- $A$`>=`$B$ is exactly the same as `!(0 [<] `$A$`.compareTo(`$B$`))`

where `compareTo` is a valid operator function available in the current scope
and `[<]` (read "boxed less") is a special operator unavailable for in-code use in Kotlin and performing
integer "less-than" comparison of two integer numbers. The `compareTo` overloaded function
must have return type `kotlin.Int`, otherwise it's a compiler error.

All comparison expressions always have type `kotlin.Boolean`.

### Type-checking and containment-checking expressions

**_infixOperation_:**  
  ~  _elvisExpression_ {_inOperator_ {_NL_} (_elvisExpression_ | _ifExpression_) | _isOperator_ {_NL_} _type_}   

**_inOperator_:**  
  ~  `in` | `!in`   

**_isOperator_:**  
  ~  `is` | `!is`   

#### Type-checking expression

A type checking expression employs the use of an type-checking operators `is` or `!is`
and has an expression as a left-hand side operand and a type name as a right-hand
side operand. The type must be [runtime-available][Runtime-available types], otherwise
a compiler error should be generated. The expression checks whether the runtime type of
the expression on the left is the same (not the same for `!is`) as the type denoted
by the right-hand side argument.

Type-checking expression always has type `kotlin.Boolean`.

##### TODO()

- Smart casts!

#### Containment-checking expression

A *containment-checking expression* is a binary expression employing the containment operator
(`in` or `!in`). These are [overloadable][Overloadable operators] operators with the following expansion:

- $A$` in `$B$ is exactly the same as $B$`.contains(`$B$`)`;
- $A$` !in `$B$ is exactly the same as `!(`$B$`.contains(`$B$`))`;

where `contains` is a valid operator function available in the current scope. This
function must have return type `kotlin.Boolean`, otherwise a compiler error is generated.
Containment-checking expressions always have type `kotlin.Boolean`.

### Elvis operator expression

**_elvisExpression_:**  
  ~  _infixFunctionCall_ {{_NL_} `?:` {_NL_} (_infixFunctionCall_ | _ifExpression_)}   

*Elvis operator expression* is a binary expression that emplys the elvis operator (`?:`).
It checks whether the left-hand side expression is equal to `null`, and if it is,
evaluates and return the right-hand side expression.

This operator is **lazy**, meaning that if the left-hand side expression is not equal
to `null`, the right-hand side expression is never evaluated.

The type of elvis operator expression is the [least upper bound][The least upper bound]
of the non-nullable variant of the type of the left-hand side expression and the
type of the right-hand side expression. TODO(): not that simple, too

### Range expression

**_rangeExpression_:**  
  ~  _additiveExpression_ {`..` {_NL_} (_additiveExpression_ | _ifExpression_)}   

A *range expression* is a binary expression employing the range operator `..`.
It is an [overloadable][Overloadable operators] operator with the following expansion:

- $A$`..`$B$ is exactly the same as $A$`.rangeTo(`$B$`)`

where `rangeTo` is a valid operator function available in the current scope.
The return type of this function is not restricted.
The range expression has the same type as the return type of the corresponding
`rangeTo` overload variant.

### Additive expression

**_additiveExpression_:**  
  ~  _multiplicativeExpression_ {_additiveOperator_ {_NL_} (_multiplicativeExpression_ | _ifExpression_)}   

**_additiveOperator_:**  
  ~  `+` | `-`   

An *additive expression* is a binary expression employing the addition (`+`) or subtraction (`-`) operators.
These are [overloadable][Overloadable operators] operators with the following expansions:

- $A$`+`$B$ is exactly the same as $A$`.plus(`$B$`)`
- $A$`-`$B$ is exactly the same as $A$`.minus(`$B$`)`

where `plus` or `minus` is a valid operator function available in the current scope.
The return type of this function is not restricted.
The range expression has the same type as the return type of the corresponding
operator function overload variant.

### Multiplicative expression

**_multiplicativeExpression_:**  
  ~  _asExpression_ {_multiplicativeOperator_ {_NL_} (_asExpression_ | _ifExpression_)}   

**_multiplicativeOperator_:**  
  ~  `*`   
    | `/`   
    | `%`   

An *multiplicative expression* is a binary expression employing the multiplication (`*`), division (`/`) or remainder (`%`) operators.
These are [overloadable][Overloadable operators] operators with the following expansions:

- $A$`*`$B$ is exactly the same as $A$`.times(`$B$`)`
- $A$`/`$B$ is exactly the same as $A$`.div(`$B$`)`
- $A$`%`$B$ is exactly the same as $A$`.rem(`$B$`)`

> As of Kotlin version 1.2.31, there exists an additional overloading function for
> `%` called `mod`, which is deprecated

where `times`, `div`, `rem` is a valid operator function available in the current scope.
The return type of this function is not restricted.
The range expression has the same type as the return type of the corresponding
operator function overload variant.

### Cast expression

**_asExpression_:**  
  ~  _prefixUnaryExpression_ [{_NL_} _asOperator_ {_NL_} _type_]   

**_asOperator_:**  
  ~  `as`   
    | `as?`   
    | `:`   

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

**_prefixUnaryExpression_:**  
  ~  {_unaryPrefix_} _postfixUnaryExpression_   
    | _unaryPrefix_ {_unaryPrefix_} _ifExpression_   

**_unaryPrefix_:**  
  ~  _annotation_   
    | _labelDefinition_   
    | _prefixUnaryOperator_ {_NL_}   

**_prefixUnaryOperator_:**  
  ~  `++`   
    | `--`   
    | `-`   
    | `+`   
    | `!`   

#### Annotated and labeled expression

Any expression in Kotlin may be prefixed with any number of [annotations][Annotations]
and [labels][Labels]. These do not change the value of the expression and can be used
by external tools and platform-dependent features.

#### Prefix increment expression

A *prefix increment* expression is an expression employing the prefix form of
operator `++`. It is an [overloadable][Overloadable operators] operator with the following expansion:

`++`$A$ is exactly the same as evaluating the expression $A$`.inc()` where
`inc` is a suitable `operator` function, assigning the value to $A$ and then
returning the value of $A$ as the result of the expression.

The left-hand side of a postfix increment expression must be an [assignable expressions][Assignments].
Otherwise a compiler error must be generated.

The type of prefix increment is always equal to the type of the right-hand side
expression.

#### Prefix decrement expression

A *prefix increment* expression is an expression employing the prefix form of
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

**_postfixUnaryExpression_:**  
  ~  _primaryExpression_ {_postfixUnarySuffix_}   

**_postfixUnarySuffix_:**  
  ~  _postfixUnaryOperator_   
    | _typeArguments_   
    | _callSuffix_   
    | _indexingSuffix_   
    | _navigationSuffix_   

**_postfixUnaryOperator_:**  
  ~  `++`   
    | `--`   
    | `!!`   

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

**_postfixUnaryExpression_:**  
  ~  _primaryExpression_ {_postfixUnarySuffix_}   

**_postfixUnarySuffix_:**  
  ~  _postfixUnaryOperator_   
    | _typeArguments_   
    | _callSuffix_   
    | _indexingSuffix_   
    | _navigationSuffix_   

**_indexingSuffix_:**  
  ~  `[` {_NL_} _expression_ {{_NL_} `,` {_NL_} _expression_} {_NL_} `]`   

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

**_postfixUnaryExpression_:**  
  ~  _primaryExpression_ {_postfixUnarySuffix_}   

**_postfixUnarySuffix_:**  
  ~  _postfixUnaryOperator_   
    | _typeArguments_   
    | _callSuffix_   
    | _indexingSuffix_   
    | _navigationSuffix_

**_navigationSuffix_:**  
  ~  {_NL_} _memberAccessOperator_ {_NL_} (_simpleIdentifier_ | `class`)   

**_callSuffix_:**  
  ~  [_typeArguments_] [_valueArguments_] _annotatedLambda_   
    | [_typeArguments_] _valueArguments_   

**_annotatedLambda_:**  
  ~  {annotation | _IdentifierAt_} {_NL_} _lambdaLiteral_   

**_valueArguments_:**  
  ~  `(` {_NL_} [_valueArgument_] {_NL_} `)`   
    | `(` {_NL_} _valueArgument_ {{_NL_} `,` {_NL_} _valueArgument_} {_NL_} `)`   

**_typeArguments_:**  
  ~  `<` {_NL_} _typeProjection_ {{_NL_} `,` {_NL_} _typeProjection_} {_NL_} `>`   

**_typeProjection_:**  
  ~  [_typeProjectionModifierList_] _type_ | `*`   

**_typeProjectionModifierList_:**  
  ~  {_varianceAnnotation_}   

**_navigationSuffix_:**  
  ~  {_NL_} _memberAccessOperator_ {_NL_} (_simpleIdentifier_ | `class`)   

**_memberAccessOperator_:**  
  ~  `.` | `?.` | `::`   

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

**_anonymousFunction_:**  
  ~  `fun`   
    [{_NL_} _type_ {_NL_} `.`]   
    {_NL_} _functionValueParameters_   
    [{_NL_} `:` {_NL_} _type_]   
    [{_NL_} _typeConstraints_]   
    [{_NL_} _functionBody_]   

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

**_lambdaLiteral_:**  
  ~  `{` {_NL_} _statements_ {_NL_} `}`   
    | `{` {_NL_} _lambdaParameters_ {_NL_} `->` {_NL_} _statements_ {_NL_} `}`

**_lambdaParameters_:**  
  ~  [_lambdaParameter_] {{_NL_} `,` {_NL_} _lambdaParameter_}   

**_lambdaParameter_:**  
  ~  _variableDeclaration_   
    | _multiVariableDeclaration_ [{_NL_} `:` {_NL_} _type_]   

Lambda literals TODO()

### Object literals

**_objectLiteral_:**  
  ~  `object` {_NL_} `:` {_NL_} _delegationSpecifiers_ [{_NL_} _classBody_]   
    | `object` {_NL_} _classBody_   

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

**_thisExpression_:**  
  ~  `this` [_AtIdentifier_]

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

**_superExpression_:**  
  ~  `super` [`<` {_NL_} _type_ {_NL_} `>`] [_AtIdentifier_]

Super form is a special kind of expression that can only be used as the receiver
of a function or property access expression. Any usage of such an expression in
any other context is prohibited.

Super forms are used in classifier declarations to access the method implementations
from base classifier types without invoking overriding behaviour.

TODO()

### Jump expressions

**_jumpExpression_:**  
  ~  `throw` {_NL_} _expression_   
    | (`return` | `return@` _Identifier_) [_expression_]   
    | `continue` | `continue@` _Identifier_   
    | `break` | `break@` _Identifier_   

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

### Safe call expression

### Type check expression

## TODOS()

- Overloadable operators && operator expansion
- Smart casts vs compile-time types
- What does `decaying` for vararg actually mean?

- !!! object literal typing looks just like restricted union types. Are there any traps hidden here?
- The whole last paragraph in [Object literals][Object literals] is pretty shady
- What does it mean for returning to be disallowed?
