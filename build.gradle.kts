import at.phatbl.shellexec.ShellExec

plugins {
    id("at.phatbl.shellexec") version "1.1.3"
}

val htmlBuildDir = "$buildDir/spec/html"
val pdfBuildDir = "$buildDir/spec/pdf"
val resourcesBuildDir = "$htmlBuildDir/resources"
val jsBuildDir = "$resourcesBuildDir/js"

tasks.create<Copy>("copyStatic") {
    group = "internal"

    from("$projectDir/web/resources")
    into(resourcesBuildDir)
}

tasks.create<Copy>("copyBuiltJs") {
    group = "internal"

    mustRunAfter("web:webpack-bundle")

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
    dependsOn("web:webpack-bundle")
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

    finalizedBy("web:clean", "docs:clean")
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

    finalizedBy("web:clean", "docs:clean")
}

tasks.create<Delete>("clean") {
    group = "build"

    dependsOn("web:clean")
    dependsOn("docs:clean")

    delete("$projectDir/build")
}

tasks.create<ShellExec>("syncGrammarWithKotlinGrammarApache2Repo") {
    group = "internal"
    command = """echo -e Run the following command: git checkout release \&\& git subtree push --prefix grammar/src/main/antlr git@github.com:Kotlin/kotlin-grammar-apache2 release"""
}
