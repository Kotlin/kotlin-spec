import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput

plugins {
    kotlin("js")
}

group = "org.jetbrains.kotlin.spec"
version = "0.1"

val buildMode = findProperty("mode")?.toString() ?: "production" // production | development

repositories {
    mavenCentral()
    jcenter()
}

tasks.create<Copy>("copyKatex") {
    dependsOn(":kotlinNpmInstall")
    group = "internal"

    from("$rootDir/build/js/node_modules/katex/dist")
    into("$buildDir/js/katex".also { File(it).mkdirs() })
}

kotlin {
    js {
        moduleName = "main"
        compilations.all {
            packageJson {
                dependencies["jquery"] = "2.2.4"
            }

            kotlinOptions {
                moduleKind = "amd"
            }
        }
        browser {
            webpackTask {
                dependsOn("copyKatex")


                output.apply {
                    libraryTarget = KotlinWebpackOutput.Target.AMD_REQUIRE
                    library = "main"
                }

                destinationDirectory = file("${buildDir}/js")
                outputFileName = "main.js"

                mode = Mode.valueOf(buildMode.toUpperCase())
                sourceMaps = (mode == Mode.DEVELOPMENT)
            }
        }
    }

    sourceSets {
        main {
            dependencies {
                compileOnly("kotlin.js.externals:kotlin-js-jquery:2.0.0-0")
                implementation(npm("katex", "0.11.1"))
                implementation(npm("jquery", "2.2.4"))
                implementation(npm("kotlin-playground", "1.24.2"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.12.0")
            }
        }
    }
}
