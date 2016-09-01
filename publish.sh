#!/bin/sh

# GitHub Pages

echo "Cloning GitHub Pages"
rm -rf .pages
git clone  --depth 1 -b gh-pages --single-branch https://github.com/JetBrains/kotlin-spec.git .pages
echo "done"

./build.sh

echo "Pushing to GitHub..."

cd .pages
git commit --all -m "Update GitHub pages"
git push origin gh-pages

echo "done"