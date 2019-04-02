fun case_1(x: Int?, y: Int) {
    if (@Anno !(x == y)) {
        x.inv()
    }
}

val x = @Anno !y