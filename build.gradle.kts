import at.phatbl.shellexec.ShellExec

plugins {
    kotlin("jvm") version "1.9.23" apply false
    id("at.phatbl.shellexec") version "1.5.2"
}

val htmlBuildDir = "${layout.buildDirectory.get()}/spec/html"
val pdfBuildDir = "${layout.buildDirectory.get()}/spec/pdf"
val resourcesBuildDir = "$htmlBuildDir/resources"
val jsBuildDir = "$resourcesBuildDir/js"

tasks.create<Copy>("copyStatic") {
    group = "internal"

    from("$projectDir/web/resources")
    into(resourcesBuildDir)
}

tasks.create<Copy>("copyBuiltJs") {
    group = "internal"

    mustRunAfter("web:build")

    from("$projectDir/web/build/js")
    into(jsBuildDir)
}

tasks.create<Copy>("copyHtml") {
    group = "internal"

    mustRunAfter("docs:buildHtml", "docs:buildHtmlBySections")

    from("$projectDir/docs/build/spec/html")
    into(htmlBuildDir)
}

tasks.create<Copy>("copyPdf") {
    group = "internal"

    mustRunAfter("docs:buildPdf", "docs:buildPdfBySections")

    from("$projectDir/docs/build/spec/pdf")
    into(pdfBuildDir)
}

tasks.create<Copy>("copyStubIndexToRedirectToIntroduction") {
    group = "internal"

    mustRunAfter("docs:buildPdf", "docs:buildPdfBySections")

    from("$projectDir/web/resources/html")
    into(htmlBuildDir)
}

tasks.create("buildJs") {
    group = "internal"

    dependsOn("copyStatic")
    dependsOn("web:build")
    dependsOn("copyBuiltJs")

    doFirst {
        File(jsBuildDir).mkdirs()
    }
}

tasks.create("buildWeb") {
    group = "build"

    dependsOn("docs:buildHtml")
    dependsOn("docs:buildHtmlBySections")
    dependsOn("copyHtml")
    dependsOn("buildJs")
}

tasks.create("buildWebFullOnly") {
    group = "build"

    dependsOn("docs:buildHtml")
    dependsOn("copyHtml")
    dependsOn("buildJs")
}

tasks.create("buildWebBySectionsOnly") {
    group = "build"

    dependsOn("docs:buildHtmlBySections")
    dependsOn("copyHtml")
    dependsOn("buildJs")
    dependsOn("copyStubIndexToRedirectToIntroduction")
}

tasks.create("buildPdf") {
    group = "build"

    dependsOn("docs:buildPdf")
    dependsOn("docs:buildPdfBySections")
    dependsOn("copyPdf")
}

tasks.create<ShellExec>("syncGrammarWithKotlinGrammarApache2Repo") {
    group = "internal"
    command = """echo -e "Run the following commands: git checkout release; ...; git subtree push --prefix grammar/src/main/antlr git@github.com:Kotlin/kotlin-grammar-apache2 release""""
}
