## Syntax

### Grammar

#### Lexical grammar

##### Character classes

**_LF_:**  
  ~  _<unicode character Line Feed U+000A>_

**_CR_:**  
  ~  _<unicode character Carriage Return U+000D>_

**_WS_:**  
  ~  _<one of the following characters: SPACE U+0020, TAB U+0009, Form Feed U+000C>_

**_Underscore_:**  
  ~  _<unicode character Low Line U+005F>_

**_Letter_:**  
  ~  _<any unicode character from classes Ll, Lm, Lo, Lt, Lu or Nl>_

**_UnicodeDigit_:**  
  ~  _<any unicode character from class Nd>_

**_LineCharacter_:**  
  ~  _<any unicode character excluding LF and CR>_

**_BinaryDigit_:**  
  ~  `0` | `1`

**_DecimalDigit_:**  
  ~  `0` | `1` | `2` | `3` | `4` | `5` | `6` | `7` | `8` | `9`

**_HexDigit_:**  
  ~  _DecimalDigit_   
      | `A` | `B` | `C` | `D` | `E` | `F`   
      | `a` | `b` | `c` | `d` | `e` | `f`

##### Keywords and operators

**_Operator_:**  
  ~  `.` | `,` | `(` | `)` | `[` | `]` | `@[` | `{` | `}` | `*` | `%` | `/` | `+` | `-` | `++` | `--`   
      | `&&` | `||` | `!` | `!!` | `:` | `;` | `=` | `+=` | `-=` | `*=` | `/=` | `%=` | `->` | `=>`   
      | `..` | `::` | `?::` | `;;` | `#` | `@` | `?` | `?:` | `<` | `>` | `\m` | `>=` | `!=` | `!==`   
      | `==` | `===` | `'` | `"` | `"""`

**_SoftKeyword_:**  
  ~  `public` | `private` | `protected` | `internal`   
    | `enum` | `sealed` | `annotation` | `data` | `inner`   
    | `tailrec` | `operator` | `inline` | `infix` | `external`   
    | `suspend` | `override` | `abstract` | `final` | `open`   
    | `const` | `lateinit` | `vararg` | `noinline` | `crossinline`   
    | `reified` | `expect` | `actual`   

**_Keyword_:**  
  ~  `package` | `import` | `class` | `interface`   
    | `fun` | `object` | `val` | `var` | `typealias`   
    | `constructor` | `by` | `companion` | `init`   
    | `this` | `super` | `typeof` | `where`   
    | `if` | `else` | `when` | `try` | `catch`   
    | `finally` | `for` | `do` | `while` | `throw`   
    | `return` | `continue` | `break` | `as`   
    | `is` | `in` | `!is` | `!in` | `out`   
    | `get` | `set` | `dynamic` | `@file`   
    | `@field` | `@property` | `@get` | `@set`   
    | `@receiver` | `@param` | `@setparam` | `@delegate`   

##### Whitespace and comments

**_NL_:**  
  ~  _LF_ | _CR_ [_LF_]

**_ShebangLine_:**  
  ~  `#!` {_LineCharacter_}

**_LineComment_:**  
  ~  `//` {_LineCharacter_}

**_DelimitedComment_:**  
  ~  `/*` {_DelimitedComment_ | <any character>} `*/`

##### Number literals

**_RealLiteral_:**  
  ~  _FloatLiteral_ | _DoubleLiteral_

**_FloatLiteral_:**  
  ~  _DoubleLiteral_ (`f` | `F`)
      | _DecDigits_ (`f` | `F`)

**_DoubleLiteral_:**  
  ~  [_DecDigits_] `.` _DecDigits_ [_DoubleExponent_]
      | _DecDigits_ _DoubleExponent_

**_LongLiteral_:**  
  ~  (_IntegerLiteral_ | _HexLiteral_ | _BinLiteral_) `L`

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

**_BooleanLiteral_:**  
  ~  `true` | `false`

**_NullLiteral_:**  
  ~  `null`

##### Identifiers

**_Identifier_:**  
  ~  (_Letter_ | _Underscore_) {_Letter_ | _Underscore_ | _UnicodeDigit_}   
      | `` ` `` {_EscapedIdentifierCharacter_} `` ` ``

**_EscapedIdentifierCharacter_:**  
  ~  _<any character except CR, LF, `` ` ``, `[`, `]`, `<` or `>`>_

**_IdentifierOrSoftKey_:**  
  ~  _Identifier_ | _SoftKeyword_

**_AtIdentifier_:**  
  ~  `@` _IdentifierOrSoftKey_

**_IdentifierAt_:**  
  ~  _IdentifierOrSoftKey_ `@`

##### String literals

Syntax literals are fully defined in syntax grammar due to the complex nature of string interpolation

**_CharacterLiteral_:**  
  ~  `'` (_EscapeSeq_ | _<any character except CR, LF, `'` and `\`>_) `'`

**_EscapeSeq_:**  
  ~  _UnicodeCharacterLiteral_ | _EscapedCharacter_

**_UnicodeCharacterLiteral_:**  
  ~  `\` `u` _HexDigit_ _HexDigit_ _HexDigit_ _HexDigit_

**_EscapedCharacter_:**  
  ~  `\` (`t` | `b` | `r` | `n` | `'` | `"` | `\` | `$`)

**_FieldIdentifier_:**  
  ~  `$` _IdentifierOrSoftKey_

**_LineStrRef_:**  
  ~  _FieldIdentifier_

**_LineStrEscapedChar_:**  
  ~  _EscapedCharacter_ | _UnicodeCharacterLiteral_

**_LineStrExprStart_:**  
  ~  `${`

**_MultiLineStringQuote_:**  
  ~  `"` {`"`}

**_MultiLineStrRef_:**  
  ~  _FieldIdentifier_

**_MultiLineStrText_:**  
  ~  {<any character except `"` and `$`} | `$`

**_MultiLineStrExprStart_:**  
  ~  `${`

#### Syntax grammar

<#include "grammar.generated.md">
