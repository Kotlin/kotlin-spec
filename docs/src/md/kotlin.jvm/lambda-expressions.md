## Lambda expressions

TODO(Everything)

> Note: creating excessive lambda instances may potentially lead to logical memory leaks.
> To avoid this problem, lambda expressions which do not capture any properties are implemented as singleton classes.
> Thus, all instances of such lambdas actually reference the same singleton object.
> ```kotlin
> fun example() {
>     val lambdaProvider = {
>         { Unit } // stateless lambda
>     }
>     val lambda1 = lambdaProvider()
>     val lambda2 = lambdaProvider()
>     
>     // Both references are the same
>     assert(lambda1 === lambda2)
> }
> ```
