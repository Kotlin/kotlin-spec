class Foo(@setparamann private val field: Int) {}

class Foo(@`setparamann` private val field: Int) {}

class Foo(@`setparam ` val field: Int) {}

class Foo {
    @ann @setparamann var field: Int = 10
}

class Foo(@setparamann @ann protected val field: Int) {}

class Foo {
    @ann @setparamann var field: Int = 10
}

class Foo {
    @ann @`setparamann` var field: Int = 10
}

class Foo(@`setparam-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`setparam)` protected val field: Int) {}
