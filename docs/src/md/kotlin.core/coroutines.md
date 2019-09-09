## Coroutines

TODO(everything: state machine, context, etc.)

> Note: as of Kotlin 1.2, the support for coroutines is experimental

### Suspending functions

Any [function declaration][Function declaration] or a getter/setter declaration in Kotlin may be marked *suspending* using the special `suspend` modifier.
A function type for a particular value may also be marked suspending using the `suspend` modifier.
Both normal functions and extension functions, top-level and member, anonymous and named, may be marked as suspending.

TODO(`suspend val`?)

A suspending function is different from normal functions by potentially having zero or more *suspension points* --- statements in its body that may pause the function execution to be resumed at a later moment in time.
Each call to another suspending function inside the body of a suspending function is a suspension point.
Suspension points are important because at such a point another function may start being executed in the same flow of execution, leading to potential changes in shared state in the middle of a function execution flow.

Normal functions may not call suspending functions directly, meaning they do not have suspension points.
Suspending functions may call normal functions without any limitation, such calls are not suspension points.
The exception for this rule are inlined lambda parameters that, if the inlined higher-order function invoking them is called from a suspending function, may also have suspension points and call other suspending functions.

> Note: suspending functions interleaving each other in this manner is not dissimilar to how functions from different threads interact on platforms that support multithreading.
> There are, however, several key differences.
> First, suspending functions may pause only at suspension points, this process cannot be paused at arbitrary execution point.
> Second, this may happen in one platform thread.
> In multithreaded environment, suspension functions may also be interleaved by the usual rules of concurrent execution on this platform, independent of the interleaving of coroutines.

The implementation of suspending functions on a particular platform is platform-dependent.
Please refer to the platform documentation for details.

### Coroutines

A *coroutine* is a concept similar to a thread of concurrent programming, but representing cooperating multitasking, e.g. the switching between different contexts of execution is done by the coroutines themselves rather than operating system or language virtual machine.
Coroutines run suspending functions and can only switch contexts at suspension points.

TODO(Write this)

### Coroutine intrinsics

TODO(Do we need them?)
