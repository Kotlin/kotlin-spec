#!/bin/bash

function doOpen {
    for opencmd in "open" "xdg-open" "cygstart" "gnome-open" "kfmclient exec" "exo-open"; do
        if type "$(echo "$opencmd" | cut -f 1 -d " ")" > /dev/null 2>&1; then
            eval $opencmd "\"$1\""
            return 0
        fi
    done
    echo "No open command found! Path being opened: $1" 1>&2
    return 1
}

# GitHub Pages
repo=`git remote -v | grep "^origin" | head -n 1 | sed  -E "s/[[:space:]]+/ /g" | cut -d " " -f 2`
echo "Detected remote origin: $repo"

echo "Cloning GitHub Pages"
rm -rf .pages
git clone  --depth 1 -b gh-pages --single-branch $repo .pages

echo "Building"
./build.sh

echo "Build complete, please review before pushing"
doOpen .pages/kotlin-spec.pdf
doOpen .pages/index.html

read -p "Push? [y/N] " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]
then    
    rm -rf .pages
    [[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # exit if not confirmed
fi

echo "Pushing to GitHub..."

cd .pages
git commit --all -m "Update GitHub pages"
git push origin gh-pages
cd ..
rm -rf .pages

echo "Done"
