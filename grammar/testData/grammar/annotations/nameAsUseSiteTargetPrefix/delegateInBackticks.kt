class Foo(@`delegate` private val field: Int) {}

class Foo {
    @ann @`delegate` var field: Int = 10
}

class Foo(@`delegate` @ann protected val field: Int) {}

class Foo {
    @ann @`delegate` var field: Int = 10
}

class Foo(@ann @`delegate` val field: Int) {}

class Foo(@`delegate` @ann val field: Int) {}
