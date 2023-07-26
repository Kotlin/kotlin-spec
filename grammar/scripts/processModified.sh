#!/bin/bash

REF="$1"

for i in `git status -s | grep "^ M.*\.antlrtree.txt$" | cut -d ' ' -f 3`; do
    git diff --numstat HEAD $i | grep -q "$REF";
    if [[ $? == 0 ]]; then
        echo $i;
    fi
done
