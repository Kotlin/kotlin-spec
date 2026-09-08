import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput

plugins {
    kotlin("multiplatform")
}

group = "org.jetbrains.kotlin.spec"
version = "0.1"

val buildMode = findProperty("mode")?.toString() ?: "production" // production | development

repositories {
    mavenCentral()
}

tasks.register<Copy>("copyKatex") {
    dependsOn(":kotlinNpmInstall")
    group = "internal"

    from("$rootDir/build/js/node_modules/katex/dist")
    into("${layout.buildDirectory.get()}/js/katex".also { File(it).mkdirs() })
}

kotlin {
    js {
        compilerOptions {
            moduleName.set("main")
            moduleKind.set(JsModuleKind.MODULE_AMD)
        }
        compilations.all {
            packageJson {
                dependencies["jquery"] = "2.2.4"
            }
        }
        binaries.executable()
        browser {
            webpackTask(Action {
                dependsOn("copyKatex")

                output.apply {
                    libraryTarget = KotlinWebpackOutput.Target.AMD
                    library = "main"
                }

                outputDirectory = file("${layout.buildDirectory.get()}/js")
                mainOutputFileName = "main.js"

                mode = Mode.valueOf(buildMode.uppercase())
                sourceMaps = (mode == Mode.DEVELOPMENT)
            })
        }
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDir("src/main/kotlin")
            dependencies {
                implementation(npm("katex", "0.16.10"))
                implementation(npm("jquery", "2.2.4"))
                implementation(npm("kotlin-playground", "1.34.0"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            }
        }
    }
}
