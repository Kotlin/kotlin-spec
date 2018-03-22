#!/bin/bash

mkdir -p ../.pages

echo "Rendering HTML..."
gpp -H index.md | pandoc --toc -c index.css --mathjax -s -f markdown-raw_html -o ../.pages/index.html
cp index.css ../.pages/
echo "done"

echo "Rendering PDF..."
gpp -H index.md | pandoc --toc -H preamble.tex --variable documentclass=book -s -f markdown-raw_html+smart -o ../.pages/kotlin-spec.pdf
echo "done"
