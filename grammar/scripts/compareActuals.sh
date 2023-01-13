#!/bin/bash

TEST_DATA="$1"

for fo in `find ${TEST_DATA} -name "*.antlrtree.txt"`; do
    fa="$i.actual";
    if [[ -e $fa ]]; then
        meld $fa $fo;
    fi
done
