class Foo(@`receiver` private val field: Int) {}

class Foo {
    @ann @`receiver` var field: Int = 10
}

class Foo(@`receiver` @ann protected val field: Int) {}

class Foo {
    @ann @`receiver` var field: Int = 10
}

class Foo(@ann @`receiver` val field: Int) {}

class Foo(@`receiver` @ann val field: Int) {}
