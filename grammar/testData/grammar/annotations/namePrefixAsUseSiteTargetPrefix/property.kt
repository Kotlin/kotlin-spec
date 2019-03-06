class Foo(@propertyann private val field: Int) {}

class Foo(@`propertyann` private val field: Int) {}

class Foo(@`property ` val field: Int) {}

class Foo {
    @ann @propertyann var field: Int = 10
}

class Foo(@propertyann @ann protected val field: Int) {}

class Foo {
    @ann @propertyann var field: Int = 10
}

class Foo {
    @ann @`propertyann` var field: Int = 10
}

class Foo(@`property-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`property)` protected val field: Int) {}
