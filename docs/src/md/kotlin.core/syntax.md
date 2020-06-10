## Syntax and grammar

### Lexical grammar

#### Whitespace and comments

:::{ .grammar-rule #grammar-rule-LF }
**_LF_:**  
  ~  _<unicode character Line Feed U+000A>_
:::
:::{ .grammar-rule #grammar-rule-CR }
**_CR_:**  
  ~  _<unicode character Carriage Return U+000D>_
:::
:::{ .grammar-rule #grammar-rule-ShebangLine }
**_ShebangLine_:**  
  ~ `'#!'` {_<any character excluding CR and LF >_}
:::
:::{ .grammar-rule #grammar-rule-DelimitedComment }
**_DelimitedComment_:**  
  ~ `'/*'` { _[DelimitedComment](#grammar-rule-DelimitedComment)_ | _<any character>_ } `'*/'`
:::
:::{ .grammar-rule #grammar-rule-LineComment }
**_LineComment_:**  
  ~ `'//'` {_<any character excluding CR and LF >_}
:::
:::{ .grammar-rule #grammar-rule-WS }
**_WS_:**  
  ~  _<one of the following characters: SPACE U+0020, TAB U+0009, Form Feed U+000C>_
:::
:::{ .grammar-rule #grammar-rule-NL }
**_NL_:**  
  ~  _[LF](#grammar-rule-LF )_ | (_[CR](#grammar-rule-CR )_ [_[LF](#grammar-rule-LF )_])
:::
:::{ .grammar-rule #grammar-rule-Hidden }
**_Hidden_:**
  ~ _[DelimitedComment](#grammar-rule-DelimitedComment)_ | _[LineComment](#grammar-rule-LineComment)_ | _[WS](#grammar-rule-WS)_
:::

#### Keywords and operators

::: { .grammar-rule #grammar-rule-RESERVED }
**_RESERVED_:**  
  ~ `'...'`
:::
::: { .grammar-rule #grammar-rule-DOT }
**_DOT_:**  
  ~ `'.'`
:::
::: { .grammar-rule #grammar-rule-COMMA }
**_COMMA_:**  
  ~ `','`
:::
::: { .grammar-rule #grammar-rule-LPAREN }
**_LPAREN_:**  
  ~ `'('`
:::
::: { .grammar-rule #grammar-rule-RPAREN }
**_RPAREN_:**  
  ~ `')'`
:::
::: { .grammar-rule #grammar-rule-LSQUARE }
**_LSQUARE_:**  
  ~ `'['`
:::
::: { .grammar-rule #grammar-rule-RSQUARE }
**_RSQUARE_:**  
  ~ `']'`
:::
::: { .grammar-rule #grammar-rule-LCURL }
**_LCURL_:**  
  ~ `'{'`
:::
::: { .grammar-rule #grammar-rule-RCURL }
**_RCURL_:**  
  ~ `'}'`
:::
::: { .grammar-rule #grammar-rule-MULT }
**_MULT_:**  
  ~ `'*'`
:::
::: { .grammar-rule #grammar-rule-MOD }
**_MOD_:**  
  ~ `'%'`
:::
::: { .grammar-rule #grammar-rule-DIV }
**_DIV_:**  
  ~ `'/'`
:::
::: { .grammar-rule #grammar-rule-ADD }
**_ADD_:**  
  ~ `'+'`
:::
::: { .grammar-rule #grammar-rule-SUB }
**_SUB_:**  
  ~ `'-'`
:::
::: { .grammar-rule #grammar-rule-INCR }
**_INCR_:**  
  ~ `'++'`
:::
::: { .grammar-rule #grammar-rule-DECR }
**_DECR_:**  
  ~ `'--'`
:::
::: { .grammar-rule #grammar-rule-CONJ }
**_CONJ_:**  
  ~ `'&&'`
:::
::: { .grammar-rule #grammar-rule-DISJ }
**_DISJ_:**  
  ~ `'||'`
:::
::: { .grammar-rule #grammar-rule-EXCL_WS }
**_EXCL_WS_:**  
  ~ `'!'` [_Hidden_](#grammar-rule-Hidden)
:::
::: { .grammar-rule #grammar-rule-EXCL_NO_WS }
**_EXCL_NO_WS_:**  
  ~ `'!'`
:::
::: { .grammar-rule #grammar-rule-COLON }
**_COLON_:**  
  ~ `':'`
:::
::: { .grammar-rule #grammar-rule-SEMICOLON }
**_SEMICOLON_:**  
  ~ `';'`
:::
::: { .grammar-rule #grammar-rule-ASSIGNMENT }
**_ASSIGNMENT_:**  
  ~ `'='`
:::
::: { .grammar-rule #grammar-rule-ADD_ASSIGNMENT }
**_ADD_ASSIGNMENT_:**  
  ~ `'+='`
:::
::: { .grammar-rule #grammar-rule-SUB_ASSIGNMENT }
**_SUB_ASSIGNMENT_:**  
  ~ `'-='`
:::
::: { .grammar-rule #grammar-rule-MULT_ASSIGNMENT }
**_MULT_ASSIGNMENT_:**  
  ~ `'*='`
:::
::: { .grammar-rule #grammar-rule-DIV_ASSIGNMENT }
**_DIV_ASSIGNMENT_:**  
  ~ `'/='`
:::
::: { .grammar-rule #grammar-rule-MOD_ASSIGNMENT }
**_MOD_ASSIGNMENT_:**  
  ~ `'%='`
:::
::: { .grammar-rule #grammar-rule-ARROW }
**_ARROW_:**  
  ~ `'->'`
:::
::: { .grammar-rule #grammar-rule-DOUBLE_ARROW }
**_DOUBLE_ARROW_:**  
  ~ `'=>'`
:::
::: { .grammar-rule #grammar-rule-RANGE }
**_RANGE_:**  
  ~ `'..'`
:::
::: { .grammar-rule #grammar-rule-COLONCOLON }
**_COLONCOLON_:**  
  ~ `'::'`
:::
::: { .grammar-rule #grammar-rule-DOUBLE_SEMICOLON }
**_DOUBLE_SEMICOLON_:**  
  ~ `';;'`
:::
::: { .grammar-rule #grammar-rule-HASH }
**_HASH_:**  
  ~ `'#'`
:::
::: { .grammar-rule #grammar-rule-AT_NO_WS }
**_AT_NO_WS_:**  
  ~ `'@'`
:::
::: { .grammar-rule #grammar-rule-AT_POST_WS }
**_AT_POST_WS_:**  
  ~ `'@'` ([_Hidden_](#grammar-rule-Hidden) | [_NL_](#grammar-rule-NL))
:::
::: { .grammar-rule #grammar-rule-AT_PRE_WS }
**_AT_PRE_WS_:**  
  ~ ([_Hidden_](#grammar-rule-Hidden) | [_NL_](#grammar-rule-NL)) `'@'`
:::
::: { .grammar-rule #grammar-rule-AT_BOTH_WS }
**_AT_BOTH_WS_:**  
  ~ ([_Hidden_](#grammar-rule-Hidden) | [_NL_](#grammar-rule-NL)) `'@'` ([_Hidden_](#grammar-rule-Hidden) | [_NL_](#grammar-rule-NL))
:::
::: { .grammar-rule #grammar-rule-QUEST_WS }
**_QUEST_WS_:**  
  ~ `'?'` [_Hidden_](#grammar-rule-Hidden)
:::
::: { .grammar-rule #grammar-rule-QUEST_NO_WS }
**_QUEST_NO_WS_:**  
  ~ `'?'`
:::
::: { .grammar-rule #grammar-rule-LANGLE }
**_LANGLE_:**  
  ~ `'<'`
:::
::: { .grammar-rule #grammar-rule-RANGLE }
**_RANGLE_:**  
  ~ `'>'`
:::
::: { .grammar-rule #grammar-rule-LE }
**_LE_:**  
  ~ `'<='`
:::
::: { .grammar-rule #grammar-rule-GE }
**_GE_:**  
  ~ `'>='`
:::
::: { .grammar-rule #grammar-rule-EXCL_EQ }
**_EXCL_EQ_:**  
  ~ `'!='`
:::
::: { .grammar-rule #grammar-rule-EXCL_EQEQ }
**_EXCL_EQEQ_:**  
  ~ `'!=='`
:::
::: { .grammar-rule #grammar-rule-AS_SAFE }
**_AS_SAFE_:**  
  ~ `'as?'`
:::
::: { .grammar-rule #grammar-rule-EQEQ }
**_EQEQ_:**  
  ~ `'=='`
:::
::: { .grammar-rule #grammar-rule-EQEQEQ }
**_EQEQEQ_:**  
  ~ `'==='`
:::
::: { .grammar-rule #grammar-rule-SINGLE_QUOTE }
**_SINGLE_QUOTE_:**  
  ~ `'\''`
:::
::: { .grammar-rule #grammar-rule-RETURN_AT }
**_RETURN_AT_:**  
  ~ `'return@'` [_Identifier_](#grammar-rule-Identifier)
:::
::: { .grammar-rule #grammar-rule-CONTINUE_AT }
**_CONTINUE_AT_:**  
  ~ `'continue@'` [_Identifier_](#grammar-rule-Identifier)
:::
::: { .grammar-rule #grammar-rule-BREAK_AT }
**_BREAK_AT_:**  
  ~ `'break@'` [_Identifier_](#grammar-rule-Identifier)
:::
::: { .grammar-rule #grammar-rule-THIS_AT }
**_THIS_AT_:**  
  ~ `'this@'` [_Identifier_](#grammar-rule-Identifier)
:::
::: { .grammar-rule #grammar-rule-SUPER_AT }
**_SUPER_AT_:**  
  ~ `'super@'` [_Identifier_](#grammar-rule-Identifier)
:::
::: { .grammar-rule #grammar-rule-FILE }
**_FILE_:**  
  ~ `'file'`
:::
::: { .grammar-rule #grammar-rule-FIELD }
**_FIELD_:**  
  ~ `'field'`
:::
::: { .grammar-rule #grammar-rule-PROPERTY }
**_PROPERTY_:**  
  ~ `'property'`
:::
::: { .grammar-rule #grammar-rule-GET }
**_GET_:**  
  ~ `'get'`
:::
::: { .grammar-rule #grammar-rule-SET }
**_SET_:**  
  ~ `'set'`
:::
::: { .grammar-rule #grammar-rule-RECEIVER }
**_RECEIVER_:**  
  ~ `'receiver'`
:::
::: { .grammar-rule #grammar-rule-PARAM }
**_PARAM_:**  
  ~ `'param'`
:::
::: { .grammar-rule #grammar-rule-SETPARAM }
**_SETPARAM_:**  
  ~ `'setparam'`
:::
::: { .grammar-rule #grammar-rule-DELEGATE }
**_DELEGATE_:**  
  ~ `'delegate'`
:::
::: { .grammar-rule #grammar-rule-PACKAGE }
**_PACKAGE_:**  
  ~ `'package'`
:::
::: { .grammar-rule #grammar-rule-IMPORT }
**_IMPORT_:**  
  ~ `'import'`
:::
::: { .grammar-rule #grammar-rule-CLASS }
**_CLASS_:**  
  ~ `'class'`
:::
::: { .grammar-rule #grammar-rule-INTERFACE }
**_INTERFACE_:**  
  ~ `'interface'`
:::
::: { .grammar-rule #grammar-rule-FUN }
**_FUN_:**  
  ~ `'fun'`
:::
::: { .grammar-rule #grammar-rule-OBJECT }
**_OBJECT_:**  
  ~ `'object'`
:::
::: { .grammar-rule #grammar-rule-VAL }
**_VAL_:**  
  ~ `'val'`
:::
::: { .grammar-rule #grammar-rule-VAR }
**_VAR_:**  
  ~ `'var'`
:::
::: { .grammar-rule #grammar-rule-TYPE_ALIAS }
**_TYPE_ALIAS_:**  
  ~ `'typealias'`
:::
::: { .grammar-rule #grammar-rule-CONSTRUCTOR }
**_CONSTRUCTOR_:**  
  ~ `'constructor'`
:::
::: { .grammar-rule #grammar-rule-BY }
**_BY_:**  
  ~ `'by'`
:::
::: { .grammar-rule #grammar-rule-COMPANION }
**_COMPANION_:**  
  ~ `'companion'`
:::
::: { .grammar-rule #grammar-rule-INIT }
**_INIT_:**  
  ~ `'init'`
:::
::: { .grammar-rule #grammar-rule-THIS }
**_THIS_:**  
  ~ `'this'`
:::
::: { .grammar-rule #grammar-rule-SUPER }
**_SUPER_:**  
  ~ `'super'`
:::
::: { .grammar-rule #grammar-rule-TYPEOF }
**_TYPEOF_:**  
  ~ `'typeof'`
:::
::: { .grammar-rule #grammar-rule-WHERE }
**_WHERE_:**  
  ~ `'where'`
:::
::: { .grammar-rule #grammar-rule-IF }
**_IF_:**  
  ~ `'if'`
:::
::: { .grammar-rule #grammar-rule-ELSE }
**_ELSE_:**  
  ~ `'else'`
:::
::: { .grammar-rule #grammar-rule-WHEN }
**_WHEN_:**  
  ~ `'when'`
:::
::: { .grammar-rule #grammar-rule-TRY }
**_TRY_:**  
  ~ `'try'`
:::
::: { .grammar-rule #grammar-rule-CATCH }
**_CATCH_:**  
  ~ `'catch'`
:::
::: { .grammar-rule #grammar-rule-FINALLY }
**_FINALLY_:**  
  ~ `'finally'`
:::
::: { .grammar-rule #grammar-rule-FOR }
**_FOR_:**  
  ~ `'for'`
:::
::: { .grammar-rule #grammar-rule-DO }
**_DO_:**  
  ~ `'do'`
:::
::: { .grammar-rule #grammar-rule-WHILE }
**_WHILE_:**  
  ~ `'while'`
:::
::: { .grammar-rule #grammar-rule-THROW }
**_THROW_:**  
  ~ `'throw'`
:::
::: { .grammar-rule #grammar-rule-RETURN }
**_RETURN_:**  
  ~ `'return'`
:::
::: { .grammar-rule #grammar-rule-CONTINUE }
**_CONTINUE_:**  
  ~ `'continue'`
:::
::: { .grammar-rule #grammar-rule-BREAK }
**_BREAK_:**  
  ~ `'break'`
:::
::: { .grammar-rule #grammar-rule-AS }
**_AS_:**  
  ~ `'as'`
:::
::: { .grammar-rule #grammar-rule-IS }
**_IS_:**  
  ~ `'is'`
:::
::: { .grammar-rule #grammar-rule-IN }
**_IN_:**  
  ~ `'in'`
:::
::: { .grammar-rule #grammar-rule-NOT_IS }
**_NOT_IS_:**  
  ~ `'!is'` ([_Hidden_](#grammar-rule-Hidden) | [_NL_](#grammar-rule-NL))
:::
::: { .grammar-rule #grammar-rule-NOT_IN }
**_NOT_IN_:**  
  ~ `'!in'` ([_Hidden_](#grammar-rule-Hidden) | [_NL_](#grammar-rule-NL))
:::
::: { .grammar-rule #grammar-rule-OUT }
**_OUT_:**  
  ~ `'out'`
:::
::: { .grammar-rule #grammar-rule-DYNAMIC }
**_DYNAMIC_:**  
  ~ `'dynamic'`
:::
::: { .grammar-rule #grammar-rule-PUBLIC }
**_PUBLIC_:**  
  ~ `'public'`
:::
::: { .grammar-rule #grammar-rule-PRIVATE }
**_PRIVATE_:**  
  ~ `'private'`
:::
::: { .grammar-rule #grammar-rule-PROTECTED }
**_PROTECTED_:**  
  ~ `'protected'`
:::
::: { .grammar-rule #grammar-rule-INTERNAL }
**_INTERNAL_:**  
  ~ `'internal'`
:::
::: { .grammar-rule #grammar-rule-ENUM }
**_ENUM_:**  
  ~ `'enum'`
:::
::: { .grammar-rule #grammar-rule-SEALED }
**_SEALED_:**  
  ~ `'sealed'`
:::
::: { .grammar-rule #grammar-rule-ANNOTATION }
**_ANNOTATION_:**  
  ~ `'annotation'`
:::
::: { .grammar-rule #grammar-rule-DATA }
**_DATA_:**  
  ~ `'data'`
:::
::: { .grammar-rule #grammar-rule-INNER }
**_INNER_:**  
  ~ `'inner'`
:::
::: { .grammar-rule #grammar-rule-TAILREC }
**_TAILREC_:**  
  ~ `'tailrec'`
:::
::: { .grammar-rule #grammar-rule-OPERATOR }
**_OPERATOR_:**  
  ~ `'operator'`
:::
::: { .grammar-rule #grammar-rule-INLINE }
**_INLINE_:**  
  ~ `'inline'`
:::
::: { .grammar-rule #grammar-rule-INFIX }
**_INFIX_:**  
  ~ `'infix'`
:::
::: { .grammar-rule #grammar-rule-EXTERNAL }
**_EXTERNAL_:**  
  ~ `'external'`
:::
::: { .grammar-rule #grammar-rule-SUSPEND }
**_SUSPEND_:**  
  ~ `'suspend'`
:::
::: { .grammar-rule #grammar-rule-OVERRIDE }
**_OVERRIDE_:**  
  ~ `'override'`
:::
::: { .grammar-rule #grammar-rule-ABSTRACT }
**_ABSTRACT_:**  
  ~ `'abstract'`
:::
::: { .grammar-rule #grammar-rule-FINAL }
**_FINAL_:**  
  ~ `'final'`
:::
::: { .grammar-rule #grammar-rule-OPEN }
**_OPEN_:**  
  ~ `'open'`
:::
::: { .grammar-rule #grammar-rule-CONST }
**_CONST_:**  
  ~ `'const'`
:::
::: { .grammar-rule #grammar-rule-LATEINIT }
**_LATEINIT_:**  
  ~ `'lateinit'`
:::
::: { .grammar-rule #grammar-rule-VARARG }
**_VARARG_:**  
  ~ `'vararg'`
:::
::: { .grammar-rule #grammar-rule-NOINLINE }
**_NOINLINE_:**  
  ~ `'noinline'`
:::
::: { .grammar-rule #grammar-rule-CROSSINLINE }
**_CROSSINLINE_:**  
  ~ `'crossinline'`
:::
::: { .grammar-rule #grammar-rule-REIFIED }
**_REIFIED_:**  
  ~ `'reified'`
:::
::: { .grammar-rule #grammar-rule-EXPECT }
**_EXPECT_:**  
  ~ `'expect'`
:::
::: { .grammar-rule #grammar-rule-ACTUAL }
**_ACTUAL_:**  
  ~ `'actual'`
:::

#### Literals

::: { .grammar-rule #grammar-rule-DecDigitNoZero }
**_DecDigitNoZero_:**  
  ~ `'1'` | `'2'` | `'3'` | `'4'` | `'5'` | `'6'` | `'7'` | `'8'` | `'9'`
:::
::: { .grammar-rule #grammar-rule-DecDigit }
**_DecDigit_:**  
  ~ `'0'` | `'1'` | `'2'` | `'3'` | `'4'` | `'5'` | `'6'` | `'7'` | `'8'` | `'9'`
:::
::: { .grammar-rule #grammar-rule-DecDigitOrSeparator }
**_DecDigitOrSeparator_:**  
  ~ [_DecDigit_](#grammar-rule-DecDigit) | `'_'`
:::
::: { .grammar-rule #grammar-rule-DecDigits }
**_DecDigits_:**  
  ~ [_DecDigit_](#grammar-rule-DecDigit) {[_DecDigitOrSeparator_](#grammar-rule-DecDigitOrSeparator)} [_DecDigit_](#grammar-rule-DecDigit)  
  | [_DecDigit_](#grammar-rule-DecDigit)
:::
::: { .grammar-rule #grammar-rule-DoubleExponent }
**_DoubleExponent_:**  
  ~ (`'e'` | `'E'`) [(`'+'` | `'-'`)] [_DecDigits_](#grammar-rule-DecDigits)
:::
::: { .grammar-rule #grammar-rule-RealLiteral }
**_RealLiteral_:**  
  ~ [_FloatLiteral_](#grammar-rule-FloatLiteral) | [_DoubleLiteral_](#grammar-rule-DoubleLiteral)
:::
::: { .grammar-rule #grammar-rule-FloatLiteral }
**_FloatLiteral_:**  
  ~ [_DoubleLiteral_](#grammar-rule-DoubleLiteral) (`'f'` | `'F'`)  
  | [_DecDigits_](#grammar-rule-DecDigits) (`'f'` | `'F'`)
:::
::: { .grammar-rule #grammar-rule-DoubleLiteral }
**_DoubleLiteral_:**  
  ~ [[_DecDigits_](#grammar-rule-DecDigits)] `'.'` [_DecDigits_](#grammar-rule-DecDigits) [[_DoubleExponent_](#grammar-rule-DoubleExponent)]  
  | [[_DecDigits_](#grammar-rule-DecDigits)] [[_DoubleExponent_](#grammar-rule-DoubleExponent)]
:::
::: { .grammar-rule #grammar-rule-IntegerLiteral }
**_IntegerLiteral_:**  
  ~ [_DecDigitNoZero_](#grammar-rule-DecDigitNoZero) {[_DecDigitOrSeparator_](#grammar-rule-DecDigitOrSeparator)} [_DecDigit_](#grammar-rule-DecDigit)  
  | [_DecDigit_](#grammar-rule-DecDigit)
:::
:::{ .grammar-rule #grammar-rule-HexDigit }
**_HexDigit_:**  
  ~  [_DecDigit_](#grammar-rule-DecDigit)   
  | `'A'` | `'B'` | `'C'` | `'D'` | `'E'` | `'F'`   
  | `'a'` | `'b'` | `'c'` | `'d'` | `'e'` | `'f'`
:::
:::{ .grammar-rule #grammar-rule-HexDigitOrSeparator }
**_HexDigitOrSeparator_:**  
  ~  [_HexDigit_](#grammar-rule-HexDigit) | `'_'`
:::
:::{ .grammar-rule #grammar-rule-HexLiteral }
**_HexLiteral_**  
  ~ `'0'` (`'x'` | `'X'`) [_HexDigit_](#grammar-rule-HexDigit) {[_HexDigitOrSeparator_](#grammar-rule-HexDigitOrSeparator)} [_HexDigit_](#grammar-rule-HexDigit)  
  | `'0'` (`'x'` | `'X'`) [_HexDigit_](#grammar-rule-HexDigit)
:::
:::{ .grammar-rule #grammar-rule-BinDigit }
**_BinDigit_**  
  ~ `'0'` | `'1'`
:::
:::{ .grammar-rule #grammar-rule-BinDigitOrSeparator }
**_BinDigitOrSeparator_**  
  ~ [_BinDigit_](#grammar-rule-BinDigit) | `'_'`
:::
:::{ .grammar-rule #grammar-rule-BinLiteral }
**_BinLiteral_**  
  ~ `'0'` (`'b'` | `'B'`) [_BinDigit_](#grammar-rule-BinDigit) {[_BinDigitOrSeparator_](#grammar-rule-BinDigitOrSeparator)} [_BinDigit_](#grammar-rule-BinDigit)  
  | `'0'` (`'b'` | `'B'`) [_BinDigit_](#grammar-rule-BinDigit)
:::
:::{ .grammar-rule #grammar-rule-LongLiteral }
**_LongLiteral_**  
  ~ ([_IntegerLiteral_](#grammar-rule-IntegerLiteral) | [_HexLiteral_](#grammar-rule-HexLiteral)   | [_BinLiteral_](#grammar-rule-BinLiteral)) `'L'`
:::
:::{ .grammar-rule #grammar-rule-BooleanLiteral }
**_BooleanLiteral_**  
  ~ `'true'` | `'false'`
:::
:::{ .grammar-rule #grammar-rule-NullLiteral }
**_NullLiteral_**  
  ~ `'null'`
:::
:::{ .grammar-rule #grammar-rule-CharacterLiteral }
**_CharacterLiteral_**  
  ~ `'''` ([_EscapeSeq_](#grammar-rule-EscapeSeq) | <any character excluding CR, LF, `'''` or `'\'`>) `'''`
:::
:::{ .grammar-rule #grammar-rule-UniCharacterLiteral }
**_UniCharacterLiteral_**  
  ~ `'\'` `'u'` [_HexDigit_](#grammar-rule-HexDigit) [_HexDigit_](#grammar-rule-HexDigit) [_HexDigit_](#grammar-rule-HexDigit) [_HexDigit_](#grammar-rule-HexDigit) 
:::
:::{ .grammar-rule #grammar-rule-EscapedIdentifier }
**_EscapedIdentifier_**  
  ~ `'\'` (`'t'` | `'b'` | `'r'` | `'n'` | `'''` | `'"'` | `'\'` | `'$'`)
:::
:::{ .grammar-rule #grammar-rule-EscapeSeq }
**_EscapeSeq_**  
  ~ [_UniCharacterLiteral_](#grammar-rule-UniCharacterLiteral) | [_EscapedIdentifier_](#grammar-rule-EscapedIdentifier)
:::

#### Identifiers

:::{ .grammar-rule #grammar-rule-Letter }
**_Letter_**  
  ~ <any unicode character of classes LL, LM, LO, LT, LU or NL>
:::
:::{ .grammar-rule #grammar-rule-QuotedSymbol }
**_QuotedSymbol_**  
  ~ <any character excluding CR, LF and ``'`'``>
:::
:::{ .grammar-rule #grammar-rule-UnicodeDigit }
**_UnicodeDigit_**  
  ~ <any unicode character of class ND>
:::
:::{ .grammar-rule #grammar-rule-Identifier }
**_Identifier_**  
  ~ ([_Letter_](#grammar-rule-Letter) | `'_'`) {[_Letter_](#grammar-rule-Letter) | `'_'` | [_UnicodeDigit_](#grammar-rule-UnicodeDigit)}  
  | ``'`'`` [_QuotedSymbol_](#grammar-rule-QuotedSymbol) {[_QuotedSymbol_](#grammar-rule-QuotedSymbol)} ``'`'``
:::

:::{ #escaped-identifiers }

Kotlin supports *escaping* identifiers by enclosing any sequence of characters into backtick (``` ` ```) characters, allowing to use any name as an identifier.
This allows not only using non-alphanumeric characters (like `@` or `#`) in names, but also using keywords like `if` or `when` as identifiers.
Actual set of characters that is allowed to be escaped may, however, be a subject to platform restrictions.
Consult particular platform sections for details.

> Note: for example, the following characters are not allowed in identifiers used as declaration names on the JVM platform even when escaped due to JVM restrictions: `(`, `)`, `{`, `}`, `[`, `]`, `.`

Escaped identifiers are treated the same as corresponding non-escaped identifier if it is allowed.
For example, an escaped identifier ``` `foo` ``` and non-escaped identifier `foo` may be used interchangeably and refer to the same program entity.

:::

:::{ .grammar-rule #grammar-rule-IdentifierOrSoftKey }
**_IdentifierOrSoftKey_**  
  ~ [_Identifier_](#grammar-rule-Identifier)  
  | [_ABSTRACT_](#grammar-rule-ABSTRACT)  
  | [_ANNOTATION_](#grammar-rule-ANNOTATION)  
  | [_BY_](#grammar-rule-BY)  
  | [_CATCH_](#grammar-rule-CATCH)  
  | [_COMPANION_](#grammar-rule-COMPANION)  
  | [_CONSTRUCTOR_](#grammar-rule-CONSTRUCTOR)  
  | [_CROSSINLINE_](#grammar-rule-CROSSINLINE)  
  | [_DATA_](#grammar-rule-DATA)  
  | [_DYNAMIC_](#grammar-rule-DYNAMIC)  
  | [_ENUM_](#grammar-rule-ENUM)  
  | [_EXTERNAL_](#grammar-rule-EXTERNAL)  
  | [_FINAL_](#grammar-rule-FINAL)  
  | [_FINALLY_](#grammar-rule-FINALLY)  
  | [_IMPORT_](#grammar-rule-IMPORT)  
  | [_INFIX_](#grammar-rule-INFIX)  
  | [_INIT_](#grammar-rule-INIT)  
  | [_INLINE_](#grammar-rule-INLINE)  
  | [_INNER_](#grammar-rule-INNER)  
  | [_INTERNAL_](#grammar-rule-INTERNAL)  
  | [_LATEINIT_](#grammar-rule-LATEINIT)  
  | [_NOINLINE_](#grammar-rule-NOINLINE)  
  | [_OPEN_](#grammar-rule-OPEN)  
  | [_OPERATOR_](#grammar-rule-OPERATOR)  
  | [_OUT_](#grammar-rule-OUT)  
  | [_OVERRIDE_](#grammar-rule-OVERRIDE)  
  | [_PRIVATE_](#grammar-rule-PRIVATE)  
  | [_PROTECTED_](#grammar-rule-PROTECTED)  
  | [_PUBLIC_](#grammar-rule-PUBLIC)  
  | [_REIFIED_](#grammar-rule-REIFIED)  
  | [_SEALED_](#grammar-rule-SEALED)  
  | [_TAILREC_](#grammar-rule-TAILREC)  
  | [_VARARG_](#grammar-rule-VARARG)  
  | [_WHERE_](#grammar-rule-WHERE)  
  | [_GET_](#grammar-rule-GET)  
  | [_SET_](#grammar-rule-SET)  
  | [_FIELD_](#grammar-rule-FIELD)  
  | [_PROPERTY_](#grammar-rule-PROPERTY)  
  | [_RECEIVER_](#grammar-rule-RECEIVER)  
  | [_PARAM_](#grammar-rule-PARAM)  
  | [_SETPARAM_](#grammar-rule-SETPARAM)  
  | [_DELEGATE_](#grammar-rule-DELEGATE)  
  | [_FILE_](#grammar-rule-FILE)  
  | [_EXPECT_](#grammar-rule-EXPECT)  
  | [_ACTUAL_](#grammar-rule-ACTUAL)  
  | [_CONST_](#grammar-rule-CONST)  
  | [_SUSPEND_](#grammar-rule-SUSPEND)
:::

Some of the keywords used in Kotlin are allowed to be used as identifiers even when not escaped.
Such keywords are called *soft keywords*.
You can see the complete list of soft keyword in the rule above.
All other keywords are considered *hard keywords* and may only be used as identifiers if escaped.

> Note: for example, this is a valid property declaration in Kotlin:
>
> ```kotlin
> val field = 2
> ```
>
> even though `field` is a keyword

#### String mode grammar

:::{ .grammar-rule #grammar-rule-QUOTE_OPEN }
**_QUOTE_OPEN_**  
  ~ `'"'`
:::
:::{ .grammar-rule #grammar-rule-TRIPLE_QUOTE_OPEN }
**_TRIPLE_QUOTE_OPEN_**  
  ~ `'"""'`
:::
:::{ .grammar-rule #grammar-rule-FieldIdentifier }
**_FieldIdentifier_**  
  ~ `'$'` [_IdentifierOrSoftKey_](#grammar-rule-IdentifierOrSoftKey)
:::

Opening a double quote (QUOTE_OPEN) rule puts the lexical grammar into the special "line string" mode with the following rules.
Closing double quote (QUOTE_CLOSE) rule exits this mode.

:::{ .grammar-rule #grammar-rule-QUOTE_CLOSE }
**_QUOTE_CLOSE_**  
  ~ `'"'`
:::
:::{ .grammar-rule #grammar-rule-LineStrRef }
**_LineStrRef_**  
  ~ [_FieldIdentifier_](#grammar-rule-FieldIdentifier)
:::
:::{ .grammar-rule #grammar-rule-LineStrText }
**_LineStrText_**  
  ~ {<any character except `'\'`, `'"'` or `'$'`>} | `'$'`
:::
:::{ .grammar-rule #grammar-rule-LineStrEscapedChar }
**_LineStrEscapedChar_**  
  ~ [_EscapedIdentifier_](#grammar-rule-EscapedIdentifier) | [_UniCharacterLiteral_](#grammar-rule-UniCharacterLiteral)
:::
:::{ .grammar-rule #grammar-rule-LineStrExprStart }
**_LineStrExprStart_**  
  ~ `'${'`
:::

Opening a triple double quote (TRIPLE_QUOTE_OPEN) rule puts the lexical grammar into the special "multiline string" mode with the following rules.
Closing triple double quote (TRIPLE_QUOTE_CLOSE) rule exits this mode.

:::{ .grammar-rule #grammar-rule-TRIPLE_QUOTE_CLOSE }
**_TRIPLE_QUOTE_CLOSE_**  
  ~ [[_MultilineStringQuote_](#grammar-rule-MultilineStringQuote)] `'"""'`
:::
:::{ .grammar-rule #grammar-rule-MultilineStringQuote }
**_MultilineStringQuote_**  
  ~ `'"""'` {`'"'`}
:::
:::{ .grammar-rule #grammar-rule-MultiLineStrRef }
**_MultiLineStrRef_**  
  ~ [_FieldIdentifier_](#grammar-rule-FieldIdentifier)
:::
:::{ .grammar-rule #grammar-rule-MultiLineStrText }
**_MultiLineStrText_**  
  ~ {<any character except `'"'` or `'$'`>} | `'$'`
:::
:::{ .grammar-rule #grammar-rule-MultiLineStrExprStart }
**_MultiLineStrExprStart_**  
  ~ `'${'`
:::

#### Tokens

These are all the valid tokens in one rule.
Note that syntax grammar ignores tokens [_DelimitedComment_](#grammar-rule-DelimitedComment), [_LineComment_](#grammar-rule-LineComment) and [_WS_](#grammar-rule-WS).

:::{ .grammar-rule #grammar-rule-KotlinToken }
**_KotlinToken_**  
  ~ [_ShebangLine_](#grammar-rule-ShebangLine)  
  | [_DelimitedComment_](#grammar-rule-DelimitedComment)  
  | [_LineComment_](#grammar-rule-LineComment)  
  | [_WS_](#grammar-rule-WS)  
  | [_NL_](#grammar-rule-NL)  
  | [_RESERVED_](#grammar-rule-RESERVED)  
  | [_DOT_](#grammar-rule-DOT)  
  | [_COMMA_](#grammar-rule-COMMA)  
  | [_LPAREN_](#grammar-rule-LPAREN)  
  | [_RPAREN_](#grammar-rule-RPAREN)  
  | [_LSQUARE_](#grammar-rule-LSQUARE)  
  | [_RSQUARE_](#grammar-rule-RSQUARE)  
  | [_LCURL_](#grammar-rule-LCURL)  
  | [_RCURL_](#grammar-rule-RCURL)  
  | [_MULT_](#grammar-rule-MULT)  
  | [_MOD_](#grammar-rule-MOD)  
  | [_DIV_](#grammar-rule-DIV)  
  | [_ADD_](#grammar-rule-ADD)  
  | [_SUB_](#grammar-rule-SUB)  
  | [_INCR_](#grammar-rule-INCR)  
  | [_DECR_](#grammar-rule-DECR)  
  | [_CONJ_](#grammar-rule-CONJ)  
  | [_DISJ_](#grammar-rule-DISJ)  
  | [_EXCL_WS_](#grammar-rule-EXCL_WS)  
  | [_EXCL_NO_WS_](#grammar-rule-EXCL_NO_WS)  
  | [_COLON_](#grammar-rule-COLON)  
  | [_SEMICOLON_](#grammar-rule-SEMICOLON)  
  | [_ASSIGNMENT_](#grammar-rule-ASSIGNMENT)  
  | [_ADD_ASSIGNMENT_](#grammar-rule-ADD_ASSIGNMENT)  
  | [_SUB_ASSIGNMENT_](#grammar-rule-SUB_ASSIGNMENT)  
  | [_MULT_ASSIGNMENT_](#grammar-rule-MULT_ASSIGNMENT)  
  | [_DIV_ASSIGNMENT_](#grammar-rule-DIV_ASSIGNMENT)  
  | [_MOD_ASSIGNMENT_](#grammar-rule-MOD_ASSIGNMENT)  
  | [_ARROW_](#grammar-rule-ARROW)  
  | [_DOUBLE_ARROW_](#grammar-rule-DOUBLE_ARROW)  
  | [_RANGE_](#grammar-rule-RANGE)  
  | [_COLONCOLON_](#grammar-rule-COLONCOLON)  
  | [_DOUBLE_SEMICOLON_](#grammar-rule-DOUBLE_SEMICOLON)  
  | [_HASH_](#grammar-rule-HASH)  
  | [_AT_NO_WS_](#grammar-rule-AT_NO_WS)  
  | [_AT_POST_WS_](#grammar-rule-AT_POST_WS)  
  | [_AT_PRE_WS_](#grammar-rule-AT_PRE_WS)  
  | [_AT_BOTH_WS_](#grammar-rule-AT_BOTH_WS)  
  | [_QUEST_WS_](#grammar-rule-QUEST_WS)  
  | [_QUEST_NO_WS_](#grammar-rule-QUEST_NO_WS)  
  | [_LANGLE_](#grammar-rule-LANGLE)  
  | [_RANGLE_](#grammar-rule-RANGLE)  
  | [_LE_](#grammar-rule-LE)  
  | [_GE_](#grammar-rule-GE)  
  | [_EXCL_EQ_](#grammar-rule-EXCL_EQ)  
  | [_EXCL_EQEQ_](#grammar-rule-EXCL_EQEQ)  
  | [_AS_SAFE_](#grammar-rule-AS_SAFE)  
  | [_EQEQ_](#grammar-rule-EQEQ)  
  | [_EQEQEQ_](#grammar-rule-EQEQEQ)  
  | [_SINGLE_QUOTE_](#grammar-rule-SINGLE_QUOTE)  
  | [_RETURN_AT_](#grammar-rule-RETURN_AT)  
  | [_CONTINUE_AT_](#grammar-rule-CONTINUE_AT)  
  | [_BREAK_AT_](#grammar-rule-BREAK_AT)  
  | [_THIS_AT_](#grammar-rule-THIS_AT)  
  | [_SUPER_AT_](#grammar-rule-SUPER_AT)  
  | [_FILE_](#grammar-rule-FILE)  
  | [_FIELD_](#grammar-rule-FIELD)  
  | [_PROPERTY_](#grammar-rule-PROPERTY)  
  | [_GET_](#grammar-rule-GET)  
  | [_SET_](#grammar-rule-SET)  
  | [_RECEIVER_](#grammar-rule-RECEIVER)  
  | [_PARAM_](#grammar-rule-PARAM)  
  | [_SETPARAM_](#grammar-rule-SETPARAM)  
  | [_DELEGATE_](#grammar-rule-DELEGATE)  
  | [_PACKAGE_](#grammar-rule-PACKAGE)  
  | [_IMPORT_](#grammar-rule-IMPORT)  
  | [_CLASS_](#grammar-rule-CLASS)  
  | [_INTERFACE_](#grammar-rule-INTERFACE)  
  | [_FUN_](#grammar-rule-FUN)  
  | [_OBJECT_](#grammar-rule-OBJECT)  
  | [_VAL_](#grammar-rule-VAL)  
  | [_VAR_](#grammar-rule-VAR)  
  | [_TYPE_ALIAS_](#grammar-rule-TYPE_ALIAS)  
  | [_CONSTRUCTOR_](#grammar-rule-CONSTRUCTOR)  
  | [_BY_](#grammar-rule-BY)  
  | [_COMPANION_](#grammar-rule-COMPANION)  
  | [_INIT_](#grammar-rule-INIT)  
  | [_THIS_](#grammar-rule-THIS)  
  | [_SUPER_](#grammar-rule-SUPER)  
  | [_TYPEOF_](#grammar-rule-TYPEOF)  
  | [_WHERE_](#grammar-rule-WHERE)  
  | [_IF_](#grammar-rule-IF)  
  | [_ELSE_](#grammar-rule-ELSE)  
  | [_WHEN_](#grammar-rule-WHEN)  
  | [_TRY_](#grammar-rule-TRY)  
  | [_CATCH_](#grammar-rule-CATCH)  
  | [_FINALLY_](#grammar-rule-FINALLY)  
  | [_FOR_](#grammar-rule-FOR)  
  | [_DO_](#grammar-rule-DO)  
  | [_WHILE_](#grammar-rule-WHILE)  
  | [_THROW_](#grammar-rule-THROW)  
  | [_RETURN_](#grammar-rule-RETURN)  
  | [_CONTINUE_](#grammar-rule-CONTINUE)  
  | [_BREAK_](#grammar-rule-BREAK)  
  | [_AS_](#grammar-rule-AS)  
  | [_IS_](#grammar-rule-IS)  
  | [_IN_](#grammar-rule-IN)  
  | [_NOT_IS_](#grammar-rule-NOT_IS)  
  | [_NOT_IN_](#grammar-rule-NOT_IN)  
  | [_OUT_](#grammar-rule-OUT)  
  | [_DYNAMIC_](#grammar-rule-DYNAMIC)  
  | [_PUBLIC_](#grammar-rule-PUBLIC)  
  | [_PRIVATE_](#grammar-rule-PRIVATE)  
  | [_PROTECTED_](#grammar-rule-PROTECTED)  
  | [_INTERNAL_](#grammar-rule-INTERNAL)  
  | [_ENUM_](#grammar-rule-ENUM)  
  | [_SEALED_](#grammar-rule-SEALED)  
  | [_ANNOTATION_](#grammar-rule-ANNOTATION)  
  | [_DATA_](#grammar-rule-DATA)  
  | [_INNER_](#grammar-rule-INNER)  
  | [_TAILREC_](#grammar-rule-TAILREC)  
  | [_OPERATOR_](#grammar-rule-OPERATOR)  
  | [_INLINE_](#grammar-rule-INLINE)  
  | [_INFIX_](#grammar-rule-INFIX)  
  | [_EXTERNAL_](#grammar-rule-EXTERNAL)  
  | [_SUSPEND_](#grammar-rule-SUSPEND)  
  | [_OVERRIDE_](#grammar-rule-OVERRIDE)  
  | [_ABSTRACT_](#grammar-rule-ABSTRACT)  
  | [_FINAL_](#grammar-rule-FINAL)  
  | [_OPEN_](#grammar-rule-OPEN)  
  | [_CONST_](#grammar-rule-CONST)  
  | [_LATEINIT_](#grammar-rule-LATEINIT)  
  | [_VARARG_](#grammar-rule-VARARG)  
  | [_NOINLINE_](#grammar-rule-NOINLINE)  
  | [_CROSSINLINE_](#grammar-rule-CROSSINLINE)  
  | [_REIFIED_](#grammar-rule-REIFIED)  
  | [_EXPECT_](#grammar-rule-EXPECT)  
  | [_ACTUAL_](#grammar-rule-ACTUAL)  
  | [_Identifier_](#grammar-rule-Identifier)  
  | [_RealLiteral_](#grammar-rule-RealLiteral)  
  | [_IntegerLiteral_](#grammar-rule-IntegerLiteral)  
  | [_HexLiteral_](#grammar-rule-HexLiteral)  
  | [_BinLiteral_](#grammar-rule-BinLiteral)  
  | [_LongLiteral_](#grammar-rule-LongLiteral)  
  | [_BooleanLiteral_](#grammar-rule-BooleanLiteral)  
  | [_NullLiteral_](#grammar-rule-NullLiteral)  
  | [_CharacterLiteral_](#grammar-rule-CharacterLiteral)  
  | [_QUOTE_OPEN_](#grammar-rule-QUOTE_OPEN)  
  | [_QUOTE_CLOSE_](#grammar-rule-QUOTE_CLOSE)  
  | [_TRIPLE_QUOTE_OPEN_](#grammar-rule-TRIPLE_QUOTE_OPEN)  
  | [_TRIPLE_QUOTE_CLOSE_](#grammar-rule-TRIPLE_QUOTE_CLOSE)  
  | [_LineStrRef_](#grammar-rule-LineStrRef)  
  | [_LineStrText_](#grammar-rule-LineStrText)  
  | [_LineStrEscapedChar_](#grammar-rule-LineStrEscapedChar)  
  | [_LineStrExprStart_](#grammar-rule-LineStrExprStart)  
  | [_MultilineStringQuote_](#grammar-rule-MultilineStringQuote)  
  | [_MultiLineStrRef_](#grammar-rule-MultiLineStrRef)  
  | [_MultiLineStrText_](#grammar-rule-MultiLineStrText)  
  | [_MultiLineStrExprStart_](#grammar-rule-MultiLineStrExprStart)
:::
:::{ .grammar-rule #grammar-rule-EOF }
**_EOF_**  
  ~ <end of input>
:::

### Syntax grammar

The grammar below replaces some lexical grammar rules with explicit literals (where such replacement in trivial and always correct, for example, for keywords) for better readability.

<#include "kotlin.core/grammar.generated.md">

### Documentation comments

Kotlin supports special comment syntax for code documentation purposes, called KDoc.
The syntax is based on [Markdown](https://tools.ietf.org/html/rfc7763) and [Javadoc](https://www.oracle.com/java/technologies/javase/javadoc-tool.html).
Documentation comments start with `/**` and end with `*/` and allows external tools to generate documentation based on the comment contents.
