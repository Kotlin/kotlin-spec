## Statements

TODO()

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
