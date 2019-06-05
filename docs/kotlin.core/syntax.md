## Syntax

### Grammar

#### Lexical grammar

##### Character classes

:::{ .grammar-rule #grammar-rule-LF }
**_LF_:**  
  ~  _<unicode character Line Feed U+000A>_
:::
:::{ .grammar-rule #grammar-rule-CR }
**_CR_:**  
  ~  _<unicode character Carriage Return U+000D>_
:::
:::{ .grammar-rule #grammar-rule-WS }
**_WS_:**  
  ~  _<one of the following characters: SPACE U+0020, TAB U+0009, Form Feed U+000C>_
:::
:::{ .grammar-rule #grammar-rule-Underscore }
**_Underscore_:**  
  ~  _<unicode character Low Line U+005F>_
:::
:::{ .grammar-rule #grammar-rule-Letter }
**_Letter_:**  
  ~  _<any unicode character from classes Ll, Lm, Lo, Lt, Lu or Nl>_
:::
:::{ .grammar-rule #grammar-rule-UnicodeDigit }
**_UnicodeDigit_:**  
  ~  _<any unicode character from class Nd>_
:::
:::{ .grammar-rule #grammar-rule-LineCharacter }
**_LineCharacter_:**  
  ~  _<any unicode character excluding LF and CR>_
:::
:::{ .grammar-rule #grammar-rule-BinaryDigit }
**_BinaryDigit_:**  
  ~  `'0'` | `'1'`
:::
:::{ .grammar-rule #grammar-rule-DecimalDigit }
**_DecimalDigit_:**  
  ~  `'0'` | `'1'` | `'2'` | `'3'` | `'4'` | `'5'` | `'6'` | `'7'` | `'8'` | `'9'`
:::
:::{ .grammar-rule #grammar-rule-HexDigit }
**_HexDigit_:**  
  ~  _DecimalDigit_   
      | `'A'` | `'B'` | `'C'` | `'D'` | `'E'` | `'F'`   
      | `'a'` | `'b'` | `'c'` | `'d'` | `'e'` | `'f'`
:::

##### Keywords and operators

**_Operator_:**  
  ~  `'.'` | `','` | `'('` | `')'` | `'['` | `']'` | `'@['` | `'{'` | `'}'` | `'*'` | `'%'` | `'/'` | `'+'` | `'-'` | `'++'` | `'--'`   
      | `'&&'` | `'||'` | `'!'` | `'!!'` | `':'` | `';'` | `'='` | `'+='` | `'-='` | `'*='` | `'/='` | `'%='` | `'->'` | `'=>'`   
      | `'..'` | `'::'` | `'?::'` | `';;'` | `'#'` | `'@'` | `'?'` | `'?:'` | `'<'` | `'>'` | `'\m'` | `'>='` | `'!='` | `'!=='`   
      | `'=='` | `'==='` | `'''` | `'"'` | `'"""'`

**_SoftKeyword_:**  
  ~  `'public'` | `'private'` | `'protected'` | `'internal'`   
    | `'enum'` | `'sealed'` | `'annotation'` | `'data'` | `'inner'`   
    | `'tailrec'` | `'operator'` | `'inline'` | `'infix'` | `'external'`   
    | `'suspend'` | `'override'` | `'abstract'` | `'final'` | `'open'`   
    | `'const'` | `'lateinit'` | `'vararg'` | `'noinline'` | `'crossinline'`   
    | `'reified'` | `'expect'` | `'actual'`   

**_Keyword_:**  
  ~  `'package'` | `'import'` | `'class'` | `'interface'`   
    | `'fun'` | `'object'` | `'val'` | `'var'` | `'typealias'`   
    | `'constructor'` | `'by'` | `'companion'` | `'init'`   
    | `'this'` | `'super'` | `'typeof'` | `'where'`   
    | `'if'` | `'else'` | `'when'` | `'try'` | `'catch'`   
    | `'finally'` | `'for'` | `'do'` | `'while'` | `'throw'`   
    | `'return'` | `'continue'` | `'break'` | `'as'`   
    | `'is'` | `'in'` | `'!is'` | `'!in'` | `'out'`   
    | `'get'` | `'set'` | `'dynamic'` | `'@file'`   
    | `'@field'` | `'@property'` | `'@get'` | `'@set'`   
    | `'@receiver'` | `'@param'` | `'@setparam'` | `'@delegate'`   

##### Whitespace and comments

:::{ .grammar-rule #grammar-rule-NL }
**_NL_:**  
  ~  _LF_ | _CR_ [_LF_]
:::
:::{ .grammar-rule #grammar-rule-ShebangLine }
**_ShebangLine_:**  
  ~  `'#!'` {_LineCharacter_}
:::
:::{ .grammar-rule #grammar-rule-LineComment }
**_LineComment_:**  
  ~  `'//'` {_LineCharacter_}
:::
:::{ .grammar-rule #grammar-rule-DelimitedComment }
**_DelimitedComment_:**  
  ~  `'/*'` {_DelimitedComment_ | <any character>} `'*/'`
:::

##### Number literals

:::{ .grammar-rule #grammar-rule-RealLiteral }
**_RealLiteral_:**  
  ~  _FloatLiteral_ | _DoubleLiteral_
:::
:::{ .grammar-rule #grammar-rule-FloatLiteral }
**_FloatLiteral_:**  
  ~  _DoubleLiteral_ (`'f'` | `'F'`)
      | _DecDigits_ (`'f'` | `'F'`)
:::
:::{ .grammar-rule #grammar-rule-DoubleLiteral }
**_DoubleLiteral_:**  
  ~  [_DecDigits_] `'.'` _DecDigits_ [_DoubleExponent_]
      | _DecDigits_ _DoubleExponent_
:::
:::{ .grammar-rule #grammar-rule-LongLiteral }
**_LongLiteral_:**  
  ~  (_IntegerLiteral_ | _HexLiteral_ | _BinLiteral_) `'L'`
:::
:::{ .grammar-rule #grammar-rule-IntegerLiteral }
**_IntegerLiteral_:**  
  ~  _DecDigitNoZero_ {_DecDigitOrSeparator_} _DecDigit_
      | _DecDigit_
:::
:::{ .grammar-rule #grammar-rule-HexLiteral }
**_HexLiteral_:**  
  ~  `'0'` (`'x'`|`'X'`) _HexDigit_ {_HexDigitOrSeparator_} _HexDigit_   
      | `'0'` (`'x'`|`'X'`) _HexDigit_
:::
:::{ .grammar-rule #grammar-rule-BinLiteral }
**_BinLiteral_:**  
  ~  `'0'` (`'b'`|`'B'`) _BinDigit_ {_BinDigitOrSeparator_} _BinDigit_   
      | `'0'` (`'b'`|`'B'`) _BinDigit_
:::
:::{ .grammar-rule #grammar-rule-DegDigitNoZero }
**_DecDigitNoZero_:**  
  ~  _DecDigit_ - `'0'`
:::
:::{ .grammar-rule #grammar-rule-DecDigitOrSeparator }
**_DecDigitOrSeparator_:**  
  ~  _DecDigit_ | _Underscore_
:::
:::{ .grammar-rule #grammar-rule-HexDigitOrSeparator }
**_HexDigitOrSeparator_:**  
  ~  _HexDigit_ | _Underscore_
:::
:::{ .grammar-rule #grammar-rule-BinDigitOrSeparator }
**_BinDigitOrSeparator_:**  
  ~  _BinDigit_ | _Underscore_
:::
:::{ .grammar-rule #grammar-rule-DecDigits }
**_DecDigits_:**  
  ~  _DecDigit_ {_DecDigitOrSeparator_} _DecDigit_ | _DecDigit_
:::
:::{ .grammar-rule #grammar-rule-BooleanLiteral }
**_BooleanLiteral_:**  
  ~  `'true'` | `'false'`
:::
:::{ .grammar-rule #grammar-rule-NullLiteral }
**_NullLiteral_:**  
  ~  `'null'`
:::

##### Identifiers

:::{ .grammar-rule #grammar-rule-Identifier }
**_Identifier_:**  
  ~  (_Letter_ | _Underscore_) {_Letter_ | _Underscore_ | _UnicodeDigit_}   
      | `` '`' `` {_EscapedIdentifierCharacter_} `` '`' ``
:::
:::{ .grammar-rule #grammar-rule-EscapedIdentifierCharacter }
**_EscapedIdentifierCharacter_:**  
  ~  _<any character except CR, LF, `` '`'' ``, `'['`, `']'`, `'<'` or `'>'`>_
:::
:::{ .grammar-rule #grammar-rule-IdentifierOrSoftKey }
**_IdentifierOrSoftKey_:**  
  ~  _Identifier_ | _SoftKeyword_
:::
:::{ .grammar-rule #grammar-rule-AtIdentifier }
**_AtIdentifier_:**  
  ~  `'@'` _IdentifierOrSoftKey_
:::
:::{ .grammar-rule #grammar-rule-IdentifierAt }
**_IdentifierAt_:**  
  ~  _IdentifierOrSoftKey_ `'@'`
:::

##### String literals

Syntax literals are fully defined in syntax grammar due to the complex nature of string interpolation

:::{ .grammar-rule #grammar-rule-CharacterLiteral }
**_CharacterLiteral_:**  
  ~  `'''` (_EscapeSeq_ | _<any character except CR, LF, `'''` and `'\'`>_) `'''`
:::
:::{ .grammar-rule #grammar-rule-EscapeSeq }
**_EscapeSeq_:**  
  ~  _UnicodeCharacterLiteral_ | _EscapedCharacter_
:::
:::{ .grammar-rule #grammar-rule-UnicodeCharacterLiteral }
**_UnicodeCharacterLiteral_:**  
  ~  `'\'` `'u'` _HexDigit_ _HexDigit_ _HexDigit_ _HexDigit_
:::
:::{ .grammar-rule #grammar-rule-EscapedCharacter }
**_EscapedCharacter_:**  
  ~  `'\'` (`'t'` | `'b'` | `'r'` | `'n'` | `'` | `'"'` | `'\'` | `'$'`)
:::
:::{ .grammar-rule #grammar-rule-FieldIdentifier }
**_FieldIdentifier_:**  
  ~  `'$'` _IdentifierOrSoftKey_
:::
:::{ .grammar-rule #grammar-rule-LineStrRef }
**_LineStrRef_:**  
  ~  _FieldIdentifier_
:::
:::{ .grammar-rule #grammar-rule-LineStrEscapedChar }
**_LineStrEscapedChar_:**  
  ~  _EscapedCharacter_ | _UnicodeCharacterLiteral_
:::
:::{ .grammar-rule #grammar-rule-LineStrExprStart }
**_LineStrExprStart_:**  
  ~  `'${'`
:::
:::{ .grammar-rule #grammar-rule-MultiLineStringQuote }
**_MultiLineStringQuote_:**  
  ~  `'"'` {`'"'`}
:::
:::{ .grammar-rule #grammar-rule-MultiLineStrRef }
**_MultiLineStrRef_:**  
  ~  _FieldIdentifier_
:::
:::{ .grammar-rule #grammar-rule-MultiLineStrText }
**_MultiLineStrText_:**  
  ~  {<any character except `'"'` and `'$'`} | `'$'`
:::
:::{ .grammar-rule #grammar-rule-MultiLineStrExprStart }
**_MultiLineStrExprStart_:**  
  ~  `'${'`
:::

##### Misc

:::{ .grammar-rule #grammar-rule-EOF }
**_EOF_:**  
  ~  _<end of input>_
:::

TODO: redo all the lexical grammar, right now it is a hand-written mess

#### Syntax grammar

<#include "kotlin.core/grammar.generated.md">
