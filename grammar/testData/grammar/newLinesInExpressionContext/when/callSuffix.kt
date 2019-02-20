fun foo() = true

fun test() = when {
    foo
    () -> 1
    else -> 2
}