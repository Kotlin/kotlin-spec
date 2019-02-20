fun test(): Int {
    try {
        return 1
    } catch (e: @My Exception) {
        return -1
    }
}