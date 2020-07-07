# Kotlin Language Specification

[![TeamCity (simple build status)](https://img.shields.io/teamcity/https/teamcity.jetbrains.com/e/Kotlin_Spec_DocsMaster.svg?style=flat)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=Kotlin_Spec_DocsMaster&branch_Kotlin_dev=%3Cdefault%3E&tab=buildTypeStatusDiv)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Version](https://img.shields.io/github/v/release/Kotlin/kotlin-spec?style=flat)](https://github.com/Kotlin/kotlin-spec/releases)

This repository contains the specification of the [Kotlin programming language](https://kotlinlang.org).

For user documentation please refer to the [Kotlin Website](https://kotlinlang.org/docs/reference/).

## Artifacts

HTML version of the specification is available on the [Kotlin Website](https://kotlinlang.org/spec).

PDF version can be downloaded using the links in the HTML version.

## Kotlin grammar

The grammar is a part of the specification and is also contained in this repository.

The reference grammar files in ANTLR4 format are contained in the [grammar folder](https://github.com/Kotlin/kotlin-spec/tree/master/grammar/src/main/antlr/).
The human-friendly version in .md format (generated from ANTLR4) is located in the [docs folder](https://github.com/Kotlin/kotlin-spec/tree/master/docs/src/md/kotlin.core/grammar.generated.md).

## Building the specification

The specification is built using [Gradle](https://gradle.org/).
Therefore, most dependencies are downloaded by the build system.

However, there are several external depencencies which should be installed separately.
For instructions on how to do this, please refer to your operating system documentation.

* [Pandoc](https://pandoc.org/)
* [npm](https://www.npmjs.com/)

After installing these dependencies, building the specification is as easy as running:

```
./gradlew buildWeb buildPdf
```

which creates both HTML and PDF versions of the specification in `./build/`.

> When doing the build for the very first time, you will see Gradle downloading `ideaIC-LATEST-EAP-SNAPSHOT.zip`, which may take quite a long time.
> This IDEA snapshot is used for specification and grammar tests, and will be cached (until a new version is available), so this lengthy download should happen only once (in a while).

## Contributing

If you want to contribute to the specification, that's great!
You can help us make the Kotlin specification better by one of the following ways.

1. Create an [issue](https://github.com/Kotlin/kotlin-spec/issues) and describe what you think can be improved
1. Make a [pull request](https://github.com/Kotlin/kotlin-spec/pulls) and extends the specification
1. Discuss the specification on the Kotlin [forums](https://discuss.kotlinlang.org/)
1. Drop an email to [Marat Akhin](mailto:marat.akhin@jetbrains.com) or [Mikhail Belyaev](mailto:mikhail.belyaev@jetbrains.com) with your suggestions

We welcome any and all feedback to the specification, but may tweak, change or iterate with you on the contribution before including it in the specification.

TODO(Legal)

## Reference

If one needs to reference this specification, they may use the following:

> Marat Akhin, Mikhail Belyaev et al. "Kotlin language specification: Kotlin/Core", JetBrains / JetBrains Research, 2020
