class Foo(@param private val field: Int) {}

class Foo {
    @ann @param var field: Int = 10
}

class Foo(@param @ann protected val field: Int) {}

class Foo {
    @ann @param var field: Int = 10
}

class Foo {
    @param @ann var field: Int = 10
}
