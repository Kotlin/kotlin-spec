class Foo(@`property` private val field: Int) {}

class Foo {
    @ann @`property` var field: Int = 10
}

class Foo(@`property` @ann protected val field: Int) {}

class Foo {
    @ann @`property` var field: Int = 10
}

class Foo(@ann @`property` val field: Int) {}

class Foo(@`property` @ann val field: Int) {}
