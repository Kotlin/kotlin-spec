fun main() = {
    return!! // Expecting an element
    return++ // Expecting an element
    return() // Expecting an element
    return::class // Unsupported [Class literals with empty left hand side are not yet supported]
}