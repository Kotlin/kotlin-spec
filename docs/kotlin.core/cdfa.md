## Control- and data-flow analysis

TODO(Unreachable code w.r.t. Nothing)

### Control flow graph

We define all kinds of control-flow analysis on a classic model of the kotlin program called a control-flow graph (TODO: link?).
A control-flow graph of a program is a graph that loosely defines all feasible paths the flow of a particular program can take during execution.
All the control-flow graphs given in this section are *intraprocedural*, meaning that they describe the flow inside a single function, not taking function calls into account.
It may, however, include multiple function bodies if said functions are *declared* inside each other.

The following sections describe control-flow graph *fragments* associated with a particular kotlin code construct.
These fragments are introduced using visual notation rather than relational notation to simplify the understanding of the graph structure.
To represent intermediate values arising during computation, we use *implicit registers*, denoted `$1`, `$2`, `$3`, etc. 
These are unique in each fragment (assigning the same register twice in the same graph may only occur in unrelated program paths) and are assumed to be unique in the whole graph, too.
The numbers given are only notational.

TODO(register reduction ($1 = $2 should be removed))
TODO(maybe we do need phi-nodes in the end?)

We use a special kind of nodes (`eval` nodes), represented in dashed lines, to introduce sub-fragments into bigger fragments.
`eval x` here means that this node must be replaced with a whole fragment associated with `x`.
When this replacement is performed, the value produced by `eval` is the same value that the metaregister `$result` holds in the corresponding fragment.
All incoming edges of a fragment are connected to the incoming edges of the `eval` node, while all the outgoing edges of a fragment are connected to
the outgoing edges of the `eval` node.
It is important, however, that, if such edges are absent either in the fragment or near the `eval` node, they are removed from the graph.
Sometimes we also use the `eval b` notation where `b` is not a single statement, but rather a control-flow structure body.
The fragment for a control-flow structure body is the sequence of its statements, connected in order of appearance.

For some types of analysis, it is important which boolean conditions hold on each path.
We use special `assume` nodes to introduce these conditions.
`assume x` means that boolean condition `x` is always `true` when program flow passes through this particular node.

Some nodes are *labeled*, similarly to how statements may be labeled in kotlin.
These are special in the sense that if a fragment mentions a particular labeled node, this node is the same as any other node with this label in the whole graph.
This is important when building graphs representing loops.

#### Expressions

Simple expressions, like literals and references, do not affect the control-flow of the program in any way and are not given here.

##### Function calls and operators

We do not consider operator calls as something different from function calls, as these are just special types of function calls.
Henceforth, they are not treated specially.

```kotlin
x.f(arg1,..., argN)
```

```diagram
               +
               v
      +~~~~~~~~+~~~~~~~~+
      :                 :
      :   $2 = eval x   :
      :                 :
      +~~~~~~~~+~~~~~~~~+
               |
               v
     +~~~~~~~~~+~~~~~~~~~~+
     :                    :
     :   $1 = eval arg1   :
     :                    :
     +~~~~~~~~~+~~~~~~~~~~+
               |
               v
              ...
               +
               v
     +~~~~~~~~~+~~~~~~~~~~+
     :                    :
     :   $N = eval argN   :
     :                    :
     +~~~~~~~~~+~~~~~~~~~~+
               |
               v
+--------------+----------------+
|                               |
|   $result = $2.f($1,...,$N)   |
|                               |
+--------------+----------------+
               |
               v
```

```kotlin
f(arg1,..., argN)
```

```diagram
              +
              v
    +~~~~~~~~~+~~~~~~~~~~+
    :                    :
    :   $1 = eval arg1   :
    :                    :
    +~~~~~~~~~+~~~~~~~~~~+
              |
              v
             ...
              +
              v
    +~~~~~~~~~+~~~~~~~~~~+
    :                    :
    :   $N = eval argN   :
    :                    :
    +~~~~~~~~~+~~~~~~~~~~+
              |
              v
+-------------+--------------+
|                            |
|   $result = f($1,...,$N)   |
|                            |
+-------------+--------------+
              |
              v
```

##### Conditional expressions

To simplify our notation, we consider only `if`-expressions with both branches present.
Any `if`-statement in kotlin may be trivially turned into such an expression by replacing the missing branch with a `kotlin.Unit` object expression.

```kotlin
if(c) t else f
```

```diagram
                   +
                   v
          +~~~~~~~~+~~~~~~~~+
          :                 :
          :   $1 = eval c   :
          :                 :
          +~~~~~+~~~~~+~~~~~+
             t  |     |  f
         +------+     +-------+
         v                    v
 +-------+-------+    +-------+--------+
 |               |    |                |
 |   assume $1   |    |   assume !$1   |
 |               |    |                |
 +-------+-------+    +-------+--------+
         |                    |
         v                    v
+~~~~~~~~+~~~~~~~~+  +~~~~~~~~+~~~~~~~~+
:                 :  :                 :
:   $2 = eval t   :  :   $2 = eval f   :
:                 :  :                 :
+~~~~~~~~+~~~~~~~~+  +~~~~~~~~+~~~~~~~~+
         |                    |
         +-------+    +-------+
                 v    v
          +------+----+------+
          |                  |
          |   $result = $2   |
          |                  |
          +---------+--------+
                    |
                    v
```

```kotlin
when { 
    c1 -> b1
    else -> bE
}
```

```diagram
                    +
                    v
          +~~~~~~~~~+~~~~~~~~+
          :                  :
          :   $1 = eval c1   :
          :                  :
          +~~~~~~+~~~~~+~~~~~+
              t  |     |  f
         +-------+     +-------+
         v                     v
 +-------+-------+     +-------+--------+
 |               |     |                |
 |   assume $1   |     |   assume !$1   |
 |               |     |                |
 +-------+-------+     +-------+--------+
         |                     |
         v                     v
+~~~~~~~~+~~~~~~~~~+  +~~~~~~~~+~~~~~~~~~+
:                  :  :                  :
:   $2 = eval b1   :  :   $2 = eval bE   :
:                  :  :                  :
+~~~~~~~~+~~~~~~~~~+  +~~~~~~~+~~~~~~~~~~+
         |                    |
         +-------+     +------+
                 v     v
           +-----+-----+------+
           |                  |
           |   $result = $2   |
           |                  |
           +--------+---------+
                    |
                    v
```

We only consider when expressions having exactly two branches for simplicity.
A when expression with more than two branches may be trivially desugared into a series of nested when expression as follows:

```kotlin
when { 
    <entry1>
    <entries...>
    else -> bE
}
```

is the same as

```kotlin
when { 
    <entry1>
    else -> {
        when {
            <entries...>
            else -> bE
        }
    }
}
```

##### Boolean operators

```kotlin
!x
```

```diagram
                        +
                        v
               +~~~~~~~~+~~~~~~~~+
               :                 :
               :   $1 = eval x   :
               :                 :
               +~~~~~+~~~~~+~~~~~+
                     |     |
           +---------+     +-----------+
        t  |                           |  f
           v                           v
   +-------+-------+          +--------+-------+
   |               |          |                |
   |   assume $1   |          |   assume !$1   |
   |               |          |                |
   +-------+-------+          +--------+-------+
           |                           |
           v                           v
+----------+----------+     +----------+---------+
|                     |     |                    |
|   $result = false   |     |   $result = true   |
|                     |     |                    |
+----------+----------+     +----------+---------+
        f  |                           |  t
           v                           v
```

```kotlin
x || y
```

```diagram
                        +
                        v
               +~~~~~~~~+~~~~~~~~+
               :                 :
               :   $1 = eval x   :
               :                 :
               +~~~~~+~~~~~+~~~~~+
                     |     |
          +----------+     +-------+
       t  |                        |  f
          v                        v
  +-------+-------+       +--------+-------+
  |               |       |                |
  |   assume $1   |       |   assume !$1   |
  |               |       |                |
  +-------+-------+       +--------+-------+
          |                        |
          |                        v
          |               +~~~~~~~~+~~~~~~~~+
          |               :                 :
          |               :   $2 = eval y   :
          |               :                 :
          |               +~~~~~+~~~~~+~~~~~+
          |                     |     |
          |              +------+     +-------+
          |           t  |                    |  f
          |              v                    v
          |      +-------+-------+   +--------+-------+
          |      |               |   |                |
          |      |   assume $2   |   |   assume !$2   |
          |      |               |   |                |
          |      +-------+-------+   +--------+-------+
          |              |                    |
          |   +----------+                    |
          v   v                               v
+---------+---+------+             +----------+----------+
|                    |             |                     |
|   $result = true   |             |   $result = false   |
|                    |             |                     |
+---------+----------+             +----------+----------+
       t  |                                   |  f
          v                                   v

```

```kotlin
x && y
```

```diagram
                               +
                               v
                      +~~~~~~~~+~~~~~~~~+
                      :                 :
                      :   $1 = eval x   :
                      :                 :
                      +~~~~~+~~~~~+~~~~~+
                            |     |
                     +------+     +--------+
                  t  |                     |  f
                     v                     v
             +-------+-------+    +--------+-------+
             |               |    |                |
             |   assume $1   |    |   assume !$1   |
             |               |    |                |
             +-------+-------+    +--------+-------+
                     |                     |
                     v                     |
            +~~~~~~~~+~~~~~~~~+            |
            :                 :            |
            :   $2 = eval y   :            |
            :                 :            |
            +~~~~~+~~~~~+~~~~~+            |
                  |     |                  |
           +------+     +------+           |
        t  |                   |  f        |
           v                   v           |
   +-------+-------+   +-------+-------+   |
   |               |   |               |   |
   |   assume $2   |   |   assume !$2  |   |
   |               |   |               |   |
   +-------+-------+   +-------+-------+   |
           |                   |           |
           |                   |           |
           v                   v           v
+----------+---------+    +----+-----------+----+
|                    |    |                     |
|   $result = true   |    |   $result = false   |
|                    |    |                     |
+----------+---------+    +----------+----------+
        t  |                         |  f
           v                         v
```

```kotlin
x ?: y
```

```diagram
                             +
                             v
                    +~~~~~~~~+~~~~~~~~+
                    :                 :
                    :   $1 = eval x   :
                    :                 :
                    +~~~~~+~~~~~+~~~~~+
                          |     |
              +-----------+     +------------+
              v                              v
+-------------+------------+   +-------------+------------+
|                          |   |                          |
|   assume ($1 === null)   |   |   assume ($1 !== null)   |
|                          |   |                          |
+-------------+------------+   +-------------+------------+
              |                              |
              v                              v
     +~~~~~~~~+~~~~~~~~+              +------+------+
     :                 :              |             |
     :   $2 = eval y   :              |   $3 = $1   |
     :                 :              |             |
     +~~~~~~~~+~~~~~~~~+              +------+------+
              |                              |
              v                              |
       +------+------+                       |
       |             |                       |
       |   $3 = $2   |                       |
       |             |                       |
       +------+------+                       |
              |                              |
              |      +-----------------------+
              v      v
        +-----+------+-----+
        |                  |
        |   $result = $3   |
        |                  |
        +--------+---------+
                 |
                 v
```

```kotlin
x?.y
```

```diagram
                             +
                             v
                    +~~~~~~~~+~~~~~~~~+
                    :                 :
                    :   $1 = eval x   :
                    :                 :
                    +~~~~~+~~~~~+~~~~~+
                          |     |
              +-----------+     +------------+
              v                              v
+-------------+------------+   +-------------+------------+
|                          |   |                          |
|   assume ($1 === null)   |   |   assume ($1 !== null)   |
|                          |   |                          |
+-------------+------------+   +-------------+------------+
              |                              |
              v                              v
       +------+------+               +-------+-------+
       |             |               |               |
       |   $3 = $2   |               |   $3 = $1.y   |
       |             |               |               |
       +------+------+               +-------+-------+
              |                              |
              |      +-----------------------+
              v      v
        +-----+------+-----+
        |                  |
        |   $result = $3   |
        |                  |
        +--------+---------+
                 |
                 v
```

```kotlin
try { a... } catch (e1: T1) { b1... } ... catch (eN: TN) { bN... }
```

```diagram
         +                       +                           +
         v                       v                           v
+~~~~~~~~+~~~~~~~~+    +~~~~~~~~~+~~~~~~~~+         +~~~~~~~~+~~~~~~~~~+
:                 :    :                  :         :                  :
:   $1 = eval a   :    :   $1 = eval b1   :   ...   :   $1 = eval bN   :
:                 :    :                  :         :                  :
+~~~~~~~~+~~~~~~~~+    +~~~~~~~~~+~~~~~~~~+         +~~~~~~~~+~~~~~~~~~+
         |                       |                           |
         +-----------------+     |    +----------------------+
                           v     v    v
                       +---+-----+----+---+
                       |                  |
                       |   $result = $1   |
                       |                  |
                       +---------+--------+
                                 |
                                 v
```



```kotlin
a!!
```

```diagram
                             +
                             v
                    +~~~~~~~~+~~~~~~~~+
                    :                 :
                    :   $1 = eval a   :
                    :                 :
                    +~~~~~+~~~~~+~~~~~+
                          |     |
              +-----------+     +-------+
              v                         v
+-------------+------------+   +--------+--------+
|                          |   |                 |
|   assume ($1 !== null)   |   |   unreachable   |
|                          |   |                 |
+-------------+------------+   +-----------------+
              |
              v
     +--------+---------+
     |                  |
     |   $result = $1   |
     |                  |
     +--------+---------+
              |
              v
```

```kotlin
a as T
```

```diagram
                             +
                             v
                    +~~~~~~~~+~~~~~~~~+
                    :                 :
                    :   $1 = eval a   :
                    :                 :
                    +~~~~~+~~~~~+~~~~~+
                          |     |
              +-----------+     +-------+
              v                         v
+-------------+------------+   +--------+--------+
|                          |   |                 |
|     assume ($1 is T)     |   |   unreachable   |
|                          |   |                 |
+-------------+------------+   +-----------------+
              |
              v
     +--------+---------+
     |                  |
     |   $result = $1   |
     |                  |
     +--------+---------+
              |
              v
```

```kotlin
a as? T
```

```diagram
                         +
                         v
                +~~~~~~~~+~~~~~~~~+
                :                 :
                :   $1 = eval a   :
                :                 :
                +~~~~~+~~~~~+~~~~~+
                      |     |
            +---------+     +----------+
            v                          v
+-----------+----------+   +-----------+-----------+
|                      |   |                       |
|   assume ($1 is T)   |   |   assume ($1 !is T)   |
|                      |   |                       |
+-----------+----------+   +-----------+-----------+
            |                          |
            v                          v
     +------+------+           +-------+-------+
     |             |           |               |
     |   $2 = $1   |           |   $2 = null   |
     |             |           |               |
     +------+------+           +-------+-------+
            |                          |
            +---------+      +---------+
                      v      v
                +-----+------+-----+
                |                  |
                |   $result = $2   |
                |                  |
                +---------+--------+
                          |
                          v
```

```kotlin
{ a: T ... -> body... }
```

```diagram
             +                       +
             v                       v
+------------+-----------+   +~~~~~~~+~~~~~~~+
|                        |   :               :
|   $result = $literal   |   :   eval body   :
|                        |   :               :
+------------+-----------+   +~~~~~~~~~~~~~~~+
             |
             v
```

```kotlin
return
return@label
```

```diagram
         +
         v
+--------+--------+
|                 |
|   unreachable   |
|                 |
+-----------------+
```

```kotlin
return a
return@label a
throw a
```

```diagram
            +
            v
     +~~~~~~+~~~~~+
     :            :
     :   eval a   :
     :            :
     +~~~~~~+~~~~~+
            |
            v
   +--------+--------+
   |                 |
   |   unreachable   |
   |                 |
   +-----------------+
```

```kotlin
break@loop
```

```diagram
        +  
        v  
+~~~~~~~+~~~~~~~~+
:                :
:   @loop:exit   :
:                :
+~~~~~~~~~~~~~~~~+
```

```kotlin
continue@loop
```

```diagram
         +
         v
+~~~~~~~~+~~~~~~~~+
|                 |
|    backedge     |
|                 |
+~~~~~~~~~~~~~~~~~+
         |
         v  
+~~~~~~~~+~~~~~~~~+
:                 :
:   @loop:entry   :
:                 :
+~~~~~~~~~~~~~~~~~+
```

TODO(an example of a complex expression full graph)

#### Statements

```kotlin
loop@ while(c) { b... }
```

```diagram
                              +
                              v
                              |
                     +--------+--------+
                     |                 |
                     |   @loop.entry   |
                     |                 |
                     +--------+--------+
                              |
        +-----------------+   |
        |                 v   v
+-------+------+     +~~~~+~~~+~~~~~~~~+
|              |     :                 :
|   backedge   |     :   $1 = eval c   :
|              |     :                 :
+-------+------+     +~~~~~+~~~~~+~~~~~+
        ^               t  |     |  f
        |           +------+     +--------+
        |           v                     v
        |   +-------+-------+    +--------+-------+
        |   |               |    |                |
        |   |   assume $1   |    |   assume !$1   |
        |   |               |    |                |
        |   +-------+-------+    +--------+-------+
        |           |                     |
        |           v                     v
        |     +~~~~~+~~~~~~+     +--------+--------+
        |     :            :     |                 |
        |     :   eval b   :     |   @loop.exit    |
        |     :            :     |                 |
        |     +~~~~~+~~~~~~+     +--------+--------+
        |           |                     |
        +-----------+                     |
                                          v

```

```kotlin
loop@ do { b... } while(c)
```

```diagram
                              +
                              v
                              |
                     +--------+--------+
                     |                 |
                     |   @loop.entry   |
                     |                 |
                     +--------+--------+
                              |
        +-----------------+   |
        |                 v   v
+-------+------+       +~~+~~~+~~~~~+
|              |       :            :
|   backedge   |       :   eval b   :
|              |       :            :
+-------+------+       +~~~~~~+~~~~~+
        ^                     |
        |                     v
        |            +~~~~~~~~+~~~~~~~~+
        |            :                 :
        |            :   $1 = eval c   :
        |            :                 :
        |            +~~~~~+~~~~~+~~~~~+
        |               t  |     |  f
        |           +------+     +--------+
        |           v                     v
        |   +-------+-------+    +--------+-------+
        |   |               |    |                |
        |   |   assume $1   |    |   assume !$1   |
        |   |               |    |                |
        |   +-------+-------+    +--------+-------+
        |           |                     |
        +-----------+                     v
                                 +--------+--------+
                                 |                 |
                                 |   @loop.exit    |
                                 |                 |
                                 +--------+--------+
                                          |
                                          |
                                          v

```

#### Declarations

```kotlin
var a = b
var a by b
val a = b
val a by b
```

```diagram
TODO
```

```kotlin
fun f() { body... }
```

```diagram
TODO
```

```kotlin
class A() { ... }
```

```diagram
TODO???
```

#### Examples

```kotlin
fun f() = listOf(1, 2).map { it + 2 }.filter { it > 0 }
```

```diagram
                                          +
                                          v
                                    +-----+------+
                                    |            |
                                    |   $1 = 1   |
                                    |            |
                                    +-----+------+
                                          |
                                          v
                                    +-----+------+
                                    |            |
                                    |   $2 = 2   |
                                    |            |
                                    +-----+------+
                                          |
                                          v
                              +-----------+-------------+
                              |                         |
                              |   $3 = listOf($1, $2)   |
                              |                         |
                              +-----+--------------+----+
                                    |              |
                             +------+              +------------+
                             v                                  v
                  +----------+----------+                +------+------+
                  |                     |                |             |
                  |   $4 = { it + 2 }   |                |   $5 = it   |
                  |                     |                |             |
                  +----------+----------+                +------+------+
                             |                                  |
                             v                                  v
                  +----------+----------+                 +-----+------+
                  |                     |                 |            |
                  |   $8 = $3.map($4)   |                 |   $6 = 2   |
                  |                     |                 |            |
                  +------+-------+------+                 +-----+------+
                         |       |                              |
               +---------+       +--------+                     v
               v                          v            +--------+---------+
    +----------+----------+        +------+-------+    |                  |
    |                     |        |              |    |   $7 = $5 + $2   |
    |   $9 = { it > 0 }   |        |   $10 = it   |    |                  |
    |                     |        |              |    +------------------+
    +----------+----------+        +------+-------+
               |                          |
               v                          v
+--------------+--------------+     +-----+-------+
|                             |     |             |
|   $result = $8.filter($9)   |     |   $11 = 0   |
|                             |     |             |
+--------------+--------------+     +-----+-------+
               |                          |
               |                          v
               |               +----------+----------+
               |               |                     |
               |               |   $12 = $10 > $11   |
               |               |                     |
               v               +---------------------+
```

### Performing analysis on the control-flow graph

The analyses defined in this document follow the pattern of analysis based on monotone frameworks (TODO: citation) which is based on modeling abstract program states as elements of lattices and joining these states using standard lattice operations.
This analysis may have limited path sensitivity through the analysis of conditions used in `assume` nodes.
Further describing monotone frameworks and analysis based on top of them goes out of scope of this document (TODO: or does it?).

#### Variable initialization analysis

Kotlin allows non-delegated properties (see [Property declarations]) to not have initializers in their declaration as long as the property is *definitely assigned* before its first usage.
This property is checked by the variable initialization analysis.
This analysis operates on abstract values from the flat lattice or two values $\{Assigned, Unassigned\}$ with the top value $\top$ signifying the unknown state for the property.
The analysis itself uses the abstract values of functional lattice from all the property declarations to their respective abstract states based on the lattice given above.
The abstract states are propagated in a forward manner using the standard join operation to merge states from different paths.

The CFG nodes relevant to this analysis include only property declarations and direct assignments.
Every declaration adds itself to the domain by assigning the $Unassigned$ value to itself.
Every direct assigment of a property changes the value for this property to $Assigned$.

The results of the analysis are interpreted as follows. 
For every property any usage of the said property in any statement is a compile-time error unless the abstract state of this property in this statement is $Assigned$.
For every immutable property (declared using `val` keyword) any assignment to this property is a compile-time error unless the abstract state of this property is $Unassigned$.

TODO: example?

Let's consider the following example:

`val x: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow Unassigned, \star \rightarrow \bot \}$ \
`var y: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow Unassigned, \mathtt{y} \rightarrow Unassigned, \star \rightarrow \bot \}$ \
`if(c) {       //`{.kotlin} \
`    x = 40    //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow Assigned, \mathtt{y} \rightarrow Unassigned, \star \rightarrow \bot \}$ \
`    y = 4     //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow Assigned, \mathtt{y} \rightarrow Assigned, \star \rightarrow \bot \}$ \
`} else {      //`{.kotlin} \
`    x = 20    //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow Assigned, \mathtt{y} \rightarrow Unassigned, \star \rightarrow \bot \}$ \
`}             //`{.kotlin}         $\{ \mathtt{x} \rightarrow Assigned, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$ \
`y = 5         //`{.kotlin}         $\{ \mathtt{x} \rightarrow Assigned, \mathtt{y} \rightarrow Assigned, \star \rightarrow \bot \}$ \
`val z = x + y //`{.kotlin}         $\{ \mathtt{x} \rightarrow Assigned, \mathtt{y} \rightarrow Assigned, \mathtt{z} \rightarrow Assigned, \star \rightarrow \bot \}$ \

TODO: somehow fix this atrocities for indentation in inline code

There are no incorrect states in this example, so it is indeed correct.

Let's consider another example:

`val x: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow Unassigned, \star \rightarrow \bot \}$ \
`var y: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow Unassigned, \mathtt{y} \rightarrow Unassigned, \star \rightarrow \bot \}$ \
`while(c) {    //`{.kotlin}         $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$ Error! \
`    x = 40    //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$ \
`    y = 4     //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$ \
`}             //`{.kotlin} \
`val z = x + y //`{.kotlin}         $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$ 2 errors! \

Here the state of both properties at line 3 is $\top$ because the corresponding edges of CFG are the lowest upper bound of the statements at lines 5 and 2, which, after a rather trivial fixed point calculation, is derived to be $\top$.
This is a compiler error in the case of `x` because one cannot reassign an immutable variable.

At line 7 there is another error as both variables are used, but there are paths through which they were not previously assigned values, namely, the paths where loop did not take any iterations.

TODO: draw graphs for all these?

#### Smart casting analysis

See the [corresponding section][Smart casts] for details.
