# Kotlin Language Specification

This repository contains the specification of the [Kotlin programming language](https://kotlinlang.org).
For user documentation please refer to the [Kotlin Website](https://kotlinlang.org).

## Downloads

The Kotlin Language Specification is available in the following formats:  

PDF | Single-page HTML | [AsciiDoc](http://www.methods.co.nz/asciidoc/) Source |
:----:|:----:|:----:
[kotlin-spec.pdf](kotlin-spec.pdf) | [View online](http://jetbrains.github.io/kotlin-spec/) | [kotlin-spec.asc](kotlin.asc)

## Build

Prerequisites:
* `asciidoctor` (to set up, follow the instructions on [this page](http://asciidoctor.org/docs/install-toolchain/))
* `asciidoctor-pdf` (to set up, follow the instructions on [this page](http://asciidoctor.org/docs/convert-asciidoc-to-pdf/#install-the-published-gem)) 

The build script does the following:

- Renders the single-page HTML version
- Uploads it to GitHub Pages (requires **write access** to the repo)
- Renders the PDF version

To run the build script:

``` bash
$ ./build.sh
```