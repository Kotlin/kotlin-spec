#!/bin/bash

echo "Rendering HTML..."
asciidoctor index.adoc
mv index.html ../.pages/index.html
echo "done"

echo "Rendering PDF..."
asciidoctor-pdf -r asciidoctor-mathematical index.adoc
mv index.pdf ../.pages/kotlin-spec.pdf
echo "done"
