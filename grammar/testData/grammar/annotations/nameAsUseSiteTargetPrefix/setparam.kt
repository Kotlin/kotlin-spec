class Foo(@setparam private val field: Int) {}

class Foo {
    @ann @setparam var field: Int = 10
}

class Foo(@setparam @ann protected val field: Int) {}

class Foo {
    @ann @setparam var field: Int = 10
}

class Foo {
    @setparam @ann var field: Int = 10
}
