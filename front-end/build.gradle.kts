import org.jetbrains.kotlin.gradle.frontend.KotlinFrontendExtension
import org.jetbrains.kotlin.gradle.frontend.npm.NpmInstallTask
import org.jetbrains.kotlin.gradle.frontend.npm.UnpackGradleDependenciesTask
import org.jetbrains.kotlin.gradle.frontend.webpack.WebPackExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    java
    id("kotlin2js") version "1.3.41"
    id("kotlin-dce-js") version "1.3.41"
    id("org.jetbrains.kotlin.frontend") version "0.0.45"
}

group = "org.jetbrains.kotlin.spec"
version = "0.1"

val buildMode = "development" // production | development

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://kotlin.bintray.com/js-externals") }
}

dependencies {
    implementation(kotlin("stdlib-js"))
    compileOnly("kotlin.js.externals:kotlin-js-jquery:2.0.0-0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.12.0")
}

java.sourceSets {
    "main" {
        java.srcDir("src/main/kotlin")
    }
}

tasks.create<Copy>("copyKatex") {
    group = "internal"

    from("$buildDir/node_modules/katex/dist")
    into("$buildDir/js/katex".also { File(it).mkdirs() })
}

tasks.withType<UnpackGradleDependenciesTask> {
    enabled = false
}

tasks.withType<NpmInstallTask> {
    doFirst {
        File("$projectDir/build/.unpack.txt").apply {
            if (exists()) writeText("") else createNewFile()
        }
    }
}

tasks.create<Copy>("copyImportedLibraries") {
    group = "internal"

    from("$buildDir/node_modules_imported")
    into("$buildDir/node_modules")
}

tasks.create<Delete>("removeUnnecessaryImportedLibraries") {
    group = "internal"

    delete("$buildDir/node_modules_imported/@kotlin-externals")
}

tasks.withType<Kotlin2JsCompile> {
    dependsOn("removeUnnecessaryImportedLibraries").mustRunAfter("npm-configure")
    dependsOn("copyKatex").mustRunAfter("npm-install")
    dependsOn("copyImportedLibraries").mustRunAfter("npm-install")

    kotlinOptions {
        moduleKind = "amd"
        kotlinOptions.sourceMap = true
    }
}

fun KotlinFrontendExtension.webpack(configure: WebPackExtension.() -> Unit) {
    bundle("webpack", delegateClosureOf(configure))
}

kotlinFrontend {
    sourceMaps = true

    npm {
        dependency("katex", "0.11.1")
        dependency("jquery")
        dependency("kotlin-playground")
    }

    webpack {
        webpackConfigFile = "webpack.$buildMode.js"
        mode = buildMode
    }
}
