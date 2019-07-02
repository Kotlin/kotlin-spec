## Lambda expressions

On the JVM, there is an optimization: to avoid potential memory leaks, 
lambda expressions that do not capture any properties are created as singleton classes.
Thus, all returned instances of such a lambda actually are references to a single object.

>Example:
> ```kotlin
> fun example() {
>     val lambdaProvider = {
>         { Unit }  // returns a stateless lambda
>     }
>     val lambda1 = lambdaProvider.invoke()
>     val lambda2 = lambdaProvider.invoke() // the same exact instance as `lambda1`
> }
> ```  