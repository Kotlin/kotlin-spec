class Foo(@receiverann private val field: Int) {}

class Foo(@`receiverann` private val field: Int) {}

class Foo(@`receiver ` val field: Int) {}

class Foo {
    @ann @receiverann var field: Int = 10
}

class Foo(@receiverann @ann protected val field: Int) {}

class Foo {
    @ann @receiverann var field: Int = 10
}

class Foo {
    @ann @`receiverann` var field: Int = 10
}

class Foo(@`receiver-` @`ann` protected val field: Int) {}

class Foo(@`ann` @`receiver)` protected val field: Int) {}
