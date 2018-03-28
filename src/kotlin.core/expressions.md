## Expressions

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

### Conditional expression

**_ifExpression_:**  
  ~  `if` {_NL_} `(` {_NL_} _expression_ {_NL_} `)` {_NL_} _controlStructureBody_ [[`;`] {_NL_} `else` {_NL_} _controlStructureBody_]   
    | `if` {_NL_} `(` {_NL_} _expression_ {_NL_} `)` {_NL_} [`;` {_NL_}] `else` {_NL_} _controlStructureBody_   

**Conditional expressions** use the boolean value of one expression (*condition*) to decide
which of two other expressions (*branches*) should be evaluated.
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
several different other expressions (*cases*) to be evaluated depending on boolean conditions.
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
      are checked for equality using constant conditions.

The type of the resulting expression is
the [least upper bound][Least upper bound] of the types of all the entries (TODO(): not that simple).
If the expression is not exhaustive, it
has type [`kotlin.Unit`][`kotlin.Unit`] and the whole construct may not be used as an expression,
but only as a statement.

#### TODO()

### Cast expression

### Elvis operator expression

### Not-null assertion operator expression

### Operator expressions

### Safe call expression

### Type check expression

## TODOS()

- Control structure body typing
- Smart casts vs compile-time types
- The whole "used as an expression" vs "used as a statement" business
