#!/bin/sh

# HTML

echo "Rendering HTML..."
asciidoctor kotlin-spec.asc -o index.html
mv index.html .pages
echo "done"

echo "Rendering PDF..."
asciidoctor-pdf -r asciidoctor-mathematical kotlin-spec.asc # -o kotlin-spec.pdf
mv kotlin-spec.pdf .pages
echo "done"
