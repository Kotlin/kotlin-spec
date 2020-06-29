fun main() {
    val x = null as Int as Int
    val x = null as Int as? Int
    val x = null as? Int as Int
    val x = null as? Int as? Int
    val x = (null as? Int is Int) as Int is Int
    val x = (null as Int is Int) as? Int is Int
    val x = (null is Int) as Int as? Int is Int
    val x = (null is Int) as? Int as Int is Int
}