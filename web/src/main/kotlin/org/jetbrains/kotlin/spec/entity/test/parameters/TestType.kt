package org.jetbrains.kotlin.spec.entity.test.parameters

enum class TestType(val shortName: String) {
    Positive("pos"),
    Negative("neg");

    override fun toString(): String {
        return name.lowercase()
    }

    companion object {
        fun getByShortName(shortName: String) =
                values().find { it.shortName == shortName }
                        ?: throw IllegalArgumentException("TestType $shortName is not found")

    }
}