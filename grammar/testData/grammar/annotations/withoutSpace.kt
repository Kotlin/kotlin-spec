class Foo(@`anno`@anno private val field: Int) {}

class Foo {
    @[ann]@`anno` var field: Int = 10
}

class Foo(@`anno`@[ann anno] protected val field: Int) {}

class Foo {
    @[`ann` `ann`]@[`test`] var field: Int = 10
}

class Foo(@[`test`]@[test] val field: Int) {}

class Foo(@[test]@test@[`test`]@`test` val field: Int) {}
