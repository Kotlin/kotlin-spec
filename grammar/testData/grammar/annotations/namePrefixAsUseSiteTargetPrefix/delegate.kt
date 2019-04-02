class Foo(@delegateann private val field: Int) {}

class Foo(@`delegateann` private val field: Int) {}

class Foo(@`delegate ` val field: Int) {}

class Foo {
    @ann @delegateann var field: Int = 10
}

class Foo(@delegateann @ann protected val field: Int) {}

class Foo {
    @ann @delegateann var field: Int = 10
}

class Foo {
    @ann @`delegateann` var field: Int = 10
}

class Foo(@`delegate-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`delegate)` protected val field: Int) {}
