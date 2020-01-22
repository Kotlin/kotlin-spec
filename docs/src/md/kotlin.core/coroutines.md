## Coroutines

TODO([Kotlin 1.3+, Experimental] Everything: state machine, context, etc.)
TODO([Kotlin 1.3+, Experimental] Update w.r.t. structured concurrency)

> Note: as of Kotlin 1.2, the support for coroutines is experimental.

### Suspending functions

Any [function declaration][Function declaration] or [getter/setter declaration][Getters and setters] in Kotlin may be marked *suspending* using the special `suspend` modifier.
There are no additional restrictions: regular functions, extension functions, top-level and member functions, anonymous and named functions, all these may be marked as suspending.

A function type for a particular value may also be marked suspending using the `suspend` modifier.

TODO(`suspend val`?)

A suspending function is different from non-suspending functions by potentially having zero or more *suspension points* --- statements in its body that may pause the function execution to be resumed at a later moment in time.
The main source of suspension points are calls to other suspending functions which represent possible suspension points.

Suspension points are very important because at these points another function may start being executed in the same flow of execution, leading to potential changes in the shared state in the middle of a function execution flow.

Non-suspending functions may not call suspending functions directly, as they do not support suspension points.
Suspending functions may call non-suspending functions without any limitations; such calls do not create suspension points.

> Important: an exception to this rule are non-suspending inlined lambda parameters: if the higher-order function invoking such a lambda is called from a suspending function, this lambda is allowed to also have suspension points and call other suspending functions.

> Note: suspending functions interleaving each other in this manner are not dissimilar to how functions from different threads interact on platforms that support multi-threading.
> There are, however, several key differences.
> First, suspending functions may pause only at suspension points, i.e., they cannot be paused at arbitrary execution point.
> Second, this interleaving may happen on one platform thread.
>
> In a multi-threaded environment suspension functions may also be interleaved by the platform-dependent concurrent execution, independent of the interleaving of coroutines.

The implementation of suspending functions is platform-dependent.
Please refer to the platform documentation for details.

### Coroutines

A *coroutine* is a concept similar to a thread in traditional concurrent programming, but based on *cooperating multitasking*, e.g., the switching between different execution contexts is done by the coroutines themselves rather than an operating system or a virtual machine.
Coroutines run suspending functions and can only switch contexts at suspension points.

TODO(Everything)

### Coroutine intrinsics

TODO(Do we need them?)
