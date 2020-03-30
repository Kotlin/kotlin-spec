## Control- and data-flow analysis

### Control flow graph

We define all kinds of control-flow analysis for Kotlin on a classic model called a control-flow graph (CFG).
A CFG of a program is a graph that loosely defines all feasible paths the flow of a particular program can take during execution.
All CFGs given in this section are *intraprocedural*, meaning that they describe the flow inside a single function, not taking function calls into account.
It may, however, include multiple function bodies if said functions are *declared* inside each other (as is the case for [lambdas][Lambda literals]).

The following sections describe CFG *fragments* associated with a particular Kotlin code construct.
These fragments are introduced using visual notation rather than relational notation to simplify the understanding of the graph structure.
To represent intermediate values created during computation, we use *implicit registers*, denoted `$1`, `$2`, `$3`, etc.
These are considered to be unique in each CFG fragment (assigning the same register twice in the same CFG may only occur in unrelated program paths) and in the complete CFG, too.
The numbers given are only notational.

TODO(maybe we do need phi-nodes in the end?)

We introduce a special kind of `eval` nodes, represented in dashed lines, to connect CFG fragments into bigger fragments.
`eval x` here means that this node must be replaced with a whole fragment associated with `x`.
When this replacement is performed, the value produced by `eval` is the same value that the meta-register `$result` holds in the corresponding fragment.
All incoming edges of a fragment are connected to the incoming edges of the `eval` node, while all the outgoing edges of a fragment are connected to the outgoing edges of the `eval` node.
It is important, however, that, if such edges are absent either in the fragment or in the `eval` node, they (edges) are removed from the CFG.

We also use the `eval b` notation where `b` is not a single statement, but rather a control-flow structure body.
The fragment for a control-flow structure body is the sequence of fragments for its statements, connected in the program order.

Some of the fragments have two kinds of outgoing edges, labeled `t` and `f` on the pictures.
In a similar fashion, some `eval` nodes have two outgoing edges with the same labels.
If such a fragment is inserted into such a node, only edges with matching labels are merged into each other.
If either the fragment or the node have only unlabeled outgoing edges, the process is performed same as above.

For some types of analyses, it is important which boolean conditions hold on each path.
We use special `assume` nodes to introduce these conditions.
`assume x` means that boolean condition `x` is always `true` when program flow passes through this particular node.

Some nodes are *labeled*, similarly to how statements may be labeled in Kotlin.
These are special in the sense that if a fragment mentions a particular labeled node, this node is the same as any other node with this label in the complete CFG.
This is important when building graphs representing loops.

There are two other special kinds of nodes: `unreachable` nodes, signifying unreachable code, and `backedge` nodes, important for some kinds of analyses.

#### Expressions

Simple expressions, like literals and references, do not affect the control-flow of the program in any way and are irrelevant w.r.t. CFG.

##### Function calls and operators

> Note: we do not consider [operator calls][Operator overloading] as being different from function calls, as they are just special types of function calls.
> Henceforth, they are not treated separately.

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

> Note: to simplify the notation, we consider only `if`-expressions with both branches present.
> Any `if`-statement in Kotlin may be trivially turned into such an expression by replacing the missing `else` branch with a `kotlin.Unit` object expression.

```kotlin
if(c) tt else ff
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
:   $2 = eval tt  :  :   $2 = eval ff  :
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

> Important: we only consider `when` expressions having exactly two branches for simplicity.
> A `when` expression with more than two branches may be trivially desugared into a series of nested when expression as follows:
>
> ```kotlin
> when { 
>     <entry1>
>     <entries...>
>     else -> bE
> }
> ```
> 
> is the same as
> 
> ```kotlin
> when { 
>     <entry1>
>     else -> {
>         when {
>             <entries...>
>             else -> bE
>         }
>     }
> }
> ```

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

##### Other expressions

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
       |   $3 = null |               |   $3 = $1.y   |
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
try { a... }
catch (e1: T1) { b1... }
...
catch (eN: TN) { bN... }
finally { c... }
```

```diagram
         +                       +                           +              +
         v                       v                           v              |
+~~~~~~~~+~~~~~~~~+    +~~~~~~~~~+~~~~~~~~+         +~~~~~~~~+~~~~~~~~~+    |
:                 :    :                  :         :                  :    |
:   $1 = eval a   :    :   $1 = eval b1   :   ...   :   $1 = eval bN   :    |
:                 :    :                  :         :                  :    |
+~~~~~~~~+~~~~~~~~+    +~~~~~~~~~+~~~~~~~~+         +~~~~~~~~+~~~~~~~~~+    |
         |                       |                           |              |
         +-----------------+     |    +----------------------+              |
                           v     v    v                                     |
                       +---+-----+----+---+                                 |
                       |                  |                                 |
                       |   $result = $1   |                                 |
                       |                  |                                 |
                       +---------+--------+                                 |
                                 |                                          |
                                 v                                          v
                         +~~~~~~~+~~~~~~+                           +~~~~~~~+~~~~~~+
                         :          (2) :                           :          (1) :
                         :   eval c     :                           :   eval c     :
                         :              :                           :              :
                         +~~~~~~~+~~~~~~+                           +~~~~~~~+~~~~~~+
                                 |
                                 v
```

> Important: in this diagram we consider `finally` block *twice*.
> The (1) block is used when handling the `finally` block and its body.
> The (2) block is used when considering the `finally` block w.r.t. rest of the CFG.

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
         +
         |
         v
+~~~~~~~~+~~~~~~~~+
:                 :
:   $1 = eval b   :
:                 :
+~~~~~~~~+~~~~~~~~+
         |
         v
  +------+-------+
  |              |
  |    a = $1    |
  |              |
  +------+-------+
         |
         v
```

```kotlin
fun f() { body... }
```

```diagram
          +
          |
          v
+~~~~~~~~~+~~~~~~~~~~+
:                    :
:   $1 = eval body   :
:                    :
+~~~~~~~~~~~~~~~~~~~~+
```

```kotlin
class A() { ... }
```

```diagram
+-------+------+
|              |
|     TODO     |
|              |
+-------+------+
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



```kotlin
fun f(x: Int) {
    var y = x
    loop@ while(y != 500) {
        y++
        if(y % 20 == 3) break@loop
    }
}
```

```diagram
                      +------------+
                      |            |
                      |   $1 = x   |                             
                      |            |
                      +------+-----+
                             |                                   
                             v
                      +------+-----+
                      |            |                        
                      |   y = $1   |
                      |            |
                      +------+-----+
                             |                              
                             v
                    +--------+--------+
                    |                 |
                    |   @loop.entry   +<----------------------------+                     
                    |                 |                             |
                    +--------+--------+                             |
                             |                                      |
                             v                                      |
                       +-----+------+                               |
                       |            |                               |
                       |   $2 = y   |                               |
                       |            |                               |
                       +-----+------+                               |
                             |                                      |
                             v                                      |
                      +------+-------+                              |
                      |              |                              |
                      |   $3 = 500   |                              |
                      |              |                              |
                      +------+-------+                              |
                             |                                      |
                             v                                      |
                 +-----------+------------+                         |
                 |                        |                         |
                 |   $4 = $2.equals($3)   |                         |
                 |                        |                         |
                 +----+--------------+----+                         |
                      |              |                              |
                      v              v                              |
           +----------+----+    +----+-----------+                  |
           |               |    |                |                  |
           |   assume $4   |    |   assume !$4   |                  |
           |               |    |                |                  |
           +-------+-------+    +--------+-------+                  |
                   |                     |                          |
                   v                     v                          |
           +-------+-------+     +-------+-------+                  |
           |               |     |               |                  |
           |   $5 = false  |     |   $5 = true   |                  |
           |               |     |               |                  |
           +-------+-------+     +-------+-------+                  |
                   |                     |                          |
                   v                     v                          |
           +-------+--------+    +-------+-------+                  |
           |                |    |               |                  |
           |   assume !$5   |    |   assume $5   |                  |
           |                |    |               |                  |
           +-------+--------+    +-------+-------+                  |
                   |                     |                          |
                   v                     v                          |
           +-------+--------+      +-----+------+                   |
           |                |      |            |                   |
      +--->+   @loop.exit   |      |   $6 = y   |                   |
      |    |                |      |            |                   |
      |    +-------+--------+      +-----+------+                   |
      |            |                     |                          |
      |            v                     v                          |
      |                         +--------+----------+               |
      |                         |                   |               |
      |                         |   $7 = $6.inc()   |               |
      |                         |                   |               |
      |                         +--------+----------+               |
      |                                  |                          |
      |                                  v                          |
      |                            +-----+------+                   |
      |                            |            |                   |
      |                            |   y = $7   |                   |
      |                            |            |                   |
      |                            +-----+------+                   |
      |                                  |                          |
      |                                  v                          |
      |                            +-----+------+                   |
      |                            |            |                   |
      |                            |   $8 = y   |                   |
      |                            |            |                   |
      |                            +-----+------+                   |
      |                                  |                          |
      |                                  v                          |
      |                            +-----+-------+                  |
      |                            |             |                  |
      |                            |   $9 = 20   |                  |
      |                            |             |                  |
      |                            +-----+-------+                  |
      |                                  |                          |
      |                                  v                          |
      |                        +---------+------------+             |
      |                        |                      |             |
      |                        |   $10 = $8.rem($9)   |             |
      |                        |                      |             |
      |                        +---------+------------+             |
      |                                  |                          |
      |                                  v                          |
      |                           +------+------+                   |
      |                           |             |                   |
      |                           |   $11 = 3   |                   |
      |                           |             |                   |
      |                           +------+------+                   |
      |                                  |                          |
      |                                  v                          |
      |                    +-------------+-------------+            |
      |                    |                           |            |
      |                    |   $12 = $10.equals($11)   |            |
      |                    |                           |            |
      |                    +----+----------------+-----+            | 
      |                         |                |                  |
      |                         v                v                  |
      |             +-----------+----+      +----+------------+     |
      |             |                |      |                 |     |
      |             |   assume $12   |      |   assume !$12   |     |
      |             |                |      |                 |     |
      |             +-------+--------+      +--------+--------+     |
      |                     |                        |              |
      +---------------------+                        +--------------+
```

#### kotlin.Nothing` and its influence on the CFG

As discussed in the [type system][`kotlin.Nothing`] section of this specification, `kotlin.Nothing` is an uninhabited type, meaning an instance of this type can never exist at runtime.
For the purposes of control-flow graph (and related analyses) this means as soon as an expression is known statically to have `kotlin.Nothing` type it makes all subsequent code **unreachable**.

> Important: each specific analysis may decide to either use this information or ignore it for a given program.
> If unreachability from `kotlin.Nothing` is used, it can be represented in different ways, e.g., by changing the CFG structure or via [$\killDataFlow$][Preliminary analysis and $\killDataFlow$ instruction] instruction.

### Performing analyses on the control-flow graph

The analyses defined in this document follow the pattern of analyses based on monotone frameworks, which work by modeling abstract program states as elements of lattices and joining these states using standard lattice operations.
Such analysis may achieve limited path sensitivity via the analysis of conditions used in the `assume` nodes.
Further description of monotone frameworks and corresponding analyses goes outside the scope of this document.

#### Preliminary analysis and $\killDataFlow$ instruction

Some analyses described further in this document are based on special instructions called $\killDataFlow(\upsilon)$ where $\upsilon$ is a program variable.
These are not present in the graph representation described above and need to be inferred before such analyses may actually take place.

$\killDataFlow$ inference is based on a standard control-flow analysis with the lattice of natural numbers over "min" and "max" operations.
That is, for every assignable property $x$ an element of this lattice is a natural number $N$, with the least upper bound of two numbers defined as maximum function and the greatest lower bound as minimum function.

> Note: such lattice has 0 as its bottom element and does not have a top element.

We assume the following transfer functions for our analysis.

$$
\begin{alignedat}{2}
&\llbracket \texttt{x = y} \rrbracket(s)
&&= s[x \rightarrow s(x) + 1]
\\
\\
&\llbracket \backedge \rrbracket(s)
&&= \{\star \rightarrow 0 \}
\\
\\
&\llbracket l \rrbracket(s)
&&= \bigsqcup_{p \in predecessor(l)} \llbracket p \rrbracket(s)
\end{alignedat}
$$

After running this analysis, for every backedge $b$ and every variable $x$ present in $s$, if $\exists b_p, b_s: b_p \in predecessors(b) \land b_s \in successors(b) \land \llbracket b_p \rrbracket(x) > \llbracket b_s \rrbracket(x)$, a $\killDataFlow(x)$ instruction must be inserted after $b$.

> Informally: this somewhat complicated condition matches variables which have been assigned to in the loop body w.r.t. this loop's backedge.

> Note: this analysis does involve a possibly **infinite** lattice (a lattice of natural numbers) and may seem to diverge on some graphs.
> However, if we assume that every backedge in an arbitrary CFG is marked with a `backedge` instruction, it is trivial to prove that no number in the lattice will ever exceed the number of assignments (which is **finite**) in the analyzed program as any loop in the graph will contain at least one backedge.

#### Variable initialization analysis

Kotlin allows [non-delegated properties][Property declaration] to not have initializers in their declaration as long as the property is *definitely assigned* before its first usage.
This property is checked by the variable initialization analysis (VIA).
VIA operates on abstract values from a flat lattice of two values $\{\Assigned, \Unassigned\}$.
The analysis itself uses abstract values from a map lattice of all property declarations to their abstract states based on the lattice given above.
The abstract states are propagated in a forward manner using the standard join operation to merge states from different paths.

The CFG nodes relevant to VIA include only property declarations and direct assignments.
Every property declaration adds itself to the domain by setting the $\Unassigned$ value to itself.
Every direct assignment of a property changes the value for this property to $\Assigned$.

The results of the analysis are interpreted as follows. 
For every property, any usage of the said property in any statement is a compile-time error unless the abstract state of this property at this statement is $\Assigned$.
For every immutable property (declared using `val` keyword), any assignment to this property is a compile-time error unless the abstract state of this property is $\Unassigned$.

Let's consider the following example:

`val x: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow \Unassigned, \star \rightarrow \bot \}$    
`var y: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow \Unassigned, \mathtt{y} \rightarrow \Unassigned, \star \rightarrow \bot \}$    
`if(c) {       //`{.kotlin}    
`    x = 40    //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow \Assigned, \mathtt{y} \rightarrow \Unassigned, \star \rightarrow \bot \}$    
`    y = 4     //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow \Assigned, \mathtt{y} \rightarrow \Assigned, \star \rightarrow \bot \}$    
`} else {      //`{.kotlin}    
`    x = 20    //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow \Assigned, \mathtt{y} \rightarrow \Unassigned, \star \rightarrow \bot \}$    
`}             //`{.kotlin}         $\{ \mathtt{x} \rightarrow \Assigned, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$    
`y = 5         //`{.kotlin}         $\{ \mathtt{x} \rightarrow \Assigned, \mathtt{y} \rightarrow \Assigned, \star \rightarrow \bot \}$    
`val z = x + y //`{.kotlin}         $\{ \mathtt{x} \rightarrow \Assigned, \mathtt{y} \rightarrow \Assigned, \mathtt{z} \rightarrow \Assigned, \star \rightarrow \bot \}$    

TODO(somehow fix these atrocities for indentation in inline code)

There are no incorrect states in this example, so the code is correct.

Let's consider another example:

`val x: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow \Unassigned, \star \rightarrow \bot \}$    
`var y: Int    //`{.kotlin}         $\{ \mathtt{x} \rightarrow \Unassigned, \mathtt{y} \rightarrow \Unassigned, \star \rightarrow \bot \}$    
`while(c) {    //`{.kotlin}         $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$ Error!    
`    x = 40    //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$    
`    y = 4     //`{.kotlin .indent} $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$    
`}             //`{.kotlin}    
`val z = x + y //`{.kotlin}         $\{ \mathtt{x} \rightarrow \top, \mathtt{y} \rightarrow \top, \star \rightarrow \bot \}$ More errors!    

In this example, the state of both properties at line 3 is $\top$, as it is the least upper bound of the states from lines 5 and 2 (from the `while` loop), which, after a rather trivial fixed point calculation, is derived to be $\top$.
This is a compile-time error in the case of `x`, because one cannot reassign an immutable property.

At line 7 there is another compile-time error when both properties are used, because there are paths in the CFG which reach line 7 when the properties have not been assigned (the case when the `while` loop was skipped).

TODO(draw graphs for all these?)

#### Smart casting analysis

See the [corresponding section][Smart casts] for details.

### References

1. Frances E. Allen. "Control flow analysis." ACM SIGPLAN Notices, 1970.
2. Flemming Nielson, Hanne R. Nielson, and Chris Hankin. "Principles of program analysis." Springer, 2015.
