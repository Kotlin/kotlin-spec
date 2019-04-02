class Foo(@set private val field: Int) {}

class Foo {
    @ann @set var field: Int = 10
}

class Foo(@set @ann protected val field: Int) {}

class Foo {
    @ann @set var field: Int = 10
}

class Foo {
    @set @ann var field: Int = 10
}
