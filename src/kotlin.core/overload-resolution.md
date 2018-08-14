## Overload resolution

Kotlin supports _function overloading_, that is, the ability for several functions
of the same name to coexist in the same scope, with the compiler picking the
most suitable one when such a function is called. This section describes this
mechanism in detail.

### Intro

Unlike other object-oriented languages, Kotlin does not only have object methods,
but also top-level functions, local functions, extension functions and function-like
values, which complicates the overloading process quite a lot. Kotlin also
has infix functions, operator and property overloading which all work in a similar
but rather different way.

### Receivers

Every function or property that is defined as a method or an extension has one
or more special parameters called _receiver_ parameters.
When calling such a callable using navigation operators (`.` or `?.`) the left
hand side parameter is called an _explicit receiver_ or this particular call.
In addition to the explicit receiver, each such call may indirectly access
zero or more _implicit receivers_.

Implicit receivers are available in some syntactic scope according to the
following rules:

- All receivers available in outer scope are also available in all nested scopes;
- In the scope of a classifier definition, the following receivers are available:
    - The implicit `this` object of the defined type;
    - The companion object (if one exist) of this class;
    - The companion objects (if any exists) of all its superclasses;
- If a function or a property is an extension, the `this` parameter of the extension
  is also available inside the extension definition;
- The scope of a lambda expression if it has an extension function type contains
  the `this` argument of this lambda expression.

The available receivers are prioritized in the following way:

- The receivers provided in most inner scope have higher priority;
- In a classifier body, the implicit `this` reference has higher priority
  than the companion object receiver;
- Current class companion object receiver has higher priority than any of
  the base classes.

The implicit receiver having the highest priority is also called the _default
implicit receiver_. The default implicit receiver is available in the scope
as `this`. Other available receivers may be accessed using
[this-expressions][This-expressions] of different form.

If an implicit receiver is available in some scope, it may be used to call functions
implicitely without using the navigation operator.

### The forms of call-expression

Any function in Kotlin may be called in several different ways:

- A fully-qualified call: `package.foo()`;
- A call with an explicit receiver: `a.foo()`;
- An infix function call: `a foo b`;
- An overloaded operator call: `a + b`;
- A call without an explicit receiver: `foo()`;

For each of these cases, a compiler should first pick a number of
_overload candidates_ which is a set of callables that may be the intended
callees and then _choose the most specific function_ to call based on the types
of the function and the call operands. Please note that the overload candidates
are picked **before** the most specific function is chosen.

### Callables and invoke convention

A *callable* $X$ for the purpose of this document is one of the following:

- A function named $X$ at declaration site;
- A property named $X$ at declaration site with an operator function called
  `invoke` that is available as member or extension in the current scope.

In the latter case a call $X$`(`$Y_0$`,`$Y_1$`,...,`$Y_N$`)` is an overloadable
operator which is expanded to $X$`.invoke(`$Y_0$`,`$Y_1$`,...,`$Y_N$`)`.
The call may contain type parameters, named parameters, variable argument parameter
expansion and trailing lambda parameters, all of which are forwarded as-is to
the corresponding `invoke` function.

A *member callable* is either a member function or a member property with member
operator `invoke`. An *extension callable* is either an extension function,
a member property with an extension operator `invoke` or an extension property
with an extension operator `invoke`.

When calculating overload resolution sets, member callables
produce the following separate sets (ordered by priority, bigger priority first):

- Member functions;
- Member properties.

Extension callables produce the following
separate sets (ordered by priority, bigger priority first):

- Extension functions;
- Member properties with extension invoke;
- Extension properties with member invoke;
- Extension properties with extension invoke.

This division is more granular than all other means of dividing resolution
candidates into sets, meaning that it is performed the last.

### Overload resolution for a fully-qualified call

If the callable name is fully-qualified (that is, contains full package path),
then the overloading candidate set simply contains all the callables with
the same name in the same package. As a package name may never clash with any
other declared entity, after performing division of callables, these are the only
sets available.

Example:
```kotlin
package a.b.c

fun foo(a: Int) {}
fun foo(a: Double) {}
fun foo(a: List<Char>) {}
val foo = {}
. . .
a.b.c.foo()
```

Here the overload candidates set contains all the callables named `foo` from the
package `a.b.c`.

### A call with an explicit receiver

If a function call is done using a navigation operator (`.` or `?.`, not to be
confused with a [fully-qualified call][Overload resolution for a fully-qualified call]),
then the left hand side operand of this operator is the explicit receiver of this
call.

A call of callable `f` with explicit receiver `e` is correct if one (or more) of the following holds:

1. `f` is a member callable of the classifier type of `e` or any of its supertypes;
2. `f` is an extension callable for the classifier type of `e` or any of its supertypes,
   including local and imported extensions.

Please note that callables for case 2 not only include top-level
declared extension callables, but also extension callables available in any
of the available implicit receivers. For example, if a class contains a member extension
function for another class and an object of this class is available as an implicit
receiver, this extension function may be used for the call if it has a suitable type.

Then for a callable named `f` the following sets are looked upon (in this order):

1. The sets of non-extension member callables named `f` of the receiver type;
2. The sets of local extension callables named `f` whose receiver type conforms to
   the explicit receiver type, in all declaration scopes containing the current declaration
   scope, ordered by the size of the scope (smallest first), excluding the package scope;
3. The sets of explicitly imported extension callables named `f` whose receiver type conforms to
   the explicit receiver type;
4. The sets of extension callables named `f` whose receiver type conforms to
   the explicit receiver type, declared in the current package;
5. The sets of star-imported extension callables named `f` whose receiver type conforms to
   the explicit receiver type;
6. The sets of implicitly imported extension callables named `f` whose receiver type conforms to
   the explicit receiver type.

TODO() : all this X-imported things need to be defined somewhere

When looked upon these sets, the first set that contains **any** callable
with the corresponding name and conforming types is picked. This means,
among other things, that if the set constructed during step 2 contains a
more suitable candidate function, but the set constructed in step 1
is not empty, the function from set 1 is picked even it is a less suitable
candidate.

### Infix function calls

In reality, infix function calls are a special case of function calls with an
explicit receiver using the left hand operand as the receiver.

There is a slight difference though: during the overload candidate set
selection the only functions considered for inclusion are the ones with the
`infix` modifier. All other functions
(and all properties) are not even considered for inclusion.
Aside from this small difference, candidates are selected using the same
rules as for normal calls with explicit receiver.

> This also means that all the properties available through `invoke`-convention
> are non-eligible for such calls, because there is no way of specifying
> the `infix` modifier for them

Different platform implementations may extend the set of functions deemed valid
candidates for inclusion as infix functions.

### Operator calls

According to TODO(), some operator expressions in Kotlin can be overloaded
using specially-named functions. This makes operator expressions semantically
equivalent to function calls with explicit receiver, where the receiver expression
is selected according to operator form (see TODO()). The selection of an exact
function that is called in each particular case is based on the same rules
as for function calls with explicit receivers, the only difference being
that only functions with `operator` modifier are considered for inclusion when
building overload candidate sets. Properties are never considered for inclusion
for operator calls.

> This also means that all the properties available through `invoke`-convention
> are non-eligible for such calls, because there is no way of specifying
> the `operator` modifier for them, even though the `invoke` callable is required
> to always have such modifier.
> This means that, as `invoke`-convention itself is an operator call, it is
> impossible to employ more than one `invoke`-conventions in a single call.

Different platform implementations may extend the set of functions deemed valid
candidates for inclusion as operator functions.

Please note that this is valid not only for dedicated operator expressions, but
also for `for`-loops iteration convention and property delegates.

### A call without an explicit receiver

A call that is performed with unqualified function name and without using a
navigation operator is a call without an explicit receiver. It may in fact
have one or more implicit receivers or be a top-level function.

As with function calls with explicit receiver, a valid implementation should
first pick a valid overload candidate set and then search this set for the
_most specific function_ to match the call.

Than for a function named `f` the following sets are looked upon (in this order):

1. The sets of local non-extension functions named `f` available in current scope, in order of
   the scope they are declared in, smallest scope first;
2. The overload candidate sets for each implicit receiver and `f`, calculated as if it was
   the explicit receiver, in the order of the receiver priority
   (see previous section);
3. Top-level non-extension functions named `f`, in this order:
   a. Functions explicitely imported into current file;
   b. Functions declared in the same package;
   c. Functions star-imported into current file;
   d. Implicitly imported functions (kotlin standard library or platform-specific);

When looked upon these sets, the first set that contains **any** function
with the corresponding name and conforming types is picked.

### Calls with named parameters

Most of the call forms listed above may use named parameters in call expressions,
for example, `f(a = 2)`, where `a` is a named formal parameter specified in
the declaration of `f`. Such calls are treated the same way as normal calls, but
the overload resolution sets are filtered to only contain callables that actually
have formal parameters for all the corresponding names.

> For properties called through invoke-convention, the named parameters must
> be present in the declaration of the `invoke` operator function

Unlike positional arguments, named arguments specify directly which of the
formal parameters of the function the argument corresponds to. The matching
of formal parameters and arguments is performed separately for each function
candidate and while the number of defaults (
  see [the MSC definition process][Choosing the most specific function from the overload candidate set]) does affect resolution process,
the fact that some argument was mapped using named or positional argument does
not affect is in any way.

### Calls with trailing lambda expressions

Most of the call forms listed above may have a single lambda expression presented outside
of the parentheses or replacing them (see [Call expression]). This has no effect on
overload resolution process, aside from argument reordering that may happen due to
variable argument parameters or parameters with defaults between the arguments.

> This means that calls `f(1,2){ g() } ` and `f(1,2, body = { g() })` are completely equivalent
> from the overload resolution standpoint, assuming that `body` is the name
> of the last formal parameter of `f`

### Calls with specified type parameters

Most of the call forms listed above may have a list of type arguments that precedes
the list of value arguments of the call. In this case, all the potential overload
sets only include callables that contain an exactly same number of formal type
arguments at declaration site. In the case of property callable through `invoke`
convention, the type parameters must be present at the `invoke` operator function
declaration.

### Determining function applicability for a specific call

#### Rationale

A function is *applicable* for a specific call if and only if the function type
parameters may be assigned the values of the arguments specified in the call and
all the type constraints of the function still hold.

#### Description

Determining function applicability for a specific call is a
[type constraint][Type constraints] problem.
First, for every argument of the function supplied in the call,
the type inference is performed. This excludes lambda arguments, as
the inference of the specific type for them needs the results of overloading
to finish.

After that the following constraint system is built:

- For every parameter of the call (excluding lambda parameters) inferred to have
  type $T_i$, corresponding to function argument of type $U_j$,
  a constraint $T_i <: U_j$ is constructed;
- All the declaration-site type constraints for the function are also added to the system;
- For each lambda parameter with the number of lambda arguments known to be $K$,
  a constraint and corresponding to function argument of type $U_m$,
  an artificial constraint is added in the form $R(L_1,...,L_K) <: U_m$,
  where $R, L_1, ..., L_K$ are all fresh variables;
- For each lambda parameter with the number of lambda arguments not known
  (that is, being equal to 0 or 1), an artificial constraint is added in the
  form $kotlin.Function <: U_m$, where $kotlin.Function$ is the common base
  of all functional types; TODO(): what's the name???

If this constraint system is sound, the function is applicable for the call.
Only applicable functions are considered for the next step: finding the most
specific overload candidate from the candidate set.

### Choosing the most specific function from the overload candidate set

#### Rationale

The main rationale behind choosing the most specific function from a candidate set
is that the function chosen could be easily forwarded to by all the other functions
in the set, while the reverse is not true. If there are several functions with
this property, none of them is the most specific and an ambiguity error should
be reported by the compiler.

Let's look at an example with two functions:

```kotlin
fun f(arg: Int, arg2: String) {}        // (1)
fun f(arg: Any?, arg2: CharSequence) {} // (2)
...
f(2, "Hello")
```

Here both functions are applicable for the call, but also function (1) could easily
call function (2) by forwarding both arguments into it, but the reverse is impossible.
As a result, function (1) is more specific of the two.
Let's rename the functions to make it more clear:

```kotlin
fun f1(arg: Int, arg2: String) {
    f2(arg, arg2) // perfectly valid
}
fun f2(arg: Any?, arg2: CharSequence) {
    f1(arg, arg2) // invalid: function f1 is not appicable
}
```

The rest of this section will try to clarify this mechanism a little more.

#### Description

When an overload resolution set is picked up and contains more than one callable,
the next step is to find the most appropriate candidate from these callables.

Firts, the appicable set is divided into two sub-sets: the callables that employ
type parameters (generic callables) and the callables that don't (non-generic callables).
If there are any non-generic applicable candidates, the choise is limited only to
non-generic subset. Otherwise, all canidates (of generic callables) is considered.

This process employs the usage of [type constraint][Type constraints] system of
Kotlin, similar to the process of
[determining function applicability][Determining function applicability for a specific call].
For every two members of the candidate set (let's call them $F_1$ and $F_2$),
the following constraint system is constructed and solved:

- For every non-default argument of the call the corresponding value parameters'
  types $X_1, X_2, X_3 ... X_N$ of $F_1$ and value parameters' types $Y_1, Y_2, Y_3 ... Y_N$
  of $F_2$ a type constraint $X_K <: Y_K$ is built. During construction of these
  constraints, all the type parameters of $F_1$ are considered bound and all the
  type parameters of $F_2$ are considered free;
- All the declaration-site type constraints of $X_1, X_2, X_3 ... X_N$ and
  $Y_1, Y_2, Y_3 ... Y_N$ are also added to the system.

If this constraint system is sound, it means that $F_1$ is equally or more applicable
as an overload candidate. After that the check is repeated with $F_1$ and $F_2$ swapped.
If $F_1$ is equally or more applicable than $F_2$ and $F_2$ is equally or more applicable
than $F_1$, this means that the two callables are equally applicable and an additional
decision procedure needs to be invoked.

All the members of the overload candidate set are sorted according to the criteria
of applicability, determining the most applicable callable. If there are several
callables which are both more applicable than other candidates and equally applicable
to each other, an additional step is performed:

- For each candidate, the number of default parameters not specified in the call is counted;
- The candidate with the least default parameters is a more specific candidate;
- If the number of defaulted parameters is equal for several candidates,
  the candidate having any variable-argument parameters is less specific than
  any candidate without them.

If, even after this additional step, there are several candidates that are equally
applicable for the call, there is an **overload ambiguity** that must be reported
as a compiler error.

> NOTE: Please note that, unlike the applicability test, the candidate comparison
> constraint system is **not** based on the actuall call, meaning that when comparing
> two candidates, only constraints visible at declaration site apply.

### About type inference

[Type inference][Type inference] in Kotlin is a pretty complicated process, which is
performed after resolving all the overload candidates. Due to the complexity of
the process, type inference may not affect the way overload resolution candidate
is picked up.

#### TODO:

- Properties business
- Function types (type system section???)
- Definition of "applicable function"
- Definition of "type parameter level"
- Calls with named parameters `f(x = 2)`
- Calls with trailing lambda without parameter type
    * Lambdas with parameter types seem to be covered, **nope, they are not**
- Calls with specified type parameters `f<Double>(3)`
