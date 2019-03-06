class Foo(@fieldann private val field: Int) {}

class Foo(@`fieldann` private val field: Int) {}

class Foo(@`field ` val field: Int) {}

class Foo {
    @ann @fieldann var field: Int = 10
}

class Foo(@fieldann @ann protected val field: Int) {}

class Foo {
    @ann @fieldann var field: Int = 10
}

class Foo {
    @ann @`fieldann` var field: Int = 10
}

class Foo(@`field-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`field)` protected val field: Int) {}
