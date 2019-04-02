class Foo(@field private val field: Int) {}

class Foo {
    @ann @field var field: Int = 10
}

class Foo(@field @ann protected val field: Int) {}

class Foo {
    @ann @field var field: Int = 10
}

class Foo {
    @field @ann var field: Int = 10
}
