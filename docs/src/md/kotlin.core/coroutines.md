## Asynchronous programming with coroutines

TODO([Kotlin 1.3+, Experimental] Everything: state machine, context, etc.)
TODO([Kotlin 1.3+, Experimental] Update w.r.t. structured concurrency)

### Suspending functions

Most [functions][Function declaration] in Kotlin may be marked *suspending* using the special `suspend` modifier.
There are almost no additional restrictions: regular functions, extension functions, top-level functions, local functions, lambda literals, all these may be suspending functions.

> Note: the following functions and function values cannot be marked as suspending.
> 
> * [anonymous function declarations];
> * [constructors][Class declaration];
> * [property getter/setters][Getters and setters];
> * [delegation-related operator functions][Delegated property declaration].

> Note: platform-specific implementations may extend the restrictions on which kinds of functions may be suspending.

Suspending functions have a [suspending function type][Suspending function types], also marked using the `suspend` modifier.

TODO(`suspend val`?)

A suspending function is different from non-suspending functions by potentially having zero or more *suspension points* --- statements in its body which may pause the function execution to be resumed at a later moment in time.
The main source of suspension points are calls to other suspending functions which represent possible suspension points.

> Note: suspension points are important because at these points another function may start in the same flow of execution, leading to potential changes in the shared state.

Non-suspending functions may not call suspending functions directly, as they do not support suspension points.
Suspending functions may call non-suspending functions without any limitations; such calls do not create suspension points.

> Important: an exception to this rule are non-suspending inlined lambda parameters: if the higher-order function invoking such a lambda is called from a suspending function, this lambda is allowed to also have suspension points and call other suspending functions.

> Note: suspending functions interleaving each other in this manner are not dissimilar to how functions from different threads interact on platforms with multi-threading support.
> There are, however, several key differences.
> First, suspending functions may pause only at suspension points, i.e., they cannot be paused at an arbitrary execution point.
> Second, this interleaving may happen on a single platform thread.
>
> In a multi-threaded environment suspending functions may also be interleaved by the platform-dependent concurrent execution, independent of the interleaving of coroutines.

The implementation of suspending functions is platform-dependent.
Please refer to the platform documentation for details.

### Coroutines

A *coroutine* is a concept similar to a thread in traditional concurrent programming, but based on *cooperative multitasking*, e.g., the switching between different execution contexts is done by the coroutines themselves rather than the operating system or a virtual machine.

In Kotlin, coroutines are used to implement [suspending functions] and can switch contexts only at suspension points.

A call to a suspending function creates and starts a coroutine.
As one can call a suspending function only from another suspending function, we need a way to bootstrap this process from a non-suspending context.

> Note: this is required as most platforms are unaware of coroutines or suspending functions, and do not provide a suspending entry point.
> However, a Kotlin compiler may elect to provide a suspending entry point on a specific platform.

One of the ways of starting suspending function from a non-suspending context is via a *coroutine builder*: a non-suspending function which takes a suspending function type argument (e.g., a suspending lambda literal) and handles the coroutine lifecycle.

The implementation of coroutines is platform-dependent.
Please refer to the platform documentation for details.

TODO(Coroutine support in the standard library?)
