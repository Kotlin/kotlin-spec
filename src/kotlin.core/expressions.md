## Expressions

TODO()

### Constant literals

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
that superseed traditional string literals. Please refer to the corresponding
section.

### Cast expression

### Elvis operator expression

### Not-null assertion operator expression

### Operator expressions

### Safe call expression

### Type check expression
