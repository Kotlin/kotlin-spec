## Statements

TODO()

**_statements_:**  
  ~  [_statement_ {semis _statement_} [_semis_]]   

**_statement_:**  
  ~  {_labelDefinition_}   
    ( _declaration_   
    | _assignment_   
    | _loopStatement_   
    | _expression_)   

Unlike some other languages, Kotlin statements include bare expressions and declarations.
This section is focused on those statements that are *not* expressions or declarations.
For information about expressions and declarations please refer to the corresponding
sections of this document.

### Assignments

**_assignment_:**  
  ~  _directlyAssignableExpression_ `=` {_NL_} _expression_   
    | _assignableExpression_ _assignmentAndOperator_ {_NL_} _expression_   

**_assignmentAndOperator_:**  
  ~  `+=`   
    | `-=`   
    | `*=`   
    | `/=`   
    | `%=`   

An *assignment* is a statement that writes a new value to some location, denoted
by its left hand side. Both left-hand and right-hand sides of an assignment are
expressions, although there are several restrictions for the expression on the
left hand side.

For an expression to be *assignable*, i.e. be allowed to occur on the left-hand
side of an assignment, it **must** be one of the following:

- An identifier referring to a mutable property;
- A navigation expression referring to a mutable property;
- An [indexing expression][Indexing expression], see details below.

Unlike some other languages, Kotlin assignments **are not** expressions and cannot
be used as such.

#### Simple assignment

A *simple assigment* is the assignment form employing the assign operator `=`.
If the left-hand side of the assignment refers to a mutable property, a mutation of
that property is performed when the assignment is evaluated:

- If the property is [delegated][Property delegation], the corresponding operator
  function `setValue` is called using the right-hand side expression value as value argument;
- If the property has a setter, it is called using the right-hand side expression value as
  value argument;
- If the property is just a variable without delegation or setter, it's value is directly changed
  to the value of the right-hand side expression.

If the left-hand side of the assignment is an indexing expression, the whole statement
is treated as an overloaded operator with the following expansion:

$A$`[`$B_1$,$B_2$,$B_3$,...,$B_N$`] = `$C$ is exactly the same as calling
$A$`.set(`$B_1$,$B_2$,$B_3$,...,$B_N$,$C$`)` where `set` is a sutable `operator`
function.

#### Operator assignments

An *operator assignment* is a combined-form assignment that involves one of the following
operators: `+=`, `-=`, `*=`, `/=`, `%=`. All these are overloadable operators with the
following expansions:

- $A$`+=`$B$ is exactly the same as one of the following (in this order):
    - $A$`.plusAssign(`$B$`)` if a corresponding suitable `plusAssign` operator function
      exists and is available;
    - $A$` = `$A$`.plus(`$B$`)` if a corresponding suitable `plus` operator function exists
      and is available.
- $A$`-=`$B$ is exactly the same as one of the following (in this order):
    - $A$`.minusAssign(`$B$`)` if a corresponding suitable `minusAssign` operator function
      exists and is available;
    - $A$` = `$A$`.minus(`$B$`)` if a corresponding suitable `minus` operator function exists
      and is available.
- $A$`*=`$B$ is exactly the same as one of the following (in this order):
    - $A$`.timesAssign(`$B$`)` if a corresponding suitable `timesAssign` operator function
      exists and is available;
    - $A$` = `$A$`.times(`$B$`)` if a corresponding suitable `times` operator function exists
      and is available.
- $A$`/=`$B$ is exactly the same as one of the following (in this order):
    - $A$`.divAssign(`$B$`)` if a corresponding suitable `divAssign` operator function
      exists and is available;
    - $A$` = `$A$`.div(`$B$`)` if a corresponding suitable `div` operator function exists
      and is available;
- $A$`%=`$B$ is exactly the same as one of the following (in this order):
    - $A$`.remAssign(`$B$`)` if a corresponding suitable `remAssign` operator function
      exists and is available;
    - $A$` = `$A$`.rem(`$B$`)` if a corresponding suitable `rem` operator function
      exists and is available.

> As of Kotlin version 1.2.31, there exists an additional overloading function for
> `%` called `mod`, which is deprecated

The expanded simple assignment is then proceeded as described in the previous
section.

> Although for most real-world usecases operators `++` and `--` are very similar to
> operator assignments, in Kotlin they are actually expressions and are described as such
> in the [corresponding section][Expressions] of this document.

### Loop statements

Loop statements are constructs that repeat evaluating a certain number of statements
until a *loop exit condition* applies.

**_loopStatement_:**  
  ~  _forStatement_   
    | _whileStatement_   
    | _doWhileStatement_   

Loops are closely related to the semantics of several [jump expressions][Jump expressions],
as these expressions, namely `break` and `continue`, are only allowed in the body of
a loop. Please refer to the corresponding section for details.

#### While loop

**_whileStatement_:**  
  ~  `while` {_NL_} `(` _expression_ `)` {_NL_} _controlStructureBody_   
    | `while` {_NL_} `(` _expression_ `)` {_NL_} `;`  

*While loop statement* is very similar to an [`if` expression][Conditional expression]
in the way that it contains a condition expression and a body consisting of one
or more statements. While loop repeats evaluating its body for as long as the
condition expression evaluates to true or a [jump expression][Jump expressions]
is evaluated to finish the loop.

> This also means that the condition expression is evaluated before every evaluation
> of the body, including the first one.

As for the `if` expression, the condition subexpression **must have** type `kotlin.Boolean`.

#### Do-while loop

**_doWhileStatement_:**  
  ~  `do` {_NL_} [_controlStructureBody_] {_NL_} `while` {_NL_} `(` _expression_ `)`   

A *do-while statement* is very similar to the while statement, but with a few differences.
First, it has a different syntax. Second, it evaluates the loop condition expression
**after** evaluating the loop body.

> This also means that the body is always evaluated at least once

As for the `if` expression, the condition subexpression **must have** type `kotlin.Boolean`.

#### For loop

**_forStatement_:**  
  ~  `for` {_NL_} `(` {_annotation_} (variableDeclaration | _multiVariableDeclaration_) `in` _expression_ `)` {_NL_} [_controlStructureBody_]   

> Unlike other languages, Kotlin does not have a free-form condition-based for loops.
> The only form of for-loop available in Kotlin is (what it's called in other languages)
> "the foreach loop", iterating over arrays and other datastructures

A *for statement* is a special kind of loop statements that is used to iterate over
data structures containing a number of elements. The for loop consists of a loop body,
a **container expression** and the **iteration variable declaration**.

The for loop is actually an [overloadable][Overloadable operators] syntax form
with the following expansion:

`for(`$VarDecl$`) in `$C$`) $Body$` is exactly the same as

```kotlin
val __iterator = C.iterator()
while(__iterator.hasNext()) {
    VarDecl = __iterator.next()
    <... all the statements from Body>
}
```

where `iterator`, `hasNext`, `next` are all acceptable operator functions available
in the current scope.

> Please note that expansions are hygenic, meaning the generated iterator variable
> never clashes with any other values in the program and cannot be accessed outside
> the expansion


### Code blocks

**_block_:**  
  ~  `{` {_NL_} _statements_ {_NL_} `}`   

**_statements_:**  
  ~  [_statement_ {semis _statement_} [_semis_]]   

A *code block* is a series of statements between curly braces separated by
newlines or/and semicolons. A code block may be empty. Evaluating a code block
means evaluating all its statements in the order they are given inside it.

The *last expression* of a code block is the last statement in the block (if any)
if and only if this statement is also an expression. The last expressions are
important when defining functions and control structure expressions.

The code block is said to contain no last expression if it does not contain
any statements of the last statement is not an expression (e.g. it is an assignment,
a loop or a declaration).

> This usually means that a synthetic last expression with no runtime semantics and
> with type `kotlin.Unit` is introduced instead

A *control structure body* is either a single statement or a code block. The *last expression*
of a control structure body is either the last expression of the code block
(if it is a code block) or the single statement itself if it is an expression.
If the control structure body is not a code block or an expression, it has no
last expression.

> This is usually equivalent to wrapping the single statement in a new synthetic
> code block

In some contexts, a control structure body is expected to have a value and/or a type.
The value of a control structure body is:

- The value of its last expression if it exists;
- The singleton `kotlin.Unit` object otherwise.

The *type of a control structure body* is the type of its value.

### TODO

- Are declarations statements or not?
    - In the current grammar, they are
- Wording
- Mutable vs immutable properties
- How expansions with new variables actually work