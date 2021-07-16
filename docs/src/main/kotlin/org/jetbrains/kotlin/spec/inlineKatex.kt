package org.jetbrains.kotlin.spec

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
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

        val npx = "../build/js/node_modules/.bin/katex"
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

private object DoNothing: PandocVisitor() {
    override fun visit(doc: Pandoc): Pandoc = doc
}

private object InlineKatexFilter : CliktCommand() {
    val format: Format by argument("Pandoc output format").convert { Format(it) }
    val disableStaticMath by option().flag("--enable-static-math")

    val isHTML get() = format.isHTML()

    override fun run() {
        if(isHTML && !disableStaticMath) makeFilter(InlineKatex)
        else makeFilter(DoNothing)
    }
}

fun main(args: Array<String>) = InlineKatexFilter.main(args)
