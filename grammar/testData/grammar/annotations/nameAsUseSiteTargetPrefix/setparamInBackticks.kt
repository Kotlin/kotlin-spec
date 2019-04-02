class Foo(@`setparam` private val field: Int) {}

class Foo {
    @ann @`setparam` var field: Int = 10
}

class Foo(@`setparam` @ann protected val field: Int) {}

class Foo {
    @ann @`setparam` var field: Int = 10
}

class Foo(@ann @`setparam` val field: Int) {}

class Foo(@`setparam` @ann val field: Int) {}
