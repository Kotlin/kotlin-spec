fun test(x: (Any) -> (Any) -> (Any) -> (Any) -> (Any) -> Unit) {}
fun main() = test(fun (a) = fun (b) = fun (vararg c) = fun (@Anno d) = fun (@Anno vararg e))