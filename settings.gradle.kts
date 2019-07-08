rootProject.name = "kotlin-spec"

val withGrammarProject: String? by settings

if (withGrammarProject?.toBoolean() != false) {
    include("grammar")
}
