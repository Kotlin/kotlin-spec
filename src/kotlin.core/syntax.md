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
:::{ .grammar-rule #grammar-rule-kotlinFile }
**_kotlinFile_:**
 ~ [_shebangLine_]  
   {_NL_}  
   {_fileAnnotation_}  
   [_packageHeader_]  
   _importList_  
   {_topLevelObject_}  
   _EOF_
:::

:::{ .grammar-rule #grammar-rule-script }
**_script_:**
 ~ [_shebangLine_]  
   {_NL_}  
   {_fileAnnotation_}  
   [_packageHeader_]  
   _importList_  
   {_statement_ _semi_}  
   _EOF_
:::

:::{ .grammar-rule #grammar-rule-fileAnnotation }
**_fileAnnotation_:**
 ~ `'@file'`  
   {_NL_}  
   `':'`  
   {_NL_}  
   ((`'['` (_unescapedAnnotation_ {_unescapedAnnotation_}) `']'`) | _unescapedAnnotation_)  
   {_NL_}
:::

:::{ .grammar-rule #grammar-rule-packageHeader }
**_packageHeader_:**
 ~ `'package'` _identifier_ [_semi_]
:::

:::{ .grammar-rule #grammar-rule-importList }
**_importList_:**
 ~ {_importHeader_}
:::

:::{ .grammar-rule #grammar-rule-importHeader }
**_importHeader_:**
 ~ `'import'` _identifier_ [(`'.'` `'*'`) | _importAlias_] [_semi_]
:::

:::{ .grammar-rule #grammar-rule-importAlias }
**_importAlias_:**
 ~ `'as'` _simpleIdentifier_
:::

:::{ .grammar-rule #grammar-rule-topLevelObject }
**_topLevelObject_:**
 ~ _declaration_ [_semis_]
:::

:::{ .grammar-rule #grammar-rule-classDeclaration }
**_classDeclaration_:**
 ~ [_modifiers_]  
   (`'class'` | `'interface'`)  
   {_NL_}  
   _simpleIdentifier_  
   [{_NL_} _typeParameters_]  
   [{_NL_} _primaryConstructor_]  
   [{_NL_} `':'` {_NL_} _delegationSpecifiers_]  
   [{_NL_} _typeConstraints_]  
   [({_NL_} _classBody_) | ({_NL_} _enumClassBody_)]
:::

:::{ .grammar-rule #grammar-rule-primaryConstructor }
**_primaryConstructor_:**
 ~ [[_modifiers_] `'constructor'` {_NL_}] _classParameters_
:::

:::{ .grammar-rule #grammar-rule-classParameters }
**_classParameters_:**
 ~ `'('`  
   {_NL_}  
   [_classParameter_ {{_NL_} `','` {_NL_} _classParameter_}]  
   {_NL_}  
   `')'`
:::

:::{ .grammar-rule #grammar-rule-classParameter }
**_classParameter_:**
 ~ [_modifiers_]  
   [`'val'` | `'var'`]  
   {_NL_}  
   _simpleIdentifier_  
   `':'`  
   {_NL_}  
   _type_  
   [{_NL_} `'='` {_NL_} _expression_]
:::

:::{ .grammar-rule #grammar-rule-delegationSpecifiers }
**_delegationSpecifiers_:**
 ~ _annotatedDelegationSpecifier_ {{_NL_} `','` {_NL_} _annotatedDelegationSpecifier_}
:::

:::{ .grammar-rule #grammar-rule-annotatedDelegationSpecifier }
**_annotatedDelegationSpecifier_:**
 ~ {_annotation_} {_NL_} _delegationSpecifier_
:::

:::{ .grammar-rule #grammar-rule-delegationSpecifier }
**_delegationSpecifier_:**
 ~ _constructorInvocation_  
 | _explicitDelegation_  
 | _userType_  
 | _functionType_
:::

:::{ .grammar-rule #grammar-rule-constructorInvocation }
**_constructorInvocation_:**
 ~ _userType_ _valueArguments_
:::

:::{ .grammar-rule #grammar-rule-explicitDelegation }
**_explicitDelegation_:**
 ~ (_userType_ | _functionType_)  
   {_NL_}  
   `'by'`  
   {_NL_}  
   _expression_
:::

:::{ .grammar-rule #grammar-rule-classBody }
**_classBody_:**
 ~ `'{'`  
   {_NL_}  
   [_classMemberDeclarations_]  
   {_NL_}  
   `'}'`
:::

:::{ .grammar-rule #grammar-rule-classMemberDeclarations }
**_classMemberDeclarations_:**
 ~ {_classMemberDeclaration_ [_semis_]}
:::

:::{ .grammar-rule #grammar-rule-classMemberDeclaration }
**_classMemberDeclaration_:**
 ~ _declaration_  
 | _companionObject_  
 | _anonymousInitializer_  
 | _secondaryConstructor_
:::

:::{ .grammar-rule #grammar-rule-anonymousInitializer }
**_anonymousInitializer_:**
 ~ `'init'` {_NL_} _block_
:::

:::{ .grammar-rule #grammar-rule-secondaryConstructor }
**_secondaryConstructor_:**
 ~ [_modifiers_]  
   `'constructor'`  
   {_NL_}  
   _functionValueParameters_  
   [{_NL_} `':'` {_NL_} _constructorDelegationCall_]  
   {_NL_}  
   [_block_]
:::

:::{ .grammar-rule #grammar-rule-constructorDelegationCall }
**_constructorDelegationCall_:**
 ~ (`'this'` {_NL_} _valueArguments_)  
 | (`'super'` {_NL_} _valueArguments_)
:::

:::{ .grammar-rule #grammar-rule-enumClassBody }
**_enumClassBody_:**
 ~ `'{'`  
   {_NL_}  
   [_enumEntries_]  
   [{_NL_} `';'` {_NL_} [_classMemberDeclarations_]]  
   {_NL_}  
   `'}'`
:::

:::{ .grammar-rule #grammar-rule-enumEntries }
**_enumEntries_:**
 ~ _enumEntry_ {{_NL_} `','` {_NL_} _enumEntry_} {_NL_} [`','`]
:::

:::{ .grammar-rule #grammar-rule-enumEntry }
**_enumEntry_:**
 ~ [_modifiers_ {_NL_}] _simpleIdentifier_ [{_NL_} _valueArguments_] [{_NL_} _classBody_]
:::

:::{ .grammar-rule #grammar-rule-functionDeclaration }
**_functionDeclaration_:**
 ~ [_modifiers_]  
   _functionHeader_  
   {_NL_}  
   _functionValueParameters_  
   [{_NL_} `':'` {_NL_} _type_]  
   [{_NL_} _typeConstraints_]  
   [{_NL_} _functionBody_]
:::

:::{ .grammar-rule #grammar-rule-functionHeader }
**_functionHeader_:**
 ~ `'fun'`  
   [{_NL_} _typeParameters_]  
   [{_NL_} _receiverType_ {_NL_} `'.'`]  
   {_NL_}  
   _simpleIdentifier_
:::

:::{ .grammar-rule #grammar-rule-functionValueParameters }
**_functionValueParameters_:**
 ~ `'('`  
   {_NL_}  
   [_functionValueParameter_ {{_NL_} `','` {_NL_} _functionValueParameter_}]  
   {_NL_}  
   `')'`
:::

:::{ .grammar-rule #grammar-rule-functionValueParameter }
**_functionValueParameter_:**
 ~ [_modifiers_] _parameter_ [{_NL_} `'='` {_NL_} _expression_]
:::

:::{ .grammar-rule #grammar-rule-parameter }
**_parameter_:**
 ~ _simpleIdentifier_  
   {_NL_}  
   `':'`  
   {_NL_}  
   _type_
:::

:::{ .grammar-rule #grammar-rule-setterParameter }
**_setterParameter_:**
 ~ _simpleIdentifier_ {_NL_} [`':'` {_NL_} _type_]
:::

:::{ .grammar-rule #grammar-rule-functionBody }
**_functionBody_:**
 ~ _block_  
 | (`'='` {_NL_} _expression_)
:::

:::{ .grammar-rule #grammar-rule-objectDeclaration }
**_objectDeclaration_:**
 ~ [_modifiers_]  
   `'object'`  
   {_NL_}  
   _simpleIdentifier_  
   [{_NL_} `':'` {_NL_} _delegationSpecifiers_]  
   [{_NL_} _classBody_]
:::

:::{ .grammar-rule #grammar-rule-companionObject }
**_companionObject_:**
 ~ [_modifiers_]  
   `'companion'`  
   {_NL_}  
   `'object'`  
   [{_NL_} _simpleIdentifier_]  
   [{_NL_} `':'` {_NL_} _delegationSpecifiers_]  
   [{_NL_} _classBody_]
:::

:::{ .grammar-rule #grammar-rule-propertyDeclaration }
**_propertyDeclaration_:**
 ~ [_modifiers_]  
   (`'val'` | `'var'`)  
   [{_NL_} _typeParameters_]  
   [{_NL_} _receiverType_ {_NL_} `'.'`]  
   ({_NL_} (_multiVariableDeclaration_ | _variableDeclaration_))  
   [{_NL_} _typeConstraints_]  
   [{_NL_} ((`'='` {_NL_} _expression_) | _propertyDelegate_)]  
   [(_NL_ {_NL_}) `';'`]  
   {_NL_}  
   (([_getter_] [{_NL_} [_semi_] _setter_]) | ([_setter_] [{_NL_} [_semi_] _getter_]))
:::

:::{ .grammar-rule #grammar-rule-multiVariableDeclaration }
**_multiVariableDeclaration_:**
 ~ `'('`  
   {_NL_}  
   _variableDeclaration_  
   {{_NL_} `','` {_NL_} _variableDeclaration_}  
   {_NL_}  
   `')'`
:::

:::{ .grammar-rule #grammar-rule-variableDeclaration }
**_variableDeclaration_:**
 ~ {_annotation_} {_NL_} _simpleIdentifier_ [{_NL_} `':'` {_NL_} _type_]
:::

:::{ .grammar-rule #grammar-rule-propertyDelegate }
**_propertyDelegate_:**
 ~ `'by'` {_NL_} _expression_
:::

:::{ .grammar-rule #grammar-rule-getter }
**_getter_:**
 ~ ([_modifiers_] `'get'`)  
 | ([_modifiers_] `'get'` {_NL_} `'('` {_NL_} `')'` [{_NL_} `':'` {_NL_} _type_] {_NL_} _functionBody_)
:::

:::{ .grammar-rule #grammar-rule-setter }
**_setter_:**
 ~ ([_modifiers_] `'set'`)  
 | ([_modifiers_] `'set'` {_NL_} `'('` {_annotation_ | _parameterModifier_} _setterParameter_ `')'` [{_NL_} `':'` {_NL_} _type_] {_NL_} _functionBody_)
:::

:::{ .grammar-rule #grammar-rule-typeAlias }
**_typeAlias_:**
 ~ [_modifiers_]  
   `'typealias'`  
   {_NL_}  
   _simpleIdentifier_  
   [{_NL_} _typeParameters_]  
   {_NL_}  
   `'='`  
   {_NL_}  
   _type_
:::

:::{ .grammar-rule #grammar-rule-typeParameters }
**_typeParameters_:**
 ~ `'<'`  
   {_NL_}  
   _typeParameter_  
   {{_NL_} `','` {_NL_} _typeParameter_}  
   {_NL_}  
   `'>'`
:::

:::{ .grammar-rule #grammar-rule-typeParameter }
**_typeParameter_:**
 ~ [_typeParameterModifiers_] {_NL_} _simpleIdentifier_ [{_NL_} `':'` {_NL_} _type_]
:::

:::{ .grammar-rule #grammar-rule-typeParameterModifiers }
**_typeParameterModifiers_:**
 ~ _typeParameterModifier_ {_typeParameterModifier_}
:::

:::{ .grammar-rule #grammar-rule-typeParameterModifier }
**_typeParameterModifier_:**
 ~ (_reificationModifier_ {_NL_})  
 | (_varianceModifier_ {_NL_})  
 | _annotation_
:::

:::{ .grammar-rule #grammar-rule-type }
**_type_:**
 ~ [_typeModifiers_] (_parenthesizedType_ | _nullableType_ | _typeReference_ | _functionType_)
:::

:::{ .grammar-rule #grammar-rule-typeModifiers }
**_typeModifiers_:**
 ~ _typeModifier_ {_typeModifier_}
:::

:::{ .grammar-rule #grammar-rule-typeModifier }
**_typeModifier_:**
 ~ _annotation_  
 | (`'suspend'` {_NL_})
:::

:::{ .grammar-rule #grammar-rule-parenthesizedType }
**_parenthesizedType_:**
 ~ `'('`  
   {_NL_}  
   _type_  
   {_NL_}  
   `')'`
:::

:::{ .grammar-rule #grammar-rule-nullableType }
**_nullableType_:**
 ~ (_typeReference_ | _parenthesizedType_) {_NL_} (_quest_ {_quest_})
:::

:::{ .grammar-rule #grammar-rule-typeReference }
**_typeReference_:**
 ~ _userType_  
 | `'dynamic'`
:::

:::{ .grammar-rule #grammar-rule-functionType }
**_functionType_:**
 ~ [_receiverType_ {_NL_} `'.'` {_NL_}]  
   _functionTypeParameters_  
   {_NL_}  
   `'->'`  
   {_NL_}  
   _type_
:::

:::{ .grammar-rule #grammar-rule-receiverType }
**_receiverType_:**
 ~ [_typeModifiers_] (_parenthesizedType_ | _nullableType_ | _typeReference_)
:::

:::{ .grammar-rule #grammar-rule-userType }
**_userType_:**
 ~ _simpleUserType_ {{_NL_} `'.'` {_NL_} _simpleUserType_}
:::

:::{ .grammar-rule #grammar-rule-parenthesizedUserType }
**_parenthesizedUserType_:**
 ~ (`'('` {_NL_} _userType_ {_NL_} `')'`)  
 | (`'('` {_NL_} _parenthesizedUserType_ {_NL_} `')'`)
:::

:::{ .grammar-rule #grammar-rule-simpleUserType }
**_simpleUserType_:**
 ~ _simpleIdentifier_ [{_NL_} _typeArguments_]
:::

:::{ .grammar-rule #grammar-rule-functionTypeParameters }
**_functionTypeParameters_:**
 ~ `'('`  
   {_NL_}  
   [_parameter_ | _type_]  
   {{_NL_} `','` {_NL_} (_parameter_ | _type_)}  
   {_NL_}  
   `')'`
:::

:::{ .grammar-rule #grammar-rule-typeConstraints }
**_typeConstraints_:**
 ~ `'where'` {_NL_} _typeConstraint_ {{_NL_} `','` {_NL_} _typeConstraint_}
:::

:::{ .grammar-rule #grammar-rule-typeConstraint }
**_typeConstraint_:**
 ~ {_annotation_}  
   _simpleIdentifier_  
   {_NL_}  
   `':'`  
   {_NL_}  
   _type_
:::

:::{ .grammar-rule #grammar-rule-block }
**_block_:**
 ~ `'{'`  
   {_NL_}  
   _statements_  
   {_NL_}  
   `'}'`
:::

:::{ .grammar-rule #grammar-rule-statements }
**_statements_:**
 ~ [_statement_ {_semis_ _statement_} [_semis_]]
:::

:::{ .grammar-rule #grammar-rule-statement }
**_statement_:**
 ~ {_label_ | _annotation_} (_declaration_ | _assignment_ | _loopStatement_ | _expression_)
:::

:::{ .grammar-rule #grammar-rule-declaration }
**_declaration_:**
 ~ _classDeclaration_  
 | _objectDeclaration_  
 | _functionDeclaration_  
 | _propertyDeclaration_  
 | _typeAlias_
:::

:::{ .grammar-rule #grammar-rule-assignment }
**_assignment_:**
 ~ (_directlyAssignableExpression_ `'='` {_NL_} _expression_)  
 | (_assignableExpression_ _assignmentAndOperator_ {_NL_} _expression_)
:::

:::{ .grammar-rule #grammar-rule-expression }
**_expression_:**
 ~ _disjunction_
:::

:::{ .grammar-rule #grammar-rule-disjunction }
**_disjunction_:**
 ~ _conjunction_ {{_NL_} `'||'` {_NL_} _conjunction_}
:::

:::{ .grammar-rule #grammar-rule-conjunction }
**_conjunction_:**
 ~ _equality_ {{_NL_} `'&&'` {_NL_} _equality_}
:::

:::{ .grammar-rule #grammar-rule-equality }
**_equality_:**
 ~ _comparison_ {_equalityOperator_ {_NL_} _comparison_}
:::

:::{ .grammar-rule #grammar-rule-comparison }
**_comparison_:**
 ~ _infixOperation_ [_comparisonOperator_ {_NL_} _infixOperation_]
:::

:::{ .grammar-rule #grammar-rule-infixOperation }
**_infixOperation_:**
 ~ _elvisExpression_ {(_inOperator_ {_NL_} _elvisExpression_) | (_isOperator_ {_NL_} _type_)}
:::

:::{ .grammar-rule #grammar-rule-elvisExpression }
**_elvisExpression_:**
 ~ _infixFunctionCall_ {{_NL_} _elvis_ {_NL_} _infixFunctionCall_}
:::

:::{ .grammar-rule #grammar-rule-infixFunctionCall }
**_infixFunctionCall_:**
 ~ _rangeExpression_ {_simpleIdentifier_ {_NL_} _rangeExpression_}
:::

:::{ .grammar-rule #grammar-rule-rangeExpression }
**_rangeExpression_:**
 ~ _additiveExpression_ {`'..'` {_NL_} _additiveExpression_}
:::

:::{ .grammar-rule #grammar-rule-additiveExpression }
**_additiveExpression_:**
 ~ _multiplicativeExpression_ {_additiveOperator_ {_NL_} _multiplicativeExpression_}
:::

:::{ .grammar-rule #grammar-rule-multiplicativeExpression }
**_multiplicativeExpression_:**
 ~ _asExpression_ {_multiplicativeOperator_ {_NL_} _asExpression_}
:::

:::{ .grammar-rule #grammar-rule-asExpression }
**_asExpression_:**
 ~ _prefixUnaryExpression_ [{_NL_} _asOperator_ {_NL_} _type_]
:::

:::{ .grammar-rule #grammar-rule-prefixUnaryExpression }
**_prefixUnaryExpression_:**
 ~ {_unaryPrefix_} _postfixUnaryExpression_
:::

:::{ .grammar-rule #grammar-rule-unaryPrefix }
**_unaryPrefix_:**
 ~ _annotation_  
 | _label_  
 | (_prefixUnaryOperator_ {_NL_})
:::

:::{ .grammar-rule #grammar-rule-postfixUnaryExpression }
**_postfixUnaryExpression_:**
 ~ _primaryExpression_  
 | (_primaryExpression_ (_postfixUnarySuffix_ {_postfixUnarySuffix_}))
:::

:::{ .grammar-rule #grammar-rule-postfixUnarySuffix }
**_postfixUnarySuffix_:**
 ~ _postfixUnaryOperator_  
 | _typeArguments_  
 | _callSuffix_  
 | _indexingSuffix_  
 | _navigationSuffix_
:::

:::{ .grammar-rule #grammar-rule-directlyAssignableExpression }
**_directlyAssignableExpression_:**
 ~ (_postfixUnaryExpression_ _assignableSuffix_)  
 | _simpleIdentifier_
:::

:::{ .grammar-rule #grammar-rule-assignableExpression }
**_assignableExpression_:**
 ~ _prefixUnaryExpression_
:::

:::{ .grammar-rule #grammar-rule-assignableSuffix }
**_assignableSuffix_:**
 ~ _typeArguments_  
 | _indexingSuffix_  
 | _navigationSuffix_
:::

:::{ .grammar-rule #grammar-rule-indexingSuffix }
**_indexingSuffix_:**
 ~ `'['`  
   {_NL_}  
   _expression_  
   {{_NL_} `','` {_NL_} _expression_}  
   {_NL_}  
   `']'`
:::

:::{ .grammar-rule #grammar-rule-navigationSuffix }
**_navigationSuffix_:**
 ~ {_NL_} _memberAccessOperator_ {_NL_} (_simpleIdentifier_ | _parenthesizedExpression_ | `'class'`)
:::

:::{ .grammar-rule #grammar-rule-callSuffix }
**_callSuffix_:**
 ~ ([_typeArguments_] [_valueArguments_] _annotatedLambda_)  
 | ([_typeArguments_] _valueArguments_)
:::

:::{ .grammar-rule #grammar-rule-annotatedLambda }
**_annotatedLambda_:**
 ~ {_annotation_} [_label_] {_NL_} _lambdaLiteral_
:::

:::{ .grammar-rule #grammar-rule-valueArguments }
**_valueArguments_:**
 ~ (`'('` {_NL_} `')'`)  
 | (`'('` {_NL_} _valueArgument_ {{_NL_} `','` {_NL_} _valueArgument_} {_NL_} `')'`)
:::

:::{ .grammar-rule #grammar-rule-typeArguments }
**_typeArguments_:**
 ~ `'<'`  
   {_NL_}  
   _typeProjection_  
   {{_NL_} `','` {_NL_} _typeProjection_}  
   {_NL_}  
   `'>'`
:::

:::{ .grammar-rule #grammar-rule-typeProjection }
**_typeProjection_:**
 ~ ([_typeProjectionModifiers_] _type_)  
 | `'*'`
:::

:::{ .grammar-rule #grammar-rule-typeProjectionModifiers }
**_typeProjectionModifiers_:**
 ~ _typeProjectionModifier_ {_typeProjectionModifier_}
:::

:::{ .grammar-rule #grammar-rule-typeProjectionModifier }
**_typeProjectionModifier_:**
 ~ (_varianceModifier_ {_NL_})  
 | _annotation_
:::

:::{ .grammar-rule #grammar-rule-valueArgument }
**_valueArgument_:**
 ~ [_annotation_]  
   {_NL_}  
   [_simpleIdentifier_ {_NL_} `'='` {_NL_}]  
   [`'*'`]  
   {_NL_}  
   _expression_
:::

:::{ .grammar-rule #grammar-rule-primaryExpression }
**_primaryExpression_:**
 ~ _parenthesizedExpression_  
 | _literalConstant_  
 | _stringLiteral_  
 | _simpleIdentifier_  
 | _callableReference_  
 | _functionLiteral_  
 | _objectLiteral_  
 | _collectionLiteral_  
 | _thisExpression_  
 | _superExpression_  
 | _ifExpression_  
 | _whenExpression_  
 | _tryExpression_  
 | _jumpExpression_
:::

:::{ .grammar-rule #grammar-rule-parenthesizedExpression }
**_parenthesizedExpression_:**
 ~ `'('`  
   {_NL_}  
   _expression_  
   {_NL_}  
   `')'`
:::

:::{ .grammar-rule #grammar-rule-collectionLiteral }
**_collectionLiteral_:**
 ~ (`'['` {_NL_} _expression_ {{_NL_} `','` {_NL_} _expression_} {_NL_} `']'`)  
 | (`'['` {_NL_} `']'`)
:::

:::{ .grammar-rule #grammar-rule-literalConstant }
**_literalConstant_:**
 ~ _BooleanLiteral_  
 | _IntegerLiteral_  
 | _HexLiteral_  
 | _BinLiteral_  
 | _CharacterLiteral_  
 | _RealLiteral_  
 | _NullLiteral_  
 | _LongLiteral_
:::

:::{ .grammar-rule #grammar-rule-stringLiteral }
**_stringLiteral_:**
 ~ _lineStringLiteral_  
 | _multiLineStringLiteral_
:::

:::{ .grammar-rule #grammar-rule-lineStringLiteral }
**_lineStringLiteral_:**
 ~ _QUOTE_OPEN_ {_lineStringContent_ | _lineStringExpression_} _QUOTE_CLOSE_
:::

:::{ .grammar-rule #grammar-rule-multiLineStringLiteral }
**_multiLineStringLiteral_:**
 ~ _TRIPLE_QUOTE_OPEN_ {_multiLineStringContent_ | _multiLineStringExpression_ | _MultiLineStringQuote_} _TRIPLE_QUOTE_CLOSE_
:::

:::{ .grammar-rule #grammar-rule-lineStringContent }
**_lineStringContent_:**
 ~ _LineStrText_  
 | _LineStrEscapedChar_  
 | _LineStrRef_
:::

:::{ .grammar-rule #grammar-rule-lineStringExpression }
**_lineStringExpression_:**
 ~ _LineStrExprStart_ _expression_ `'}'`
:::

:::{ .grammar-rule #grammar-rule-multiLineStringContent }
**_multiLineStringContent_:**
 ~ _MultiLineStrText_  
 | _MultiLineStringQuote_  
 | _MultiLineStrRef_
:::

:::{ .grammar-rule #grammar-rule-multiLineStringExpression }
**_multiLineStringExpression_:**
 ~ _MultiLineStrExprStart_  
   {_NL_}  
   _expression_  
   {_NL_}  
   `'}'`
:::

:::{ .grammar-rule #grammar-rule-lambdaLiteral }
**_lambdaLiteral_:**
 ~ (_LCURL_ {_NL_} _statements_ {_NL_} _RCURL_)  
 | (_LCURL_ {_NL_} [_lambdaParameters_] {_NL_} _ARROW_ {_NL_} _statements_ {_NL_} `'}'`)
:::

:::{ .grammar-rule #grammar-rule-lambdaParameters }
**_lambdaParameters_:**
 ~ _lambdaParameter_ {{_NL_} _COMMA_ {_NL_} _lambdaParameter_}
:::

:::{ .grammar-rule #grammar-rule-lambdaParameter }
**_lambdaParameter_:**
 ~ _variableDeclaration_  
 | (_multiVariableDeclaration_ [{_NL_} _COLON_ {_NL_} _type_])
:::

:::{ .grammar-rule #grammar-rule-anonymousFunction }
**_anonymousFunction_:**
 ~ `'fun'`  
   [{_NL_} _type_ {_NL_} `'.'`]  
   {_NL_}  
   _functionValueParameters_  
   [{_NL_} `':'` {_NL_} _type_]  
   [{_NL_} _typeConstraints_]  
   [{_NL_} _functionBody_]
:::

:::{ .grammar-rule #grammar-rule-functionLiteral }
**_functionLiteral_:**
 ~ _lambdaLiteral_  
 | _anonymousFunction_
:::

:::{ .grammar-rule #grammar-rule-objectLiteral }
**_objectLiteral_:**
 ~ (`'object'` {_NL_} `':'` {_NL_} _delegationSpecifiers_ [{_NL_} _classBody_])  
 | (`'object'` {_NL_} _classBody_)
:::

:::{ .grammar-rule #grammar-rule-thisExpression }
**_thisExpression_:**
 ~ `'this'`  
 | _THIS_AT_
:::

:::{ .grammar-rule #grammar-rule-superExpression }
**_superExpression_:**
 ~ (`'super'` [`'<'` {_NL_} _type_ {_NL_} `'>'`] [`'@'` _simpleIdentifier_])  
 | _SUPER_AT_
:::

:::{ .grammar-rule #grammar-rule-controlStructureBody }
**_controlStructureBody_:**
 ~ _block_  
 | _statement_
:::

:::{ .grammar-rule #grammar-rule-ifExpression }
**_ifExpression_:**
 ~ (`'if'` {_NL_} `'('` {_NL_} _expression_ {_NL_} `')'` {_NL_} _controlStructureBody_ [[`';'`] {_NL_} `'else'` {_NL_} _controlStructureBody_])  
 | (`'if'` {_NL_} `'('` {_NL_} _expression_ {_NL_} `')'` {_NL_} [`';'` {_NL_}] `'else'` {_NL_} _controlStructureBody_)
:::

:::{ .grammar-rule #grammar-rule-whenExpression }
**_whenExpression_:**
 ~ `'when'`  
   {_NL_}  
   [`'('` _expression_ `')'`]  
   {_NL_}  
   `'{'`  
   {_NL_}  
   {_whenEntry_ {_NL_}}  
   {_NL_}  
   `'}'`
:::

:::{ .grammar-rule #grammar-rule-whenEntry }
**_whenEntry_:**
 ~ (_whenCondition_ {{_NL_} `','` {_NL_} _whenCondition_} {_NL_} `'->'` {_NL_} _controlStructureBody_ [_semi_])  
 | (`'else'` {_NL_} `'->'` {_NL_} _controlStructureBody_ [_semi_])
:::

:::{ .grammar-rule #grammar-rule-whenCondition }
**_whenCondition_:**
 ~ _expression_  
 | _rangeTest_  
 | _typeTest_
:::

:::{ .grammar-rule #grammar-rule-rangeTest }
**_rangeTest_:**
 ~ _inOperator_ {_NL_} _expression_
:::

:::{ .grammar-rule #grammar-rule-typeTest }
**_typeTest_:**
 ~ _isOperator_ {_NL_} _type_
:::

:::{ .grammar-rule #grammar-rule-tryExpression }
**_tryExpression_:**
 ~ `'try'` {_NL_} _block_ ((({_NL_} _catchBlock_ {{_NL_} _catchBlock_}) [{_NL_} _finallyBlock_]) | ({_NL_} _finallyBlock_))
:::

:::{ .grammar-rule #grammar-rule-catchBlock }
**_catchBlock_:**
 ~ `'catch'`  
   {_NL_}  
   `'('`  
   {_annotation_}  
   _simpleIdentifier_  
   `':'`  
   _userType_  
   `')'`  
   {_NL_}  
   _block_
:::

:::{ .grammar-rule #grammar-rule-finallyBlock }
**_finallyBlock_:**
 ~ `'finally'` {_NL_} _block_
:::

:::{ .grammar-rule #grammar-rule-loopStatement }
**_loopStatement_:**
 ~ _forStatement_  
 | _whileStatement_  
 | _doWhileStatement_
:::

:::{ .grammar-rule #grammar-rule-forStatement }
**_forStatement_:**
 ~ `'for'`  
   {_NL_}  
   `'('`  
   {_annotation_}  
   (_variableDeclaration_ | _multiVariableDeclaration_)  
   `'in'`  
   _expression_  
   `')'`  
   {_NL_}  
   [_controlStructureBody_]
:::

:::{ .grammar-rule #grammar-rule-whileStatement }
**_whileStatement_:**
 ~ (`'while'` {_NL_} `'('` _expression_ `')'` {_NL_} _controlStructureBody_)  
 | (`'while'` {_NL_} `'('` _expression_ `')'` {_NL_} `';'`)
:::

:::{ .grammar-rule #grammar-rule-doWhileStatement }
**_doWhileStatement_:**
 ~ `'do'`  
   {_NL_}  
   [_controlStructureBody_]  
   {_NL_}  
   `'while'`  
   {_NL_}  
   `'('`  
   _expression_  
   `')'`
:::

:::{ .grammar-rule #grammar-rule-jumpExpression }
**_jumpExpression_:**
 ~ (`'throw'` {_NL_} _expression_)  
 | ((`'return'` | _RETURN_AT_) [_expression_])  
 | `'continue'`  
 | _CONTINUE_AT_  
 | `'break'`  
 | _BREAK_AT_
:::

:::{ .grammar-rule #grammar-rule-callableReference }
**_callableReference_:**
 ~ [_receiverType_]  
   {_NL_}  
   `'::'`  
   {_NL_}  
   (_simpleIdentifier_ | `'class'`)
:::

:::{ .grammar-rule #grammar-rule-assignmentAndOperator }
**_assignmentAndOperator_:**
 ~ `'+='`  
 | `'-='`  
 | `'*='`  
 | `'/='`  
 | `'%='`
:::

:::{ .grammar-rule #grammar-rule-equalityOperator }
**_equalityOperator_:**
 ~ `'!='`  
 | `'!=='`  
 | `'=='`  
 | `'==='`
:::

:::{ .grammar-rule #grammar-rule-comparisonOperator }
**_comparisonOperator_:**
 ~ `'<'`  
 | `'>'`  
 | `'<='`  
 | `'>='`
:::

:::{ .grammar-rule #grammar-rule-inOperator }
**_inOperator_:**
 ~ `'in'`  
 | _NOT_IN_
:::

:::{ .grammar-rule #grammar-rule-isOperator }
**_isOperator_:**
 ~ `'is'`  
 | _NOT_IS_
:::

:::{ .grammar-rule #grammar-rule-additiveOperator }
**_additiveOperator_:**
 ~ `'+'`  
 | `'-'`
:::

:::{ .grammar-rule #grammar-rule-multiplicativeOperator }
**_multiplicativeOperator_:**
 ~ `'*'`  
 | `'/'`  
 | `'%'`
:::

:::{ .grammar-rule #grammar-rule-asOperator }
**_asOperator_:**
 ~ `'as'`  
 | `'as?'`
:::

:::{ .grammar-rule #grammar-rule-prefixUnaryOperator }
**_prefixUnaryOperator_:**
 ~ `'++'`  
 | `'--'`  
 | `'-'`  
 | `'+'`  
 | _excl_
:::

:::{ .grammar-rule #grammar-rule-postfixUnaryOperator }
**_postfixUnaryOperator_:**
 ~ `'++'`  
 | `'--'`  
 | (_EXCL_NO_WS_ _excl_)
:::

:::{ .grammar-rule #grammar-rule-memberAccessOperator }
**_memberAccessOperator_:**
 ~ `'.'`  
 | _safeNav_  
 | `'::'`
:::

:::{ .grammar-rule #grammar-rule-modifiers }
**_modifiers_:**
 ~ _annotation_ | _modifier_ {_annotation_ | _modifier_}
:::

:::{ .grammar-rule #grammar-rule-modifier }
**_modifier_:**
 ~ (_classModifier_ | _memberModifier_ | _visibilityModifier_ | _functionModifier_ | _propertyModifier_ | _inheritanceModifier_ | _parameterModifier_ | _platformModifier_) {_NL_}
:::

:::{ .grammar-rule #grammar-rule-classModifier }
**_classModifier_:**
 ~ `'enum'`  
 | `'sealed'`  
 | `'annotation'`  
 | `'data'`  
 | `'inner'`
:::

:::{ .grammar-rule #grammar-rule-memberModifier }
**_memberModifier_:**
 ~ `'override'`  
 | `'lateinit'`
:::

:::{ .grammar-rule #grammar-rule-visibilityModifier }
**_visibilityModifier_:**
 ~ `'public'`  
 | `'private'`  
 | `'internal'`  
 | `'protected'`
:::

:::{ .grammar-rule #grammar-rule-varianceModifier }
**_varianceModifier_:**
 ~ `'in'`  
 | `'out'`
:::

:::{ .grammar-rule #grammar-rule-functionModifier }
**_functionModifier_:**
 ~ `'tailrec'`  
 | `'operator'`  
 | `'infix'`  
 | `'inline'`  
 | `'external'`  
 | `'suspend'`
:::

:::{ .grammar-rule #grammar-rule-propertyModifier }
**_propertyModifier_:**
 ~ `'const'`
:::

:::{ .grammar-rule #grammar-rule-inheritanceModifier }
**_inheritanceModifier_:**
 ~ `'abstract'`  
 | `'final'`  
 | `'open'`
:::

:::{ .grammar-rule #grammar-rule-parameterModifier }
**_parameterModifier_:**
 ~ `'vararg'`  
 | `'noinline'`  
 | `'crossinline'`
:::

:::{ .grammar-rule #grammar-rule-reificationModifier }
**_reificationModifier_:**
 ~ `'reified'`
:::

:::{ .grammar-rule #grammar-rule-platformModifier }
**_platformModifier_:**
 ~ `'expect'`  
 | `'actual'`
:::

:::{ .grammar-rule #grammar-rule-label }
**_label_:**
 ~ _IdentifierAt_ {_NL_}
:::

:::{ .grammar-rule #grammar-rule-annotation }
**_annotation_:**
 ~ (_singleAnnotation_ | _multiAnnotation_) {_NL_}
:::

:::{ .grammar-rule #grammar-rule-singleAnnotation }
**_singleAnnotation_:**
 ~ (_annotationUseSiteTarget_ {_NL_} `':'` {_NL_} _unescapedAnnotation_)  
 | (`'@'` _unescapedAnnotation_)
:::

:::{ .grammar-rule #grammar-rule-multiAnnotation }
**_multiAnnotation_:**
 ~ (_annotationUseSiteTarget_ {_NL_} `':'` {_NL_} `'['` (_unescapedAnnotation_ {_unescapedAnnotation_}) `']'`)  
 | (`'@'` `'['` (_unescapedAnnotation_ {_unescapedAnnotation_}) `']'`)
:::

:::{ .grammar-rule #grammar-rule-annotationUseSiteTarget }
**_annotationUseSiteTarget_:**
 ~ `'@field'`  
 | `'@property'`  
 | `'@get'`  
 | `'@set'`  
 | `'@receiver'`  
 | `'@param'`  
 | `'@setparam'`  
 | `'@delegate'`
:::

:::{ .grammar-rule #grammar-rule-unescapedAnnotation }
**_unescapedAnnotation_:**
 ~ _constructorInvocation_  
 | _userType_
:::

:::{ .grammar-rule #grammar-rule-simpleIdentifier }
**_simpleIdentifier_:**
 ~ _Identifier_  
 | `'abstract'`  
 | `'annotation'`  
 | `'by'`  
 | `'catch'`  
 | `'companion'`  
 | `'constructor'`  
 | `'crossinline'`  
 | `'data'`  
 | `'dynamic'`  
 | `'enum'`  
 | `'external'`  
 | `'final'`  
 | `'finally'`  
 | `'get'`  
 | `'import'`  
 | `'infix'`  
 | `'init'`  
 | `'inline'`  
 | `'inner'`  
 | `'internal'`  
 | `'lateinit'`  
 | `'noinline'`  
 | `'open'`  
 | `'operator'`  
 | `'out'`  
 | `'override'`  
 | `'private'`  
 | `'protected'`  
 | `'public'`  
 | `'reified'`  
 | `'sealed'`  
 | `'tailrec'`  
 | `'set'`  
 | `'vararg'`  
 | `'where'`  
 | `'expect'`  
 | `'actual'`  
 | `'const'`  
 | `'suspend'`
:::

:::{ .grammar-rule #grammar-rule-identifier }
**_identifier_:**
 ~ _simpleIdentifier_ {{_NL_} `'.'` _simpleIdentifier_}
:::

:::{ .grammar-rule #grammar-rule-shebangLine }
**_shebangLine_:**
 ~ _ShebangLine_
:::

:::{ .grammar-rule #grammar-rule-quest }
**_quest_:**
 ~ _QUEST_NO_WS_  
 | _QUEST_WS_
:::

:::{ .grammar-rule #grammar-rule-elvis }
**_elvis_:**
 ~ _QUEST_NO_WS_ `':'`
:::

:::{ .grammar-rule #grammar-rule-safeNav }
**_safeNav_:**
 ~ _QUEST_NO_WS_ `'.'`
:::

:::{ .grammar-rule #grammar-rule-excl }
**_excl_:**
 ~ _EXCL_NO_WS_  
 | _EXCL_WS_
:::

:::{ .grammar-rule #grammar-rule-semi }
**_semi_:**
 ~ ((`';'` | _NL_) {_NL_})  
 | _EOF_
:::

:::{ .grammar-rule #grammar-rule-semis }
**_semis_:**
 ~ (`';'` | _NL_ {`';'` | _NL_})  
 | _EOF_
:::
