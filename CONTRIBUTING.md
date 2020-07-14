# Contributing

If you want to contribute to the Kotlin language specificaion, welcome!
This document describes the ways one could help with the specification together with the most important contribution guidelines.

## How to contribute?

You have several options on how to contribute.

1. Create an [issue](https://github.com/Kotlin/kotlin-spec/issues)
1. Make a [pull request](https://github.com/Kotlin/kotlin-spec/pulls)
1. Drop an email to [Marat Akhin](mailto:marat.akhin@jetbrains.com) or [Mikhail Belyaev](mailto:mikhail.belyaev@jetbrains.com)

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

TODO

### Enhance the grammar

TODO
