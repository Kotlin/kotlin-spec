package org.jetbrains.kotlin.spec.utils

enum class TestArea(val path: String, val content: String, val contentPath: String) {
    DIAGNOSTICS("diagnostics", "content1", "path1"),
    CODEGEN_BOX("codegen/box", "content2", "path2")
}