class Foo(@setann private val field: Int) {}

class Foo(@`setann` private val field: Int) {}

class Foo(@`set ` val field: Int) {}

class Foo {
    @ann @setann var field: Int = 10
}

class Foo(@setann @ann protected val field: Int) {}

class Foo {
    @ann @setann var field: Int = 10
}

class Foo {
    @ann @`setann` var field: Int = 10
}

class Foo(@`set-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`set)` protected val field: Int) {}
