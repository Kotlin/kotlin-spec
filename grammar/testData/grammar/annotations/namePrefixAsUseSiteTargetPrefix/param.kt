class Foo(@paramann private val field: Int) {}

class Foo(@`paramann` private val field: Int) {}

class Foo(@`param ` val field: Int) {}

class Foo {
    @ann @paramann var field: Int = 10
}

class Foo(@paramann @ann protected val field: Int) {}

class Foo {
    @ann @paramann var field: Int = 10
}

class Foo {
    @ann @`paramann` var field: Int = 10
}

class Foo(@`param-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`param)` protected val field: Int) {}
