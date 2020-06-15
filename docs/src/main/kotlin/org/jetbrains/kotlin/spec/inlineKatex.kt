package org.jetbrains.kotlin.spec

import com.zaxxer.nuprocess.NuAbstractProcessHandler
import com.zaxxer.nuprocess.NuProcess
import com.zaxxer.nuprocess.NuProcessBuilder
import com.zaxxer.nuprocess.NuProcessHandler
import ru.spbstu.pandoc.*
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object InlineKatex : PandocVisitor() {
    val katexCache: MutableMap<String, Inline> = mutableMapOf()

    fun katex(text: String, display: Boolean): Inline {
        val stdOut = StringBuilder()

        val exitCode = CompletableFuture<Int>()

        val processListener = object : NuAbstractProcessHandler() {
            override fun onStderr(buffer: ByteBuffer, closed: Boolean) {
                System.err.append(Charsets.UTF_8.decode(buffer))
                super.onStderr(buffer, closed)
            }

            override fun onStdout(buffer: ByteBuffer, closed: Boolean) {
                stdOut.append(Charsets.UTF_8.decode(buffer))
                super.onStdout(buffer, closed)
            }

            override fun onExit(statusCode: Int) {
                exitCode.complete(statusCode)
            }
        }

        val npx = "../front-end/build/node_modules/.bin/katex"
        val katex = if(display) NuProcessBuilder(processListener, npx, "--display-mode")
        else NuProcessBuilder(processListener, npx)

        with(katex.start()) {
            writeStdin(Charsets.UTF_8.encode(text))
            closeStdin(false)
            exitCode.get()

            return Inline.RawInline(Format.HTML, "$stdOut")
        }
    }

    override fun visit(i: Inline.Math): Inline {
        val display = i.type == MathType.DisplayMath

        return katexCache.getOrPut(i.text) { katex(i.text, display) }
    }
}

object DoNothing : PandocVisitor() {
    override fun visit(doc: Pandoc) = doc
}

fun main(args: Array<String>) =
        if(Format(args[0]).isHTML()) makeFilter(InlineKatex) else makeFilter(DoNothing)
