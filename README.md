# Kotlin Language Specification

[![TeamCity (simple build status)](https://img.shields.io/teamcity/https/teamcity.jetbrains.com/e/Kotlin_Spec_DocsMaster.svg?style=flat)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=Kotlin_Spec_DocsMaster&branch_Kotlin_dev=%3Cdefault%3E&tab=buildTypeStatusDiv)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Latest release version](https://img.shields.io/github/v/release/Kotlin/kotlin-spec?style=flat)](https://github.com/Kotlin/kotlin-spec/releases)

This repository contains the specification of the [Kotlin programming language](https://kotlinlang.org), which describes how parts of the language should function *in more detail*, as compared to a more traditional user documentation on the [Kotlin Website](https://kotlinlang.org/docs/reference/).

It would be most useful to those who are interested in how Kotlin works on a finer lever and how its features interoperate, e.g., language enthusiasts, compiler writers and Kotlin power-users.
However, if you are simply wondering, why some code you wrote works the way it does, this specification might help you get an answer to that.

Currently, the specification covers only what we call *Kotlin/Core*: fundamental parts of Kotlin which should function the same way irregardless of the underlying platform.
In the future, we plan to extend it with additional platform-specific sections covering Kotlin/JVM, Kotlin/JS and Kotlin/Native.

## Compatibility

Kotlin Language Specification is still in progress and has **experimental** stability level, meaning no compatibility should be expected between even incremental releases, any functionality can be added, removed or changed without warning. 

## Artifacts

HTML version of the specification is available on the [Kotlin Website](https://kotlinlang.org/spec).

PDF version can be downloaded using the links in the HTML version or using the [direct link](https://kotlinlang.org/spec/pdf/kotlin-spec.pdf).

## Kotlin grammar

The grammar is a part of the specification and is also contained in this repository.

The reference grammar files in ANTLR4 format are located in the [grammar folder](./grammar/src/main/antlr/).
The human-friendly version is available on the [Kotlin Website](https://kotlinlang.org/docs/reference/grammar.html), in the [HTML](https://kotlinlang.org/spec/syntax-and-grammar.html) or [PDF](https://kotlinlang.org/spec/pdf/sections/syntax-and-grammar.pdf) version of the specification.

## Building the specification

The specification is built using [Gradle](https://gradle.org/).
Therefore, most dependencies are downloaded by the build system.

However, there are several external dependencies which should be installed separately.
For instructions on how to do this, please refer to your operating system documentation.

* [Pandoc](https://pandoc.org/) (tested with version 2.9.1)
* [npm](https://www.npmjs.com/) (tested with version 6.14.6)
* [gpp](https://logological.org/gpp) (tested with version 2.25)
* [bash](https://www.gnu.org/software/bash/) (tested with version 5.0.17)

After installing these dependencies, building the specification is as easy as running:

```
./gradlew buildWeb buildPdf
```

which creates both HTML and PDF versions of the specification in `./build/spec`.

> When doing the build for the very first time, you will see Gradle downloading `ideaIC-LATEST-EAP-SNAPSHOT.zip`, which may take quite a long time.
> This IDEA snapshot is used for specification and grammar tests, and will be cached (until a new version is available), so this lengthy download should happen only once (in a while).

The PDF version is available in two flavours: single-file PDF, containing the complete specification, and a set of section-based PDFs, containing their respective sections.

The HTML version consists of a set of section-based pages linked together via an interactive table of contents.

## Contributing

If you want to contribute to the specification, that's great!
You can help us make the Kotlin specification better by one of the following ways.

1. Create an [issue](https://github.com/Kotlin/kotlin-spec/issues) and describe what you think can be improved
1. Make a [pull request](https://github.com/Kotlin/kotlin-spec/pulls) and extends the specification
1. Discuss the specification on the Kotlin [forums](https://discuss.kotlinlang.org/)
1. Drop an email to [Marat Akhin](mailto:marat.akhin@jetbrains.com) or [Mikhail Belyaev](mailto:mikhail.belyaev@jetbrains.com) with your suggestions

> Note: if doing a pull request, it should be based on the `develop` branch.

We welcome any and all feedback to the specification, but may tweak, change or iterate with you on the contribution before including it in the specification.

Further details on how to contribute to the specification are available in [CONTRIBUTING.md](CONTRIBUTING.md).

## Reference

If one needs to reference this specification, they may use the following:

> Marat Akhin, Mikhail Belyaev et al. "Kotlin language specification: Kotlin/Core", JetBrains / JetBrains Research, 2020
