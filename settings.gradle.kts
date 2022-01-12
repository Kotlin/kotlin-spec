rootProject.name = "kotlin-spec"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "at.phatbl.shellexec" -> useModule("gradle.plugin.at.phatbl:shellexec:${requested.version}")
            }
        }
    }
}

val withGrammarProject: String? by settings

include("docs")
include("docs:pdf")
include("docs:pdfSections")
include("docs:html")
include("docs:htmlSections")
include("web")

if (withGrammarProject?.toBoolean() != false) {
    include("grammar")
}
