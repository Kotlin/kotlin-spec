plugins {
    id("at.phatbl.shellexec") version "1.1.3"
}

val htmlBuildDir = "$buildDir/spec/html"
val pdfBuildDir = "$buildDir/spec/pdf"
val resourcesBuildDir = "$htmlBuildDir/resources"
val jsBuildDir = "$resourcesBuildDir/js"

tasks.create<Copy>("copyStatic") {
    from("$projectDir/front-end/resources")
    into(resourcesBuildDir)
}

tasks.create<Copy>("copyBuiltJs") {
    from("$projectDir/front-end/build/js")
    into(jsBuildDir)
}

tasks.create<Copy>("copyHtml") {
    from("$projectDir/docs/build/spec/html")
    into(htmlBuildDir)
}

tasks.create<Copy>("copyPdf") {
    from("$projectDir/docs/build/spec/pdf")
    into(pdfBuildDir)
}

tasks.create<Task>("buildJs") {
    dependsOn("copyStatic")
    dependsOn("front-end:webpack-bundle")
    dependsOn("copyBuiltJs").mustRunAfter("front-end:webpack-bundle")

    doFirst {
        File(jsBuildDir).mkdirs()
    }
}

tasks.create<Copy>("buildWeb") {
    dependsOn("docs:buildHtml")
    dependsOn("docs:buildHtmlBySections")
    dependsOn("copyHtml").mustRunAfter("docs:buildHtml", "docs:buildHtmlBySections")
    dependsOn("buildJs")
}

tasks.create<Copy>("buildPdf") {
    dependsOn("docs:buildPdf")
    dependsOn("docs:buildPdfBySections")
    dependsOn("copyPdf").mustRunAfter("docs:buildPdf", "docs:buildPdfBySections")
}
