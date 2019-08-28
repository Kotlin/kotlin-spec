rootProject.name = "kotlin-spec"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { setUrl("https://jcenter.bintray.com/") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlin", "kotlin2js" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
                "kotlin-dce-js" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
                "org.jetbrains.kotlin.frontend" -> useModule("org.jetbrains.kotlin:kotlin-frontend-plugin:${requested.version}")
                "at.phatbl.shellexec" -> useModule("gradle.plugin.at.phatbl:shellexec:${requested.version}")
            }
        }
    }
}

val withGrammarProject: String? by settings

include("docs")
include("front-end")

if (withGrammarProject?.toBoolean() != false) {
    include("grammar")
}
