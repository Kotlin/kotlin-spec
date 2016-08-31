# Kotlin Language Specification

This repository contains the specification of the [Kotlin programming language](https://kotlinlang.org).
For user documentation please refer to the [Kotlin Website](https://kotlinlang.org).

## Downloads

The Kotlin Language Specification is available in the following formats:  

PDF | Single-page HTML | [AsciiDoc](http://www.methods.co.nz/asciidoc/) Source |
:----:|:----:|:----:
[kotlin-spec.pdf](kotlin-spec.pdf) | [kotlin-spec.html](kotlin-spec.html) | [kotlin-spec.asc](kotlin.asc)

## Build

* To set up `asciidoctor`, follow the instructions on [this page](http://asciidoctor.org/docs/install-toolchain/).
* Then, to set up `asciidoctor-pdf`, follow the instructions on [this page](http://asciidoctor.org/docs/convert-asciidoc-to-pdf/#install-the-published-gem). 
* Then, to render both the HTML and PDF versions, run the following script:

``` bash
$ ./build.sh
```