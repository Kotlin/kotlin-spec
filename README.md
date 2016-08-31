# Kotlin Language Specification

This repository contains the specification of the [Kotlin programming language](https://kotlinlang.org).
For user documentation please refer to the [Kotlin Website](https://kotlinlang.org).

## Downloads

The Kotlin Language Specification is available in the following formats:  

Single-page HTML | PDF | [AsciiDoc](http://www.methods.co.nz/asciidoc/) Source |
:----:|:----:|:----:
[View online](http://jetbrains.github.io/kotlin-spec/) | [kotlin-spec.pdf](http://jetbrains.github.io/kotlin-spec/kotlin-spec.pdf) | [kotlin-spec.asc](kotlin.asc)

## Build

Prerequisites:
* `asciidoctor` (to set up, follow the instructions on [this page](http://asciidoctor.org/docs/install-toolchain/))
* `asciidoctor-pdf` (to set up, follow the instructions on [this page](http://asciidoctor.org/docs/convert-asciidoc-to-pdf/#install-the-published-gem)) 

To build the HTML and PDF versions into the `.pages` directory: 

``` bash
$ ./build.sh
```

To build everything and publish to GitHub Pages:

``` bash
$ ./publish.sh
```