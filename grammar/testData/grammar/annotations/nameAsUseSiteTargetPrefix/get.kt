class Foo(@get private val field: Int) {}

class Foo {
    @ann @get var field: Int = 10
}

class Foo(@get @ann protected val field: Int) {}

class Foo {
    @ann @get var field: Int = 10
}

class Foo {
    @get @ann var field: Int = 10
}
