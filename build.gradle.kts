plugins {
    id("at.phatbl.shellexec") version "1.1.3"
}

val htmlBuildDir = "$buildDir/spec/html"
val pdfBuildDir = "$buildDir/spec/pdf"
val resourcesBuildDir = "$htmlBuildDir/resources"
val jsBuildDir = "$resourcesBuildDir/js"

tasks.create<Copy>("copyStatic") {
    group = "internal"

    from("$projectDir/front-end/resources")
    into(resourcesBuildDir)
}

tasks.create<Copy>("copyBuiltJs") {
    group = "internal"

    mustRunAfter("front-end:webpack-bundle")

    from("$projectDir/front-end/build/js")
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

tasks.create<Task>("buildJs") {
    group = "internal"

    dependsOn("copyStatic")
    dependsOn("front-end:webpack-bundle")
    dependsOn("copyBuiltJs")

    doFirst {
        File(jsBuildDir).mkdirs()
    }
}

tasks.create<Copy>("buildWeb") {
    group = "build"

    dependsOn("docs:buildHtml")
    dependsOn("docs:buildHtmlBySections")
    dependsOn("copyHtml")
    dependsOn("buildJs")

    finalizedBy("front-end:clean", "docs:clean")
}

tasks.create<Copy>("buildPdf") {
    group = "build"

    dependsOn("docs:buildPdf")
    dependsOn("docs:buildPdfBySections")
    dependsOn("copyPdf")

    finalizedBy("front-end:clean", "docs:clean")
}

tasks.create<Delete>("clean") {
    group = "build"

    dependsOn("front-end:clean")
    dependsOn("docs:clean")

    delete("$projectDir/build")
}
