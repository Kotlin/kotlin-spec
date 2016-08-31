#!/bin/sh

# HTML

echo "Rendering HTML..."
asciidoctor kotlin-spec.asc -o .pages/index.html
echo "done"

echo "Rendering PDF..."
asciidoctor-pdf kotlin-spec.asc -o .pages/kotlin-spec.pdf
echo "done"