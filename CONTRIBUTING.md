# Contributing

If you want to contribute to the Kotlin language specification, welcome!
This document describes the ways one could help with the specification together with the most important contribution guidelines.

## How to contribute?

You have several options on how to contribute.

1. Create an [issue](https://github.com/Kotlin/kotlin-spec/issues)
1. Make a [pull request](https://github.com/Kotlin/kotlin-spec/pulls)
1. Drop an email to [Marat Akhin](mailto:marat.akhin@jetbrains.com) or [Mikhail Belyaev](mailto:mikhail.belyaev@jetbrains.com)

> Note: if doing a pull request, it should be based on the `develop` branch.

In any of these cases, if we are talking about semantic changes to the specification, please try to include not only the improvements themselves, but also the reasoning on why you believe such a change is needed.

> If we are talking about simple, but still very much appreciated improvements, e.g., fixing grammar, spelling or punctuation mistakes, no justification is required for those.

If you are contributing information specific to a particular Kotlin version or an known experimental feature, be sure to mention them in the changes.  

## What to contribute?

There are three main ways how you can contribute to the Kotlin specification, which we outline below.
However, if your idea does not fit into these categories, do not hesitate and still send it our way, as we welcome all contributions and will carefully consider even the most unconventional ones.

### Improve the text

The most straightforward way of improving the specification is making its text better.
The specification is written in Markdown and compiled to the resulting HTML and PDF files via [Pandoc](https://pandoc.org/).
As Markdown is pretty lightweight and flexible, we have only a limited set of guidelines on how it should be written.

* A sentence should be placed on a single line, such an arrangement makes it easier to compare changes between different versions.
* Kotlin code blocks should be marked as such using `` ```kotlin ... ``` `` markdown.
* As we are using implicit header references, a preferred way of referencing to a section named `Awesome section title` is `[as follows][Awesome section title]`.
	- The few exceptions to this rule are when you need to reference a particular section with a duplicated name, these sections usually have a predefined `{#anchor}`.
	They should be referenced using the explicit anchor either `[in place](#anchor)` or via a `[reference][anchor reference]` defined separately as `[anchor reference]: #anchor`.

### Provide additional tests

Besides being a description of how Kotlin should work in different situations, the specification also includes a number of *tests* which check whether a compiler implementation is compatible with the specification.

> Note: as of now, these tests cover only a subset of the specification; in the future, we hope for this test suite to evolve into a complete Kotlin compatibility testing kit.

To access these tests, one may use the "Load tests" link available next to every section header in the HTML version.
After loading the tests, the section will be marked into paragraphs (represented as numbered outlines) and sentences (represented as numbered highlights), with available tests linked to the sentences.

A specification test is linked to one *primary* sentence, which is the main thing the test is checking, and zero or more *secondary* sentences, which are somewhat related to what the test is about.
Sentences are identified by their section path plus their paragraph number plus their sentence number (aka sentence identifier).

If a sentence have one or more tests linked to it (aka a *tested sentence*), it is highlighted in green; otherwise, it is highlighted in gray.
When you click on a tested sentence, a popup window is shown, which contains the available test cases for the sentence as a code snippet, augmented with the following information:

* Test kind; currently we support these kinds of tests:
	+ front-end diagnostics tests;
	+ codegen box tests;
* Test type; currently we support these kinds of tests:
	+ positive tests, which should successfully compile;
	+ negative tests, which should not compile with a compile-time error;
* Test linkage: primary or secondary;
* Test description.

The test code can be run from the popup by clicking the green arrow icon; you can also tweak the code by switching to edit mode via the plus sign icon.

As of now, these tests are hosted in the main [Kotlin compiler repository][spec-test-data], and are used in its continuous integration process.
Because of this, we do not currently have a very user-friendly story of contributing additional specification tests.

If you nonetheless want to contribute a new test for a statement in the specification, you should do the following.

* Write the test case itself as a standalone code snippet;
* Link it to one primary sentence and zero or more secondary sentences via their sentence identifiers;
* Describe the test using the aforementioned classification (kind, type, linkage, description);
* Drop us an email with this information.

TODO(Maybe provide a test case template somewhere?)

[spec-test-data]: https://github.com/JetBrains/kotlin/tree/master/compiler/tests-spec

### Enhance the grammar

Kotlin grammar included in the specification is actually compiled from a standalone grammar description.
It is written in ANTLR v4 format and available in the [`grammar`](./grammar) folder.
Further information about how it can be used is available in the [grammar README.md](./grammar/README.md).

If you want to contribute any grammar improvements, they should be made against the ANTLR version and not against the compiled version included in the specification.
To send them, please follow the general instructions on how to contribute.
