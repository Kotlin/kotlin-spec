class Foo(@getann private val field: Int) {}

class Foo(@`getann` private val field: Int) {}

class Foo(@`get ` val field: Int) {}

class Foo {
    @ann @getann var field: Int = 10
}

class Foo(@getann @ann protected val field: Int) {}

class Foo {
    @ann @getann var field: Int = 10
}

class Foo {
    @ann @`getann` var field: Int = 10
}

class Foo(@`get-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`get)` protected val field: Int) {}
