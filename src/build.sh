#!/bin/bash

mkdir -p ../.pages

echo "Rendering HTML..."
gpp -H index.md | pandoc --toc --toc-depth=6 -c index.css --mathjax -s -f markdown-raw_html+smart+tex_math_double_backslash -o ../.pages/index.html
cp index.css ../.pages/
echo "done"

echo "Rendering PDF..."
gpp -H index.md | pandoc --toc --toc-depth=6 -H preamble.tex --variable documentclass=book -s -f markdown-raw_html+smart+tex_math_double_backslash -o ../.pages/kotlin-spec.pdf
echo "done"
