#!/bin/sh

# GitHub Pages

echo "Cloning GitHub Pages"
rm -rf .pages
git clone -b gh-pages --single-branch https://github.com/JetBrains/kotlin-spec.git .pages

echo "Rendering HTML..."
asciidoctor kotlin-spec.asc -o .pages/index.html
echo "done"

echo "Pushing to GitHub..."

cd .pages
git commit --all -m "Update GitHub pages"
git push origin gh-pages

echo "done"

# PDF

echo "Rendering PDF..."
asciidoctor-pdf kotlin-spec.asc
echo "done"